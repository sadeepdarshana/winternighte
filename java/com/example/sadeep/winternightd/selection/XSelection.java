package com.example.sadeep.winternightd.selection;

import android.graphics.Color;
import android.provider.Settings;
import android.text.Selection;
import android.text.Spannable;
import android.text.Spanned;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.example.sadeep.winternightd.activities.NoteContainingActivity;
import com.example.sadeep.winternightd.activities.NotebookActivity;
import com.example.sadeep.winternightd.field.SingleText;
import com.example.sadeep.winternightd.field.fields.Field;
import com.example.sadeep.winternightd.field.fields.SimpleIndentedField;
import com.example.sadeep.winternightd.misc.Globals;
import com.example.sadeep.winternightd.note.Note;
import com.example.sadeep.winternightd.notebook.Notebook;
import com.example.sadeep.winternightd.spans.SpansController;
import com.example.sadeep.winternightd.spans.SpansFactory;
import com.example.sadeep.winternightd.textboxes.XEditText;

/**
 * Created by Sadeep on 10/21/2016.
 */


/**
 * we don't use the built-in android selection system because we want to select not only text(checkboxes etc too) and
 * our selection may extend over several EditTexts.
 */
final public class XSelection {
    private static TextView textbox;
    private static long selectionTime;

    private XSelection(){}

    private static  boolean selectionAvailable = false;
    private static Handle [] handles;
    private static Note note;
    private static int draws=0;

    private static ViewTreeObserver.OnPreDrawListener onPreDrawListener = new ViewTreeObserver.OnPreDrawListener() {
        @Override
        public boolean onPreDraw() {
            try {
                XSelection.refreshHandlePositions();
                if(draws>2)return true ;
                draws++;
                if(note.getContext()instanceof NotebookActivity)Notebook.suspendScrollTemporary();
                note.clearFocus();
                if(!textbox.hasFocus())textbox.requestFocus();
                if (textbox instanceof XEditText) ((XEditText) textbox).setSelection(0);
                return true;
            } catch (Exception e) { return true;}
        }
    };

    public static boolean isSelectionAvailable() {
        return selectionAvailable;
    }

    public static void setActiveNote(Note note){
        XSelection.note = note;
    }

    public static CursorPosition getSelectionStart(){
        if(handles[0]==null||handles[1]==null||handles[0].getCursorPosition()==null||handles[1].getCursorPosition()==null)return null;
        return CursorPosition.min(handles[0].getCursorPosition(),handles[1].getCursorPosition());
    }
    public static CursorPosition getSelectionEnd(){
        if(handles[0]==null||handles[1]==null||handles[0].getCursorPosition()==null||handles[1].getCursorPosition()==null)return null;
        return CursorPosition.max(handles[0].getCursorPosition(),handles[1].getCursorPosition());
    }
    public static Note getSelectedNote(){
        return note;
    }


    public static void newSelection(Note note, CursorPosition pos1, CursorPosition pos2, TextView xEditText){
        clearSelections();
        selectionTime = System.currentTimeMillis();
        XSelection.note = note;
        XSelection.textbox =xEditText;

        Notebook.suspendScrollTemporary();

        Spannable txt = (Spannable)textbox.getText();
        Object[] spans = txt.getSpans(0,txt.length(),Object.class);
        //for(Object span:spans)if(span.getClass().getName().contains("START")||span.getClass().getName().contains("END"))txt.removeSpan(span);
        //Selection.removeSelection();


        note.setCursorVisible(false);

        if(handles!=null){
            if(handles[0]!=null)handles[0].dismiss();
            if(handles[1]!=null)handles[1].dismiss();
        }

        handles = new Handle[2];
        handles[0] = new Handle(note);
        handles[1] = new Handle(note);

        handles[0].updatePosition(pos1, true);
        handles[1].updatePosition(pos2, true);

        try{note.getViewTreeObserver().removeOnPreDrawListener(onPreDrawListener);}catch (Exception e){}
        note.getViewTreeObserver().addOnPreDrawListener(onPreDrawListener);


        selectionAvailable = true;
        ((NoteContainingActivity)note.getContext()).setActionBarMode(NoteContainingActivity.ACTIONBAR_SELECT);
        draws=0;

    }

    public static void clearSelections() {
        if(System.currentTimeMillis()<selectionTime+750)return;//// TODO: 8/8/2017 permanent solution
        Notebook.suspendScrollTemporary();

        if(note!=null)((NoteContainingActivity)note.getContext()).setActionBarMode(0);

        try {
            handles[0].dismiss();
            handles[1].dismiss();
            note.getViewTreeObserver().removeOnPreDrawListener(onPreDrawListener);
            highlightSelection(note,new CursorPosition(0,0),new CursorPosition(0,0));

        }catch (Exception e){}

        try {
            note.setCursorVisible(true);
        }catch (Exception e){}

        selectionAvailable = false;
        note = null;
    }



