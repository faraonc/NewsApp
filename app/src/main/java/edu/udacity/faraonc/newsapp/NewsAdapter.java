package edu.udacity.faraonc.newsapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by faraonc on 1/9/18.
 */

class NewsAdapter extends ArrayAdapter {

    private static final String LOG_TAG = NewsAdapter.class.getName();

    NewsAdapter(Context context, ArrayList<News> newsArrayList) {
        super(context, 0, newsArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.news_list_item, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.title = (TextView) listItemView.findViewById(R.id.title);
            viewHolder.author = (TextView) listItemView.findViewById(R.id.author);
            viewHolder.date = (TextView) listItemView.findViewById(R.id.date);
            listItemView.setTag(viewHolder);
        }

        ViewHolder viewHolder = (ViewHolder) listItemView.getTag();
        News item = (News) getItem(position);
        viewHolder.title.setText(item.getTitle());
        viewHolder.author.setText(item.getAuthor());

        Date dateObject = null;
        try {
            dateObject = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX").parse(item.getDate());
        } catch (ParseException e) {
            Log.e(LOG_TAG, "Error in parsing date into a Date object ", e);
        }
        if (dateObject != null) {
            String formattedDate = formatDate(dateObject);
            String formattedTime = formatTime(dateObject);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(formattedDate).append(" ").append(formattedTime);
            viewHolder.date.setText(stringBuilder.toString());
        } else {
            viewHolder.date.setText(R.string.empty);
        }

        return listItemView;
    }

    /* For caching.*/
    private class ViewHolder {
        private TextView title;
        private TextView author;
        private TextView date;
    }

    /**
     * Return the formatted date string (i.e. "Mar 3, 1984") from a Date object.
     */
    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
        return dateFormat.format(dateObject);
    }

    /**
     * Return the formatted date string (i.e. "4:30 PM") from a Date object.
     */
    private String formatTime(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(dateObject);
    }

}
