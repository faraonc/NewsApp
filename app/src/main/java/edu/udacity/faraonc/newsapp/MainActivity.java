package edu.udacity.faraonc.newsapp;


import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
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

    private final static String LOG_TAG = MainActivity.class.getName();

    private final static String URL_QUERY = "http://content.guardianapis.com/search?order-by=newest&show-tags=contributor&rights=developer-community&q=Military%20AND%20Technology%20AND%20Computer%20AND%20Science";
    private final static String API_KEY = "4b18329f-35c4-4dee-acb6-2b74e885c526";
    private final static String API_KEY_PARAM = "api-key";
    private final static String PAGE_SIZE_PARAM = "page-size";
    private final static String ORDER_BY_PARAM = "order-by";

    private final static int NEWS_LOADER_ID = 0;
    //refresh distance from SwipeRefreshLayout
    private static final int REFRESH_DISTANCE = 300;

    private NewsAdapter newsAdapter;
    private ListView newsListView;
    private TextView emptyStateTextView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LoaderManager loaderManager;

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
        display();
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
            if (HttpHelper.isConnected(MainActivity.this)) {
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
    private void display() {

        if (HttpHelper.isConnected(this)) {
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

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(URL_QUERY);
        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter(API_KEY_PARAM, API_KEY);

        // getString retrieves a String value from the preferences. The second parameter is the default value for this preference.
        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));

        uriBuilder.appendQueryParameter(ORDER_BY_PARAM, orderBy);

        String maxNewsDisplayed = sharedPrefs.getString(
                getString(R.string.settings_max_items_key),
                getString(R.string.settings_max_items_default));


        uriBuilder.appendQueryParameter(PAGE_SIZE_PARAM, maxNewsDisplayed);


        return new NewsLoader(this, uriBuilder.toString());
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

    @Override
    // This method initialize the contents of the Activity's options menu.
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Options Menu we specified in XML
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    //TODO implement onResume from Preferences update
}
