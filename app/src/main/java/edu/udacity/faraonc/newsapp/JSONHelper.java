package edu.udacity.faraonc.newsapp;


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
final class JSONHelper {

    private static final String LOG_TAG = JSONHelper.class.getSimpleName();
    private static final String RESPONSE_KEY = "response";
    private static final String RESULTS_KEY = "results";
    private static final String WEB_TITLE_KEY = "webTitle";
    private static final String WEB_URL_KEY = "webUrl";
    private static final String WEB_PUBLICATION_DATE_KEY = "webPublicationDate";
    private static final String TAGS_KEY = "tags";
    private static final String AUTHOR_KEY = "webTitle";
    private static final String SECTION_KEY = "sectionName";
    private static final String COMMA = ", ";
    private static final String ANONYMOUS = "Anonymous";
    private static final String NO_DATE = "N.d.";
    private static final String MISCELLANEOUS = "Miscellaneous";

    /*
     * Disable instantiation. Utility class only.
     */
    private JSONHelper() {
    }

    /**
     * Fetch a list news using the url.
     *
     * @param requestUrl the url for the request
     * @return a list of News
     */
    static List<News> fetch(String requestUrl) {
        URL url = toUrl(requestUrl);
        String jsonResponse = null;
        try {
            jsonResponse = sendHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error with makeHttpRequest() ", e);
        }

        List<News> newsList = extractJsonValues(jsonResponse);
        return newsList;
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
    private static String sendHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readInputStream(inputStream);
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
    private static String readInputStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
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
    private static List<News> extractJsonValues(String jsonResponse) {
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }

        List<News> newsList = new ArrayList<>();

        try {
            JSONObject response = new JSONObject(jsonResponse).optJSONObject(RESPONSE_KEY);
            JSONArray results = response.optJSONArray(RESULTS_KEY);

            for (int i = 0; i < results.length(); i++) {
                JSONObject currentResult = results.optJSONObject(i);
                String title = currentResult.optString(WEB_TITLE_KEY);

                //dont add to the news if there is no title
                if(title == null || TextUtils.isEmpty(title)){
                    continue;
                }

                //dont add to the news if there is no url
                String url = currentResult.optString(WEB_URL_KEY);
                if(url == null || TextUtils.isEmpty(url)){
                    continue;
                }

                String date = currentResult.optString(WEB_PUBLICATION_DATE_KEY);
                //set default date if it has no date
                if(date == null || TextUtils.isEmpty(date)){
                    date = NO_DATE;
                }
                String section = currentResult.optString(SECTION_KEY);
                //set default section if it has no section
                if(section == null || TextUtils.isEmpty(section)){
                    section = MISCELLANEOUS;
                }

                //assume no author found
                String author = ANONYMOUS;
                //get all the authors.
                JSONArray authors = currentResult.optJSONArray(TAGS_KEY);
                if (authors != null && authors.length() > 0) {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int j = 0; j < authors.length(); j++) {
                        JSONObject currentAuthor = authors.optJSONObject(j);
                        stringBuilder.append(currentAuthor.optString(AUTHOR_KEY));

                        if (j < (authors.length() - 1)) {
                            stringBuilder.append(COMMA);
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
