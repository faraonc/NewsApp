package edu.udacity.faraonc.newsapp;


import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * The main activity of the application.
 *
 * @author ConardJames
 * @version 010918-01
 */
public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {

    private final static String URL_QUERY = "http://content.guardianapis.com/search?order-by=newest&show-tags=contributor&page-size=30&rights=developer-community&q=Military%20AND%20Technology%20AND%20Computer%20AND%20Science";
    private final static String API_KEY = "&api-key=4b18329f-35c4-4dee-acb6-2b74e885c526";
    public static final String LOG_TAG = MainActivity.class.getName();
    private static final int NEWS_LOADER_ID = 0;
    //refresh distance from SwipeRefreshLayout
    private static final int REFRESH_DISTANCE = 300;

    NewsAdapter newsAdapter;
    ListView newsListView;
    TextView emptyStateTextView;
    SwipeRefreshLayout swipeRefreshLayout;
    LoaderManager loaderManager;

    @Override
    /**
     * Create the view and set the state for the activity.
     *
     * @param savedInstanceState the saved state of the app if not null
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.loaderManager = getLoaderManager();
        this.newsListView = (ListView) findViewById(R.id.list);

        //prevents conflict when scrolling up between the SwipeRefreshLayout and ListView
        this.newsListView.setOnScrollListener(onScrollListener);

        this.newsAdapter = new NewsAdapter(MainActivity.this, new ArrayList<News>());
        this.emptyStateTextView = (TextView) findViewById(R.id.empty_view);
        this.swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);

        //set the distance for the refresh
        this.swipeRefreshLayout.setDistanceToTriggerSync(REFRESH_DISTANCE);
        this.swipeRefreshLayout.setOnRefreshListener(onRefreshListener);

        newsListView.setEmptyView(this.emptyStateTextView);
        newsListView.setAdapter(this.newsAdapter);
        newsListView.setOnItemClickListener(onItemClickListener);
        checkNetwork();
    }

    /*
     * Prevents the scroll up conflict between ListView and SwipeRefreshLayout
     */
    private AbsListView.OnScrollListener onScrollListener = new AbsListView.OnScrollListener() {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        /**
         * Use SwipeRefreshLayout when at scrolling at the first child.
         *
         * @param view the AbsListView
         * @param firstVisibleItem the first item visible
         * @param visibleItemCount the total of item currently visible
         * @param totalItemCount the total item count
         */
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (newsListView.getChildAt(0) != null) {
                swipeRefreshLayout.setEnabled(newsListView.getFirstVisiblePosition() == 0 && newsListView.getChildAt(0).getTop() == 0);
            }
        }
    };

    /*
     * Refresh and update the news list.
     */
    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {

        @Override
        /**
         * Check internet and restart the loader to fetch new data.
         */
        public void onRefresh() {
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {
                loaderManager.restartLoader(NEWS_LOADER_ID, null, MainActivity.this);
            } else {
                View loadingIndicator = findViewById(R.id.loading_spinner);
                loadingIndicator.setVisibility(View.GONE);
                newsAdapter.clear();
                emptyStateTextView.setText(R.string.no_internet_connection);
                swipeRefreshLayout.setRefreshing(false);
            }

        }
    };

    /*
     * Set the listener when a list item is clicked.
     */
    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        /**
         * Start an implicit activity using the url from the News.
         *
         * @param adapterView the adapter view
         * @param view the current view
         * @param position the current position
         * @param l the id
         */
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            News currentNews = (News) newsAdapter.getItem(position);
            Uri newsUri = Uri.parse(currentNews.getUrl());
            Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);
            startActivity(websiteIntent);
        }
    };

    /**
     * Check the network prior to fetching.
     */
    private void checkNetwork() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            this.loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        } else {
            View loadingIndicator = findViewById(R.id.loading_spinner);
            loadingIndicator.setVisibility(View.GONE);
            this.emptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    @Override
    /**
     * Returns a Loader if it does not exist yet.
     *
     * @param id the loader id
     * @param bundle bundled arguments
     * @return the loader for our News data
     */
    public Loader<List<News>> onCreateLoader(int id, Bundle bundle) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(URL_QUERY).append(API_KEY);
        return new NewsLoader(this, stringBuilder.toString());
    }

    @Override
    /**
     * Update our UI after Loader fetches data.
     *
     * @param loader the news loader
     * @param newsList the result from the loader
     */
    public void onLoadFinished(Loader<List<News>> loader, List<News> newsList) {

        View loadingIndicator = findViewById(R.id.loading_spinner);
        loadingIndicator.setVisibility(View.GONE);

        this.emptyStateTextView.setText(R.string.no_news);
        this.newsAdapter.clear();
        if (newsList != null && !newsList.isEmpty()) {
            this.newsAdapter.addAll(newsList);
        }
        this.swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    /**
     * Reset our adapter on loader reset.
     *
     * @param loader the news loader
     */
    public void onLoaderReset(Loader<List<News>> loader) {
        this.newsAdapter.clear();
    }

}
