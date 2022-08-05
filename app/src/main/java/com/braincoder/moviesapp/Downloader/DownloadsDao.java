package com.braincoder.moviesapp.Downloader;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.braincoder.moviesapp.Models.Downloads;

import java.util.List;

@Dao
public interface DownloadsDao {

    @Query("Select * from Downloads")
    List<Downloads> getAllDownloads();

    @Query("Select * from Downloads where downloadId = :downloadId")
    Downloads getDownload(long downloadId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertDownload(Downloads download);

    @Update
    void updateDownload(Downloads download);

    @Delete
    void deleteDownload(Downloads download);

}
