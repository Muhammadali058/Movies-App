package com.example.moviesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.moviesapp.databinding.ActivityMovieBinding;
import com.ixuea.android.downloader.DownloadService;
import com.ixuea.android.downloader.callback.DownloadManager;
import com.ixuea.android.downloader.domain.DownloadInfo;
import com.ixuea.android.downloader.exception.DownloadException;

import java.io.File;
import java.io.IOException;

public class MovieActivity extends AppCompatActivity {

    ActivityMovieBinding binding;
    DownloadManager downloadManager;
    private String spiderMan = "https://doc-0c-8g-docs.googleusercontent.com/docs/securesc/fdiafrf2q9sga740asgklkhrml5juf33/1tqlej7dqmih2u9ggit498n1k5s4gs9t/1650936900000/01490653556659528805/12477839342017101161/1Ql1uKe3x4oKPSPshjViR1UjwSCR8whO-?ax=ACxEAsZcHI7ljFLe0RP-KhmhGsqaa-cPVh24RbgO_Uz-2BQY2sdDfBq2Ra-0JM7QpWG8nZAqHfOfqmWg91tQqnyYWaaTDtDrhNj-rcUh7pPhrNOYtsl0656nF5_-HuD_BEkQICr7Z0sIGeAQ-B_Qz7JyvEU8MKlbk_W7zG1Kv6xbEyvDe2jLqkAcQFkxSBG-EhzXwmgvm3m403dVLBAGcK3WlyQEaVD0BKWKVb4AlUin9T8KEACqbTgHNXbENgqj9lCY2Oyv2Njbq_Y2MIrn5vV-3H93X7NOW7x2FFfclvpCjxfuPcT9vLtuAON4HXjn7qavXgxHe2xWsA6YB-da75q8goKaAtlFmjQPBqFdL6JVnyCGGB6_ccyNEZrp0A3XAFN4oZATTmYI-pavLYwcK5s0CSqc63pumuunGfgOPYvLAXX1ZDXVRJ35ho5GHygMlGlSYPxmnW30bxNT-JtOjQuzZ-lkaV3oNbafAUEedHubIKgBykGuZnaZSFALOa5tZbfiWnbrP7qac5S7NzNe8z5KRcnZhS4wha-soJZLGt7nO8DCZSf53-OOoAp_3_cXO5n6UytoQJKV2Z8Qyqq9i7bZd0AMR7DhlehPPX8aPaYReZ1fPkDZEc9a_Q4yEcOlqosdRMq0NRzezOHbD2Jkp-GTR5tOUmyi5afOiKcSYDpZX4DEWldeD-a1GzqpjshKbPZvE_TZpPTo2wedx-cRfmQed8eXk_NpPMaz7gyNF_pEDzK992faHcs&authuser=0&nonce=76v370tj5o8f8&user=12477839342017101161&hash=bf7gl2ensiod59fpp3sf65grvg62nhdu";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         binding = ActivityMovieBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        downloadManager = (DownloadManager) DownloadService.getDownloadManager(getApplicationContext());



//        //create download info set download uri and save path.
//        File targetFile = new File(getExternalFilesDir(null) + "/movie.mp4");
//        if(!targetFile.exists()){
//            try {
//                targetFile.createNewFile();
//                Toast.makeText(MovieActivity.this, "File Created", Toast.LENGTH_SHORT).show();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        Log.i("Target path = ", targetFile.getAbsolutePath());
//        final DownloadInfo downloadInfo = new DownloadInfo.Builder().setUrl(Uri.parse(spiderMan).toString())
//                .setPath(targetFile.getAbsolutePath())
//                .build();
//
//        //set download callback.
//        downloadInfo.setDownloadListener(new com.ixuea.android.downloader.callback.DownloadListener() {
//            @Override
//            public void onStart() {
//                Log.i("Download Started = ", "true");
//            }
//
//            @Override
//            public void onWaited() {
//                Log.i("OnWaited = ", "true");
//            }
//
//            @Override
//            public void onPaused() {
//                Log.i("OnPaused = ", "true");
//            }
//
//            @Override
//            public void onDownloading(long progress, long size) {
//                Log.i("Downloading = ", String.valueOf(progress) + " / " + String.valueOf(size));
//            }
//
//            @Override
//            public void onRemoved() {
//                Log.i("OnRemoved = ", "true");
//            }
//
//            @Override
//            public void onDownloadSuccess() {
//                Log.i("OnSuccess = ", "true");
//            }
//
//            @Override
//            public void onDownloadFailed(DownloadException e) {
//                Log.i("OnFailed = ", e.getMessage());
//            }
//        });
//
//        //submit download info to download manager.
//        downloadManager.download(downloadInfo);


        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(spiderMan));
        startActivity(i);

        WebSettings webSettings = binding.webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        binding.webView.setWebViewClient(new WebViewClient());
        binding.webView.loadUrl("https://extramovies.bike/");

        binding.webView.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);

                Log.i("Url = ", url);
                Log.i("userAgent = ", userAgent);
                Log.i("contentDisposition = ", contentDisposition);
                Log.i("mimetype = ", mimetype);

//                //create download info set download uri and save path.
//                File targetFile = new File(getExternalFilesDir(null) + "/movie.mp4");
//                if(!targetFile.exists()){
//                    try {
//                        targetFile.createNewFile();
//                        Toast.makeText(MovieActivity.this, "File Created", Toast.LENGTH_SHORT).show();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//                final DownloadInfo downloadInfo = new DownloadInfo.Builder().setUrl(url)
//                        .setPath(targetFile.getAbsolutePath())
//                        .build();
//
//                //set download callback.
//                downloadInfo.setDownloadListener(new com.ixuea.android.downloader.callback.DownloadListener() {
//                    @Override
//                    public void onStart() {
//                        Log.i("Download Started = ", "true");
//                    }
//
//                    @Override
//                    public void onWaited() {
//                        Log.i("OnWaited = ", "true");
//                    }
//
//                    @Override
//                    public void onPaused() {
//                        Log.i("OnPaused = ", "true");
//                    }
//
//                    @Override
//                    public void onDownloading(long progress, long size) {
//                        Log.i("Downloading = ", String.valueOf(progress) + " / " + String.valueOf(size));
//                    }
//
//                    @Override
//                    public void onRemoved() {
//                        Log.i("OnRemoved = ", "true");
//                    }
//
//                    @Override
//                    public void onDownloadSuccess() {
//                        Log.i("OnSuccess = ", "true");
//                    }
//
//                    @Override
//                    public void onDownloadFailed(DownloadException e) {
//                        Log.i("OnFailed = ", e.getMessage());
//                    }
//                });
//
//                //submit download info to download manager.
//                downloadManager.download(downloadInfo);
//
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (binding.webView.canGoBack()) {
                        binding.webView.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

}