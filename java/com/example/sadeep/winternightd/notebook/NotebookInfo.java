package com.example.sadeep.winternightd.notebook;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.sadeep.winternightd.note.NoteInfo;

import java.io.IOException;
import java.util.Random;

import static java.lang.Math.abs;

/**
 * Created by Sadeep on 7/13/2017.
 */

public class NotebookInfo  {
    public String notebookUUID;
    public String title;
    public long createdTime;
    public String icon;

    public NotebookInfo(String notebookUUID, String title, long createdTime,String icon) {
        this.createdTime = createdTime;
        this.notebookUUID = notebookUUID;
        this.title = title;
        this.icon = icon;
    }

    private NotebookInfo(){}

    public static NotebookInfo newNotebookInfoForCurrentTime(Context context,String title){
        NotebookInfo info = new NotebookInfo();

        info.notebookUUID = "b"+java.util.UUID.randomUUID().toString().replaceAll("-","");
        info.createdTime = System.currentTimeMillis();
        info.title = title;

        try {
            String [] list = context.getAssets().list("pic");
            info.icon = list[(abs(new Random().nextInt())%list.length)];
            info.icon=info.icon.replace(".png","");
        } catch (IOException e) {

        }


        return info;
    }

}
