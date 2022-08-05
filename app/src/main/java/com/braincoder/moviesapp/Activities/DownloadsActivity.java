package com.braincoder.moviesapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.braincoder.moviesapp.Adapters.DownloadsAdapter;
import com.braincoder.moviesapp.Downloader.AppDatabase;
import com.braincoder.moviesapp.Models.Downloads;
import com.braincoder.moviesapp.R;
import com.braincoder.moviesapp.databinding.ActivityDownloadsBinding;

import java.util.ArrayList;
import java.util.List;

public class DownloadsActivity extends AppCompatActivity {

    ActivityDownloadsBinding binding;

    DownloadsAdapter downloadsAdapter;
    List<Downloads> list;
    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDownloadsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("Downloadings");

        db = AppDatabase.getInstance(this);
        setDownloadsAdapter();
    }

    private void setDownloadsAdapter(){
        list = new ArrayList<>();

        List<Downloads> downloadsList = db.downloadsDao().getAllDownloads();
        list.addAll(downloadsList);

        downloadsAdapter = new DownloadsAdapter(this, list);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(downloadsAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.downlods_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.torrent:
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.bittorrent.client"));
                startActivity(intent);
                break;
        }
        return true;
    }
}