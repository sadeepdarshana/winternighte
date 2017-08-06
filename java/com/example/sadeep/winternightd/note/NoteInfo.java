package com.example.sadeep.winternightd.note;

/**
 * Created by Sadeep on 7/9/2017.
 */

public class NoteInfo {
    public String noteUUID;
    public String currentVersionUUID;
    public long currentVersionTime;
    public long createdTime;

    public NoteInfo(String noteUUID,long createdTime, long currentVersionTime, String currentVersionUUID) {
        this.createdTime = createdTime;
        this.currentVersionTime = currentVersionTime;
        this.currentVersionUUID = currentVersionUUID;
        this.noteUUID = noteUUID;
    }
    private NoteInfo(){}

    public static NoteInfo newNoteInfoForCurrentTime(){
        NoteInfo info = new NoteInfo();

        info.noteUUID = java.util.UUID.randomUUID().toString().replaceAll("-","");
        info.currentVersionUUID = java.util.UUID.randomUUID().toString().replaceAll("-","");

        info.createdTime = System.currentTimeMillis();
        info.currentVersionTime = info.createdTime;

        return info;
    }

    public static void updateNoteInfoVersion(NoteInfo info){
        info.currentVersionUUID = java.util.UUID.randomUUID().toString().replaceAll("-","");
        info.currentVersionTime = info.createdTime;
    }
}
