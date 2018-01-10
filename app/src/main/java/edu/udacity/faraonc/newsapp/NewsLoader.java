package edu.udacity.faraonc.newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;


import java.util.List;

/**
 * Created by faraonc on 1/9/18.
 */

public class NewsLoader extends AsyncTaskLoader<List<News>> {

    private String url;

    public NewsLoader(Context context, String url) {
        super(context);
        this.url = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<News> loadInBackground() {
        if (this.url == null) {
            return null;
        }
        List<News> newsList = JSONHelper.fetch(this.url);
        return newsList;
    }
}