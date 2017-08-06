package com.example.sadeep.winternightd.misc;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * Created by Sadeep on 7/23/2017.
 */

public class SexyCard extends CardView {
    public SexyCard(Context context) {
        super(context);
        init();
    }


    public SexyCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SexyCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        setCardElevation(1.3f* Globals.dp2px);
        setCardBackgroundColor(0xffffffff);
    }

    @Override
    protected void onSizeChanged(int w, final int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        post(new Runnable() {
            @Override
            public void run() {
                resetPaddings(h);
            }
        });

        setContentPadding(5,5,5,5);
    }

    private void resetPaddings(int h) {
        float pad = 0;

        if(h>Globals.dp2px*1600)pad=Globals.dp2px*0f;
        else if(h>Globals.dp2px*800)pad=Globals.dp2px*.5f;
        else if(h>Globals.dp2px*400)pad=Globals.dp2px*1.0f;
        else if(h>Globals.dp2px*80)pad=Globals.dp2px*2f;
        else pad=Globals.dp2px*2;

        //setContentPadding((int)(pad+0.01f),getContentPaddingTop(),(int)(pad+0.01f),getContentPaddingBottom());
    }
}
