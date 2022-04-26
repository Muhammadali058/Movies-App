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
import com.example.moviesapp.Models.Movies;
import com.example.moviesapp.R;
import com.example.moviesapp.databinding.BannerMoviesHolderBinding;
import com.example.moviesapp.databinding.MoviesHolderBinding;

import java.util.List;

public class BannerMoviesAdapter extends RecyclerView.Adapter<BannerMoviesAdapter.ViewHolder> {

    Context context;
    List<Movies> list;

    public BannerMoviesAdapter(Context context, List<Movies> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.banner_movies_holder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Movies movie = list.get(position);

        Glide.with(context).load(movie.getImageUrl())
                .placeholder(R.drawable.avatar)
                .into(holder.binding.image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MovieActivity.class);
                intent.putExtra("movie", movie);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        BannerMoviesHolderBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = BannerMoviesHolderBinding.bind(itemView);
        }
    }
}
