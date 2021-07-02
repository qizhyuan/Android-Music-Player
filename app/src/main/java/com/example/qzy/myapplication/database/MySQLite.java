package com.example.qzy.myapplication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by QZY on 2017/12/26.
 */

public class MySQLite {
    private MySQLiteHelper mySQLiteHelper;

    public MySQLite(Context context){
        mySQLiteHelper = new MySQLiteHelper(context);
    }

    public void InsertAndUpdate(String[] info){
        SQLiteDatabase db = mySQLiteHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("songName", info[0]);
        values.put("artistName", info[1]);
        values.put("songId", info[2]);

        db.insert("favourite_music", null, values);//插入数据
        db.close();
    }

    private String[] getInfo(String name, String[] item){
        SQLiteDatabase db = mySQLiteHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from favourite_music where songId=?",new String[]{name});
        String[] result = new String[item.length];
        if(cursor.moveToFirst()){
            for (int i = 0; i < item.length; ++i){
                String res = cursor.getString(cursor.getColumnIndex(item[i]));
                result[i] = res;
            }

        }
             cursor.close();
             db.close();
             return result;
        }

    public boolean isExist(String songId){
        return getInfo(songId, new String[]{"songId"})[0] != null;
    }

    public int getNext(String songId){

        String[] Info =getArray("songId");
        int l = Info.length;
        int position = 0;
        for(int i = 0; i < l; ++i){
            if(Info[i].equals(songId)){
                position = i;
                break;
            }
        }
        return (position + 1) % l;
    }

    public int getPrevious(String songId){
        String [] Info = getArray("songId");
        int l = Info.length;
        int position = 0;
        for(int i = 0; i < l; ++i){
            if(Info[i].equals(songId)){
                position = i;
                break;
            }
        }
        return (position + l - 1) % l;
    }

    public String[] getArray(String item){
        SQLiteDatabase db = mySQLiteHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from favourite_music",null);
        String [] res = new String[cursor.getCount()];
        int i = 0;
        if(cursor.moveToFirst()){
            do{
                res[i] = cursor.getString(cursor.getColumnIndex(item));
                ++i;
            }while(cursor.moveToNext());

        }

        cursor.close();
        db.close();
        return res;
    }

    public int getPosition(String songId){
        String [] item = getArray("songId");
        int res = 0;
        for(int i = 0; i < item.length; ++i){
            if(item[i].equals(songId)){
                res = i;
                break;
            }
        }
        return res;
    }

    public void delete(String name){
        SQLiteDatabase db = mySQLiteHelper.getWritableDatabase();
        db.execSQL("delete from favourite_music where songId=?",new String[]{name});
        db.close();
    }

}
