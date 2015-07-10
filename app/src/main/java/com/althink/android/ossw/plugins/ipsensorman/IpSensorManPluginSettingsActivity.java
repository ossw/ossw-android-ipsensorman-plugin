package com.althink.android.ossw.plugins.ipsensorman;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import java.util.List;

public class IpSensorManPluginSettingsActivity extends PreferenceActivity {

    private final static String TAG = IpSensorManPluginContentProvider.class.getSimpleName();
    public static final String CYCLING_SPEED_UNIT_PARAM_VALUE = "cycling_speed_unit";
    public static final String CYCLING_WHEEL_CIRCUMFERENCE_PARAM_VALUE = "cycling_wheel_circumference";

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
        bindPreferenceSummaryToValue(findPreference(CYCLING_SPEED_UNIT_PARAM_VALUE));
        bindPreferenceSummaryToValue(findPreference(CYCLING_WHEEL_CIRCUMFERENCE_PARAM_VALUE));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            preference.setSummary(stringValue);

            switch(preference.getKey()) {
                case IpSensorManPluginSettingsActivity.CYCLING_SPEED_UNIT_PARAM_VALUE:
                    //updateSampleStringParameter((String)value);
                    break;
                case IpSensorManPluginSettingsActivity.CYCLING_WHEEL_CIRCUMFERENCE_PARAM_VALUE:
                    //updateSampleStringParameter((String)value);
                    break;
            }

            return true;
        }
    };

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }
}
