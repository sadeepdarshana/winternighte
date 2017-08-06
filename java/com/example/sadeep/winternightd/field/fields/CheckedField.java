package com.example.sadeep.winternightd.field.fields;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.example.sadeep.winternightd.R;
import com.example.sadeep.winternightd.textboxes.XEditText;
import com.example.sadeep.winternightd.misc.Globals;

/**
 * Created by Sadeep on 10/15/2016.
 */

/**
 * A SimpleIndentedField that has
 */
public class CheckedField extends SimpleIndentedField {

    public static final int classFieldType = 1855431776;


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
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.setMargins( -7* Globals.dp2px,0 , 3* Globals.dp2px,0);
        checkedCheckView.setLayoutParams(lp);

        checkedCheckView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)checkedCheckView.setButtonTintList(new ColorStateList(new int[][]{new int[]{0}}, new int[]{Color.parseColor("#FF26A69A")}));
                else checkedCheckView.setButtonTintList(new ColorStateList(new int[][]{new int[]{0}}, new int[]{Color.parseColor("#f59d22")}));

            }
        });

        addView(checkedCheckView,0);
    }

    /**
     * backspace has been pressed at the start of the Fields main textbox.
     *
     * Here we need to remove ('backspace') the CheckBox [of the CheckedField]. So we convert this into a SimpleIndentedField
     */
    @Override
    public void onBackspaceKeyPressedAtStart(XEditText xEditText)
    {
        revertToSimpleIndentedField();
    }
}
