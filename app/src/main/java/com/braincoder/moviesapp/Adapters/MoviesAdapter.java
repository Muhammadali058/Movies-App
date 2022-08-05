package com.braincoder.moviesapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.braincoder.moviesapp.Activities.MovieActivity;
import com.braincoder.moviesapp.Models.Movies;
import com.braincoder.moviesapp.R;
import com.braincoder.moviesapp.databinding.MoviesHolderBinding;

import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {

    Context context;
    List<Movies> list;

    public MoviesAdapter(Context context, List<Movies> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.movies_holder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Movies movie = list.get(position);

        holder.binding.name.setText(movie.getName());
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
        MoviesHolderBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = MoviesHolderBinding.bind(itemView);
        }
    }
}
