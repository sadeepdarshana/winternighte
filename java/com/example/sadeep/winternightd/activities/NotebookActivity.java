package com.example.sadeep.winternightd.activities;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.example.sadeep.winternightd.R;
import com.example.sadeep.winternightd.animation.XAnimation;
import com.example.sadeep.winternightd.animation.XAnimationListener;
import com.example.sadeep.winternightd.attachbox.AttachBoxManager;
import com.example.sadeep.winternightd.attachbox.OnAttachBoxItemClick;
import com.example.sadeep.winternightd.buttons.customizedbuttons.AttachBoxOpener;
import com.example.sadeep.winternightd.clipboard.XClipboard;
import com.example.sadeep.winternightd.dumping.RawFieldDataStream;
import com.example.sadeep.winternightd.field.fields.SimpleIndentedField;
import com.example.sadeep.winternightd.localstorage.DataConnection;
import com.example.sadeep.winternightd.misc.Utils;
import com.example.sadeep.winternightd.note.Note;
import com.example.sadeep.winternightd.misc.Globals;
import com.example.sadeep.winternightd.notebook.Notebook;
import com.example.sadeep.winternightd.bottombar.BottomBar;
import com.example.sadeep.winternightd.selection.XSelection;
import com.example.sadeep.winternightd.misc.NoteContainingActivityRootView;
import com.example.sadeep.winternightd.temp.d;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;


public class NotebookActivity extends NoteContainingActivity {

    public static long classContextSessionId;   //used for the GC of the activity
    public long contextSessionId;               //   ''

    public Notebook notebook;
    public BottomBar newNoteBottomBar;
    public Note newNote;
    public Note activeNote;

    private String notebookUUID="";
    private String title="";

    LinearLayout bottombarSpace;
    private LinearLayout notebookSpace;

    public NoteContainingActivityRootView rootView;

