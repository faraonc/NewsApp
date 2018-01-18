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
 * The NewsAdapter for the LisView.
 *
 * @author ConardJames
 * @version 010918-01
 */
class NewsAdapter extends ArrayAdapter {

    private static final String LOG_TAG = NewsAdapter.class.getName();

    /**
     * Construct a NewsAdapter with the given list of News.
     *
     * @param context       for resource access
     * @param newsArrayList the list of news.
     */
    NewsAdapter(Context context, ArrayList<News> newsArrayList) {
        super(context, 0, newsArrayList);
    }

    @NonNull
    @Override
    /**
     * Get the view for each entry.
     *
     * @param   position    current index in the adapter.
     * @param   convertView recycled view.
     * @param   parent      contains the view.
     * @return the view for the entry.
     */
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.news_list_item, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.title = (TextView) listItemView.findViewById(R.id.title);
            viewHolder.author = (TextView) listItemView.findViewById(R.id.contributors);
            viewHolder.date = (TextView) listItemView.findViewById(R.id.date);
            viewHolder.section = (TextView) listItemView.findViewById(R.id.section);
            listItemView.setTag(viewHolder);
        }

        ViewHolder viewHolder = (ViewHolder) listItemView.getTag();
        News item = (News) getItem(position);
        viewHolder.title.setText(item.getTitle());
        viewHolder.author.setText(item.getContributors());
        viewHolder.section.setText(item.getSection());
        String dateString = item.getDate();

        try {
            Date dateObject = new SimpleDateFormat(getContext().getString(R.string.date_format)).parse(dateString);
            String formattedDate = formatDate(dateObject);
            String formattedTime = formatTime(dateObject);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(formattedDate).append(" ").append(formattedTime);
            viewHolder.date.setText(stringBuilder.toString());
        } catch (ParseException e) {
            viewHolder.date.setText(dateString);
        }

        return listItemView;
    }

    /**
     * For caching.
     */
    private class ViewHolder {
        private TextView title;
        private TextView author;
        private TextView date;
        private TextView section;
    }

    /**
     * Return the formatted date string (i.e. "Mar 3, 1984") from a Date object.
     *
     * @param dateObject the Date to be used for formatting
     * @return the formatted String representation
     */
    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(getContext().getString(R.string.julian_calendar_format));
        return dateFormat.format(dateObject);
    }

    /**
     * Return the formatted date string (i.e. "4:30 PM") from a Date object.
     *
     * @param dateObject the Date to be used for formatting
     * @return the formatted String representation
     */
    private String formatTime(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat(getContext().getString(R.string.twelve_hour_format));
        return timeFormat.format(dateObject);
    }

}
