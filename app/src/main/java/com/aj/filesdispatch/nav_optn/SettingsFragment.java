package com.aj.filesdispatch.nav_optn;

import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;

import com.aj.filesdispatch.R;

import java.util.Objects;

public class SettingsFragment extends PreferenceFragmentCompat {
    EditTextPreference visible_name;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        visible_name = findPreference("username");
        Objects.requireNonNull(visible_name).setOnBindEditTextListener(new EditTextPreference.OnBindEditTextListener() {
            @Override
            public void onBindEditText(@NonNull EditText editText) {

            }
        });
    }
}