package com.example.moviesapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviesapp.Downloader.DB;
import com.example.moviesapp.Downloader.MyDownloadListener;
import com.example.moviesapp.Downloader.MyDownloadModel;
import com.example.moviesapp.Downloader.MyService;
import com.example.moviesapp.R;
import com.example.moviesapp.databinding.DownloadHolderBinding;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class MyDownloadAdapter extends RecyclerView.Adapter<MyDownloadAdapter.ViewHolder>{
    Context context;
    List<MyDownloadModel> list;
    MyService myService;
    private DB db;

    public MyDownloadAdapter(Context context, List<MyDownloadModel> list) {
        this.context = context;
        this.list = list;

        this.db = new DB(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.download_holder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MyDownloadModel myDownloadModel = list.get(position);

        holder.binding.fileName.setText(myDownloadModel.getFileName());
        holder.binding.status.setText(myDownloadModel.getStatus());
        holder.binding.progress.setProgress(myDownloadModel.getProgress());
        holder.binding.fileSize.setText(bytesIntoHumanReadable(myDownloadModel.getDownloaded()) + "/" + bytesIntoHumanReadable(myDownloadModel.getFileSize()));

//        if(myDownloadModel.getStatus().equalsIgnoreCase("Downloading")){
//            if(!myService.isExistsInDownloadingList(myDownloadModel)){
//                Log.i("Exists", "false");
//                myService.addToList(myDownloadModel);
//                myService.resume(myDownloadModel);
//            }else {
//                Log.i("Exists", "true");
//            }
//        }

        if(myDownloadModel.isPaused()){
            holder.binding.pauseResumeBtn.setImageResource(R.drawable.ic_resume);
        }else {
            holder.binding.pauseResumeBtn.setImageResource(R.drawable.ic_pause);
        }

        if(myDownloadModel.getStatus().equalsIgnoreCase("completed")){
            holder.binding.pauseResumeBtn.setVisibility(View.GONE);
        }else {
            holder.binding.pauseResumeBtn.setVisibility(View.VISIBLE);
        }

        if(!myDownloadModel.getStatus().equalsIgnoreCase("Completed")) {
            myService.setDownloadListener(myDownloadModel, new MyDownloadListener() {
                @Override
                public void onStart(MyDownloadModel myDownloadModel) {
                    holder.binding.fileName.setText(myDownloadModel.getFileName());
                }

                @Override
                public void onPause(MyDownloadModel myDownloadModel) {
                    holder.binding.status.setText(myDownloadModel.getStatus());
                }

                @Override
                public void onResume(MyDownloadModel myDownloadModel) {
                    holder.binding.status.setText(myDownloadModel.getStatus());
                }

                @Override
                public void onProgress(MyDownloadModel myDownloadModel) {
                    holder.binding.progress.setProgress(myDownloadModel.getProgress());
                    holder.binding.fileSize.setText(bytesIntoHumanReadable(myDownloadModel.getDownloaded()) + "/" + bytesIntoHumanReadable(myDownloadModel.getFileSize()));
                }

                @Override
                public void onComplete(MyDownloadModel myDownloadModel) {
                    holder.binding.status.setText(myDownloadModel.getStatus());

                    holder.binding.pauseResumeBtn.setVisibility(View.GONE);
                }

                @Override
                public void onFailed(String msg) {
                    holder.binding.status.setText(myDownloadModel.getStatus());

                    holder.binding.pauseResumeBtn.setImageResource(R.drawable.ic_resume);
                }
            });
        }

        holder.binding.pauseResumeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myDownloadModel.isPaused()){
                    myService.resume(myDownloadModel);

                    holder.binding.pauseResumeBtn.setImageResource(R.drawable.ic_pause);
                    holder.binding.status.setText("Downloading");
                }else {
                    myService.pause(myDownloadModel);

                    holder.binding.pauseResumeBtn.setImageResource(R.drawable.ic_resume);
                    holder.binding.status.setText("Paused");
                }
            }
        });

        final int pos = position;
        PopupMenu popupMenu = new PopupMenu(context, holder.binding.status);
        popupMenu.inflate(R.menu.menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.remove:
                        db.deleteDownload(myDownloadModel.getId());
                        list.remove(myDownloadModel);
                        notifyItemRemoved(pos);
                        break;
                    case R.id.delete:
                        Log.i("PopupMenu = ", "Deleted");
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
//                if(myDownloadModel.getStatus().equalsIgnoreCase("Completed")) {
//                    popupMenu.show();
//                }

                popupMenu.show();
                return true;
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myDownloadModel.getStatus().equalsIgnoreCase("Completed")) {
                    openFile(myDownloadModel.getFilePath());
                }
            }
        });

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

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public void setService(MyService myService){
        this.myService = myService;
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

    class ViewHolder extends RecyclerView.ViewHolder{

        DownloadHolderBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DownloadHolderBinding.bind(itemView);
        }
    }

}
