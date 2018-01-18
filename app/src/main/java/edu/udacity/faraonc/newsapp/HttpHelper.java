package edu.udacity.faraonc.newsapp;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for accessing external web services using JSON formatted data.
 *
 * @author ConardJames
 * @version 010918-01
 */
final class HttpHelper {

    private static final String LOG_TAG = HttpHelper.class.getSimpleName();
    private static final int READ_TIMEOUT = 10000;
    private static final int CONNECT_TIMEOUT = 15000;
    private static final int SUCCESS_CODE = 200;

    /*
     * Disable instantiation. Utility class only.
     */
    private HttpHelper() {
    }

    /**
     * Fetch a list news using the url.
     *
     * @param requestUrl the url for the request
     * @return a list of News
     */
    static List<News> fetch(String requestUrl, Context context) {
        URL url = toUrl(requestUrl);
        String jsonResponse = null;
        try {
            jsonResponse = sendHttpRequest(url, context.getString(R.string.get), context.getString(R.string.char_format));
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error with makeHttpRequest() ", e);
        }

        List<News> newsList = extractJsonValues(jsonResponse, context);
        return newsList;
    }

    /**
     * Check the network prior to fetching.
     */
    static boolean isConnected(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Convert the string into a URL object.
     *
     * @param stringUrl the string to be converted
     * @return the URL object of the string
     */
    private static URL toUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with constructing the URL ", e);
        }
        return url;
    }

    /**
     * Send our HTTP request using the URL object.
     *
     * @param url the URL object containing the request
     * @return the JSON response from the server
     * @throws IOException
     */
    private static String sendHttpRequest(URL url, String requestType, String charFormat) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(READ_TIMEOUT);
            urlConnection.setConnectTimeout(CONNECT_TIMEOUT);
            urlConnection.setRequestMethod(requestType);
            urlConnection.connect();

            if (urlConnection.getResponseCode() == SUCCESS_CODE) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readInputStream(inputStream, charFormat);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error retrieving the news JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Read the inputstream from the server.
     *
     * @param inputStream the byte stream from the server
     * @return the response of the server as String
     * @throws IOException
     */
    private static String readInputStream(InputStream inputStream, String charFormat) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName(charFormat));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Extract values from the server's response.
     *
     * @param jsonResponse the server's response.
     * @return a list of News based on the server's response
     */
    private static List<News> extractJsonValues(String jsonResponse, Context context) {
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }

        List<News> newsList = new ArrayList<>();

        try {
            JSONObject response = new JSONObject(jsonResponse).optJSONObject(context.getString(R.string.response_key));
            JSONArray results = response.optJSONArray(context.getString(R.string.results_key));

            for (int i = 0; i < results.length(); i++) {
                JSONObject currentResult = results.optJSONObject(i);
                String title = currentResult.optString(context.getString(R.string.web_title_key));

                //dont add to the news if there is no title
                if (title == null || TextUtils.isEmpty(title)) {
                    continue;
                }

                //dont add to the news if there is no url
                String url = currentResult.optString(context.getString(R.string.web_url_key));
                if (url == null || TextUtils.isEmpty(url)) {
                    continue;
                }

                String date = currentResult.optString(context.getString(R.string.web_publication_date_key));
                //set default date if it has no date
                if (date == null || TextUtils.isEmpty(date)) {
                    date = context.getString(R.string.no_date);
                }
                String section = currentResult.optString(context.getString(R.string.section_key));
                //set default section if it has no section
                if (section == null || TextUtils.isEmpty(section)) {
                    section = context.getString(R.string.miscellaneous);
                    ;
                }

                //assume no author found
                String author = context.getString(R.string.anonymous);
                ;
                //get all the authors.
                JSONArray authors = currentResult.optJSONArray(context.getString(R.string.tags_key));
                if (authors != null && authors.length() > 0) {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int j = 0; j < authors.length(); j++) {
                        JSONObject currentAuthor = authors.optJSONObject(j);
                        stringBuilder.append(currentAuthor.optString(context.getString(R.string.author_key)));

                        if (j < (authors.length() - 1)) {
                            stringBuilder.append(context.getString(R.string.comma_delimiter));
                        }
                    }
                    author = stringBuilder.toString();
                }
                newsList.add(new News(title, author, date, url, section));
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error parsing the news in extractJsonValues ", e);
        }

        return newsList;
    }

}
