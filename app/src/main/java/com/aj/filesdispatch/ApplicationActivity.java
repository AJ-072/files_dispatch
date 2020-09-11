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

import com.aj.filesdispatch.Activities.FindConnection;

import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

import static com.aj.filesdispatch.Activities.FindConnection.AVATAR;
import static com.aj.filesdispatch.Activities.FindConnection.BUDDY_NAME;

public class ApplicationActivity extends Application {
    public static WifiP2pManager wifiP2pManager;
    public static WifiP2pManager.Channel wifiChannel;
    public static String OptnlUserName;
    public static int OptnlAvatarName;
    private static final String TAG = "ApplicationActivity";
    private NotificationChannel showConnected;
    public static final int[] Avatars={R.drawable.ic_avatar1,R.drawable.ic_avatar2,R.drawable.ic_avatar3,R.drawable.ic_avatar4,R.drawable.ic_avatar5,R.drawable.ic_avatar6,R.drawable.ic_avatar7};
    public static SharedPreferences sharedPreferences, defaultPreference;
    public static final String show = "DEVICE_CONNECTED";
    public static final String DARK_MODE = "DARK_MODE";
    public static final String SHARED = "SHARED_PREFERENCE";
    public static final String FILE_TO_SEND = "FileToSend";
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

        //create Notification channel on API level 26 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            showConnected = new NotificationChannel(show, NOTIFICATION_CHANNEL_1, NotificationManager.IMPORTANCE_HIGH);
        }
        NotificationManagerCompat.from(this).createNotificationChannel(showConnected);
    }

    public void setUser() {
        OptnlUserName = "User" + String.format(Locale.ROOT, "%04d", new Random().nextInt(100000));
        OptnlAvatarName = Avatars[new Random().nextInt(6)];
        if (defaultPreference.getString(BUDDY_NAME, null) == null)
            defaultPreference.edit().putString(BUDDY_NAME, OptnlUserName).apply();
        if (sharedPreferences.getInt(AVATAR, -1) == -1)
            sharedPreferences.edit().putInt(AVATAR,OptnlAvatarName).apply();
    }
}

