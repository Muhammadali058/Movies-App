package com.braincoder.moviesapp.Models;

import androidx.annotation.NonNull;

public class Links {
    String name, link;

    public Links() {
    }

    public Links(String name, String link) {
        this.name = name;
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
