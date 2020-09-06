package com.aj.filesdispatch.dispatchmanager;

import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Parcel;
import android.os.Parcelable;

public class WifiP2pService implements Parcelable {
    private WifiP2pDevice device;
    private String registrationType;
    private String display_Name;
    private int port;
    private String IpAddress;

    public WifiP2pService(WifiP2pDevice device, String display_Name, int port) {
        this.device = device;
        this.display_Name = display_Name;
        this.port = port;
    }

    protected WifiP2pService(Parcel in) {
        device = in.readParcelable(WifiP2pDevice.class.getClassLoader());
        registrationType = in.readString();
        display_Name = in.readString();
        port = in.readInt();
        IpAddress = in.readString();
    }

    public static final Creator<WifiP2pService> CREATOR = new Creator<WifiP2pService>() {
        @Override
        public WifiP2pService createFromParcel(Parcel in) {
            return new WifiP2pService(in);
        }

        @Override
        public WifiP2pService[] newArray(int size) {
            return new WifiP2pService[size];
        }
    };

    public String getDisplay_Name() {
        return display_Name;
    }

    public WifiP2pDevice getDevice() {
        return device;
    }

    public int getPort() {
        return port;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public WifiP2pService() {

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(device, flags);
        dest.writeString(registrationType);
        dest.writeString(display_Name);
        dest.writeInt(port);
        dest.writeString(IpAddress);
    }

    public void setDevice(WifiP2pDevice device) {
        this.device = device;
    }
}
