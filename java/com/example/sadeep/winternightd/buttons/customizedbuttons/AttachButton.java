package com.example.sadeep.winternightd.buttons.customizedbuttons;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import com.example.sadeep.winternightd.buttons.RectangularButton;
import com.example.sadeep.winternightd.misc.XColors;

/**
 * Created by Sadeep on 6/18/2017.
 */

public class AttachButton extends RectangularButton implements AttachBoxOpener {

    private boolean attachboxOpened=false;

    public AttachButton(Context context) {
        super(context);
    }

    public AttachButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getBackgroundColorTouchDown() {
        return Color.parseColor("#EEEEEE");
    }

    @Override
    protected int[] getBackgroundColorForMode() {
        return new int[]{ Color.parseColor("#00000000")        , Color.parseColor("#EEEEEE")};
    }

    @Override
    protected int[] getContentColorForMode() {
        return  new int[]{XColors.attachboxContent, Color.parseColor("#666666")   };
    }

    @Override
    public boolean isAttachboxOpen() {
        return attachboxOpened;
    }

    @Override
    public void setAttachboxOpened(boolean open) {
        this.attachboxOpened = open;
        if(open)setMode(1);
        else setMode(0);
    }
}
