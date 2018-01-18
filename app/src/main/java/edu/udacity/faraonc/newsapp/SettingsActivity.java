package edu.udacity.faraonc.newsapp;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Set;

/**
 * The settings activity to change user preferences.
 *
 * @author ConardJames
 * @version 011718-01
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    /**
     * Create the view and set the state for the activity.
     *
     * @param savedInstanceState the saved state of the app if not null
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    /**
     * The preference fragment to save user settings.
     */
    public static class NewsPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

        @Override
        /**
         * Create the view and set the state for the activity.
         *
         * @param savedInstanceState the saved state of the app if not null
         */
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            //add search news content
            Preference searchNewsContent = findPreference(getString(R.string.settings_search_news_key));
            bindPreferenceSummaryToValue(searchNewsContent);

            //add news media content
            Preference mediaContent = findPreference(getString(R.string.settings_media_content_key));
            bindPreferenceSummaryToValue(mediaContent);

            //add production origin
            Preference productionOrigin = findPreference(getString(R.string.settings_production_origin_key));
            bindMultiPreferenceListSummaryToValue(productionOrigin);

            //add maximum displayed news
            Preference maxNewsDisplayed = findPreference(getString(R.string.settings_max_items_key));
            bindPreferenceSummaryToValue(maxNewsDisplayed);

            //add order-by
            Preference orderBy = findPreference(getString(R.string.settings_order_by_key));
            bindPreferenceSummaryToValue(orderBy);

            //add order-date
            Preference orderDate = findPreference(getString(R.string.settings_order_date_key));
            bindPreferenceSummaryToValue(orderDate);

            //add a button for resetting preferences
            Preference button = findPreference(getString(R.string.settings_default_key));
            button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        /**
                         * Handles the buttons from the dialog box.
                         *
                         * @param the dialog box
                         * @param the button pressed
                         */
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    resetDefaultSettings();
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //cancel reset
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(getString(R.string.confirm_reset)).setPositiveButton(R.string.confirm, dialogClickListener)
                            .setNegativeButton(R.string.cancel, dialogClickListener).show();
                    return true;
                }
            });

        }

        /**
         * Reset to default settings
         */
        private void resetDefaultSettings() {

            //clear the settings
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor = settings.edit();
            editor.clear();
            editor.apply();

            //reset summaries
            Preference searchNewsContent = findPreference(getString(R.string.settings_search_news_key));
            onPreferenceChange(searchNewsContent, getString(R.string.settings_search_news_default));

            Preference mediaContent = findPreference(getString(R.string.settings_media_content_key));
            onPreferenceChange(mediaContent, getString(R.string.settings_media_content_default));

            Preference productionOrigin = findPreference(getString(R.string.settings_production_origin_key));
            onPreferenceChange(productionOrigin, null);

            Preference maxNewsDisplayed = findPreference(getString(R.string.settings_max_items_key));
            onPreferenceChange(maxNewsDisplayed, getString(R.string.settings_max_items_default));

            Preference orderBy = findPreference(getString(R.string.settings_order_by_key));
            onPreferenceChange(orderBy, getString(R.string.settings_order_by_default));

            Preference orderDate = findPreference(getString(R.string.settings_order_date_key));
            onPreferenceChange(orderDate, getString(R.string.settings_order_date_default));

        }

        @Override
        /**
         * Updates the summary on preference changes.
         */
        public boolean onPreferenceChange(Preference preference, Object value) {

            if (preference instanceof MultiSelectListPreference) {
                MultiSelectListPreference multiSelectListPreference = (MultiSelectListPreference) preference;
                Set<String> stringSet = (Set<String>) value;

                if (stringSet == null || stringSet.isEmpty()) {
                    preference.setSummary(getString(R.string.all));
                } else {
                    StringBuilder stringBuilder = new StringBuilder();
                    int index = 0;
                    for (String stringValue : stringSet) {
                        index++;
                        int prefIndex = multiSelectListPreference.findIndexOfValue(stringValue);
                        if (prefIndex >= 0) {
                            CharSequence[] labels = multiSelectListPreference.getEntries();
                            stringBuilder.append(labels[prefIndex]);
                            if (index != stringSet.size()) {
                                stringBuilder.append(getString(R.string.comma_delimiter));
                            }
                        }
                    }
                    preference.setSummary(stringBuilder.toString());
                }
            } else if (preference instanceof ListPreference) {
                String stringValue = value.toString();
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex >= 0) {
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary(labels[prefIndex]);
                }
            } else {
                String stringValue = value.toString();
                if (!stringValue.equals(getString(R.string.settings_search_news_default)) && stringValue.trim().length() > 0
                        && stringValue.matches(".*\\w.*") && stringValue.length() > 0) {

                    preference.setSummary(stringValue);
                } else {
                    preference.setSummary(getString(R.string.settings_search_news_default));
                }
            }
            return true;
        }

        /**
         * Binds the summary and value for MultiSelectListPreference.
         *
         * @param preference used by the user.
         */
        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String preferenceString = preferences.getString(preference.getKey(), getString(R.string.empty));
            onPreferenceChange(preference, preferenceString);
        }

        /**
         * Binds the summary and value for non-MultiSelectListPreference.
         *
         * @param preference used by the user.
         */
        private void bindMultiPreferenceListSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            Set<String> selections = preferences.getStringSet(preference.getKey(), null);
            onPreferenceChange(preference, selections);
        }

    }
}
