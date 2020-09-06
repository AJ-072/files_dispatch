package com.aj.filesdispatch.dispatchmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.aj.filesdispatch.Services.DispatchService;

public class WifiBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "WifiBroadcastReceiver";
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private AppCompatActivity activity;

    public WifiBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, AppCompatActivity activity) {
        this.channel = channel;
        this.activity = activity;
        this.manager = manager;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {

            } else {
                activity.stopService(new Intent(activity, DispatchService.class));
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            WifiP2pDevice device = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
            if (device != null&&preferences.getString("username",null)!=null) {
                preferences.edit().putString("username", device.deviceName).apply();
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            Log.d(TAG, "onReceive: connection change");

            WifiP2pInfo networkInfo = intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_INFO);
           if (networkInfo != null)
               if (activity instanceof FindConnection)
                manager.requestConnectionInfo(channel, (WifiP2pManager.ConnectionInfoListener) activity);
        }
    }
}
