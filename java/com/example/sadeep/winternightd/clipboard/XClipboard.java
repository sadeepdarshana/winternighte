package com.example.sadeep.winternightd.clipboard;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.SystemClock;
import android.text.SpannableStringBuilder;
import android.widget.TextView;

import com.example.sadeep.winternightd.field.fields.Field;
import com.example.sadeep.winternightd.field.fields.SimpleIndentedField;
import com.example.sadeep.winternightd.note.Note;
import com.example.sadeep.winternightd.activities.NotebookActivity;
import com.example.sadeep.winternightd.misc.Utils;
import com.example.sadeep.winternightd.textboxes.XEditText;
import com.example.sadeep.winternightd.selection.CursorPosition;
import com.example.sadeep.winternightd.selection.XSelection;

import java.util.Vector;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * Created by Sadeep on 12/27/2016.
 */
final public class XClipboard {
    private XClipboard(){}

    public static CharSequence clipedSelectionStartText="";
    public static Vector<Field> clipedSelectionFields=new Vector<>();

    private static long lastCopyTime;

    private static ClipboardManager.OnPrimaryClipChangedListener clipboardListener;

    public static void initialize(Context context){
        if(clipboardListener!=null)return;
        final ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);

        clipboardListener = new ClipboardManager.OnPrimaryClipChangedListener() {

            @Override
            public void onPrimaryClipChanged() {
                lastCopyTime = SystemClock.uptimeMillis();
                try{if(clipboardManager.getPrimaryClip().getDescription().getLabel().equals("WNXClipboard"))return;}catch (Exception e){}
                clipedSelectionFields = new Vector<Field>();
                clipedSelectionStartText = clipboardManager.getPrimaryClip().getItemAt(0).getText();
            }
        };

        clipboardManager.addPrimaryClipChangedListener(clipboardListener);
    }


    public static void copySelectionToClipboard(){
        if(!XSelection.isSelectionAvailable())return;

        CursorPosition start = XSelection.getSelectionStart();
        CursorPosition end = XSelection.getSelectionEnd();

        Note note = XSelection.getSelectedNote();

        Vector<Object> fieldsandstrings = new Vector<>();


        if(start.fieldIndex==end.fieldIndex){
            fieldsandstrings.add(note.getFieldAt(start.fieldIndex).duplicateSelection(start.characterIndex,end.characterIndex));
        }
        else {
            for(int i=start.fieldIndex;i<=end.fieldIndex;i++){
                int a=-2,b=-1;

                if(i==start.fieldIndex)a=start.characterIndex;
                if(i==end.fieldIndex)b=end.characterIndex;

                Object o = note.getFieldAt(i).duplicateSelection(a,b);
                fieldsandstrings.add(o);
            }
        }

        clipedSelectionStartText=null;
        clipedSelectionFields=new Vector<Field>();

        for(int c=0;c<fieldsandstrings.size();c++){
            if(fieldsandstrings.get(c) instanceof CharSequence)clipedSelectionStartText=(CharSequence)fieldsandstrings.get(c);
            else clipedSelectionFields.add((Field)fieldsandstrings.get(c));
        }

    }

    public static void pasteClipboardToCurrentCursor(Context context, XEditText intoXEditText) {

        Note note =null;
        if(XSelection.isSelectionAvailable()){
            note = XSelection.getSelectedNote();
            CursorPosition start = XSelection.getSelectionStart();
            note.eraseContent(start,XSelection.getSelectionEnd());
            XSelection.clearSelections();
            note.setCursor(start);
        }
        else {
            if (context instanceof NotebookActivity)
                note = ((NotebookActivity) context).getActiveNote();
            //todo other activities
        }

        CursorPosition cp = note.getCurrentCursorPosition();

        if(cp==null){
            cp=new CursorPosition(intoXEditText.boundField.getFieldIndex(),intoXEditText.getSelectionStart());
        }

        if(cp.isInternal()){
            SimpleIndentedField field = (SimpleIndentedField) note.getFieldAt(cp.fieldIndex);
            XEditText edittext = (XEditText) field.getMainTextBox();

            CharSequence oldtext = edittext.getText();
            CharSequence textbegin = android.text.TextUtils.concat(oldtext.subSequence(0,cp.characterIndex),XClipboard.clipedSelectionStartText);
            CharSequence textend = oldtext.subSequence(cp.characterIndex,oldtext.length());

            edittext.setText(textbegin);

            int c=0;
            for(c=0;c<XClipboard.clipedSelectionFields.size();c++){
                note.addView(clipedSelectionFields.get(c).duplicate(),cp.fieldIndex+c+1);
            }

            if(note.getFieldAt(cp.fieldIndex+c)instanceof SimpleIndentedField){
                XEditText xEditText = (XEditText) ((SimpleIndentedField)note.getFieldAt(cp.fieldIndex+c)).getMainTextBox();
                int n=xEditText.length();
                xEditText.setText(android.text.TextUtils.concat(xEditText.getText(),textend));
                xEditText.requestFocus();
                xEditText.setSelection(n);
            }
        }
    }

    public static void requestPaste(Context context, XEditText xEditText){
        pasteClipboardToCurrentCursor(context,xEditText);
    }

    public static void requestCopy(){


        XClipboard.copySelectionToClipboard();
        CursorPosition cp = XSelection.getSelectionStart();
        Note note =  XSelection.getSelectedNote();
        XSelection.clearSelections();
        note.setCursor(cp);

        final ClipboardManager clipboardManager = (ClipboardManager) note.getContext().getSystemService(CLIPBOARD_SERVICE);
        clipboardManager.setPrimaryClip(ClipData.newPlainText("WNXClipboard",generateTextRepresentation()));
    }

    public static void requestCut(){
        XClipboard.copySelectionToClipboard();
        CursorPosition cp = XSelection.getSelectionStart();
        Note note =  XSelection.getSelectedNote();
        if(XSelection.getSelectedNote().getIsEditable())XSelection.replaceSelectionWith("");
        XSelection.clearSelections();
        note.setCursor(cp);

        final ClipboardManager clipboardManager = (ClipboardManager) note.getContext().getSystemService(CLIPBOARD_SERVICE);
        clipboardManager.setPrimaryClip(ClipData.newPlainText("WNXClipboard",generateTextRepresentation()));

    }

    private static CharSequence generateTextRepresentation(){
        SpannableStringBuilder s = new SpannableStringBuilder("");
        if(clipedSelectionStartText!=null)s.append(clipedSelectionStartText);
        for(Field field:clipedSelectionFields){
            if(field instanceof SimpleIndentedField){
                s.append("\n");
                SimpleIndentedField sfield = (SimpleIndentedField)field;
                String indent = "";
                for(int i=0;i<sfield.getIndent();i++)indent+="    ";
                s.append(indent);
                s.append(((SimpleIndentedField) field).getMainTextBox().getText());

            }
        }
        return Utils.duplicateCharSequence(s);
    }

    public static void updateLastCopyTime(TextView textbox) {
        if(SystemClock.uptimeMillis()-lastCopyTime>15000)return;

        try {
            java.lang.reflect.Field field = null;
            field =TextView.class.getDeclaredField("sLastCutCopyOrTextChangedTime");
            field.setAccessible(true);

            field.set(textbox,lastCopyTime);


        } catch (Exception e) {}
    }
}
