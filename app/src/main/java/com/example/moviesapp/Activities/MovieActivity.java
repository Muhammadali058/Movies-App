package com.example.moviesapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.moviesapp.Adapters.ResolutionLinksAdapter;
import com.example.moviesapp.HP;
import com.example.moviesapp.Models.Links;
import com.example.moviesapp.Models.Movies;
import com.example.moviesapp.R;
import com.example.moviesapp.databinding.ActivityMovieBinding;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MovieActivity extends AppCompatActivity {

    ActivityMovieBinding binding;
    ResolutionLinksAdapter resolutionLinksAdapter;
    List<Links> resolutionLinks;

    ArrayAdapter linksAdapter;
    List<Links> links;
//    String url = "https://extramovies.bike/90-ml-2019-full-movie-hindi-dubbed-hdrip-esubs/"; //
    String url = "https://extramovies.bike/iron-man-3-2013-hq-dual-audio-hindi-english-1080p-bluray-msubs-download/"; // torrent
    Movies movie;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         binding = ActivityMovieBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        movie = (Movies) getIntent().getSerializableExtra("movie");
        url = movie.getLink();
        binding.name.setText(movie.getName());
        Glide.with(this).load(movie.getImageUrl())
                .placeholder(R.drawable.avatar)
                .into(binding.image);

        init();
        loadResolutionLinks();
    }

    private void init(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");

        resolutionLinks = new ArrayList<>();
        resolutionLinksAdapter = new ResolutionLinksAdapter(this, resolutionLinks, new ResolutionLinksAdapter.OnClickListener() {
            @Override
            public void onClick(int position) {
                Links link = resolutionLinks.get(position);
                loadLinksFromResolutionLink(link.getLink());
            }
        });
        binding.resolutionRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.resolutionRecyclerView.setAdapter(resolutionLinksAdapter);

        links = new ArrayList<>();
        linksAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, links);
        binding.linksListView.setAdapter(linksAdapter);

        binding.linksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Links link = (Links) adapterView.getItemAtPosition(i);
                if(resolutionLinks.size() > 0) {
                    getLinkFromResolution(link.getLink());
                }else {
                    getLink(link.getLink());
                }
            }
        });
    }

    private void loadResolutionLinks(){
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Document doc = null;
                try {
                    doc = Jsoup.connect(url).get();

                    Elements ttdbox = doc.getElementsByClass("ttdbox");
                    Elements isTorrent = ttdbox.get(2).getElementsByTag("h4");

                    if(isTorrent.size() > 0){
                        Elements anchors = isTorrent.get(0).getElementsByTag("a");

                        links.clear();

                        Elements torrent = ttdbox.get(2).select("p a.torrent");
                        if(torrent.size() > 0){
                            Links link = new Links("Torrent Download", HP.website + torrent.get(0).attr("href"));
                            links.add(link);
                        }

                        for (Element a: anchors){
                            Links link = new Links(a.text(), HP.website + a.attr("href"));
                            links.add(link);
                        }

                        MovieActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                linksAdapter.notifyDataSetChanged();
                                progressDialog.dismiss();
                            }
                        });
                    }else {
                        Elements paragraphs = ttdbox.get(2).getElementsByTag("p");

                        resolutionLinks.clear();
                        for (int i = 1; i < paragraphs.size(); i++){
                            Element p = paragraphs.get(i);
                            if (i % 2 != 0){
                                if(p.text().toLowerCase().contains("480p")){
                                    Element a = paragraphs.get(i +1).getElementsByTag("a").get(0);
                                    Links link = new Links("480p", a.attr("href"));
                                    resolutionLinks.add(link);
                                }else if(p.text().toLowerCase().contains("720p")){
                                    Element a = paragraphs.get(i +1).getElementsByTag("a").get(0);
                                    Links link = new Links("720p", a.attr("href"));
                                    resolutionLinks.add(link);
                                }else if(p.text().toLowerCase().contains("1080p")){
                                    Element a = paragraphs.get(i +1).getElementsByTag("a").get(0);
                                    Links link = new Links("1080p", a.attr("href"));
                                    resolutionLinks.add(link);
                                }
                            }
                        }

                        MovieActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                resolutionLinksAdapter.notifyDataSetChanged();
                                progressDialog.dismiss();
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private void loadLinksFromResolutionLink(String link){
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Document doc = null;
                try {
                    doc = Jsoup.connect(link).get();

                    Elements elements = doc.select(".entry-content h4 a");

                    links.clear();
                    for (Element a : elements){
                        Links link = new Links(a.text(), HP.website + a.attr("href"));
                        links.add(link);
                    }

                    MovieActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            linksAdapter.notifyDataSetChanged();
                            progressDialog.dismiss();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void getLinkFromResolution(String link){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Document doc = null;
                try {
                    doc = Jsoup.connect(link).get();

                    Element a = doc.select("a.dl").get(0);

                    openGoogleChrome(a.attr("href"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void getLink(String link){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Document doc = null;
                try {
                    doc = Jsoup.connect(link).get();

                    Element a = doc.select("a.dl").get(0);

                    openGoogleChrome(a.attr("href"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void openGoogleChrome(String url){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

}