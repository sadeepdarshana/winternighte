package com.example.sadeep.winternightd.buttons;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Sadeep on 10/19/2016.
 */


/**
 * A button that can switch between modes and highlight when being touched, could be used in toolbars etc.
 *
 * The image source of the underlying ImageView should be set via XML or code outside this class.
 *
 * This abstract class should be extended providing the colors (via the 3 abstract methods)in order to use.
 *
 * Functionalities,
 *      1. background color is changed (setBackgroundColor()) and content bitmap's color is changed (by applying a filter) to suit the mode.
 *      2. button is [kept] highlighted when the finger is on the button (as long as the finger is touching the button)
 */

public abstract class RectangularButton extends ImageView {



    private int mode;

    private int [] backgroundColorForMode; //backgroundColorForMode[x] is the background color when mode is x.
    private int [] contentColorForMode;    //contentColorForMode[x] is the content color when mode is x. (color of the filter applied over the content)

    protected int backgroundColorTouchDown;  //touchdown(background will be this color as long as the finger is on button)

    public RectangularButton(Context context) {
        super(context);
        init();
    }
    public RectangularButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public RectangularButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    private void init() {

        contentColorForMode = getContentColorForMode();
        backgroundColorForMode = getBackgroundColorForMode();
        backgroundColorTouchDown = getBackgroundColorTouchDown();


        //the whole purpose of having this OnTouchListener is to highlight the button when user touches it. (not to be confused with mode changes, this is as long as the finger is on button)
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN) setBackgroundColor(backgroundColorTouchDown);//highlights the button when the finger touches the button

                if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    updateColorsForModeChanges(); //reverts to the default colors of the current mode.
                    RectangularButton.this.performClick(); //Because we return true for onTouch, android doesn't fire the Click event. So we do.
                }

                if(event.getAction() == MotionEvent.ACTION_CANCEL)updateColorsForModeChanges(); //remove the highlighting when user moves the finger away from the button.

                return true; //We consume the touch event here. So we need to manually fire the 'Click' event here.(done above)
            }
        });

        setMode(0);
    }

    private void updateColorsForModeChanges() {
        getDrawable().setColorFilter(new PorterDuffColorFilter(contentColorForMode[mode], PorterDuff.Mode.MULTIPLY));
        setBackgroundColor(backgroundColorForMode[mode]);
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
        updateColorsForModeChanges();
    }

    public void refresh(){
        init();
    }

    protected abstract int getBackgroundColorTouchDown();
    protected abstract int[] getBackgroundColorForMode();
    protected abstract int[] getContentColorForMode();

}
