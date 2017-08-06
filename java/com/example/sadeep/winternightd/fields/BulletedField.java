package com.example.sadeep.winternightd.fields;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.sadeep.winternightd.misc.Globals;

/**
 * Created by Sadeep on 10/15/2016.
 */


/**
 * A SimpleIndentedField that has a bullet at the start.
 * BulletedField is created by inserting a TextView as the 0th child of the SimpleIndentedField to display the bullet.
 */
public class BulletedField extends SimpleIndentedField {


    public static final int classFieldType = 3;

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
        bulletTextView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        bulletTextView.setTextSize(TypedValue.COMPLEX_UNIT_FRACTION,  Globals.defaultFontSize);
        bulletTextView.setText("◯   ");

        addView(bulletTextView,0);

    }
}
