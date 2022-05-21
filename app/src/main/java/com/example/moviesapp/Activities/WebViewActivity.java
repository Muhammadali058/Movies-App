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
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.moviesapp.Adapters.MyDownloadAdapter;
import com.example.moviesapp.Downloader.DB;
import com.example.moviesapp.Downloader.MyDownloadModel;
import com.example.moviesapp.Downloader.MyService;
import com.example.moviesapp.databinding.ActivityWebViewBinding;

import java.util.ArrayList;
import java.util.List;

public class WebViewActivity extends AppCompatActivity {

    ActivityWebViewBinding binding;
    String url = "https://cdn.videvo.net/videvo_files/video/premium/video0037/large_watermarked/ghetto%20levels%2001_preview.mp4";
    MyService myService;
    boolean isBind = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWebViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        url = getIntent().getStringExtra("url");

        WebSettings webSettings = binding.webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        binding.webView.loadUrl(url);

        binding.webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                try {
                    if(url.toLowerCase().contains("google drive") || url.toLowerCase().contains("sharedrive")){
                        openGoogleChrome(url);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });

        binding.webView.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
//                Log.i("Link = ", url);
//                Log.i("contentDisposition = ", contentDisposition);
//                Log.i("mimetype = ", mimetype);
//                Log.i("contentLength = ", contentLength + "");

//                Intent i = new Intent(Intent.ACTION_VIEW);
//                i.setData(Uri.parse(url));
//                startActivity(i);

                Intent intent = new Intent(WebViewActivity.this, DownloadsActivity.class);
                intent.putExtra("url", url);
                startActivity(intent);
            }
        });
    }

    private void openGoogleChrome(String url){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
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