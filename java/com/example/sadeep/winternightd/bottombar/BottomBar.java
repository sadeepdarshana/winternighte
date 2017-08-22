package com.example.sadeep.winternightd.bottombar;

import android.animation.Animator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.example.sadeep.winternightd.R;
import com.example.sadeep.winternightd.activities.NotebookActivity;
import com.example.sadeep.winternightd.animation.XAnimationListener;
import com.example.sadeep.winternightd.animation.XAnimation;
import com.example.sadeep.winternightd.note.Note;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by Sadeep on 7/10/2017.
 */

public class BottomBar extends LinearLayout{


    public ExtendedToolbar extendedToolbar;
    public NewNoteBar newNoteBar;


    private Note note;

    public static final int MODE_COLLAPSED = 0;

    public static final int MODE_EXPANDED = 1;

    public int storedHeight=-1;
    public static final int ANIMATION_DURATION =400;

    public boolean layoutShown=true;


    public BottomBar(Context context) {
        super(context);

        setOrientation(VERTICAL);

        LayoutInflater.from(context).inflate(R.layout.bottombar,this,true);

        extendedToolbar = new ExtendedToolbar(context,false,false, false){

            @Override
            protected void onSendClick(View v) {
                BottomBar.this.onSendClick(v);
            }

            @Override
            protected void onAttachClick(View v) {
                BottomBar.this.onAttachClick(v);
            }
        };
        newNoteBar = new NewNoteBar(context,true,false){
            @Override
            protected void onNoteFocused(){
                BottomBar.this.setGlassModeEnabled(false);
                BottomBar.this.setToolbarVisibility(true);
            }

            @Override
            protected void onNoteHeightMatured() {
                setNoteBoxMode(MODE_EXPANDED);
            }

            @Override
            protected void onNoteIsEmpty() {
                setNoteBoxMode(MODE_COLLAPSED);
            }

            @Override
            protected void onSendClick(View v) {
                BottomBar.this.onSendClick(v);
            }

            @Override
            protected void onSendLongClick(View view) {
                BottomBar.this.onSendLongClick(view);

            }

            @Override
            protected void onAttachClick(View v) {
                BottomBar.this.onAttachClick(v);
            }

            @Override
            protected void onNoteHasContent() {
                BottomBar.this.setToolbarVisibility(true);
                BottomBar.this.setGlassModeEnabled(false);
            }
        };

        addView(extendedToolbar,0);
        addView(newNoteBar,3);

        note = newNoteBar.getNote();
    }


    public Note getNote() {
        return note;
    }




    protected void onAttachClick(View v) {

    }

    protected void onSendClick(View v) {

    }
    protected void onSendLongClick(View v) {

    }



    public void setNoteBoxMode(int mode){
        switch (mode) {
            case MODE_EXPANDED:
                extendedToolbar.setButtonsVisibility(true, true);
                newNoteBar.setButtonsVisibility(false, true);
                break;

            case MODE_COLLAPSED:
                extendedToolbar.setButtonsVisibility(false,true);
                newNoteBar.setButtonsVisibility(true,true);
                break;
        }

    }

    public void setToolbarVisibility(boolean visible){
        extendedToolbar.setToolbarVisibility(visible,true);
    }
    public void setGlassModeEnabled(boolean visible){
        newNoteBar.setGlassModeEnabled(visible,true);
    }


    private Animator currentAnimation;



    public void hide(){
        if(currentAnimation!=null && currentAnimation.isRunning())currentAnimation.cancel();
        if(getLayoutParams().height==WRAP_CONTENT)storedHeight=getHeight();
        currentAnimation = XAnimation.changeDimension(this,ANIMATION_DURATION,XAnimation.DIMENSION_HEIGHT,storedHeight,0);
    }

    public void show(){
        if(currentAnimation!=null && currentAnimation.isRunning())currentAnimation.cancel();

        //((NotebookActivity)getContext()).newNoteBottomBar.setGlassModeEnabled(true);
        //((NotebookActivity)getContext()).refreshBottomBar();
        currentAnimation = XAnimation.changeDimension(this, ANIMATION_DURATION, XAnimation.DIMENSION_HEIGHT, 0, storedHeight, new XAnimationListener() {
            @Override
            public void onEnd() {
                getLayoutParams().height = WRAP_CONTENT;
                requestLayout();
                ((NotebookActivity)getContext()).refreshBottomBar();
            }

            @Override
            public void onStep() {
            }
        });

    }
}
