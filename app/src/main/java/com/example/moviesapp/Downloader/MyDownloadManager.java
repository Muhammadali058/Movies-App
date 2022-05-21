package com.example.moviesapp.Downloader;

import android.content.Context;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class MyDownloadManager {
    Context context;
    String url;
    private boolean isPaused = false;
    MyService myService;
    private DB db;
    public MyDownloadListener downloadListener = null;
    public MyDownloadModel myDownloadModel = null;

    public MyDownloadManager(Context context, String url, MyService myService) {
        this.context = context;
        this.url = url;
        this.myService = myService;
        this.db = new DB(context);
    }

    public void start(){
        myDownloadModel = new MyDownloadModel();
        myDownloadModel.setUrl(url);
        myDownloadModel.setFileName("");
        myDownloadModel.setFilePath("");
        myDownloadModel.setFileSize(0);
        myDownloadModel.setProgress(0);
        myDownloadModel.setDownloaded(0);
        myDownloadModel.setStatus("Downloading");
        myDownloadModel.setPaused(false);

        long id = db.addDownload(myDownloadModel);
        myDownloadModel.setId(id);

        new MyDownloadThread(context, myDownloadModel, this).start();

//        startNotification(myDownloadModel);

        Intent intent = new Intent("Receiver");
        intent.putExtra("action", "started");
        intent.putExtra("model", myDownloadModel);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public void resume(MyDownloadModel downloadModel){
        this.myDownloadModel = downloadModel;

        isPaused = false;

        downloadModel.setPaused(false);
        downloadModel.setStatus("Downloading");
        updateModel(downloadModel);

        if(downloadListener != null) {
            downloadListener.onResume(downloadModel);
        }

        new MyDownloadThread(context, myDownloadModel, this).start();
    }

    public void pause(MyDownloadModel downloadModel){
        downloadModel.setPaused(true);
        downloadModel.setStatus("Paused");
        updateModel(downloadModel);

        isPaused = true;
    }

    public boolean isPaused(){
        return isPaused;
    }

    public MyDownloadListener getDownloadListener(){
        return this.downloadListener;
    }

    public void setDownloadListener(MyDownloadListener myDownloadListener){
        this.downloadListener = myDownloadListener;
    }

    public void startNotification(MyDownloadModel myDownloadModel){
        myService.sendNotification(1, myDownloadModel.getFileName(), "Downloading...");
    }

    public void completedNotification(MyDownloadModel myDownloadModel){
        myService.sendNotification(2, myDownloadModel.getFileName(), "Completed");
    }

    public void remove(MyDownloadModel myDownloadModel){
        myService.remove(myDownloadModel);
    }

    private void updateModel(MyDownloadModel myDownloadModel){
        db.updateDownload(myDownloadModel);
    }

}
