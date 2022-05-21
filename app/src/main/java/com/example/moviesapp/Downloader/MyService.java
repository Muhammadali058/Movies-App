package com.example.moviesapp.Downloader;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.moviesapp.Activities.DownloadsActivity;

import java.util.ArrayList;
import java.util.List;

public class MyService extends Service {
    private String channelId = "channelId";
    private DB db;
    public List<MyDownloadManager> downloadsList;
    public final Binder binder = new MyServiceBinder();

    public class MyServiceBinder extends Binder {
        public MyService getService(){
            return MyService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        this.db = new DB(getApplicationContext());
        downloadsList = new ArrayList<>();
        Log.i("Service = ", "onCreate Called");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String url = intent.getStringExtra("url");

        if (db.isUrlExists(url)){
            Toast.makeText(this, "Downloading already added", Toast.LENGTH_SHORT).show();
        }else {
            MyDownloadManager myDownloadManager = new MyDownloadManager(getApplicationContext(), url, this);
            downloadsList.add(myDownloadManager);
            myDownloadManager.start();
        }

        Log.i("Service = ", "onStartCommand");
//        return Service.START_STICKY;
//        return START_NOT_STICKY;
        return Service.START_REDELIVER_INTENT;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i("Service: onBind = ", "onBind Called");
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        dropDownloadListeners();

        Log.i("Service: onUnbind = ", "onUnbind Called");
        return true;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.i("Service: onRebind = ", "onRebind Called");
    }

    public void addToList(MyDownloadModel myDownloadModel){
        MyDownloadManager myDownloadManager = new MyDownloadManager(getApplicationContext(), myDownloadModel.getUrl(), this);
        myDownloadManager.myDownloadModel = myDownloadModel;
        downloadsList.add(myDownloadManager);
    }

    public boolean isExistsInDownloadingList(MyDownloadModel myDownloadModel){
        for (int i = 0; i < downloadsList.size(); i++) {
            if(downloadsList.get(i).myDownloadModel.getId() == myDownloadModel.getId()){
                return true;
            }
        }

        return false;
    }

    public void pause(MyDownloadModel myDownloadModel){
        for (int i = 0; i < downloadsList.size(); i++) {
            if(downloadsList.get(i).myDownloadModel.getId() == myDownloadModel.getId()){
                downloadsList.get(i).pause(myDownloadModel);
                break;
            }
        }
    }

    public void resume(MyDownloadModel downloadModel){
        for (int i = 0; i < downloadsList.size(); i++) {
            if(downloadsList.get(i).myDownloadModel.getId() == downloadModel.getId()){
                downloadsList.get(i).resume(downloadModel);
                break;
            }
        }
    }

    public void remove(MyDownloadModel myDownloadModel){
        for (int i = 0; i < downloadsList.size(); i++) {
            if(downloadsList.get(i).myDownloadModel.getId() == myDownloadModel.getId()){
                downloadsList.remove(i);
                break;
            }
        }

        if(downloadsList.size() == 0){
            stopForeground(true);
            stopSelf();
        }
    }

    public void setDownloadListener(MyDownloadModel myDownloadModel, MyDownloadListener downloadListener){
        if(!isExistsInDownloadingList(myDownloadModel)){
            addToList(myDownloadModel);
        }

        for (int i = 0; i < downloadsList.size(); i++) {
            if(downloadsList.get(i).myDownloadModel.getId() == myDownloadModel.getId()){
                downloadsList.get(i).setDownloadListener(downloadListener);
                break;
            }
        }
    }

    private void dropDownloadListeners(){
        for (int i = 0; i < downloadsList.size(); i++) {
            downloadsList.get(i).setDownloadListener(null);
        }
    }

    private void pauseAllDownloads(){
        for (int i = 0; i < downloadsList.size(); i++) {
            Log.i("Paused = ", downloadsList.get(i).myDownloadModel.getFileName());

            downloadsList.get(i).pause(downloadsList.get(i).myDownloadModel);
//            downloadsList.get(i).myDownloadModel.setStatus("Paused");
//            downloadsList.get(i).myDownloadModel.setPaused(true);
//            updateModel(downloadsList.get(i).myDownloadModel);
        }
    }

    public void sendNotification(int id, String fileName, String text){
        Intent intent = new Intent(getApplicationContext(), DownloadsActivity.class);

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channelId, "Download", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("Download Manager Channel");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), channelId);
        notificationBuilder.setAutoCancel(false)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(android.R.drawable.star_on)
//                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
//                        com.example.callmanager.R.drawable.plus))
                .setTicker("Tutorialspoint")
//                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                //.setPriority(Notification.PRIORITY_MAX)
                .setContentTitle(fileName)
                .setContentText(text)
                .setContentInfo("Information");


        if(intent!=null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
            notificationBuilder.setContentIntent(pendingIntent);
        }

        Notification notification = notificationBuilder.build();

        startForeground(id, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        pauseAllDownloads();
        Log.i("Service: onDestroy = ", "onDestroy Called");
    }

}