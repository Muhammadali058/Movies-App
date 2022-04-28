package com.example.moviesapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.example.moviesapp.Adapters.MovieImagesAdapter;
import com.example.moviesapp.Adapters.ResolutionLinksAdapter;
import com.example.moviesapp.HP;
import com.example.moviesapp.Models.Links;
import com.example.moviesapp.Models.Movies;
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
    MovieImagesAdapter movieImagesAdapter;
    List<String> imagesList;
    ResolutionLinksAdapter resolutionLinksAdapter;
    List<Links> resolutionLinks;
    ArrayAdapter linksAdapter;
    List<Links> links;

    String url = "https://extramovies.bike/jersey-2022-full-movie-hindi-1080p-camrip/";
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

        init();
        loadResolutionLinks();
        loadImages();
    }

    private void init(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");

        imagesList = new ArrayList<>();
        movieImagesAdapter = new MovieImagesAdapter(this, imagesList);
        binding.viewPager.setAdapter(movieImagesAdapter);

        resolutionLinks = new ArrayList<>();
        resolutionLinksAdapter = new ResolutionLinksAdapter(this, resolutionLinks, new ResolutionLinksAdapter.OnClickListener() {
            @Override
            public void onClick(int position) {
                Links link = resolutionLinks.get(position);
                getLinksFromResolutionLink(link.getLink());
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
                openLink(link);
            }
        });
    }

    private void loadImages(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Document doc = null;
                try {
                    doc = Jsoup.connect(url).get();

                    Elements ttdbox = doc.getElementsByClass("ttdbox");
                    Elements div = ttdbox.get(0).select("div.separator");

                    imagesList.clear();
                    if(div.size() > 0) {
                        Elements images = div.get(0).getElementsByTag("img");
                        for (Element img : images) {
                            if(img.attr("src").toLowerCase().startsWith("/")) {
                                imagesList.add(HP.website + img.attr("src"));
                            }else {
                                imagesList.add(img.attr("src"));
                            }
                        }
                    }else {
                        imagesList.add(movie.getImageUrl());
                    }

                    MovieActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            movieImagesAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
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
                    Elements paragraphs = ttdbox.get(2).getElementsByTag("p");
                    Elements btnBlue = ttdbox.get(2).getElementsByTag("p").select("p a.blue");

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
                    }else if(btnBlue.size() > 0){
                        Elements anchors = ttdbox.get(2).getElementsByTag("p").select("p a");

                        links.clear();
                        for (Element a : anchors){
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
                    }else if(paragraphs.text().toLowerCase().contains("480p") || paragraphs.text().toLowerCase().contains("720p") || paragraphs.text().toLowerCase().contains("1080p")){
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

    private void getLinksFromResolutionLink(String link){
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

    private void openLink(Links link){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Document doc = null;
                try {
                    doc = Jsoup.connect(link.getLink()).get();

                    Element a = doc.select("a.dl").get(0);

                    if(link.getName().toLowerCase().contains("google drive") || link.getLink().toLowerCase().contains("sharedrive")){
                        openGoogleChrome(a.attr("href"));
                    }else if(link.getName().toLowerCase().contains("torrent")){

                        doc = Jsoup.connect(a.attr("href")).get();
                        Elements aTorrent = doc.select("a.btn-success");
                        if(aTorrent.size() > 0){
                            openWebView(aTorrent.get(0).attr("href"));
                        }else {
                            openWebView(a.attr("href"));
                        }

                    }
                    else {
                        openWebView(a.attr("href"));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void openWebView(String url){
        Intent intent = new Intent(MovieActivity.this, WebViewActivity.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }

    private void openGoogleChrome(String url){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

}