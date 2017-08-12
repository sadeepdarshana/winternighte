package com.example.sadeep.winternightd.buttons.customizedbuttons;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;

import com.example.sadeep.winternightd.R;
import com.example.sadeep.winternightd.attachbox.AttachBoxManager;
import com.example.sadeep.winternightd.buttons.RectangularButton;

/**
 * Created by Sadeep on 10/19/2016.
 */

/**
 * A customized button with the colors we define.
 */
public class NoteHolderModeSettingsButton extends RectangularButton {
    public NoteHolderModeSettingsButton(Context context) {
        super(context);
        init();
    }

    public NoteHolderModeSettingsButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NoteHolderModeSettingsButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){

    }

    @Override
    protected int getBackgroundColorTouchDown() {
        return 0xffbbbbbb;
    }

    @Override
    protected int[] getBackgroundColorForMode() {
        return new int[]{0x00000000 };
    }

    @Override
    protected int[] getContentColorForMode() {
        return  new int[]{0xff999999};
    }


}
