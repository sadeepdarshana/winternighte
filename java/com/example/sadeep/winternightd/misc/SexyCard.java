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
        setContentPadding(0,0,5*Globals.dp2px,0);
    }
}
