package com.example.moviesapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.moviesapp.Activities.MovieActivity;
import com.example.moviesapp.Models.Links;
import com.example.moviesapp.Models.Movies;
import com.example.moviesapp.R;
import com.example.moviesapp.databinding.MoviesHolderBinding;
import com.example.moviesapp.databinding.ResolutionLinksHolderBinding;

import java.util.List;

public class ResolutionLinksAdapter extends RecyclerView.Adapter<ResolutionLinksAdapter.ViewHolder> {

    Context context;
    List<Links> list;
    OnClickListener onClickListener;

    public ResolutionLinksAdapter(Context context, List<Links> list, OnClickListener onClickListener) {
        this.context = context;
        this.list = list;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.resolution_links_holder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Links link = list.get(position);

        holder.binding.name.setText(link.getName());

        final int pos = position;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.onClick(pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ResolutionLinksHolderBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ResolutionLinksHolderBinding.bind(itemView);
        }
    }

    public interface OnClickListener{
        void onClick(int position);
    }
}
