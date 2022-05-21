package com.example.moviesapp.Downloader;

import java.io.Serializable;

public class MyDownloadModel implements Serializable {
    long id;
    String url;
    String fileName;
    String filePath;
    long fileSize;
    int progress;
    long downloaded;
    String status;
    boolean isPaused;

    public MyDownloadModel() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
        return id + " = " + fileName;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == (MyDownloadModel)obj)
            return true;
        else
            return false;
    }
}
