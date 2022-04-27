package com.example.moviesapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.example.moviesapp.R;

import java.util.List;

public class MovieImagesAdapter extends PagerAdapter {

    Context context;
    List<String> imagesList;

    public MovieImagesAdapter(Context context, List<String> imagesList) {
        this.context = context;
        this.imagesList = imagesList;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.movie_images_holder, container, false);

        ImageView imageView = view.findViewById(R.id.image);
        Glide.with(context).load(imagesList.get(position))
                .placeholder(R.drawable.loading)
                .into(imageView);

        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return imagesList.size();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout)object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view == (LinearLayout)object);
    }
}
