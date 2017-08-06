package com.example.sadeep.winternightd.note;

import android.content.Context;
import android.view.ViewGroup;

import com.example.sadeep.winternightd.dumping.FieldDataStream;
import com.example.sadeep.winternightd.field.fields.Field;

/**
 * Created by Sadeep on 6/14/2017.
 */

public final class NoteFactory {
    private NoteFactory(){}

    public static Note createNewNote(Context context, boolean isEditable, ViewGroup parent){
            return new Note(context,isEditable,true,parent);
    }
    public static Note fromFieldDataStream(Context context,FieldDataStream stream, boolean isEditable, ViewGroup parent,NoteInfo noteInfo){
        Note note = new Note(context,isEditable,false,parent);
        stream.resetCursor();
        note.readFromFieldDataStream(stream);
        note.noteInfo = noteInfo;
        return note;
    }

    public static void assignNewNoteInfo(Note note){

    }

}
