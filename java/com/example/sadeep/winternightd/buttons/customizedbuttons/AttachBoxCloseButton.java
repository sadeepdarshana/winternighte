package com.example.sadeep.winternightd.buttons.customizedbuttons;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;

import com.example.sadeep.winternightd.attachbox.AttachBoxManager;
import com.example.sadeep.winternightd.buttons.RectangularButton;

/**
 * Created by Sadeep on 10/19/2016.
 */

/**
 * A customized button with the colors we define.
 * Instances of this class are used as buttons in the toolbar.
 */
public class AttachBoxCloseButton extends RectangularButton {
    public AttachBoxCloseButton(Context context) {
        super(context);
        init();
    }

    public AttachBoxCloseButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AttachBoxCloseButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                AttachBoxManager.tryDismiss();
            }
        });
    }

    @Override
    protected int getBackgroundColorTouchDown() {
        return 0x00000000;
    }

    @Override
    protected int[] getBackgroundColorForMode() {
        return new int[]{0x00000000 };
    }

    @Override
    protected int[] getContentColorForMode() {
        return  new int[]{0xffffffff};
    }


}
