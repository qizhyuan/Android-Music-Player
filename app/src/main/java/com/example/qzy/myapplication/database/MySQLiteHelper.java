package com.example.qzy.myapplication.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by QZY on 2017/12/26.
 */

public class MySQLiteHelper extends SQLiteOpenHelper {

    public MySQLiteHelper(Context context) {
        super(context,"favourite_music.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("create table if not exists favourite_music ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "songName NVARCHAR,"
                + "artistName NVARCHAR,"
                + "songId NVARCHAR)"
                );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
