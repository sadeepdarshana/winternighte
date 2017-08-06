package com.example.sadeep.winternightd.fields;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.sadeep.winternightd.misc.Globals;

/**
 * Created by Sadeep on 10/18/2016.
 */


/**
 * A SimpleIndentedField that has a number at the start.
 * NumberedField is created by inserting a TextView as the 0th child of the SimpleIndentedField to display the number.
 */
public class NumberedField extends SimpleIndentedField {


    public static final int classFieldType = 5;

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

        number=0;

        numberTextView = new TextView(getContext());
        numberTextView.setPadding(0,0,0,0);
        numberTextView.setBackgroundColor(Color.TRANSPARENT);
        numberTextView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        numberTextView.setTextSize(TypedValue.COMPLEX_UNIT_FRACTION,  Globals.defaultFontSize);
        setNumber();

        addView(numberTextView,0);

    }

    public int getNumber(){
        return number;
    }

    private void setNumber(int num){
        number = num;
        numberTextView.setText(String.valueOf(num)+".  ");
    }
    private void setNumber(){
        setNumber(getNumber());
    }
}
