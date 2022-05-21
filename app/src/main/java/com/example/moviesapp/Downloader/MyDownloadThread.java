package com.example.moviesapp.Downloader;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.URLUtil;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

public class MyDownloadThread extends Thread{

    Context context;
    MyDownloadModel myDownloadModel;
    private DB db;
    private MyDownloadManager myDownloadManager;
    private File destDir;
    private String fileName = null;

    public MyDownloadThread(Context context, MyDownloadModel myDownloadModel, MyDownloadManager myDownloadManager) {
        this.context = context;
        this.myDownloadModel = myDownloadModel;
        this.myDownloadManager = myDownloadManager;

        this.db = new DB(context);

        File dir = new File(context.getExternalFilesDir(null) + "/Downloads");
        if(!dir.exists())
            dir.mkdir();

        this.destDir = dir;
    }

    @Override
    public void run() {
        downloadFile();
    }

    private File getLocalFile(String url){
        if(fileName == null) {
            try {
                URL url1 = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) url1.openConnection();
                connection.connect();

                if (connection.getResponseCode() == 200) {
                    String raw = connection.getHeaderField("Content-Disposition");
                    if(raw != null && raw.toLowerCase().contains("filename") && raw.indexOf("=") != -1){
                        fileName = raw.split("=")[1];
                        if (fileName.contains("\"")) {
                            fileName = fileName.replaceAll("\"", "");
                        }
                    }else {
                        fileName = URLUtil.guessFileName(url, null, null);
                        Log.i("Guess Filename = ", fileName);
                    }
                }else {
                    fileName = URLUtil.guessFileName(url, null, null);
                    Log.i("Guess Filename = ", fileName);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Log.i("Filename = ", fileName);
        File file = new File(destDir.getPath() + "/" + fileName);

//        if(!file.exists()) {
//            try {
//                file.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

        return file;
    }

    private void downloadFile(){
        InputStream inputStream;
        OutputStream outputStream;
        try {
            File localFile = getLocalFile(myDownloadModel.getUrl());

            // When download start
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    myDownloadModel.setFileName(fileName);

                    updateModel(myDownloadModel);

                    if(myDownloadManager.getDownloadListener() != null) {
                        myDownloadManager.startNotification(myDownloadModel);
                        myDownloadManager.getDownloadListener().onStart(myDownloadModel);
                    }
                }
            });

            URL url1 = new URL(myDownloadModel.getUrl());
            HttpURLConnection connection = (HttpURLConnection) url1.openConnection();

            if(localFile.exists()){
                connection.addRequestProperty("Range", "bytes=" + localFile.length() + "-");
                outputStream = new FileOutputStream(localFile, true);
            }else {
                outputStream = new FileOutputStream(localFile);
            }

            connection.connect();

            long fileSize = 0;
            long downloaded = 0;
            if(connection.getResponseCode() == 206){
                fileSize = localFile.length() + connection.getContentLength();
                downloaded = localFile.length();
            }else if(connection.getResponseCode() == 200){
                fileSize = connection.getContentLength();
            }else {
                onFailed(myDownloadModel, connection.getResponseCode());
                return;
            }

            if(fileSize > 0) {
                inputStream = new BufferedInputStream(connection.getInputStream());

                byte[] data = new byte[1024];
                int count = 0;
                while ((count = inputStream.read(data)) != -1) {
                    downloaded += count;

                    outputStream.write(data, 0, count);

                    int progress = (int) ((downloaded * 100) / fileSize);
                    long finalDownloaded = downloaded;
                    long finalFileSize = fileSize;
                    String finalFilePath = localFile.getPath();

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            // When progress changed
                            if (myDownloadManager.getDownloadListener() != null) {
                                myDownloadModel.setFileSize(finalFileSize);
                                myDownloadModel.setDownloaded(finalDownloaded);
                                myDownloadModel.setProgress(progress);

                                myDownloadManager.getDownloadListener().onProgress(myDownloadModel);

                            }

                            // When download completed
                            if (finalDownloaded >= finalFileSize) {
                                myDownloadModel.setStatus("Completed");
                                myDownloadModel.setFileSize(finalFileSize);
                                myDownloadModel.setDownloaded(finalDownloaded);
                                myDownloadModel.setProgress(progress);
                                myDownloadModel.setFilePath(finalFilePath);

                                updateModel(myDownloadModel);

                                if (myDownloadManager.getDownloadListener() != null) {
                                    myDownloadManager.getDownloadListener().onProgress(myDownloadModel);
                                    myDownloadManager.getDownloadListener().onComplete(myDownloadModel);
                                }

                                myDownloadManager.completedNotification(myDownloadModel);
                                myDownloadManager.remove(myDownloadModel);

                            }

                            // When download paused
                            if (myDownloadManager.isPaused()) {
                                myDownloadModel.setPaused(true);
                                myDownloadModel.setStatus("Paused");
                                updateModel(myDownloadModel);

                                if (myDownloadManager.getDownloadListener() != null) {
                                    myDownloadManager.getDownloadListener().onPause(myDownloadModel);
                                }
                            }
                        }
                    });

                    if (myDownloadManager.isPaused())
                        break;
                }

                outputStream.flush();
                outputStream.close();
                inputStream.close();
            } else {
                onFailed(myDownloadModel, connection.getResponseCode());
                return;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void onFailed(MyDownloadModel myDownloadModel, int responseCode){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                String msg = "Error " + responseCode;
                if(responseCode == 416){
                    msg = "File already exists";
                }

                myDownloadModel.setStatus(msg);

                if (myDownloadManager.getDownloadListener() != null) {
                    myDownloadManager.getDownloadListener().onFailed(msg);
                }

                updateModel(myDownloadModel);
            }
        });
    }

    private void updateModel(MyDownloadModel myDownloadModel){
        db.updateDownload(myDownloadModel);
    }

}
