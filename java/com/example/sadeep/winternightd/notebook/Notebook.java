package com.example.sadeep.winternightd.notebook;

import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.sadeep.winternightd.localstorage.NotebookCursorReader;
import com.example.sadeep.winternightd.activities.NotebookActivity;
import com.example.sadeep.winternightd.bottombar.BottomBar;
import com.example.sadeep.winternightd.localstorage.NotebookDataHandler;
import com.example.sadeep.winternightd.misc.NotebookItemChamber;
import com.example.sadeep.winternightd.misc.Utils;
import com.example.sadeep.winternightd.note.Note;
import com.example.sadeep.winternightd.notebook.NotebookViewHolderUtils.NoteHolder;
import com.example.sadeep.winternightd.selection.XSelection;

import java.util.ArrayList;

/**
 * Created by Sadeep on 10/26/2016.
 */
public class Notebook extends RecyclerView {
    public NotebookActivity notebookActivity;
    private String notebookGuid;

    private NotebookDataHandler dataHandler;
    public LinearLayoutManager layoutManager;

    public BottomBar bottomBar;

    public static boolean scrollEnabled = true;

    public Editor editor;

    public NoteHolderController noteHolderController = new NoteHolderController();

    static Notebook deletethis=null;

    private static Handler scrollresumer = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            deletethis.setLayoutFrozen(false);
            //scrollEnabled = true;
        }
    };;

    public Notebook(NotebookActivity notebookActivity, String notebookGuid, BottomBar bottomBar) {
        super(notebookActivity);
        deletethis=this;
        this.notebookActivity = notebookActivity;
        this.notebookGuid = notebookGuid;
        this.bottomBar = bottomBar;

        setOverScrollMode(OVER_SCROLL_NEVER);

        editor = new Editor(this);


        layoutManager = new LinearLayoutManager(notebookActivity) {
            @Override
            public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
                if (Notebook.scrollEnabled)return super.scrollVerticallyBy(dy,recycler,state);
                return 0;
            }
        };

        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        setLayoutManager(layoutManager);


        refresh();

        postDelayed(new Runnable() {
            @Override
            public void run() {
                layoutManager.scrollToPositionWithOffset(0,500);
            }
        },2);

    }

    @Override
    public void onChildAttachedToWindow(View child) {
        super.onChildAttachedToWindow(child);//if(true)return;
        if(child instanceof NoteHolder){
            NoteHolder noteHolder = (NoteHolder)child;

            if(noteHolder.getNote().noteInfo.noteUUID.equals(editor.getActiveNoteUUID()))
                noteHolder.setMode(NoteHolderModes.MODE_EDIT,false);
            else
                noteHolder.setMode(NoteHolderModes.MODE_VIEW,false);

                try{
                    ((NotebookItemChamber.ChamberContentView)noteHolder.getLowerChamber().getChildAt(0)).onAttached();
                }catch (Exception e){}
                try{
                    ((NotebookItemChamber.ChamberContentView)noteHolder.getUpperChamber().getChildAt(0)).onAttached();
                }catch (Exception e){}
        }
    }

    public NotebookDataHandler getNotebookDataHandler() {
        return dataHandler;
    }

    public void refresh() {
        dataHandler = new NotebookDataHandler(notebookGuid);
        setAdapter(new NotebookAdapter(notebookActivity,new NotebookCursorReader(dataHandler.getCursor()),this));

        postDelayed(new Runnable() {
            @Override
            public void run() { 
                smoothScrollToPosition(0);
            }
        },50);

        Utils.hideKeyboard(getContext());
    }

    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);
        notebookActivity.onNotebookScrolled(dy);
    }


    public static void suspendScrollTemporary() {
        deletethis.setLayoutFrozen(true);
        //scrollEnabled = false;

        scrollresumer.removeCallbacksAndMessages(null);
        scrollresumer.sendEmptyMessageDelayed(0,400);
    }

    public class NoteHolderController{
        private ArrayList<NoteHolder>noteHolders = new ArrayList<>();

        public void addNoteHolder(NoteHolder noteHolder){
            noteHolders.add(noteHolder);
        }

        public void setAllNoteHoldersModeExcept(int mode,NoteHolder except,boolean animate){
            for(int i=0;i<noteHolders.size();i++){
                NoteHolder noteHolder = noteHolders.get(i);
                if(noteHolder == except)continue;
                if(noteHolder.getMode()!=mode)noteHolder.setMode(mode,animate);
            }
        }
    }

    public class Editor{
        private Notebook notebook;
        private Note activeNote;


        public Editor(Notebook notebook) {
            this.notebook = notebook;
        }

        public String getActiveNoteUUID() {
            if(activeNote !=null)return activeNote.noteInfo.noteUUID;
            return null;
        }


        public void pushActiveNote() {
            notebookActivity.activeNote=notebookActivity.newNote;
            if(activeNote==null)return;
            notebook.getNotebookDataHandler().addExistingNote(activeNote);
            activeNote = null;
            notebookActivity.refreshBottomBar();
            refresh();
            notebook.scrollToPosition(0);
            notebookActivity.onNotebookScrolled(-1);
            notebookActivity.newNoteBottomBar.setGlassModeEnabled(true);
            notebookActivity.newNoteBottomBar.setToolbarVisibility(false);
        }

        public void cancelActiveNote(){
            notebookActivity.activeNote=notebookActivity.newNote;
            if(XSelection.isSelectionAvailable())XSelection.clearSelections();
            try {
                noteHolderController.setAllNoteHoldersModeExcept(NoteHolderModes.DEFAULT_MODE, null, true);

                NotebookCursorReader reader = new NotebookCursorReader(dataHandler.getCursorForNote(activeNote.noteInfo.noteUUID));
                activeNote.revertTo(reader.getFieldDataStream(0));

                activeNote = null;
            }catch (Exception e){}
            Utils.hideKeyboard(getContext());

            notebookActivity.refreshBottomBar();
        }

        public Note getActiveNote() {
            return activeNote;
        }

        public void setActiveNote(Note activeNote) {
            this.activeNote = activeNote;
            notebookActivity.activeNote=activeNote;
        }
    }
}
