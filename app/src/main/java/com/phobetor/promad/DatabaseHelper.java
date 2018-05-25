package com.phobetor.promad;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by logan on 25/4/17.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "ReadAloud.db";
    public static final String TABLE_NAME = "RecentFiles";
    public static final String COL_1 = "Path";
    public static final String COL_2 = "FileName";
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "create table " + TABLE_NAME + "(" + COL_1 + " varchar(30) primary key, " + COL_2 + " varchar(15));";
        db.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String path, String FName)
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, path);
        contentValues.put(COL_2, FName);
        long l = db.insert(TABLE_NAME, null, contentValues);

        if(l<0)
            return false;
        else
            return true;
    }

    public Cursor getRecentFiles()
    {
        SQLiteDatabase db = getWritableDatabase();
        //String query = "select " + COL_2 + " from " + TABLE_NAME +";";
        String query = "select * from " + TABLE_NAME +";";
        Cursor result = db.rawQuery(query, null);
        return result;
    }

    public int deleteFile(String fileName)
    {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_NAME, "FileName = ?", new String[]{fileName});
    }
}
