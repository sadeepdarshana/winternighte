package com.example.sadeep.winternightd.localstorage;

import android.content.ContentValues;

import com.example.sadeep.winternightd.dumping.RawFieldDataStream;
import com.example.sadeep.winternightd.note.NoteInfo;

import java.util.UUID;

/**
 * Created by Sadeep on 7/12/2017.
 */

public class NotebookValuesWriter {

    public static void writeNoteInfo(ContentValues values,NoteInfo info){
        values.put("noteId",info.noteUUID);
        values.put("created", info.createdTime);
        values.put("cvtime", info.currentVersionTime);
        values.put("cvId", info.currentVersionUUID);
    }
    public static void writeDataStreams(ContentValues values,RawFieldDataStream stream){
        values.put("strings0",stream.strings[0]);
        values.put("strings1",stream.strings[1]);
        values.put("ints0",stream.ints0);
        values.put("ints1",stream.ints1);
        values.put("bindata",stream.bindata);
        values.put("fieldTypes",stream.fieldTypes);
    }

    public static ContentValues generateContentValues(RawFieldDataStream stream, NoteInfo info){
        ContentValues values = new ContentValues();
        writeDataStreams(values,stream);
        writeNoteInfo(values,info);
        return values;
    }
}
