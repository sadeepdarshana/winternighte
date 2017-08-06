package com.example.sadeep.winternightd.localstorage;

import android.database.Cursor;

import com.example.sadeep.winternightd.dumping.FieldDataStream;
import com.example.sadeep.winternightd.dumping.RawFieldDataStream;
import com.example.sadeep.winternightd.note.NoteInfo;
import com.example.sadeep.winternightd.notebook.NotebookInfo;

/**
 * Created by Sadeep on 7/12/2017.
 */

/**
 * A wrapper for the SQLite data cursor of the catalog table.
 * Provides methods to obtain NotebookInfo which is used to generate the catalog of Notebooks.
 */

public class CatalogCursorReader {
    private Cursor cursor;

    public CatalogCursorReader(Cursor cursor) {
        this.cursor = cursor;
    }


    public NotebookInfo getNotebookInfo(int position){
        cursor.moveToPosition(position);
        return new NotebookInfo(cursor.getString(0),cursor.getString(1),cursor.getLong(2));
    }

    public Cursor getCursor(){
        return cursor;
    }
}
