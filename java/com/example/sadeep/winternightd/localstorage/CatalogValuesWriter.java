package com.example.sadeep.winternightd.localstorage;

import android.content.ContentValues;

import com.example.sadeep.winternightd.dumping.RawFieldDataStream;
import com.example.sadeep.winternightd.note.NoteInfo;
import com.example.sadeep.winternightd.notebook.NotebookInfo;

/**
 * Created by Sadeep on 7/12/2017.
 */

public class CatalogValuesWriter {

    public static void writeNotebookInfo(ContentValues values,NotebookInfo info){
        values.put("created",info.createdTime);
        values.put("title",info.title);
        values.put("notebookId",info.notebookUUID);
        values.put("icon",info.icon);
    }

    public static ContentValues generateContentValues(NotebookInfo info){
        ContentValues values = new ContentValues();
        writeNotebookInfo(values,info);
        return values;
    }
}
