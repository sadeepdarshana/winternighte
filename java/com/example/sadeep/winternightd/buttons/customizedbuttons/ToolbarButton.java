package com.example.sadeep.winternightd.buttons.customizedbuttons;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import com.example.sadeep.winternightd.buttons.RectangularButton;

/**
 * Created by Sadeep on 10/19/2016.
 */

/**
 * A customized button with the colors we define.
 * Instances of this class are used as buttons in the toolbar.
 *
 * Modes
 *   0-button inactive(released)
 *   1-button active(pressed)
 */
public class ToolbarButton extends RectangularButton {
    public ToolbarButton(Context context) {
        super(context);
    }

    public ToolbarButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ToolbarButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getBackgroundColorTouchDown() {
        return Color.parseColor("#f5f5f5");
    }

    @Override
    protected int[] getBackgroundColorForMode() {
        return new int[]{Color.TRANSPARENT          , Color.argb(35, 0, 0, 0)};
    }

    @Override
    protected int[] getContentColorForMode() {
        return  new int[]{Color.parseColor("#00AC00"), Color.parseColor("#0Eb42A")   };
    }
}
