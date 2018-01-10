package edu.udacity.faraonc.newsapp;


import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * The NewsLoader for fetching News data.
 *
 * @author ConardJames
 * @version 010918-01
 */
class NewsLoader extends AsyncTaskLoader<List<News>> {

    private String url;

    /**
     * Create a Loader for fetching news data.
     *
     * @param context for resource access
     * @param url     the URL for request
     */
    NewsLoader(Context context, String url) {
        super(context);
        this.url = url;
    }

    @Override
    /**
     * Start the loader.
     */
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    /**
     * Fetch the data in the background.
     *
     * @return the list of News
     */
    public List<News> loadInBackground() {
        if (this.url == null) {
            return null;
        }
        List<News> newsList = JSONHelper.fetch(this.url);
        return newsList;
    }
}