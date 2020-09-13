package com.aj.filesdispatch.nav_optn;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.aj.filesdispatch.R;

import java.util.Objects;

public class About extends AppCompatActivity {
    private TextView version, about;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("About");
        version = findViewById(R.id.ver);
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String ver = "Version :" + packageInfo.versionName;
            version.setText(ver);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        about = findViewById(R.id.us);
        about.setOnClickListener(v -> {

        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}