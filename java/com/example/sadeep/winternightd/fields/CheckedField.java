package com.example.sadeep.winternightd.fields;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.example.sadeep.winternightd.R;
import com.example.sadeep.winternightd.misc.Globals;

/**
 * Created by Sadeep on 10/15/2016.
 */

/**
 * A SimpleIndentedField that has a checkbox at the start.
 * CheckedField is created by inserting a CheckBox as the 0th child of the SimpleIndentedField.
 */
public class CheckedField extends SimpleIndentedField {

    public static final int classFieldType = 4;


    private CheckBox checkedCheckView;

    public CheckedField(Context context) {
        this(false,context);
    }

    public CheckedField(boolean isEditable, Context context) {
        super(isEditable, context);
        init();
    }

    private void init(){

        fieldType = classFieldType;

        checkedCheckView = (CheckBox) LayoutInflater.from(getContext()).inflate(R.layout.checkbox,this,false);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT); lp.setMargins( /*-7*/0* Globals.dp2px,0 ,0,0);  checkedCheckView.setLayoutParams(lp);

        addView(checkedCheckView,0);

    }
}
