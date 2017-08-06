package com.example.sadeep.winternightd.catalog;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sadeep.winternightd.dumping.FieldDataStream;
import com.example.sadeep.winternightd.localstorage.CatalogCursorReader;
import com.example.sadeep.winternightd.localstorage.NotebookCursorReader;
import com.example.sadeep.winternightd.misc.Globals;
import com.example.sadeep.winternightd.note.Note;
import com.example.sadeep.winternightd.note.NoteFactory;
import com.example.sadeep.winternightd.notebook.Notebook;
import com.example.sadeep.winternightd.notebook.NotebookInfo;
import com.example.sadeep.winternightd.selection.XSelection;

import java.util.ArrayList;

import static com.example.sadeep.winternightd.catalog.CatalogViewHolderUtils.VIEWTYPE_NOTEBOOK_ROOT;


/**
 * Created by Sadeep on 6/17/2017.
 */

class CatalogAdapter extends RecyclerView.Adapter <CatalogViewHolderUtils.CatalogViewHolder> {

    private CatalogCursorReader cursor;
    private Context context;


    public CatalogAdapter(Context context, CatalogCursorReader cursor) {
        this.cursor = cursor;
        this.context = context;
    }


    @Override
    public CatalogViewHolderUtils.CatalogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == VIEWTYPE_NOTEBOOK_ROOT)
            return new CatalogViewHolderUtils.CatalogViewHolder(new CatalogViewHolderUtils.NotebookRootHolder(context));

        return null;
    }

    @Override
    public void onBindViewHolder(CatalogViewHolderUtils.CatalogViewHolder holder, int position) {
        NotebookInfo info = cursor.getNotebookInfo(position);
        ((CatalogViewHolderUtils.NotebookRootHolder)holder.holder).bind(info);

    }

    @Override
    public int getItemViewType(int position) {
        //// TODO: 7/13/2017 Note implemented other types
        return VIEWTYPE_NOTEBOOK_ROOT;
    }

    @Override
    public int getItemCount() {
        return cursor.getCursor().getCount();
    }
}
