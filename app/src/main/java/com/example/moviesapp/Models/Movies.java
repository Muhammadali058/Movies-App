package com.example.moviesapp.Models;

public class Movies {
    String name, imageUrl, link;

    public Movies() {
    }

    public Movies(String name, String imageUrl, String link) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
