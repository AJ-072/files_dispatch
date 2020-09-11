package com.aj.filesdispatch.nav_optn;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.aj.filesdispatch.R;

import java.util.Objects;

public class SettingsFragment extends PreferenceFragmentCompat {
    EditTextPreference visible_name;
    private static final String TAG = "SettingsFragment";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        visible_name = findPreference("UserName");
    }
}