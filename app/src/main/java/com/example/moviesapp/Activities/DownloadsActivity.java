package com.example.moviesapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;

import com.example.moviesapp.Adapters.DownloadsAdapter;
import com.example.moviesapp.Downloader.AppDatabase;
import com.example.moviesapp.Models.Downloads;
import com.example.moviesapp.databinding.ActivityDownloadsBinding;

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

}