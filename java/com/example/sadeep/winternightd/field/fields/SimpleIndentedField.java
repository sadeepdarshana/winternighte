package com.example.sadeep.winternightd.field.fields;

/**
 * Created by Sadeep on 10/14/2016.
 */

import android.content.Context;
import android.graphics.Point;
import android.text.Layout;
import android.text.Spanned;
import android.widget.TextView;

import com.example.sadeep.winternightd.activities.NoteContainingActivity;
import com.example.sadeep.winternightd.activities.NotebookActivity;
import com.example.sadeep.winternightd.dumping.FieldDataStream;
import com.example.sadeep.winternightd.field.FieldFactory;
import com.example.sadeep.winternightd.field.SingleText;
import com.example.sadeep.winternightd.misc.Globals;
import com.example.sadeep.winternightd.misc.Utils;
import com.example.sadeep.winternightd.misc.NoteContainingActivityRootView;
import com.example.sadeep.winternightd.notebook.Notebook;
import com.example.sadeep.winternightd.textboxes.EditTextView;
import com.example.sadeep.winternightd.textboxes.XEditText;
import com.example.sadeep.winternightd.spans.RichText;

/**
 * An IndentedField that has one textbox.
 * This is the most simple non-abstract Field, it simply is a text field where you type stuff.
 */
public class SimpleIndentedField extends IndentedField implements SingleText {


    public static final int classFieldType = 1422342676;


    protected EditTextView editTextView;  //the main View containing the text



    public SimpleIndentedField(Context context) {
        this(false,context);
    }
    public SimpleIndentedField(boolean isEditable,Context context) {
        super(context);
        init(isEditable);
    }


    private void init(boolean isEditable){

        fieldType = classFieldType;

        editTextView = new EditTextView(getContext(),isEditable,this);
        addView(editTextView.get());

        setClipChildren(false);

        setPadding(6* Globals.dp2px,0,0,0);

        super.setIsEditable(isEditable); //here we call super.setEditable() because we have already created the edittextview to suit the isEditable parameter. (no need of this.setEditable())
    }

    @Override
    public void setIsEditable(boolean isEditable) {//Override Field.setEditable()
        editTextView.setIsEditable(isEditable);
        super.setIsEditable(isEditable);
    }

    @Override
    public TextView getMainTextBox() {//Override SingleText's method
        return editTextView.get();
    }





//methods relating to backspace & enter keys

    /**
     * backspace has been pressed at the start of the next Field.
     *
     * When backspace is pressed in the start of the next Field we either
     *      (1) Append its text(if any) to this Field, or
     *      (2) We delete this Field.
     * Since this is a SimpleIndentedField and therefore a text Field we go with (1) without deleting this.
     *
     * There are 2 cases,
     *      1. next Field is text (implements SingleText)
     *      2. next Field is not text (doesn't implement SingleText)
     *
     *
     */
    @Override
    public void backspaceField() {

        CharSequence charSequenceNextField = null;
        if(getNote().getChildCount()-1!=this.getFieldIndex()){
            Field nextfield = getNote().getFieldAt(this.getFieldIndex()+1);
            if(nextfield instanceof SingleText)charSequenceNextField= ((SingleText)nextfield).getMainTextBox().getText();
        }

        int len = getMainTextBox().length();

        if(charSequenceNextField!=null)this.getMainTextBox().setText(android.text.TextUtils.concat(this.getMainTextBox().getText(),charSequenceNextField));
        this.getMainTextBox().requestFocus();
        ((XEditText)this.getMainTextBox()).setSelection(len);

        postDelayed(new Runnable() {
            @Override
            public void run() {
                getNote().removeView(getNote().getFieldAt(getFieldIndex()+1));
            }
        },1);


    }

