package com.aj.filesdispatch;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;

import java.util.Locale;
import java.util.Random;

public class ApplicationActivity extends Application {
    public static WifiP2pManager wifiP2pManager;
    public static WifiP2pManager.Channel wifiChannel;
    public static String userName;
    public static String OptnlUserName;
    public NotificationChannel showConnected;
    public static SharedPreferences sharedPreferences, defaultPreference;
    public static final String show = "DEVICE_CONNECTED";
    public static final String DARK_MODE = "DARK_MODE";
    public static final String SHARED = "SHARED_PREFERENCE";
    private static final String TAG = "ApplicationActivity";
    public static final String FILE_TO_SEND = "FileToSend";
    public static String AvatarName, OptnlAvatarName;
    public static final String NOTIFICATION_CHANNEL_1 = "service Started";

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = getSharedPreferences(SHARED, MODE_PRIVATE);
        defaultPreference = PreferenceManager.getDefaultSharedPreferences(this);

        //set Random Username and Avatar if not Exist
        setUser();

        //set DarkMode/LightMode
        final boolean isDarkMode = sharedPreferences.getBoolean(DARK_MODE, false);
        int mode = isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
        AppCompatDelegate.setDefaultNightMode(mode);

        //create wifiP2pManager and Channel
        wifiP2pManager = (WifiP2pManager) getSystemService(WIFI_P2P_SERVICE);
        if (wifiP2pManager != null)
            wifiChannel = wifiP2pManager.initialize(getApplicationContext(), getMainLooper(), null);

        //create Notification channel on API level 26 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            showConnected = new NotificationChannel(show, NOTIFICATION_CHANNEL_1, NotificationManager.IMPORTANCE_HIGH);
        }
        NotificationManagerCompat.from(this).createNotificationChannel(showConnected);
    }

    public void setUser() {
        OptnlUserName = "User" + String.format(Locale.ROOT, "%04d", new Random().nextInt(100000));
        OptnlAvatarName = "avatar" + new Random().nextInt(6);
        AvatarName = sharedPreferences.getString("avatarNum", null);
        userName = defaultPreference.getString("username", null);
        if (userName == null)
            defaultPreference.edit().putString("username", OptnlUserName).apply();
        if (AvatarName == null)
            sharedPreferences.edit().putString("avatarNum", OptnlAvatarName).apply();
    }
}

