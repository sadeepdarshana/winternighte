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
 * Created by Sadeep on 10/15/2016.
 */


/**
 * A SimpleIndentedField that has a bullet at the start.
 * BulletedField is created by inserting a TextView as the 0th child of the SimpleIndentedField to display the bullet.
 */
public class BulletedField extends SimpleIndentedField {


    public static final int classFieldType = 807450317;

    private TextView bulletTextView;

    public BulletedField(Context context) {
        this(false,context);
    }
    public BulletedField(boolean isEditable, Context context) {
        super(isEditable, context);
        init();
    }

    private void init(){

        fieldType = classFieldType;

        bulletTextView = new TextView(getContext());
        bulletTextView.setPadding(0,0,0,0);
        bulletTextView.setBackgroundColor(Color.TRANSPARENT);
        bulletTextView.setTextSize(TypedValue.COMPLEX_UNIT_FRACTION,  Globals.defaultFontSize);
        bulletTextView.setText("◯   ");


        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.setMargins( 2* Globals.dp2px,0 , -2* Globals.dp2px,0);
        bulletTextView.setLayoutParams(lp);

        addView(bulletTextView,0);

    }


    /**
     * backspace has been pressed at the start of the Fields main textbox.
     *
     * Here we need to remove ('backspace') the bullet [of the BulletedField]. So we convert this into a SimpleIndentedField
     */
    @Override
    public void onBackspaceKeyPressedAtStart(XEditText xEditText)
    {
        revertToSimpleIndentedField();
    }
}
