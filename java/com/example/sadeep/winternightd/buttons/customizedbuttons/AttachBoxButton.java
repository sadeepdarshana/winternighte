package com.example.sadeep.winternightd.buttons.customizedbuttons;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import com.example.sadeep.winternightd.buttons.RectangularButton;

/**
 * Created by Sadeep on 7/1/2017.
 */

public class AttachBoxButton extends RectangularButton {

    private int attachButtonId;
    private int color;

    public AttachBoxButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AttachBoxButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        String tag = (String) getTag();
        attachButtonId = Integer.parseInt(tag.split("#")[0]);
        color = Color.parseColor("#"+tag.split("#")[1]);
        refresh();
    }
    @Override
    protected int getBackgroundColorTouchDown() {
        return Color.argb(150,130,130,130);
    }

    @Override
    protected int[] getBackgroundColorForMode() {
        return new int[]{Color.TRANSPARENT};
    }

    @Override
    protected int[] getContentColorForMode() {
        return new int[]{color};
    }

    public int getAttachButtonId() {
        return attachButtonId;
    }
}
