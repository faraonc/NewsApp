package edu.udacity.faraonc.newsapp;

import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.Set;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public static class NewsPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            //add order-by
            Preference orderBy = findPreference(getString(R.string.settings_order_by_key));
            bindPreferenceSummaryToValue(orderBy);

            //add maximum displayed news
            Preference maxNewsDisplayed = findPreference(getString(R.string.settings_max_items_key));
            bindPreferenceSummaryToValue(maxNewsDisplayed);

            //add maximum displayed news
            Preference productionOrigin = findPreference(getString(R.string.settings_production_origin_key));
            bindMultiPreferenceListSummaryToValue(productionOrigin);

        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {

            if (preference instanceof MultiSelectListPreference) {
                MultiSelectListPreference multiSelectListPreference = (MultiSelectListPreference) preference;
                Set<String> stringSet = (Set<String>) value;
                StringBuilder stringBuilder = new StringBuilder();

                int index = 0;
                for (String stringValue : stringSet) {
                    index++;
                    int prefIndex = multiSelectListPreference.findIndexOfValue(stringValue);
                    if (prefIndex >= 0) {
                        CharSequence[] labels = multiSelectListPreference.getEntries();
                        stringBuilder.append(labels[prefIndex]);
                        if (index != stringSet.size()) {
                            stringBuilder.append(getString(R.string.comma));
                        }
                    }
                }
                preference.setSummary(stringBuilder.toString());


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
                preference.setSummary(stringValue);
            }
            return true;
        }


        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String preferenceString = preferences.getString(preference.getKey(), "");
            onPreferenceChange(preference, preferenceString);
        }

        private void bindMultiPreferenceListSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            Set<String> selections = preferences.getStringSet(preference.getKey(), null);
            onPreferenceChange(preference, selections);
        }

    }
}
