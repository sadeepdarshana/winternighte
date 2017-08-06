package com.example.sadeep.winternightd.field.fields;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.sadeep.winternightd.textboxes.XEditText;
import com.example.sadeep.winternightd.misc.Globals;

/**
 * Created by Sadeep on 10/18/2016.
 */


/**
 * A SimpleIndentedField that has a number at the start.
 * NumberedField is created by inserting a TextView as the 0th child of the SimpleIndentedField to display the number.
 */
public class NumberedField extends SimpleIndentedField {


    public static final int classFieldType = 340657775;

    private TextView numberTextView;
    private int number;

    public NumberedField(Context context) {
        this(false,context);
    }
    public NumberedField(boolean isEditable, Context context) {
        super(isEditable, context);
        init();
    }

    private void init(){

        fieldType = classFieldType;

        numberTextView = new TextView(getContext());
        numberTextView.setPadding(0,0,0,0);
        numberTextView.setBackgroundColor(Color.TRANSPARENT);
        numberTextView.setTextSize(TypedValue.COMPLEX_UNIT_FRACTION,  Globals.defaultFontSize);
        numberTextView.setTextColor(Color.parseColor("#992255"));

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.setMargins( 3* Globals.dp2px,0 , 1* Globals.dp2px,0);
        numberTextView.setLayoutParams(lp);

        setNumber(0);

        addView(numberTextView,0);

    }



    public int getNumber(){
        return number;
    }

    private void setNumber(int num){
        number = num;
        numberTextView.setText(String.valueOf(num)+".  ");
    }




    /**
     * backspace has been pressed at the start of the Fields main textbox.
     *
     * Here we need to remove ('backspace') the number [of the NumberedField]. So we convert this into a SimpleIndentedField
     */
    @Override
    public void onBackspaceKeyPressedAtStart(XEditText xEditText) {
        revertToSimpleIndentedField();
        //refreshThisAndAllFieldsBelowToAccountForNoteChanges(); //refresh the numbers of below Fields
    }

    /**
     * number may need to be changed as a result of the change in other Fields, calling updateNumber()
     */
    @Override
    public void refreshToAccountForNoteChanges() {
        super.refreshToAccountForNoteChanges();
        updateNumber();
    }

    /**
     * we allocate this the number when this is attached to a note
     */
    @Override
    public void onAttachedToNote() {
        super.onAttachedToNote();
        updateNumber();
    }

    /**
     * Finds the due number that should be assigned to this NumberedField and assign it to the Field
     */
    private void updateNumber() {
        if(getNote()==null)return; //this is not attached to a Note, thus no number can be given to this

        int num = 1;//default is 1, if not reassigned in the following code

        //iterate upwards to 0th Field starting from the Field immediately above to find the value for num
        for(int i = getFieldIndex()-1;i>=0;i--){
            Field f = (getNote().getFieldAt(i));
            NumberedField numberedField=null;
            try{ numberedField = (NumberedField)f;}catch (Exception e){}
            if (numberedField==null)break; //number=1

            if(numberedField.getIndent() == getIndent()){ //a NumberedField indented same as this exists
                num = numberedField.getNumber()+1; //this should get the next number
                break;
            }
            if(numberedField.getIndent()<getIndent())break; //number=1

            //ignore the NumberedFields indented more than this
            //if(numberedField.getIndent()>getIndent())continue; //line implied & thus commented
        }
        setNumber(num);
    }

}
