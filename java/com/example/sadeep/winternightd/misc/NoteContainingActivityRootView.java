package com.example.sadeep.winternightd.misc;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.sadeep.winternightd.activities.NoteContainingActivity;

/**
 * Created by Sadeep on 6/22/2017.
 */

public class NoteContainingActivityRootView extends RelativeLayout {

    private static boolean layoutEnabled = true;
    public static NoteContainingActivityRootView This = null;

    public View bottomLeftMarker;
    public NoteContainingActivityRootView(Context context, AttributeSet attrs) {
        super(context, attrs);
        This = this;

        addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if(bottom!=oldBottom)((NoteContainingActivity)getContext()).onRootLayoutSizeChanged();
            }
        });

        bottomLeftMarker = new View(context);
        bottomLeftMarker.setBackgroundColor(Color.TRANSPARENT);
        RelativeLayout.LayoutParams params= new RelativeLayout.LayoutParams(1,1);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        bottomLeftMarker.setLayoutParams(params);
        addView(bottomLeftMarker);


    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        This=this;
        if(layoutEnabled)super.onLayout(changed, l, t, r, b);
    }
    int x,y;
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(layoutEnabled){
            x=widthMeasureSpec;
            y=heightMeasureSpec;
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
        else super.onMeasure(x, y);
    }



    public static void resumeLayout() {
        layoutEnabled =true;
        if(This!=null)This.requestLayout();

    }

    public static void pauseLayout(){
        layoutEnabled = false;
    }
}
