package com.example.sadeep.winternightd.field.fields;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.TextView;

import com.example.sadeep.winternightd.activities.NotebookActivity;
import com.example.sadeep.winternightd.field.FieldFactory;
import com.example.sadeep.winternightd.misc.Globals;
import com.example.sadeep.winternightd.textboxes.XEditText;

/**
 * Created by Sadeep on 1/21/2018.
 */

public class H1Field extends SimpleIndentedField {


    public static final int classFieldType = 581749275;

    public H1Field(Context context) {
        this(false,context);
    }
    public H1Field(boolean isEditable,Context context) {
        super(context);
        init(isEditable);
    }


    private void init(boolean isEditable){
        fieldType = classFieldType;
        getMainTextBox().setGravity(Gravity.CENTER_HORIZONTAL);
        getMainTextBox().setTextColor(0xff2e74b5);
        getMainTextBox().setTypeface(null, Typeface.BOLD);

        setPadding(getPaddingLeft(),getPaddingTop(),getPaddingRight(),getPaddingBottom()+7*Globals.dp2px);
    }

    @Override
    public boolean onEnterKeyPressed(TextView textView) {
        try{((NotebookActivity)getContext()).getNotebook().setLayoutFrozen(true);}catch (Exception e){};

        CharSequence textTransferred = textView.getText().subSequence(textView.getSelectionStart(),textView.length());
        CharSequence textRemains = textView.getText().subSequence(0,textView.getSelectionStart()-1);

        SimpleIndentedField newfield = (SimpleIndentedField) FieldFactory.createNewField(getContext(), SimpleIndentedField.classFieldType,true, null);
        getNote().addView(newfield,getFieldIndex()+1);


        newfield.getMainTextBox().setText(textTransferred);
        this.getMainTextBox().setText(textRemains);

        newfield.getMainTextBox().requestFocus();
        ((XEditText)newfield.getMainTextBox()).setSelection(0);

        try{((NotebookActivity)getContext()).getNotebook().setLayoutFrozen(false);}catch (Exception e){};

        newfield.setIndent(this.getIndent());

        return true;
    }
    @Override
    public void onBackspaceKeyPressedAtStart(XEditText xEditText)
    {
        revertToSimpleIndentedField();
    }
}
