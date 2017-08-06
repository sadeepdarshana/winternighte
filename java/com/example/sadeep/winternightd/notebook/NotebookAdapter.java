package com.example.sadeep.winternightd.notebook;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.example.sadeep.winternightd.localstorage.NotebookCursorReader;
import com.example.sadeep.winternightd.note.Note;
import com.example.sadeep.winternightd.note.NoteFactory;
import com.example.sadeep.winternightd.note.NoteInfo;
import com.example.sadeep.winternightd.selection.XSelection;

import java.util.concurrent.ConcurrentHashMap;

import static com.example.sadeep.winternightd.notebook.NoteHolderModes.MODE_EDIT;
import static com.example.sadeep.winternightd.notebook.NoteHolderModes.MODE_VIEW;
import static com.example.sadeep.winternightd.notebook.NotebookViewHolderUtils.VIEWTYPE_FOOTER;
import static com.example.sadeep.winternightd.notebook.NotebookViewHolderUtils.VIEWTYPE_HEADER;
import static com.example.sadeep.winternightd.notebook.NotebookViewHolderUtils.VIEWTYPE_NOTE_HOLDER;

/**
 * Created by Sadeep on 6/17/2017.
 */

class NotebookAdapter extends RecyclerView.Adapter <NotebookViewHolderUtils.NotebookViewHolder> {
    private NotebookCursorReader cursor;

    private Context context;
    private Notebook notebook;

    private ConcurrentHashMap<String,Note> cache = new ConcurrentHashMap<>();

    public NotebookAdapter(final Context context, final NotebookCursorReader cursor, final Notebook notebook) {
        this.cursor = cursor;
        this.context = context;
        this.notebook = notebook;

        new Thread(new Runnable() {
            final int maxNumberOfNotesToCaches = 200;
            public void run(){

                int noteCount = cursor.getCursor().getCount();

                for(int i=0;i<Math.min(noteCount,maxNumberOfNotesToCaches);i++){
                    String noteUUID = cursor.getNoteInfo(i).noteUUID;
                    if(!cache.containsKey(noteUUID)){
                        Note note = NoteFactory.fromFieldDataStream(context, cursor.getFieldDataStream(i), false, notebook, cursor.getNoteInfo(i));
                        cache.putIfAbsent(noteUUID,note);
                    }
                }

            }
        }).start();
    }

    @Override
    public NotebookViewHolderUtils.NotebookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == VIEWTYPE_HEADER)return new NotebookViewHolderUtils.NotebookViewHolder(new NotebookViewHolderUtils.Header(context));
        if(viewType == VIEWTYPE_NOTE_HOLDER)return  new NotebookViewHolderUtils.NotebookViewHolder(new NotebookViewHolderUtils.NoteHolder(context,notebook));
        if(viewType == VIEWTYPE_FOOTER)return  new NotebookViewHolderUtils.NotebookViewHolder(new NotebookViewHolderUtils.Footer(context,notebook));

        return null;
    }

    @Override
    public void onBindViewHolder(NotebookViewHolderUtils.NotebookViewHolder holder, int position) {

        if(getItemViewType(position)==VIEWTYPE_NOTE_HOLDER) {

            Note note;
            NotebookViewHolderUtils.NoteHolder noteHolder = (NotebookViewHolderUtils.NoteHolder) holder.holder;
            int positionInCursor = position - 1;

            NoteInfo info = cursor.getNoteInfo(positionInCursor);

            if (info.noteUUID.equals(notebook.editor.getActiveNoteUUID()))//if (currently editing note)
            {
                noteHolder.setMode(MODE_EDIT, false);
                noteHolder.bind(notebook.editor.getActiveNote(), MODE_EDIT);

            } else {//not the currently editing note
                if (cache.containsKey(info.noteUUID)) {//already available in the cache
                    note = cache.get(info.noteUUID);
                }
                else{//instantiate and put it into cache in case we don't have to instantiate again in the future
                    note = NoteFactory.fromFieldDataStream(context, cursor.getFieldDataStream(positionInCursor), false, notebook, info);
                    cache.putIfAbsent(info.noteUUID,note);
                }
                noteHolder.bind(note, MODE_VIEW);
                noteHolder.setMode(MODE_VIEW, false);
            }
        }
    }

    @Override
    public void onViewDetachedFromWindow(NotebookViewHolderUtils.NotebookViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if(holder.holder instanceof NotebookViewHolderUtils.NoteHolder &&
                ((NotebookViewHolderUtils.NoteHolder)holder.holder).getNote()== XSelection.getSelectedNote())
        {
            XSelection.clearSelections();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position==0)return VIEWTYPE_FOOTER;
        if(position==getItemCount()-1)return  VIEWTYPE_HEADER;
        return VIEWTYPE_NOTE_HOLDER;
    }

    @Override
    public int getItemCount() {
        return cursor.getCursor().getCount()+2;
    }
}
