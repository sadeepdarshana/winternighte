package com.example.sadeep.winternightd.localstorage;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.sadeep.winternightd.localstorage.DataConnection;
import com.example.sadeep.winternightd.notebook.NotebookInfo;

/**
 * Created by Sadeep on 7/13/2017.
 */

public class CatalogDataHandler {


    public Cursor getCursor(){
        return DataConnection.readableDatabase().rawQuery("SELECT * FROM `catalog` ORDER BY created DESC",null);
    }


    public static void createCatalogTable(){
        String createSQL = "CREATE TABLE IF NOT EXISTS `catalog` (`notebookId` TEXT,`title` TEXT,`created` INTEGER,`icon` TEXT,PRIMARY KEY(`notebookId`));";
        DataConnection.execSQL(createSQL);
    }

    public static void createCatalogEntry(NotebookInfo info){
        ContentValues values = CatalogValuesWriter.generateContentValues(info);
        DataConnection.writableDatabase().insertWithOnConflict("catalog",null,values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public static void createNotebookTable(String notebookUUID){
        String createSQL = "CREATE TABLE IF NOT EXISTS `" + notebookUUID +"` ( `noteId` TEXT, `strings0` TEXT, `strings1` TEXT, `ints0` BLOB, `ints1` TEXT, `fieldTypes` BLOB, `cvtime` INTEGER, `created` INTEGER, `cvId` TEXT, PRIMARY KEY(`noteId`) )";
        DataConnection.execSQL(createSQL);
    }

    public static void addNotebook(NotebookInfo info){
        createCatalogEntry(info);
        createNotebookTable(info.notebookUUID);
    }

    public static void deleteNotebook(String  notebookUUID){
        try {
            DataConnection.writableDatabase().delete("catalog", "notebookId = '" + notebookUUID + "'", null);
        }catch (Exception e){}
    }

}
