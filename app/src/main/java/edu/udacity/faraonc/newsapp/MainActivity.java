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

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {

    private final static String URL_QUERY = "http://content.guardianapis.com/search?section=technology&order-by=newest&use-date=published&show-tags=contributor&show-elements=all&page-size=30&rights=developer-community";
    private final static String API_KEY = "&api-key=4b18329f-35c4-4dee-acb6-2b74e885c526";
    public static final String LOG_TAG = MainActivity.class.getName();
    private static final int NEWS_LOADER_ID = 0;
    private static final int REFRESH_DISTANCE = 300;

    NewsAdapter newsAdapter;
    ListView newsListView;
    TextView emptyStateTextView;
    SwipeRefreshLayout swipeRefreshLayout;
    LoaderManager loaderManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.loaderManager = getLoaderManager();
        this.newsListView = (ListView) findViewById(R.id.list);
        this.newsListView.setOnScrollListener(onScrollListener);
        this.newsAdapter = new NewsAdapter(MainActivity.this, new ArrayList<News>());
        this.emptyStateTextView = (TextView) findViewById(R.id.empty_view);
        this.swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        this.swipeRefreshLayout.setDistanceToTriggerSync(REFRESH_DISTANCE);
        this.swipeRefreshLayout.setOnRefreshListener(onRefreshListener);
        newsListView.setEmptyView(this.emptyStateTextView);
        newsListView.setAdapter(this.newsAdapter);
        newsListView.setOnItemClickListener(onItemClickListener);
        checkNetwork();
    }

    private AbsListView.OnScrollListener onScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (newsListView.getChildAt(0) != null) {
                swipeRefreshLayout.setEnabled(newsListView.getFirstVisiblePosition() == 0 && newsListView.getChildAt(0).getTop() == 0);
            }
        }
    };

    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            loaderManager.restartLoader(NEWS_LOADER_ID, null, MainActivity.this);
        }
    };

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            News currentNews = (News) newsAdapter.getItem(position);
            Uri newsUri = Uri.parse(currentNews.getUrl());
            Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);
            startActivity(websiteIntent);
        }
    };

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
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(URL_QUERY).append(API_KEY);
        return new NewsLoader(this, stringBuilder.toString());
    }

    @Override
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
    public void onLoaderReset(Loader<List<News>> loader) {
        this.newsAdapter.clear();
    }

}
