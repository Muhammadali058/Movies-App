package com.example.moviesapp.Adapters;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviesapp.Downloader.AppDatabase;
import com.example.moviesapp.Models.Downloads;
import com.example.moviesapp.R;
import com.example.moviesapp.databinding.DownloadsHolderBinding;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class DownloadsAdapter extends RecyclerView.Adapter<DownloadsAdapter.ViewHolder> {

    Context context;
    List<Downloads> list;
    AppDatabase db;

    public DownloadsAdapter(Context context, List<Downloads> list) {
        this.context = context;
        this.list = list;

        db = AppDatabase.getInstance(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.downloads_holder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    @SuppressLint("Range")
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Downloads download = list.get(position);
        Handler handler = new Handler(Looper.getMainLooper());

        if(!download.getStatus().equalsIgnoreCase("Completed")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    DownloadManager downloadManager = (DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);

                    boolean isDownloaded = false;
                    while (!isDownloaded && !download.isPaused()) {
                        DownloadManager.Query query = new DownloadManager.Query();
                        query.setFilterById(download.getDownloadId());

                        Cursor cursor = downloadManager.query(query);
                        cursor.moveToFirst();

                        long downloaded = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                        long totalSize = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

                        if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                            isDownloaded = true;

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    download.setStatus("Completed");
                                    holder.binding.pauseResumeBtn.setVisibility(View.GONE);
                                    db.downloadsDao().updateDownload(download);
                                }
                            });
                        }

                        try {
                            int progress = (int) ((downloaded * 100) / totalSize);
                            String status = getStatusMessage(cursor);

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    download.setProgress(progress);
                                    download.setFileSize(totalSize);
                                    download.setDownloaded(downloaded);

                                    holder.binding.progressBar.setProgress(download.getProgress());
                                    holder.binding.fileSize.setText(bytesIntoHumanReadable(download.getDownloaded()) + "/" + bytesIntoHumanReadable(download.getFileSize()));

                                    if (!download.getStatus().equalsIgnoreCase("Paused") && !download.getStatus().equalsIgnoreCase("Downloading")) {
                                        download.setStatus(status);
                                        holder.binding.status.setText(download.getStatus());

                                        db.downloadsDao().updateDownload(download);
                                    }
                                }
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        cursor.close();
                    }
                }
            }).start();
        }

        holder.binding.filename.setText(download.getFileName());
        holder.binding.status.setText(download.getStatus());
        holder.binding.progressBar.setProgress(download.getProgress());
        holder.binding.fileSize.setText(bytesIntoHumanReadable(download.getDownloaded()) + "/" + bytesIntoHumanReadable(download.getFileSize()));

        if(download.isPaused()){
            holder.binding.pauseResumeBtn.setImageResource(R.drawable.ic_resume);
        }else {
            holder.binding.pauseResumeBtn.setImageResource(R.drawable.ic_pause);
        }

        if(download.getStatus().equalsIgnoreCase("Completed")){
            holder.binding.pauseResumeBtn.setVisibility(View.GONE);
        }else {
            holder.binding.pauseResumeBtn.setVisibility(View.VISIBLE);
        }

        holder.binding.pauseResumeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(download.isPaused()){
                    download.setPaused(false);

                    holder.binding.pauseResumeBtn.setImageResource(R.drawable.ic_pause);
                    holder.binding.status.setText("Downloading");
                    download.setStatus("Downloading");
                    notifyItemChanged(position);

                    if(!resumeDownload(download)){
                        Toast.makeText(context, "Failed to resume download", Toast.LENGTH_SHORT).show();
                    }else {
                        db.downloadsDao().updateDownload(download);
                    }
                }else{
                    download.setPaused(true);

                    holder.binding.pauseResumeBtn.setImageResource(R.drawable.ic_resume);
                    holder.binding.status.setText("Paused");
                    download.setStatus("Paused");
//                    notifyItemChanged(position);

                    if(!pauseDownload(download)){
                        Toast.makeText(context, "Failed to pause download", Toast.LENGTH_SHORT).show();
                    }else {
                        db.downloadsDao().updateDownload(download);
                    }
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(download.getStatus().equalsIgnoreCase("Completed")) {
                    openFile(download.getFilePath());
                }
            }
        });

        PopupMenu popupMenu = new PopupMenu(context, holder.binding.status);
        popupMenu.inflate(R.menu.menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.remove:
                        list.remove(download);
                        db.downloadsDao().deleteDownload(download);
                        if(list.size() == 1)
                            notifyDataSetChanged();
                        else
                            notifyItemRemoved(position);

                        break;
                    case R.id.delete:
                        File file = new File(download.getFilePath());
                        file.delete();

                        list.remove(download);
                        db.downloadsDao().deleteDownload(download);
                        if(list.size() == 1)
                            notifyDataSetChanged();
                        else
                            notifyItemRemoved(position);

                        break;
                    default:
                        return true;
                }

                return true;
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
//                if(download.getStatus().equalsIgnoreCase("Completed")) {
//                    popupMenu.show();
//                }
                popupMenu.show();
                return true;
            }
        });
    }

    @SuppressLint("Range")
    private String getStatusMessage(Cursor cursor){
        switch (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))){
            case DownloadManager.STATUS_PAUSED:
                return "Paused";
            case DownloadManager.STATUS_FAILED:
                return "Failed";
            case DownloadManager.STATUS_RUNNING:
                return "Downloading";
            case DownloadManager.STATUS_SUCCESSFUL:
                return "Completed";
            case DownloadManager.STATUS_PENDING:
                return "Pending";
            default:
                return "Unknown";
        }
    }

    private boolean pauseDownload(Downloads download){
        int updatedRow = 0;
        ContentValues contentValues = new ContentValues();
        contentValues.put("control", 1);

        try {
            updatedRow = context.getContentResolver().update(Uri.parse("content://downloads/my_downloads"), contentValues, "title=?", new String[]{download.getFileName()});
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return 0<updatedRow;
    }

    private boolean resumeDownload(Downloads download){
        int updatedRow = 0;
        ContentValues contentValues = new ContentValues();
        contentValues.put("control", 0);

        try {
            updatedRow = context.getContentResolver().update(Uri.parse("content://downloads/my_downloads"), contentValues, "title=?", new String[]{download.getFileName()});
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return 0<updatedRow;
    }

    public void openFile(String filePath){
        File file = new File(filePath);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
            intent.setData(uri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            Uri uri = Uri.fromFile(file);
            intent.setData(uri);
        }

        context.startActivity(intent);
    }

    private String bytesIntoHumanReadable(long bytes) {
        double kilobyte = 1024;
        double megabyte = kilobyte * 1024;
        double gigabyte = megabyte * 1024;
        double terabyte = gigabyte * 1024;

        NumberFormat formatter = new DecimalFormat("#0.00");
        if ((bytes >= 0) && (bytes < kilobyte)) {
            return bytes + " B";

        } else if ((bytes >= kilobyte) && (bytes < megabyte)) {
            double ret = (bytes / kilobyte);
            return formatter.format(ret) + " KB";

        } else if ((bytes >= megabyte) && (bytes < gigabyte)) {
            double ret = (bytes / megabyte);
            return formatter.format(ret) + " MB";

        } else if ((bytes >= gigabyte) && (bytes < terabyte)) {
            double ret = (bytes / gigabyte);
            return formatter.format(ret) + " GB";

        } else if (bytes >= terabyte) {
            double ret = (bytes / terabyte);
            return formatter.format(ret)+ " TB";

        } else {
            return bytes + " Bytes";
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        DownloadsHolderBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DownloadsHolderBinding.bind(itemView);
        }
    }
}
