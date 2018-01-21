package com.example.sadeep.winternightd.buttons.customizedbuttons;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.sadeep.winternightd.misc.XColors;

/**
 * Created by Sadeep on 6/19/2017.
 */

public class CancelCircularButton extends FloatingActionButton {

    public static int colorNormal  = Color.parseColor("#fff2f2f2");
    public static int colorPressed = Color.parseColor("#ffcccccc");

    public CancelCircularButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setColor(colorNormal);
        getDrawable().setColorFilter(new PorterDuffColorFilter(XColors.attachboxContent, PorterDuff.Mode.MULTIPLY));
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN) setColor(colorPressed);//highlights the button when the finger touches the button

                if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    setColor(colorNormal);
                    CancelCircularButton.this.performClick(); //Because we return true for onTouch, android doesn't fire the Click event. So we do.
                }

                if(event.getAction() == MotionEvent.ACTION_CANCEL)setColor(colorNormal); //remove the highlighting when user moves the finger away from the button.

                return true; //We consume the touch event here. So we need to manually fire the 'Click' event here.(done above)
            }
        });

    }

    private void setColor(int color) {
        setBackgroundTintList(new ColorStateList(new int[][]{new int[]{0}}, new int[]{color}));
    }
}
