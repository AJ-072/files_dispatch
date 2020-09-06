package com.aj.filesdispatch.RecyclerAdapter;

import android.net.wifi.p2p.WifiP2pDevice;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aj.filesdispatch.R;
import com.aj.filesdispatch.dispatchmanager.WifiP2pService;

import java.util.ArrayList;

public class ServiceListAdapter extends RecyclerView.Adapter<ServiceListAdapter.DeviceViewHolder> {
    ArrayList<WifiP2pService> services=null;
    private static final String TAG = "ServiceListAdapter";
    private onClick click;

    public ServiceListAdapter(onClick click) {
        this.click = click;
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_list, parent, false);
        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: "+position);
        holder.wifiname.setText(getDeviceStatus(services.get(position).getDevice().status));
        holder.visiblename.setText(services.get(position).getDisplay_Name());
        holder.view.setOnClickListener(v -> {
            click.selectDevice(services.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return services!=null?services.size():0;

    }

    public static class DeviceViewHolder extends RecyclerView.ViewHolder {
        TextView visiblename, wifiname;
        View view;
        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            visiblename=itemView.findViewById(R.id.visible_name);
            wifiname=itemView.findViewById(R.id.wifi_name);
            view=itemView;
        }
    }
    public void setServices(ArrayList<WifiP2pService> service){
        this.services=service;
        this.notifyDataSetChanged();
    }


    public static String getDeviceStatus(int deviceStatus) {
        Log.d(TAG, "Peer status :" + deviceStatus);
        switch (deviceStatus) {
            case WifiP2pDevice.AVAILABLE:
                return "Available";
            case WifiP2pDevice.INVITED:
                return "Invited";
            case WifiP2pDevice.CONNECTED:
                return "Connected";
            case WifiP2pDevice.FAILED:
                return "Failed";
            case WifiP2pDevice.UNAVAILABLE:
                return "Unavailable";
            default:
                return "Unknown";

        }
    }
    public interface onClick{
        void selectDevice(WifiP2pService service);
    }
}
