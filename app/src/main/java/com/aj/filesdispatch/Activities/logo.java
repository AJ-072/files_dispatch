package com.aj.filesdispatch.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.aj.filesdispatch.R;

import java.util.Objects;

public class logo extends AppCompatActivity {
    public static final int STORAGE = 200;
    public static final String STORAGE_PERMISSION = "NEVER_AGAIN";
    public static final int APP_SETTINGS = 72;
    private static final String TAG = "logo";
    private SharedPreferences sharedPreferences;
    private Intent permission;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_logo);
        sharedPreferences = getSharedPreferences("Storage_request_Never_enabled", MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(STORAGE_PERMISSION, false).apply();
        permission = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Log.d(TAG, "onCreate: ");
        uri = Uri.fromParts("package", getPackageName(), null);
        
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onCreate: stat app");
            new Handler().postDelayed(this::StartApp, 1000);
        }else {
            Log.d(TAG, "onCreate: requested");
            new Handler().postDelayed(this::RequestPermission, 1000);
        }
    }

    public void RequestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Log.d(TAG, "RequestPermission: repeat");
            sharedPreferences.edit().putBoolean(STORAGE_PERMISSION, true).apply();
            new AlertDialog.Builder(this)
                    .setTitle("Permission Needed")
                    .setIcon(R.drawable.ic_logo)
                    .setMessage(R.string.repeat_request_storage)
                    .setPositiveButton("OK", (dialog, which) ->
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE))
                    .setNegativeButton("No thanks", (dialog, which) -> {
                        dialog.dismiss();
                        this.finish();
                    })
                    .setCancelable(false)
                    .create().show();
        } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            if (!sharedPreferences.getBoolean(STORAGE_PERMISSION, false)) {
                Log.d(TAG, "RequestPermission: denied");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE);
            } else {
                Log.d(TAG, "RequestPermission: manual");
                new AlertDialog.Builder(this)
                        .setTitle("Permission Needed")
                        .setIcon(R.drawable.ic_logo)
                        .setMessage(R.string.open_app_settings_storage)
                        .setPositiveButton("OK", (dialog, which) -> {
                            permission.setData(uri);
                            startActivityForResult(permission, APP_SETTINGS);
                        }).setNegativeButton("No thanks", (dialog, which) -> {
                            dialog.dismiss();
                            this.finish();
                        }).setCancelable(false)
                        .create().show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE) {
            int GrantedCode = grantResults[0];
            if (GrantedCode == PackageManager.PERMISSION_DENIED) {
                Log.d(TAG, "onRequestPermissionsResult: denied");
                RequestPermission();
            } else {
                Log.d(TAG, "onRequestPermissionsResult: success");
                StartApp();
            }
        }
    }


    public void StartApp() {
        Log.d(TAG, "StartApp: app starting");
        Intent start = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(start);
        overridePendingTransition(0, 0);
        logo.this.finish();
    }
}