    public static void replaceSelectionWith(CharSequence text){
        CursorPosition start = CursorPosition.min(handles[0].getCursorPosition(),handles[1].getCursorPosition());
        CursorPosition end = CursorPosition.max(handles[0].getCursorPosition(),handles[1].getCursorPosition());

        note.eraseContent(start,end);
        note.insertCharSequenceAt(start,text);
        start.characterIndex+=text.length();
        note.setCursor(start);
        clearSelections();
    }

    static void handlePositionUpdated(boolean handleCursorPositionChanged) {
        if(handleCursorPositionChanged) {
            updateHighlightSpans();
            SpansController.updateToolbarForCurrentSelection();
        }
    }
    static void refreshHandlePositions() {
        handles[0].updatePosition(handles[0].getCursorPosition(),false);
        handles[1].updatePosition(handles[1].getCursorPosition(),false);
    }
    private static void updateHighlightSpans() {
        if(handles[0].getCursorPosition()==null||handles[1].getCursorPosition()==null)return;
        CursorPosition start = CursorPosition.min(handles[0].getCursorPosition(),handles[1].getCursorPosition());
        CursorPosition end = CursorPosition.max(handles[0].getCursorPosition(),handles[1].getCursorPosition());

        highlightSelection(note,start,end);

    }

    /**
     * Returns the current position of the cursor. But the function is not wise enough to identify
     * the current working note. So you need to provide it.
     * @param note current working note
     * @return CursorPosition if currently being working on the note, if not null
     */
    public static CursorPosition getCurrentCursorPosition(Note note){
        if(note.getFocusedChild()==null)return null;
        if(note.getFocusedChild()instanceof SimpleIndentedField) {
            return new CursorPosition(note.indexOfChild(note.getFocusedChild()),((SimpleIndentedField) note.getFocusedChild()).getMainTextBox().getSelectionStart());
        }
        return null;
    }



    private static void highlightSelection(Note note,CursorPosition start, CursorPosition end) {

        /**
         * We first remove all the highlightings; that is,
         *  we set the Field background color to transparent
         *  we remove all XSelectionSpans
         */
        for(int c=0;c<note.getChildCount();c++){
            note.getFieldAt(c).setBackgroundColor(Color.TRANSPARENT);
            if(note.getFieldAt(c)instanceof SingleText){
                Spannable spannable = (Spannable) ((SingleText) note.getFieldAt(c)).getMainTextBox().getText();
                Object[] spans = spannable.getSpans(0, spannable.length(), SpansFactory.XSelectionSpan.class);

                for (Object x : spans) spannable.removeSpan(x);
                ((SingleText) note.getFieldAt(c)).getMainTextBox().invalidate();
            }
        }

        if(start.equals(end))return;
        /**
         * Now we add spans to suit start and end.
         */
        if(start.characterIndex==end.characterIndex && start.fieldIndex==end.fieldIndex)return;

        for(int c=start.fieldIndex+1;c<end.fieldIndex;c++){
            note.getFieldAt(c).setBackgroundColor(Globals.defaultHighlightColor);
        }

        if(start.fieldIndex==end.fieldIndex){
            Field field = note.getFieldAt(start.fieldIndex);
            if(start.characterIndex>=0&&end.characterIndex>=0) {
                if(!(field instanceof SingleText))return;
                TextView tv = ((SingleText) note.getFieldAt(start.fieldIndex)).getMainTextBox();
                Spannable spannable = (Spannable) tv.getText();
                spannable.setSpan(SpansFactory.createSpan(SpansFactory.XSelectionSpan.spanType),start.characterIndex,end.characterIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                tv.invalidate();
            }
            else if (start.characterIndex==-2 && end.characterIndex ==-1)field.setBackgroundColor(Globals.defaultHighlightColor);
        }
        else {
            if(start.characterIndex==-2) note.getFieldAt(start.fieldIndex).setBackgroundColor(Globals.defaultHighlightColor);
            else if(start.characterIndex>=0) {
                Spannable spannable = (Spannable)((SingleText) note.getFieldAt(start.fieldIndex)).getMainTextBox().getText();
                spannable.setSpan(SpansFactory.createSpan(SpansFactory.XSelectionSpan.spanType),start.characterIndex,spannable.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ((SingleText) note.getFieldAt(start.fieldIndex)).getMainTextBox().invalidate();
            }

            if(end.characterIndex==-1) note.getFieldAt(end.fieldIndex).setBackgroundColor(Globals.defaultHighlightColor);
            else if(end.characterIndex>=0) {
                Spannable spannable = (Spannable)((SingleText) note.getFieldAt(end.fieldIndex)).getMainTextBox().getText();
                spannable.setSpan(SpansFactory.createSpan(SpansFactory.XSelectionSpan.spanType),0,end.characterIndex,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ((SingleText) note.getFieldAt(end.fieldIndex)).getMainTextBox().invalidate();
            }
        }
    }
}