    /**
     * Enter key has been pressed in the Field's XEditText.
     * We need to create a new Field of the same type and put whatever the text that was there after enterkey position to that new Field.
     *
     * Notice that the created Field (or this Field) will not necessarily be a SimpleIndentedText, it could be a derived class from SimpleIndentedField.
     */
    @Override
    public boolean onEnterKeyPressed(TextView textView) {
        //if(this.getClass()==SimpleIndentedField.class)return false;

        try{((NotebookActivity)getContext()).getNotebook().setLayoutFrozen(true);}catch (Exception e){};

        CharSequence textTransferred = textView.getText().subSequence(textView.getSelectionStart(),textView.length());
        CharSequence textRemains = textView.getText().subSequence(0,textView.getSelectionStart()-1);

        SimpleIndentedField newfield = (SimpleIndentedField) FieldFactory.createNewField(getContext(), getFieldType(),true);
        getNote().addView(newfield,getFieldIndex()+1);


        newfield.getMainTextBox().setText(textTransferred);
        this.getMainTextBox().setText(textRemains);

        newfield.getMainTextBox().requestFocus();
        ((XEditText)newfield.getMainTextBox()).setSelection(0);

        try{((NotebookActivity)getContext()).getNotebook().setLayoutFrozen(false);}catch (Exception e){};

        newfield.setIndent(this.getIndent());

        return true;
    }


    /**
     * This method is intended for t
     * he use of derived classes(ex. BulletedField) of SimpleIndentedField.
     * The method transforms instances of such derived classes into a SimpleIndentedField.
     *
     * The method removes this Field and adds an identical looking (but SimpleIndentedField rather than its derived class) Field
     * into the note to take the former's position.
     */
    protected void revertToSimpleIndentedField(){


        NoteContainingActivityRootView.pauseLayout();

        final SimpleIndentedField newfield = (SimpleIndentedField) FieldFactory.createNewField(getContext(), SimpleIndentedField.classFieldType,true);
        newfield.getMainTextBox().setText(this.getMainTextBox().getText());

        getNote().addView(newfield,getFieldIndex());
        newfield.getMainTextBox().requestFocus();
        ((XEditText)newfield.getMainTextBox()).setSelection(0);


        post(new Runnable() {
            @Override
            public void run() {
                newfield.setIndent(SimpleIndentedField.this.getIndent());
                getNote().removeView(SimpleIndentedField.this);
                ((NoteContainingActivity)getContext()).getRootView().resumeLayout();
            }
        });

    }



//selection & clipboard related methods

    @Override
    public void setCursorVisible(boolean visible) {
        getMainTextBox().setCursorVisible(visible);
    }

    @Override
    public Object duplicateSelection(int characterIndex1, int characterIndex2) {
        if(characterIndex1==-2&&characterIndex2==-1)return super.duplicateSelection(characterIndex1, characterIndex2);

        if(characterIndex1==-2){
            SimpleIndentedField field = (SimpleIndentedField) duplicate();
            CharSequence text = Utils.duplicateCharSequence(getMainTextBox().getText().subSequence(0,characterIndex2));
            field.getMainTextBox().setText(text);
            return field;
        }
        if(characterIndex1>=0&&characterIndex2==-1){
            return Utils.duplicateCharSequence(getMainTextBox().getText().subSequence(characterIndex1,getMainTextBox().length()));
        }
        if(characterIndex1>=0){
            return Utils.duplicateCharSequence(getMainTextBox().getText().subSequence(characterIndex1,characterIndex2));
        }
        return null;
    }

    @Override
    public Point getAbsoluteCoordinatesForSingleTextCharacterIndex(int characterIndex) {

        TextView text = getMainTextBox();

        int[] xy = new int[2];
        text.getLocationInWindow(xy);

        Layout layout = text.getLayout();
        int line = layout.getLineForOffset(characterIndex);
        int baseline = layout.getLineBaseline(line);
        int x = (int)layout.getPrimaryHorizontal(characterIndex);
        int y = baseline;

        return new Point(xy[0] + x + text.getPaddingLeft(), y + xy[1] + text.getPaddingTop());
    }

    @Override
    public int getSingleTextCharacterIndexForAbsoluteCoordinates(int rawX, int rawY) {

        TextView text = getMainTextBox();

        int[] xy = new int[2];
        text.getLocationInWindow(xy);

        int characterPos = text.getOffsetForPosition(rawX - xy[0], rawY - xy[1]);

        return characterPos;
    }



//dumping related methods

    @Override
    public void writeToFieldDataStream(FieldDataStream stream) {
        super.writeToFieldDataStream(stream);

        RichText.generateRichText((Spanned) editTextView.getText()).writeToFieldDataStream(stream);   //0  the text of the Field
    }

    @Override
    public void readFromFieldDataStream(FieldDataStream stream) {
        super.readFromFieldDataStream(stream);

        editTextView.get().setText(RichText.readFromFieldDataStream(stream).getCharSequence());            //0  the text of the Field
    }

    public boolean isEmpty() {
        return getMainTextBox().length()==0;
    }
}
