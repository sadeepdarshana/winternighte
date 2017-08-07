package com.example.sadeep.winternightd.misc;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.example.sadeep.winternightd.animation.XAnimation;

/**
 * Created by Sadeep on 7/24/2017.
 */

public class NotebookItemChamber extends LinearLayout {
    public NotebookItemChamber(Context context) {
        super(context);
    }

    public NotebookItemChamber(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NotebookItemChamber(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public NotebookItemChamber(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setChamberContent(View content, boolean animate){
        emptyChamber(animate);
        if(content==null)return;

        if(animate)XAnimation.addAndExpand(content,this,0,300,XAnimation.DIMENSION_HEIGHT,0);
        else addView(content,0);
    }
    public void emptyChamber(boolean animate){
        for(int i=0;i<getChildCount();i++)
            if(getChildAt(i)instanceof ChamberContentView)
                ((ChamberContentView)getChildAt(i)).onRemoved();

        if(animate)for(int i=0;i<getChildCount();i++)
            XAnimation.squeezeAndRemove(getChildAt(i),300,XAnimation.DIMENSION_HEIGHT,0);
        else
            removeAllViews();
    }

    public View getChamberContent(){
        if(getChildCount()!=0)return getChildAt(0);
        return null;
    }

    public interface ChamberContentView{
        void onRemoved();

        void onAttached();
    }
}
