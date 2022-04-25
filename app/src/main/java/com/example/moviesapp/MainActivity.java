package com.example.moviesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.moviesapp.databinding.ActivityMainBinding;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        new Thread(new Runnable() {
            @Override
            public void run() {

                Document doc = null;
                try {
                    doc = Jsoup.connect("https://extramovies.bike/").get();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.i("Title = ", doc.title());

                Element content = doc.getElementById("content");
                Elements links = content.getElementsByClass("imag");
                Log.i("Links = ", String.valueOf(links.size()));
                for (Element link : links) {
                    Elements movie = link.getElementsByClass("thumbnail");
                    Elements a = movie.get(0).getElementsByTag("a");
                    Log.i("Movie = ", a.get(0).attr("href"));
                }
            }
        }).start();
    }
}