    public NotebookActivity(){
        contextSessionId=new java.util.Random().nextLong();
        classContextSessionId=contextSessionId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getIntent().getExtras();
        if(b != null) {
            notebookUUID = b.getString("notebookUUID");
            title = b.getString("title");
        }
        else {
            notebookUUID = "b237e56fe938a4e80b75b1be38f58b06e";
            title = "Home";
        }

        setTheme(R.style.notebook_activity_theme);
        setContentView(R.layout.notebook_activity);

        bottombarSpace = (LinearLayout) findViewById(R.id.bottombar_space);
        notebookSpace = (LinearLayout) findViewById(R.id.notebook_space);
        rootView = (NoteContainingActivityRootView)findViewById(R.id.rootView);

        Globals.initialize(this);
        DataConnection.initialize(this);
        XClipboard.initialize(this);


        newNoteBottomBar = new BottomBar(this){
            @Override
            protected void onAttachClick(View v) {
                if(!((AttachBoxOpener)v).isAttachboxOpen()) {
                    ((AttachBoxOpener) v).setAttachboxOpened(true);
                    AttachBoxManager.display(v,((NotebookActivity)v.getContext()).rootView.bottomLeftMarker, new OnAttachBoxItemClick() {
                        @Override
                        public void buttonClicked(int attachButtonId) {
                            newNote.attachboxRequests(attachButtonId);
                        }
                    });
                }else{
                    try {
                        AttachBoxManager.popupWindow.dismiss();
                    }catch (Exception e){}
                }
            }

            @Override
            protected void onSendClick(View v) {
                sendClick(v);
            }
        };

        bottombarSpace.addView(newNoteBottomBar);

        notebook = new Notebook(this,notebookUUID, newNoteBottomBar);
        notebook.setClipToPadding(false);
        notebookSpace.addView(notebook);


        newNote = newNoteBottomBar.getNote();
        activeNote = newNote;

        //getWindow().setBackgroundDrawableResource(R.drawable.yyy);
        setActionBarMode(NoteContainingActivity.ACTIONBAR_NORMAL);

        final boolean[] notebookScrolledToBottom = {false};//'int[1]' because java syntax doesn't allow 'int' here
        rootView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                notebookScrolledToBottom[0] = notebook.scrolledToBottom();
            }
        });
        rootView.keyboardListener = new NoteContainingActivityRootView.KeyboardListener() {
            @Override
            public void beforeKeyboardOpen() {
                if(notebookScrolledToBottom[0] && notebook.editor.getActiveNote()==null){
                    newNoteBottomBar.extendedToolbar.toolbarAnimationListener = new XAnimationListener() {
                        @Override
                        public void onEnd() {
                            newNoteBottomBar.extendedToolbar.toolbarAnimationListener=null;
                        }

                        @Override
                        public void onStep() {
                            notebook.scrollToPosition(0);
                        }
                    };
                    notebook.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            notebook.smoothScrollToPosition(0);
                        }
                    },400);
                }
            }
        };

        new CountDownTimer(1000000000000000L,500){

            @Override
            public void onTick(long millisUntilFinished) {
                if(millisUntilFinished>=1000000000000000L-500*3)return;
                disableBottomBarGlassModeIfNecessary();
                //enableBottomBarToGlassModeIfNecessary();
            }

            @Override
            public void onFinish() {

            }
        }.start();
    }

    public void refreshBottomBar(){
        if(notebook.editor.getActiveNote()==null){
            if(!newNoteBottomBar.layoutShown) {
                newNoteBottomBar.show();
                newNoteBottomBar.layoutShown=true;
                disableBottomBarGlassModeIfNecessary();
                enableBottomBarToGlassModeIfNecessary();
            }
        }else {
            if(newNoteBottomBar.layoutShown) {
                newNoteBottomBar.layoutShown=false;

                newNoteBottomBar.hide();
            }
        }
    }

    @Override
    public void onMenuItemPressed(int menuItem) {
        switch (menuItem){
            case R.id.action_cut:
                XClipboard.requestCut();
                break;
            case R.id.action_copy:
                XClipboard.requestCopy();
                break;
            case R.id.action_paste:
                XClipboard.requestPaste(this, null);
                break;
            case android.R.id.home:
                XSelection.clearSelections();
        }
    }

    @Override
    protected Drawable getActionBarDrawable() {
        return new ColorDrawable(0xff449944);
    }
    @Override
    protected String getActionBarTitle() {
        return title;
    }

    @Override
    public NoteContainingActivityRootView getRootView() {
        return rootView;
    }

    @Override
    public void onRootLayoutSizeChanged() {
        AttachBoxManager.tryDismiss();
    }



    public void sendClick(View view){
        if(newNote.isEmpty())return;
        XSelection.clearSelections();
        NoteContainingActivityRootView.pauseLayout();
        RawFieldDataStream streams=new RawFieldDataStream(newNote.getFieldDataStream());
        //for(int i=0;i<100000;i++) {
            notebook.getNotebookDataHandler().addNewNote(newNote);
            //if(i%100==0)d.p(i);
        //}
        notebook.refresh();
        notebook.scrollToPosition(0);
        newNote.convertToNewNoteWithOneDefaultField();
        ((SimpleIndentedField) newNote.getFieldAt(0)).getMainTextBox().requestFocus();

        newNoteBottomBar.setToolbarVisibility(false);

        rootView.postDelayed(new Runnable() {
            @Override
            public void run() {
                rootView.resumeLayout();
            }
        },300);
    }

    public Note getActiveNote() {
        return activeNote;
    }

    public void onNotebookScrolled(int dy) {
        if(dy<0)enableBottomBarToGlassModeIfNecessary();
        if(dy>0)disableBottomBarGlassModeIfNecessary();
    }

    public void disableBottomBarGlassModeIfNecessary(){
        if (  notebook.editor.getActiveNote() == null
            &&notebook.scrolledToBottom())
        {
            newNoteBottomBar.setGlassModeEnabled(false);
        }
    }

    public void enableBottomBarToGlassModeIfNecessary(){
        if (   notebook.editor.getActiveNote() == null
            && newNote.isEmpty()
            && !notebook.scrolledToBottom())
        {

            newNoteBottomBar.setGlassModeEnabled(true);
            newNoteBottomBar.setToolbarVisibility(false);

        }
    }

    public Notebook getNotebook() {
        return notebook;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(AttachBoxManager.displayed){
            AttachBoxManager.tryDismiss();
            return true;
        }else return super.onKeyUp(keyCode, event);
    }
}
