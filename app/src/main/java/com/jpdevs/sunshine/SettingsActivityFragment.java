package com.jpdevs.sunshine;

import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.os.Bundle;
import android.preference.PreferenceManager;
public class SettingsActivityFragment extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    public SettingsActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref_general);
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_location_key)));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        String strValue = value.toString();

        if (preference instanceof ListPreference) {
            // For list preference, look up the correct display value in
            // the preference's 'entries' list (since they have separate labels/values).
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(strValue);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else {
            preference.setSummary(strValue);
        }

        return true;
    }

    /**
     * Attaches a listener so the summary is alwas upted with the preference value.
     * Also fires the listener once, to initialize the summary (so it shows up before the value
     * is changed.)
     */
    private void bindPreferenceSummaryToValue(Preference pref) {
        // Set the listener to watch for value changes.
        pref.setOnPreferenceChangeListener(this);

        // Trigger the listener immediately with the preference's
        // current value.
        onPreferenceChange(
                pref,
                PreferenceManager
                        .getDefaultSharedPreferences(pref.getContext())
                        .getString(pref.getKey(), ""));
    }
}
