package com.braincoder.moviesapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.braincoder.moviesapp.Downloader.AppDatabase;
import com.braincoder.moviesapp.Models.Downloads;
import com.braincoder.moviesapp.databinding.ActivityWebViewBinding;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class WebViewActivity extends AppCompatActivity {

    ActivityWebViewBinding binding;
//    String url = "https://cdn.videvo.net/videvo_files/video/premium/video0037/large_watermarked/ghetto%20levels%2001_preview.mp4";
    String url = "http://extralinks.casa/more/torrent/f2ae14d40c628694972da8df6ccf185bcc6e7ac3";
    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWebViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        url = getIntent().getStringExtra("url");
        db = AppDatabase.getInstance(this);
        init();
    }

    private void init(){
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

//                    else if(url.toLowerCase().contains("torrent")){
//                        Log.i("Torrent link", "True");
//
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Log.i("Inside ", "True");
//
//                                Document doc = null;
//                                try {
//                                    doc = Jsoup.connect(url).get();
//
//                                    try {
//                                        Elements torrents = doc.select("a.btn-success");
//                                        String torrentLink = HP.website + torrents.get(0).attr("href");
//                                        Log.i("Link = ", torrentLink);
//                                    }catch (Exception e){
//                                        e.printStackTrace();
//                                        Log.i("Inner", "True");
//                                    }
//
//                                    WebViewActivity.this.runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//
//                                        }
//                                    });
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                    Log.i("Outer", "True");
//                                }
//                            }
//                        }).start();
//                    }

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

                String fileName = getFilenameFromContentDisposition(url, contentDisposition);
//                downloadFile(url, fileName);
                downloadFile(url);
            }
        });
    }

    private String getFilenameFromContentDisposition(String url, String contentDisposition){
        String fileName = "Movie.mp4";

        if(url.toLowerCase().contains("torrent")){
            fileName = URLUtil.guessFileName(url, null, "*/*");
            return fileName;
        }

        if(TextUtils.isEmpty(contentDisposition)){
            fileName = contentDisposition.split("filename=")[1];
            if (fileName.contains("\"")) {
                fileName = fileName.replaceAll("\"", "");
            }
        }else {
            fileName = URLUtil.guessFileName(url, null, "*/*");
            if(fileName.contains(".bin")){
                fileName = fileName.replace(".bin", ".mp4");
            }
        }

        return fileName;
    }

    public interface OnFilenameReceivedListener{
        void onFilenameReceived(String filename);
    }

    public class DownloadFileTasK extends AsyncTask<String, String, String>{
        OnFilenameReceivedListener onFilenameReceivedListener;

        public DownloadFileTasK(OnFilenameReceivedListener onFilenameReceivedListener) {
            this.onFilenameReceivedListener = onFilenameReceivedListener;
        }

        @Override
        protected String doInBackground(String... urls) {
            String filename = getFilename(urls[0]);
            return filename;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            onFilenameReceivedListener.onFilenameReceived(s);
        }

        private String getFilename(String url){
            String fileName = "null";

            if(url.toLowerCase().contains("torrent")){
                fileName = URLUtil.guessFileName(url, null, "*/*");
                return fileName;
            }

            try {
                URL url1 = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) url1.openConnection();
                connection.connect();

                if (connection.getResponseCode() == 200) {
                    String raw = connection.getHeaderField("Content-Disposition");
                    if (raw != null && raw.indexOf("=") != -1) {
                        fileName = raw.split("filename=")[1];
                        if (fileName.contains("\"")) {
                            fileName = fileName.replaceAll("\"", "");
                        }
                    }else {
                        fileName = guessFilename(url);
                    }
                }else {
                    fileName = guessFilename(url);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return fileName;
        }

        private String guessFilename(String url){
            String fileName = URLUtil.guessFileName(url, null, "video/*");
            if(fileName.contains(".bin")){
                fileName = fileName.replace(".bin", ".mp4");
            }

            return fileName;
        }

    }

    @SuppressLint("Range")
    private void downloadFile(String url, String fileName){
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Movies");
        if(!dir.exists())
            dir.mkdir();

        File file = new File(dir.getPath() + "/" + fileName);

        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            request = new DownloadManager.Request(Uri.parse(url))
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)// Visibility of the download Notification
                    .setDestinationUri(Uri.fromFile(file))// Uri of the destination file
                    .setTitle(fileName)// Title of the Download Notification
                    .setDescription("Downloading")// Description of the Download Notification
                    .setRequiresCharging(false)// Set if charging is required to begin the download
                    .setAllowedOverMetered(true)// Set if download is allowed on Mobile network
                    .setAllowedOverRoaming(true);
        }else {
            request = new DownloadManager.Request(Uri.parse(url))
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)// Visibility of the download Notification
                    .setDestinationUri(Uri.fromFile(file))// Uri of the destination file
                    .setTitle(fileName)// Title of the Download Notification
                    .setDescription("Downloading")// Description of the Download Notification
                    .setAllowedOverMetered(true)// Set if download is allowed on Mobile network
                    .setAllowedOverRoaming(true);
        }

        long downloadId = downloadManager.enqueue(request);

        Downloads download = new Downloads();
        download.setDownloadId(downloadId);
        download.setUrl(url);
        download.setFileName(fileName);
        download.setFilePath(file.getPath());
        download.setFileSize(0);
        download.setDownloaded(0);
        download.setProgress(0);
        download.setStatus("Downloading");
        download.setPaused(false);

        long id = db.downloadsDao().insertDownload(download);
        download.setId(id);

        Intent intent = new Intent(WebViewActivity.this, DownloadsActivity.class);
        startActivity(intent);
        finish();
    }

    @SuppressLint("Range")
    private void downloadFile(String url){
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Movies");
        if(!dir.exists())
            dir.mkdir();

        new DownloadFileTasK(new OnFilenameReceivedListener() {
            @Override
            public void onFilenameReceived(String fileName) {
                File file = new File(dir.getPath() + "/" + fileName);

                DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                DownloadManager.Request request = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    request = new DownloadManager.Request(Uri.parse(url))
                            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)// Visibility of the download Notification
                            .setDestinationUri(Uri.fromFile(file))// Uri of the destination file
                            .setTitle(fileName)// Title of the Download Notification
                            .setDescription("Downloading")// Description of the Download Notification
                            .setRequiresCharging(false)// Set if charging is required to begin the download
                            .setAllowedOverMetered(true)// Set if download is allowed on Mobile network
                            .setAllowedOverRoaming(true);
                }else {
                    request = new DownloadManager.Request(Uri.parse(url))
                            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)// Visibility of the download Notification
                            .setDestinationUri(Uri.fromFile(file))// Uri of the destination file
                            .setTitle(fileName)// Title of the Download Notification
                            .setDescription("Downloading")// Description of the Download Notification
                            .setAllowedOverMetered(true)// Set if download is allowed on Mobile network
                            .setAllowedOverRoaming(true);
                }

                long downloadId = downloadManager.enqueue(request);

                Downloads download = new Downloads();
                download.setDownloadId(downloadId);
                download.setUrl(url);
                download.setFileName(fileName);
                download.setFilePath(file.getPath());
                download.setFileSize(0);
                download.setDownloaded(0);
                download.setProgress(0);
                download.setStatus("Downloading");
                download.setPaused(false);

                long id = db.downloadsDao().insertDownload(download);
                download.setId(id);

                Intent intent = new Intent(WebViewActivity.this, DownloadsActivity.class);
                startActivity(intent);
                finish();
            }
        }).execute(url);
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