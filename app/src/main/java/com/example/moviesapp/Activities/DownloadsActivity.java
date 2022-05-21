package com.example.moviesapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;

import com.example.moviesapp.Adapters.MyDownloadAdapter;
import com.example.moviesapp.Downloader.DB;
import com.example.moviesapp.Downloader.MyDownloadModel;
import com.example.moviesapp.Downloader.MyService;
import com.example.moviesapp.databinding.ActivityDownloadsBinding;

import java.util.ArrayList;
import java.util.List;

public class DownloadsActivity extends AppCompatActivity {

    ActivityDownloadsBinding binding;
    List<MyDownloadModel> list;
    MyDownloadAdapter myDownloadAdapter;
    MyService myService;
    ServiceConnection serviceConnection;
    BroadcastReceiver broadcastReceiver;
    boolean isBind = false;
    String url = null;
    DB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDownloadsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        url = getIntent().getStringExtra("url");

        init();
    }

    private void init(){
        db = new DB(this);

        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                MyService.MyServiceBinder binder = (MyService.MyServiceBinder) iBinder;
                myService = binder.getService();
                isBind = true;

                setMyDownloadAdapter();
                myDownloadAdapter.setService(myService);
                if(url != null) {
                    downloadFile(url);
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getStringExtra("action");
                MyDownloadModel myDownloadModel = (MyDownloadModel) intent.getSerializableExtra("model");

                if(action.equalsIgnoreCase("started")){
                    list.add(myDownloadModel);
                    myDownloadAdapter.notifyItemInserted(list.size() - 1);
                }else if(action.equalsIgnoreCase("resumed")){
                    for (int i = 0; i < list.size(); i++) {
                        if(list.get(i).getId() == myDownloadModel.getId()){
                            list.set(i, myDownloadModel);
                            myDownloadAdapter.notifyItemChanged(i);
                        }
                    }
                }
            }
        };

    }

    private void setMyDownloadAdapter(){
        list = new ArrayList<>();
        list.addAll(db.getAllDownloads());

        myDownloadAdapter = new MyDownloadAdapter(this, list);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(myDownloadAdapter);
    }

    private void downloadFile(String url){
        Intent intent = new Intent(DownloadsActivity.this, MyService.class);
        intent.putExtra("url", url);
        startService(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("Receiver"));

        Intent intent = new Intent(DownloadsActivity.this, MyService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);

        if(isBind){
            unbindService(serviceConnection);
            isBind = false;
        }
    }

}