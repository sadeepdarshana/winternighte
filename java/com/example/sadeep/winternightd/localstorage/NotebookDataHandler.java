package com.example.sadeep.winternightd.localstorage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.sadeep.winternightd.dumping.RawFieldDataStream;
import com.example.sadeep.winternightd.note.Note;
import com.example.sadeep.winternightd.note.NoteFactory;
import com.example.sadeep.winternightd.note.NoteInfo;
import com.example.sadeep.winternightd.selection.XSelection;

/**
 * Created by Sadeep on 6/16/2017.
 */
public class NotebookDataHandler {

    private String notebookUUID;

    public NotebookDataHandler(String notebookUUID){
        this.notebookUUID = notebookUUID;
    }

    public Cursor getCursor(){
        return DataConnection.readableDatabase().rawQuery("SELECT * FROM "+ notebookUUID +" ORDER BY cvtime DESC",null);
    }

    public void addNewNote(Note note){
        RawFieldDataStream stream = new RawFieldDataStream(note.getFieldDataStream());
        NoteInfo info = NoteInfo.newNoteInfoForCurrentTime();

        ContentValues values = NotebookValuesWriter.generateContentValues(stream,info);

        DataConnection.writableDatabase().insertWithOnConflict(notebookUUID,null,values, SQLiteDatabase.CONFLICT_IGNORE);
    }
    public void addExistingNote(Note note){
        if(XSelection.isSelectionAvailable())XSelection.clearSelections();

        RawFieldDataStream stream = new RawFieldDataStream(note.getFieldDataStream());
        NoteInfo info = note.noteInfo;
        info.currentVersionTime = System.currentTimeMillis();

        ContentValues values = NotebookValuesWriter.generateContentValues(stream,info);

        DataConnection.writableDatabase().insertWithOnConflict(notebookUUID,null,values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void deleteNote(String  noteUUID){
        try {
            DataConnection.writableDatabase().delete(notebookUUID, "noteId = '" + noteUUID + "'", null);
        }catch (Exception e){}
    }

    public Cursor getCursorForNote(String noteUUID){
        return DataConnection.readableDatabase().rawQuery("SELECT * FROM "+ notebookUUID +" WHERE `noteId`='"+noteUUID+"'",null);
    }

}
