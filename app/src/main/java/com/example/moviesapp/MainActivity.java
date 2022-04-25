package com.example.moviesapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;

import com.example.moviesapp.Adapters.MoviesAdapter;
import com.example.moviesapp.Models.Movies;
import com.example.moviesapp.databinding.ActivityMainBinding;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    MoviesAdapter moviesAdapter;
    List<Movies> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Document doc = null;
                try {
                    doc = Jsoup.connect("https://extramovies.bike/").get();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Element content = doc.getElementById("content");
                Elements links = content.getElementsByClass("imag");

                for (Element link : links) {
                    Elements movieElem = link.getElementsByClass("thumbnail");
                    Elements a = movieElem.get(0).getElementsByTag("a");
                    Elements image = a.get(0).getElementsByTag("img");

                    Movies movie = new Movies(
                            a.get(0).attr("title"),
                            image.get(0).attr("src"),
                            a.get(0).attr("href")
                    );

                    list.add(movie);
                }

                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        moviesAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    private void init(){
        list = new ArrayList<>();
        moviesAdapter = new MoviesAdapter(this, list);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(moviesAdapter);
    }

}