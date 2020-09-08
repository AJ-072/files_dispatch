package com.aj.filesdispatch.Models;

import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Parcel;
import android.os.Parcelable;

public class WifiP2pService {
    private WifiP2pDevice device;
    private String registrationType;
    private String display_Name;
    private int port;
    private int avatarDrawable;

    public WifiP2pService(WifiP2pDevice device, String display_Name, int port) {
        this.device = device;
        this.display_Name = display_Name;
        this.port = port;
    }


    public String getDisplay_Name() {
        return display_Name;
    }

    public WifiP2pDevice getDevice() {
        return device;
    }

    public int getPort() {
        return port;
    }

    public void setDevice(WifiP2pDevice device) {
        this.device = device;
    }
}
