package com.example.sadeep.winternightd.fields;

/**
 * Created by Sadeep on 10/14/2016.
 */

import android.content.Context;

import com.example.sadeep.winternightd.general.EditTextView;

/**
 * An IndentedField that has one textbox.
 * This is the most simple non-abstract Field, it simply is a text field where you type stuff.
 */
public class SimpleIndentedField extends IndentedField{


    public static final int classFieldType = 2;

    protected EditTextView editTextView;

    public SimpleIndentedField(Context context) {
        this(false,context);
    }
    public SimpleIndentedField(boolean isEditable,Context context) {
        super(context);
        init(isEditable);
    }

    private void init(boolean isEditable){

        fieldType = classFieldType;

        editTextView = new EditTextView(isEditable,getContext());
        addView(editTextView.get());

        super.setIsEditable(isEditable); //here we call this.setIsEditable() because we have already created the edittextview to suit the isEditable parameter.
    }

    @Override
    public void setIsEditable(boolean isEditable) {
        editTextView.setIsEditable(isEditable);
        super.setIsEditable(isEditable);
    }
}
