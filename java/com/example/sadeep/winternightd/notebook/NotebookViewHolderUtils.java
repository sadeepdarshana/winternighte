package com.example.sadeep.winternightd.notebook;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import com.example.sadeep.winternightd.R;
import com.example.sadeep.winternightd.activities.NotebookActivity;
import com.example.sadeep.winternightd.bottombar.BottomBar;
import com.example.sadeep.winternightd.misc.Globals;
import com.example.sadeep.winternightd.misc.Utils;
import com.example.sadeep.winternightd.note.Note;

import static com.example.sadeep.winternightd.notebook.NoteHolderModes.MODE_EDIT;
import static com.example.sadeep.winternightd.notebook.NoteHolderModes.MODE_SETTINGS;
import static com.example.sadeep.winternightd.notebook.NoteHolderModes.MODE_VIEW;

/**
 * Created by Sadeep on 6/17/2017.
 */

public final class NotebookViewHolderUtils {
    private NotebookViewHolderUtils(){}//static class

    public static final int VIEWTYPE_HEADER = 0;
    public static final int VIEWTYPE_NOTE_HOLDER = 1;
    public static final int VIEWTYPE_FOOTER = 2;

    static class NotebookViewHolder extends RecyclerView.ViewHolder{
        public ViewGroup holder;
        public NotebookViewHolder(ViewGroup holder) {
            super(holder);
            this.holder = holder;
        }
    }


    public static class NoteHolder extends NotebookItem{

        private Notebook notebook;

        int mode = -1;//-1 means not assigned a mode yet

        public boolean noteEditable;



        public NoteHolder(Context context,Notebook notebook) {
            super(context);
            this.notebook = notebook;

            Notebook.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(Globals.dp2px*5,Globals.dp2px*5,Globals.dp2px*5,Globals.dp2px*5);
            setLayoutParams(params);

            notebook.noteHolderController.addNoteHolder(this);

        }


        public void bind(Note note,int mode){
            getNoteSpace().removeAllViews();
            setMode(mode,false);
            note.setEditable(noteEditable);
            if(note.getParent()!=null)((ViewGroup)note.getParent()).removeView(note);
            getNoteSpace().addView(note);
            onBind();

        }

        private void onBind() {
            if(mode==MODE_VIEW) NoteHolderModes.ModeView.onBind(this);
            if(mode==MODE_EDIT) NoteHolderModes.ModeEdit.onBind(this);
            if(mode==MODE_SETTINGS) NoteHolderModes.ModeSettings.onBind(this);
        }

        public void setMode(int mode,final boolean animate){
            if(mode==MODE_VIEW) NoteHolderModes.ModeView.setAsNoteHolderMode(this,animate);
            if(mode==MODE_EDIT) NoteHolderModes.ModeEdit.setAsNoteHolderMode(this,animate);
            if(mode==MODE_SETTINGS) NoteHolderModes.ModeSettings.setAsNoteHolderMode(this,animate);
        }

        public int getMode() {
            return mode;
        }


        public Note getNote() {
            return getNoteSpace().getChildCount()!=0 ?   (Note) getNoteSpace().getChildAt(0) : null;
        }

        public Notebook getNotebook() {
            return notebook;
        }
    }

    static class Header extends LinearLayout {

        public Header(Context context) {
            super(context);
            setPadding(0, 0, 0, 0);
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, Globals.dp2px * 6);
            params.setMargins(0, 0, 0, 0);
            setLayoutParams(params);
        }
    }

    static class Footer extends LinearLayout{


        private static boolean keepScrolledToBottom = false;

        private static Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                keepScrolledToBottom = false;
            }
        };

        public static void keepScrolledToBottomFor1Sec(){
            handler.removeCallbacksAndMessages(null);
            keepScrolledToBottom=true;
            handler.sendEmptyMessageDelayed(0,1000);
        }


        public Footer(Context context, final Notebook notebook) {
            super(context);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
                    notebook.bottomBar.storedHeight!=-1?notebook.bottomBar.getHeight():Utils.getHeight(new BottomBar(context))+Globals.dp2px*20);
            setLayoutParams(params);

            notebook.bottomBar.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View xv, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    {
                        if(notebook.scrolledToBottom())keepScrolledToBottomFor1Sec();
                        Footer.this.getLayoutParams().height = bottom;
                        Footer.this.requestLayout();


                        if(keepScrolledToBottom)post(new Runnable() {
                            @Override
                            public void run() {
                                notebook.scrollToPosition(0);
                            }
                        });
                    }
                }
            });
        }
    }
}


