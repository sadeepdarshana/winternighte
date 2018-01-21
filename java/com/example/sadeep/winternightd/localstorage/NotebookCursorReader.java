package com.example.sadeep.winternightd.localstorage;

import android.database.Cursor;

import com.example.sadeep.winternightd.dumping.FieldDataStream;
import com.example.sadeep.winternightd.dumping.RawFieldDataStream;
import com.example.sadeep.winternightd.note.NoteInfo;

/**
 * Created by Sadeep on 7/12/2017.
 */

/**
 * A wrapper for the SQLite data cursor of one Notebook table.
 * Provides methods to obtain the FieldDataStream and NoteInfo of a note in a given position
 * in the Notebook which can be used to generate the Note.
 */

public class NotebookCursorReader {
    private Cursor cursor;

    public NotebookCursorReader(Cursor cursor) {
        this.cursor = cursor;
    }

    public FieldDataStream getFieldDataStream(int position){
        cursor.moveToPosition(position);
        RawFieldDataStream rawStream = new RawFieldDataStream(cursor.getString(1), cursor.getString(2), cursor.getBlob(3), cursor.getString(4),cursor.getBlob(5), cursor.getBlob(6));
        return new FieldDataStream(rawStream);
    }

    public NoteInfo getNoteInfo(int position){
        cursor.moveToPosition(position);
        return new NoteInfo(cursor.getString(0),cursor.getLong(8),cursor.getLong(7),cursor.getString(9));
    }

    public Cursor getCursor(){
        return cursor;
    }
}
