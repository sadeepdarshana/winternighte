package com.example.sadeep.winternightd.notebook;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.sadeep.winternightd.note.NoteInfo;

/**
 * Created by Sadeep on 7/13/2017.
 */

public class NotebookInfo  {
    public String notebookUUID;
    public String title;
    public long createdTime;

    public NotebookInfo(String notebookUUID, String title, long createdTime) {
        this.createdTime = createdTime;
        this.notebookUUID = notebookUUID;
        this.title = title;
    }

    private NotebookInfo(){}

    public static NotebookInfo newNotebookInfoForCurrentTime(String title){
        NotebookInfo info = new NotebookInfo();

        info.notebookUUID = "b"+java.util.UUID.randomUUID().toString().replaceAll("-","");
        info.createdTime = System.currentTimeMillis();
        info.title = title;

        return info;
    }

}
