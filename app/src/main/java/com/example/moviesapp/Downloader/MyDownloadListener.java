package com.example.moviesapp.Downloader;

public interface MyDownloadListener {
    void onStart(MyDownloadModel myDownloadModel);
    void onPause(MyDownloadModel myDownloadModel);
    void onResume(MyDownloadModel myDownloadModel);
    void onProgress(MyDownloadModel myDownloadModel);
    void onComplete(MyDownloadModel myDownloadModel);
    void onFailed(String msg);
}
