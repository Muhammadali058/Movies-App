package com.example.moviesapp.Models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Downloads implements Serializable {
    @PrimaryKey(autoGenerate = true)
    long id;
    @ColumnInfo
    long downloadId;
    @ColumnInfo
    String url;
    @ColumnInfo
    String fileName;
    @ColumnInfo
    String filePath;
    @ColumnInfo
    long fileSize;
    @ColumnInfo
    int progress;
    @ColumnInfo
    long downloaded;
    @ColumnInfo
    String status;
    @ColumnInfo
    boolean isPaused;

    public Downloads() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(long downloadId) {
        this.downloadId = downloadId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public long getDownloaded() {
        return downloaded;
    }

    public void setDownloaded(long downloaded) {
        this.downloaded = downloaded;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    @Override
    public String toString() {
        return downloadId + " = " + fileName;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == (Downloads)obj)
            return true;
        else
            return false;
    }
}
