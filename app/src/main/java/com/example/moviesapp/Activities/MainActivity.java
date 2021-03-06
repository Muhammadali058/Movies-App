package com.example.moviesapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moviesapp.Adapters.BannerMoviesAdapter;
import com.example.moviesapp.Adapters.MoviesAdapter;
import com.example.moviesapp.Downloader.MyService;
import com.example.moviesapp.HP;
import com.example.moviesapp.Models.Movies;
import com.example.moviesapp.databinding.ActivityMainBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FirebaseDatabase database;
    MoviesAdapter moviesAdapter;
    BannerMoviesAdapter bannerMoviesAdapter;
    List<Movies> bannerList;
    List<Movies> list;
    private int page = 1;
    private int searchPage = 1;
    ProgressDialog progressDialog;

    MyService myService;
    ServiceConnection serviceConnection;
    boolean isBind = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                MyService.MyServiceBinder binder = (MyService.MyServiceBinder) iBinder;
                myService = binder.getService();
                isBind = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };


        init();

        database.getReference("website").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String website = snapshot.getValue(String.class);
                    HP.website = website;
                    loadBannerMovies();
                    loadMovies();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void init(){
        database = FirebaseDatabase.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching...");

        bannerList = new ArrayList<>();
        bannerMoviesAdapter = new BannerMoviesAdapter(this, bannerList);
        binding.bannerRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.bannerRecyclerView.setAdapter(bannerMoviesAdapter);

        list = new ArrayList<>();
        moviesAdapter = new MoviesAdapter(this, list);
        binding.recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        binding.recyclerView.setAdapter(moviesAdapter);

        binding.prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prevBtnClick();
            }
        });

        binding.prevLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prevBtnClick();
            }
        });

        binding.nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextBtnClick();
            }
        });

        binding.nextLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextBtnClick();
            }
        });

        binding.homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.searchTB.getText().length() > 0) {
                    searchPage = 1;
                    searchMovies();
                }else {
                    page = 1;
                    loadMovies();
                }
            }
        });

        binding.searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchMovies();
            }
        });

        binding.searchTB.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_SEARCH){
                    searchMovies();
                    return true;
                }
                return false;
            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.backBtn.setVisibility(View.GONE);
                binding.searchTB.setText("");

                loadMovies();
            }
        });

        binding.downloadsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DownloadsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void prevBtnClick() {
        if(binding.searchTB.getText().length() > 0) {
            if(searchPage > 1) {
                searchPage--;
                searchMovies();
            }
        }else {
            if(page > 1) {
                page--;
                loadMovies();
            }
        }
    }

    private void nextBtnClick() {
        if(binding.searchTB.getText().length() > 0) {
            searchPage++;
            searchMovies();
        }else {
            page++;
            loadMovies();
        }
    }

    private void loadBannerMovies(){
        progressDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Document doc = null;
                try {
                    doc = Jsoup.connect(HP.website).get();
                    Element element = doc.getElementById("slider2");
                    Elements items = element.getElementsByClass("item");

                    bannerList.clear();
                    for (Element item : items) {
                        Elements div = item.select("div.imagens");
                        Elements a = div.get(0).getElementsByTag("a");
                        Elements img = a.get(0).getElementsByTag("img");

                        Element imdb = div.select("span.imdb").get(0);

                        Movies movie = new Movies(
                                a.get(0).attr("title") + " (IMDB " + imdb.text() + ")",
                                img.get(0).attr("src"),
                                a.get(0).attr("href")
                        );

                        bannerList.add(movie);
                    }

                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            bannerMoviesAdapter.notifyDataSetChanged();
                            progressDialog.dismiss();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void loadMovies(){
        progressDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Document doc = null;
                try {
                    doc = Jsoup.connect(HP.website + "/page/" + page).get();
                    loadMovies(doc);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void searchMovies(){
        if(binding.searchTB.getText().length() > 0) {
            progressDialog.show();
            String text = binding.searchTB.getText().toString();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Document doc = null;
                    try {
                        doc = Jsoup.connect(HP.website +"/page/" + searchPage + "/?s=" + text).get();
                        loadMovies(doc);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    private void loadMovies(Document doc){
        Element content = doc.getElementById("content");
        Elements links = content.getElementsByClass("imag");

        list.clear();
        for (Element link : links) {
            Elements movieElem = link.getElementsByClass("thumbnail");
            Elements a = movieElem.get(0).getElementsByTag("a");
            Elements image = a.get(0).getElementsByTag("img");

            Element imdb = link.select("span.imdb").get(0);

            Movies movie = new Movies(
                    a.get(0).attr("title") + " (IMDB " + imdb.text() + ")",
                    image.get(0).attr("src"),
                    a.get(0).attr("href")
            );

            list.add(movie);
        }

        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                moviesAdapter.notifyDataSetChanged();
                binding.recyclerView.scrollToPosition(0);
                if(binding.searchTB.getText().length() > 0) {
                    binding.backBtn.setVisibility(View.VISIBLE);
                }else {
                    binding.backBtn.setVisibility(View.GONE);
                }
                hideKeyboard();
                progressDialog.dismiss();
            }
        });
    }

    private void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        if(imm.isAcceptingText()) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

//        Intent intent = new Intent(MainActivity.this, MyService.class);
//        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();

//        if(isBind){
//            unbindService(serviceConnection);
//            isBind = false;
//        }
    }

}