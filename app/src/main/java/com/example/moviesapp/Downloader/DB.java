package com.example.moviesapp.Downloader;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DB extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "downloadsDB";
    private static final String TABLE_DOWNLOADS = "downloads";

    public DB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "create table " + TABLE_DOWNLOADS +
                "(id integer primary key autoincrement," +
                "url text," +
                "fileName text," +
                "filePath text," +
                "fileSize integer," +
                "progress integer," +
                "downloaded integer," +
                "status text," +
                "isPaused integer)";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOWNLOADS);

        // Create tables again
        onCreate(db);
    }

    @SuppressLint("Range")
    public List<MyDownloadModel> getAllDownloads(){
        SQLiteDatabase db = getReadableDatabase();
        List<MyDownloadModel> list = new ArrayList<>();

        Cursor cursor = db.rawQuery("select * from " + TABLE_DOWNLOADS, null);
        if(cursor != null){
            while (cursor.moveToNext()){
                MyDownloadModel myDownloadModel = new MyDownloadModel();

                myDownloadModel.setId(cursor.getLong(cursor.getColumnIndex("id")));
                myDownloadModel.setUrl(cursor.getString(cursor.getColumnIndex("url")));
                myDownloadModel.setFileName(cursor.getString(cursor.getColumnIndex("fileName")));
                myDownloadModel.setFilePath(cursor.getString(cursor.getColumnIndex("filePath")));
                myDownloadModel.setFileSize(cursor.getLong(cursor.getColumnIndex("fileSize")));
                myDownloadModel.setProgress(cursor.getInt(cursor.getColumnIndex("progress")));
                myDownloadModel.setDownloaded(cursor.getLong(cursor.getColumnIndex("downloaded")));
                myDownloadModel.setStatus(cursor.getString(cursor.getColumnIndex("status")));

                int i = cursor.getInt(cursor.getColumnIndex("isPaused"));
                if(i == 1)
                    myDownloadModel.setPaused(true);
                else
                    myDownloadModel.setPaused(false);

                list.add(myDownloadModel);
            }
        }
//        cursor.close();
//        db.close();
        return list;
    }

    @SuppressLint("Range")
    public MyDownloadModel getDownload(long id){
        SQLiteDatabase db = getReadableDatabase();
        MyDownloadModel myDownloadModel = new MyDownloadModel();

        Cursor cursor = db.rawQuery("select * from " + TABLE_DOWNLOADS + " where id=?", new String[]{String.valueOf(id)});
        if(cursor != null && cursor.moveToFirst()){
            myDownloadModel.setId(cursor.getLong(cursor.getColumnIndex("id")));
            myDownloadModel.setUrl(cursor.getString(cursor.getColumnIndex("url")));
            myDownloadModel.setFileName(cursor.getString(cursor.getColumnIndex("fileName")));
            myDownloadModel.setFilePath(cursor.getString(cursor.getColumnIndex("filePath")));
            myDownloadModel.setFileSize(cursor.getLong(cursor.getColumnIndex("fileSize")));
            myDownloadModel.setProgress(cursor.getInt(cursor.getColumnIndex("progress")));
            myDownloadModel.setDownloaded(cursor.getLong(cursor.getColumnIndex("downloaded")));
            myDownloadModel.setStatus(cursor.getString(cursor.getColumnIndex("status")));

            int i = cursor.getInt(cursor.getColumnIndex("isPaused"));
            if(i == 1)
                 myDownloadModel.setPaused(true);
            else
                myDownloadModel.setPaused(false);
        }
//        cursor.close();
//        db.close();
        return myDownloadModel;
    }

    @SuppressLint("Range")
    public boolean isUrlExists(String url){
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("select url from " + TABLE_DOWNLOADS + " where url=?", new String[]{String.valueOf(url)});

        if(cursor.getCount() > 0){
            return true;
        }else
            return false;
    }

    public long addDownload(MyDownloadModel myDownloadModel){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("url", myDownloadModel.getUrl());
        values.put("fileName", myDownloadModel.getFileName());
        values.put("filePath", myDownloadModel.getFilePath());
        values.put("fileSize", myDownloadModel.getFileSize());
        values.put("progress", myDownloadModel.getProgress());
        values.put("downloaded", myDownloadModel.getDownloaded());
        values.put("status", myDownloadModel.getStatus());

        if(myDownloadModel.isPaused())
            values.put("isPaused", 1);
        else
            values.put("isPaused", 0);

        long id = db.insert(TABLE_DOWNLOADS, null, values);
//        db.close();
        return id;
    }

    public void updateDownload(MyDownloadModel myDownloadModel){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("url", myDownloadModel.getUrl());
        values.put("fileName", myDownloadModel.getFileName());
        values.put("filePath", myDownloadModel.getFilePath());
        values.put("fileSize", myDownloadModel.getFileSize());
        values.put("progress", myDownloadModel.getProgress());
        values.put("downloaded", myDownloadModel.getDownloaded());
        values.put("status", myDownloadModel.getStatus());

        if(myDownloadModel.isPaused())
            values.put("isPaused", 1);
        else
            values.put("isPaused", 0);

        db.update(TABLE_DOWNLOADS, values, "id=?", new String[]{String.valueOf(myDownloadModel.getId())});
//        db.close();
    }

    public void deleteDownload(long id){
        SQLiteDatabase db = getWritableDatabase();

        db.delete(TABLE_DOWNLOADS, "id=?", new String[]{String.valueOf(id)});

//        db.close();
    }

}
