package com.example.sadeep.winternightd.localstorage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static android.database.sqlite.SQLiteDatabase.*;

/**
 * Created by Sadeep on 6/16/2017.
 */

public class DataConnection extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "winternightd.db";

    public static  SQLiteDatabase db;
    private static DataConnection connection;


    public static void initialize(Context context){
        if(connection==null )new DataConnection(context);
    }

    private DataConnection(Context context){
        super(context, DATABASE_NAME, null, 1);
        connection = this;

    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        DataConnection.db = db;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static void execSQL(String str){
        if(connection == null)return;
        connection.getWritableDatabase().execSQL(str);
    }

    public static SQLiteDatabase writableDatabase(){
        return connection.getWritableDatabase();
    }

    public static SQLiteDatabase readableDatabase() {
        return connection.getReadableDatabase();
    }
}
