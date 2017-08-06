package com.example.sadeep.winternightd.bottombar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.sadeep.winternightd.R;
import com.example.sadeep.winternightd.animation.XAnimationListener;
import com.example.sadeep.winternightd.animation.XAnimation;
import com.example.sadeep.winternightd.misc.Utils;
import com.example.sadeep.winternightd.toolbar.Toolbar;
import com.example.sadeep.winternightd.toolbar.ToolbarController;

/**
 * Created by Sadeep on 7/10/2017.
 */

public class ExtendedToolbar extends LinearLayout{


    private View attach,send,toolbar,cancel;
    private ViewGroup toolbarContainer;

    private int attachWidth,sendWidth,toolbarHeight; //widths and heights of the Views WHEN THEY ARE SHOWN

    private boolean buttonVisibility = true;
    private boolean toolbarVisibility = true;

    public XAnimationListener toolbarAnimationListener = null;


    public ExtendedToolbar(Context context, boolean buttonVisibility, boolean toolbarVisibility, boolean cancelButton) {
        super(context);

        //setBackgroundColor(0xaaebebeb);
        setClipChildren(false);

        LayoutInflater.from(context).inflate(R.layout.extended_toolbar,this,true);

        attach = findViewById(R.id.attach);
        send = findViewById(R.id.send);
        cancel = findViewById(R.id.cancel);
        toolbarContainer = (ViewGroup) findViewById(R.id.toolbarcontainer);

        if(cancelButton)cancel.setVisibility(VISIBLE);

        Toolbar toolbar = new Toolbar(context);
        this.toolbar = toolbar;
        ToolbarController.registerToolbar(toolbar);
        toolbarContainer.addView(this.toolbar);

        attachWidth = Utils.getWidth(attach);
        sendWidth = Utils.getWidth(send);
        toolbarHeight = Utils.getHeight(this.toolbar);

        setButtonsVisibility(buttonVisibility,false);
        setToolbarVisibility(toolbarVisibility,false);

        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAttachClick(v);
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSendClick(v);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancelClick(v);
            }
        });

    }

    protected void onCancelClick(View v) {
    }

    protected void onAttachClick(View v) {

    }

    protected void onSendClick(View v) {

    }




    public boolean getButtonVisibility() {
        return buttonVisibility;
    }

    public void setButtonsVisibility(boolean visible, boolean animate){
        final int ANIMATION_DURATION = 300;

        if(buttonVisibility == visible)return;
        this.buttonVisibility=visible;

        if(animate) {
            if(visible) {
                XAnimation.changeDimension(attach, ANIMATION_DURATION, XAnimation.DIMENSION_WIDTH, 0, attachWidth);
                XAnimation.changeDimension(send, ANIMATION_DURATION, XAnimation.DIMENSION_WIDTH, 0, sendWidth);
            }else {
                XAnimation.changeDimension(attach, ANIMATION_DURATION, XAnimation.DIMENSION_WIDTH, attachWidth, 0);
                XAnimation.changeDimension(send, ANIMATION_DURATION, XAnimation.DIMENSION_WIDTH, sendWidth, 0);
            }
        }else{
            if(visible){
                attach.getLayoutParams().width=attachWidth;
                send.getLayoutParams().width=sendWidth;
            }else{
                attach.getLayoutParams().width=0;
                send.getLayoutParams().width=0;
            }
            attach.requestLayout();
            send.requestLayout();
        }
    }



    public boolean getToolbarVisibility() {
        return toolbarVisibility;
    }

    public void setToolbarVisibility(boolean visible, boolean animate){
        final int ANIMATION_DURATION = 400;

        if(toolbarVisibility == visible)return;
        this.toolbarVisibility=visible;

        if(animate) {
            if(visible) {
                XAnimation.changeDimension(toolbar, ANIMATION_DURATION, XAnimation.DIMENSION_HEIGHT, 0, toolbarHeight, new XAnimationListener() {
                    @Override
                    public void onEnd() {
                        if(toolbarAnimationListener!=null)toolbarAnimationListener.onEnd();
                    }

                    @Override
                    public void onStep(){
                        if(toolbarAnimationListener!=null)toolbarAnimationListener.onStep();
                    }
                });
            }else {
                XAnimation.changeDimension(toolbar, ANIMATION_DURATION, XAnimation.DIMENSION_HEIGHT, toolbarHeight, 0);
            }
        }else{
            if(visible){
                toolbar.getLayoutParams().height=toolbarHeight;
            }else{
                toolbar.getLayoutParams().height=0;
            }
            toolbar.requestLayout();
        }
    }


}
