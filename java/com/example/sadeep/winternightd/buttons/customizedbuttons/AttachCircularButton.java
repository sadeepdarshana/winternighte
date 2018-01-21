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

/**
 * Created by Sadeep on 6/19/2017.
 */

public class AttachCircularButton extends FloatingActionButton implements AttachBoxOpener {

    public static int colorNormal  = Color.parseColor("#fff5f5f5");
    public static int colorPressed = Color.parseColor("#ffcccccc");

    private boolean attachboxOpened=false;

    public AttachCircularButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        //getDrawable().setColorFilter(new PorterDuffColorFilter(Color.parseColor("#50B450"), PorterDuff.Mode.MULTIPLY));
        setColor(colorNormal);
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN) setColor(colorPressed);//highlights the button when the finger touches the button

                if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    setColor(colorNormal);
                    AttachCircularButton.this.performClick(); //Because we return true for onTouch, android doesn't fire the Click event. So we do.
                }

                if(event.getAction() == MotionEvent.ACTION_CANCEL)setColor(colorNormal); //remove the highlighting when user moves the finger away from the button.

                return true; //We consume the touch event here. So we need to manually fire the 'Click' event here.(done above)
            }
        });

    }

    public void setColor(int color) {
        setBackgroundTintList(new ColorStateList(new int[][]{new int[]{0}}, new int[]{color}));
    }

    @Override
    public boolean isAttachboxOpen() {
        return attachboxOpened;
    }

    @Override
    public void setAttachboxOpened(boolean open) {
        this.attachboxOpened = open;

        if(open)setColor(colorPressed);
        else setColor(colorNormal);
    }
}
