package com.aj.filesdispatch.nav_optn;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.aj.filesdispatch.R;

import java.util.Objects;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_container);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Settings");
        /*getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_container, new SettingsFragment())
                .commit();*/
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}