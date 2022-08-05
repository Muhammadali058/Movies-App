package com.braincoder.moviesapp.Downloader;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.braincoder.moviesapp.Models.Downloads;

@Database(entities = {Downloads.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract DownloadsDao downloadsDao();

    private static AppDatabase instance = null;
    public static synchronized AppDatabase getInstance(Context context){
        if(instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "downloadsDB")
                    .allowMainThreadQueries()
                    .build();
        }

        return instance;
    }
}
