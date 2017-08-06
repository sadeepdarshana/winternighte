package com.example.sadeep.winternightd.activities;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.LinearLayout;

import com.example.sadeep.winternightd.R;
import com.example.sadeep.winternightd.animation.XAnimation;
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
        rootView = (NoteContainingActivityRootView)notebookSpace.getParent();

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

        Utils.setKeyboardVisibilityListener(this,new Utils.KeyboardVisibilityListener() {
            @Override
            public void onKeyboardVisibilityChanged(boolean keyboardVisible,final int size) {
                if(!keyboardVisible)return;

                int index = notebook.layoutManager.findFirstVisibleItemPosition();
                if(index<=3 && notebook.editor.getActiveNote()==null) {
                    notebook.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            notebook.scrollToPosition(0);
                        }
                    }, 400);
                    notebook.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            notebook.scrollToPosition(0);
                        }
                    }, 250);
                    notebook.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            notebook.scrollToPosition(0);
                        }
                    }, 100);
                }
            }
        });
    }

    public void refreshBottomBar(){
        if(notebook.editor.getActiveNote()==null){
            if(!newNoteBottomBar.layoutShown) {
                newNoteBottomBar.show();
                if(((LinearLayoutManager)notebook.getLayoutManager()).findFirstCompletelyVisibleItemPosition()<=1)
                    XAnimation.vScroll(notebook,300,newNoteBottomBar.storedHeight);
                newNoteBottomBar.layoutShown=true;
                disableBottomBarGlassModeIfNecessary();
                enableBottomBarToGlassModeIfNecessary();
            }
        }else {
            if(newNoteBottomBar.layoutShown) {
                newNoteBottomBar.layoutShown=false;
                newNoteBottomBar.storedHeight=newNoteBottomBar.getHeight();
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

        NoteContainingActivityRootView.This.postDelayed(new Runnable() {
            @Override
            public void run() {
                NoteContainingActivityRootView.resumeLayout();
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
            &&((LinearLayoutManager) notebook.getLayoutManager()).findFirstCompletelyVisibleItemPosition() == 0)
        {
            newNoteBottomBar.setGlassModeEnabled(false);
        }
    }

    public void enableBottomBarToGlassModeIfNecessary(){
        if (   notebook.editor.getActiveNote() == null
            && newNote.isEmpty()
            && ((LinearLayoutManager) notebook.getLayoutManager()).findFirstCompletelyVisibleItemPosition() != 0)
        {

            newNoteBottomBar.setGlassModeEnabled(true);
            newNoteBottomBar.setToolbarVisibility(false);

        }
    }

    public Notebook getNotebook() {
        return notebook;
    }
}
