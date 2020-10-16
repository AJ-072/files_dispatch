package com.aj.filesdispatch.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aj.filesdispatch.ApplicationActivity;
import com.aj.filesdispatch.BroadcastReceiver.WifiBroadcastReceiver;
import com.aj.filesdispatch.Entities.FileItem;
import com.aj.filesdispatch.Entities.FileItemBuilder;
import com.aj.filesdispatch.Entities.WifiP2pService;
import com.aj.filesdispatch.R;
import com.aj.filesdispatch.RecyclerAdapter.ServiceListAdapter;
import com.aj.filesdispatch.Services.DispatchService;
import com.aj.filesdispatch.common.Converter;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.aj.filesdispatch.Activities.MainActivity.LOCATION;
import static com.aj.filesdispatch.Activities.MainActivity.LOCATION_PERMISSION_REPEAT;
import static com.aj.filesdispatch.ApplicationActivity.Avatars;
import static com.aj.filesdispatch.ApplicationActivity.FILE_TO_SEND;


public class FindConnection extends AppCompatActivity implements WifiP2pManager.ConnectionInfoListener, ServiceListAdapter.onClick {
    private static final String TAG = "DispatchManager";
    public static final String INSTANCE_NAME = "_fileDispatch_P2p";
    public static final String SERVICE_TYPE = "_filesdispatch._tcp";
    public static final String BUDDY_NAME = "UserName";
    public static final String IP_ADDRESS = "Ip_Address";
    public static final String AVATAR = "Avatar_drawable";
    public static final String PORT = "ListeningPort";
    private WifiP2pDnsSdServiceInfo dnsSdServiceInfo;
    private WifiP2pDnsSdServiceRequest dnsSdServiceRequest;
    private WifiP2pManager p2pManager;
    private ServiceListAdapter adapter;
    private WifiManager manager;
    private WifiP2pService wifiP2pService;
    private WifiP2pDevice device;
    private Animation avatarBackgroundAnim1, avatarBackgroundAnim2;
    private WifiP2pManager.Channel dispatchChannel;
    private List<FileItem> fileToSend = new ArrayList<>();
    private int retry = 0, myPort, avatarId;
    private String BuddyName;
    private ArrayList<WifiP2pService> services = new ArrayList<>();
    private ImageView thisAvatarIcon, avatarBg1, avatarBg2;
    private Intent service;
    private SharedPreferences preferences;
    private Map<String, String> record = new HashMap<>();
    private WifiBroadcastReceiver wifiBroadcastReceiver;
    private ServerSocket socket;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_connection);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent getFile = this.getIntent();
        if (getFile != null) {
            Log.d(TAG, "onCreate: " + getFile.getAction());
            if (Intent.ACTION_SEND.equals(getFile.getAction())) {
                File file = new File(String.valueOf(getFile.getParcelableExtra(Intent.EXTRA_STREAM)));
                fileToSend.add(new FileItemBuilder(file.getPath())
                        .setShowDes(Converter.getFileDes(file))
                        .setDateAdded(file.lastModified())
                        .setFileName(file.getName())
                        .setFileSize(file.length())
                        .setFileUri(file.getPath())
                        .build());
            } else if (Intent.ACTION_SEND_MULTIPLE.equals(getFile.getAction())) {
                ArrayList<Uri> files = getFile.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
                if (files != null)
                    for (Uri uri : files) {
                        File file = new File(String.valueOf(uri));
                        fileToSend.add(new FileItemBuilder(file.getPath())
                                .setShowDes(Converter.getFileDes(file))
                                .setDateAdded(file.lastModified())
                                .setFileName(file.getName())
                                .setFileSize(file.length())
                                .setFileUri(file.getPath())
                                .build());
                    }
            } else {
                fileToSend = getFile.getParcelableArrayListExtra(FILE_TO_SEND);
            }
        }

        if (!isSupported()) {
            finish();
        } else {
            initialise();
        }
    }

    public boolean isSupported() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_WIFI_DIRECT))
            return false;
        manager = (WifiManager) getApplication().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!manager.isP2pSupported())
            return false;
        p2pManager = (WifiP2pManager) getSystemService(WIFI_P2P_SERVICE);
        ApplicationActivity.wifiP2pManager = p2pManager;
        if (p2pManager == null)
            return false;
        dispatchChannel = p2pManager.initialize(getApplicationContext(), getMainLooper(), null);
        ApplicationActivity.wifiChannel = dispatchChannel;
        if (dispatchChannel == null)
            return false;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P && !manager.isWifiEnabled()) {
            manager.setWifiEnabled(true);
        }else{
            Log.d(TAG, "isSupported: turn on wifi");
        }
        MainActivity.RequestPermission(this);
        return true;
    }

    public void initialise() {
        service = new Intent(this, DispatchService.class);
        RecyclerView listView = findViewById(R.id.availWifiList);
        listView.setLayoutManager(new LinearLayoutManager(this));
        p2pManager.requestConnectionInfo(dispatchChannel, this);
        adapter = new ServiceListAdapter(this);
        setAvatarAndUser();
        preferences.edit().putBoolean(LOCATION_PERMISSION_REPEAT, false).apply();
        listView.setAdapter(adapter);

    }

    public void setAvatarAndUser() {
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        avatarId = preferences.getInt(AVATAR, ApplicationActivity.OptnlAvatarName);
        BuddyName = preferences.getString(BUDDY_NAME, ApplicationActivity.OptnlUserName);
        thisAvatarIcon = findViewById(R.id.this_avatar);
        avatarBg1 = findViewById(R.id.my_avatar_anim1);
        avatarBg2 = findViewById(R.id.my_avatar_anim2);
        thisAvatarIcon.setImageResource(avatarId);
        TextView myDisplayName = findViewById(R.id.my_name);
        myDisplayName.setText(BuddyName);
        avatarBackgroundAnim1 = AnimationUtils.loadAnimation(this, R.anim.search_device_anim);
        avatarBackgroundAnim2 = AnimationUtils.loadAnimation(this, R.anim.search_device_anim);
        avatarBackgroundAnim1.setInterpolator(new DecelerateInterpolator());
        avatarBackgroundAnim2.setInterpolator(new DecelerateInterpolator());
        avatarBg1.setAnimation(avatarBackgroundAnim1);
        new Handler().postDelayed(() -> avatarBg2.setAnimation(avatarBackgroundAnim2), 900);
    }

    public void setLocalService() {
        Log.d(TAG, "setLocalService: started");
        record.put(PORT, String.valueOf(getServer_port()));
        record.put(BUDDY_NAME, BuddyName);
        record.put(AVATAR, String.valueOf(avatarId));
        dnsSdServiceInfo = WifiP2pDnsSdServiceInfo.newInstance(INSTANCE_NAME, SERVICE_TYPE, record);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            this.finish();
        }
        p2pManager.addLocalService(dispatchChannel, dnsSdServiceInfo, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess: ");
                setDnsListener();
                p2pManager.requestConnectionInfo(dispatchChannel, FindConnection.this);
            }

            @Override
            public void onFailure(int reason) {
                Log.d(TAG, "onFailure: ");
                if (retry == 5) {
                    finish();
                } else retry++;
                if (!manager.isWifiEnabled()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                setLocalService();

                Log.d(TAG, "onFailure: add local " + reason);
            }
        });
    }

    public void setDnsListener() {
        WifiP2pManager.DnsSdServiceResponseListener dnsSdServiceResponseListener = (instanceName, registrationType, srcDevice) -> {
            if (instanceName.equals(INSTANCE_NAME)) {
                services.add(0, wifiP2pService);
                addToList();
            }
            Log.d(TAG, "setDnsListener: " + srcDevice.deviceName);
        };
        WifiP2pManager.DnsSdTxtRecordListener txtRecordListener = (fullDomainName, txtRecordMap, srcDevice) -> {
            Log.d(TAG, "setDnsListener: " + txtRecordMap.get(PORT));
            device = srcDevice;
            Toast.makeText(this, ServiceListAdapter.getDeviceStatus(device.status), Toast.LENGTH_SHORT).show();
            wifiP2pService = new WifiP2pService(srcDevice, txtRecordMap.get(BUDDY_NAME),
                    Integer.parseInt(Objects.requireNonNull(txtRecordMap.get(PORT))), Integer.parseInt(Objects.requireNonNull(txtRecordMap.get(AVATAR))));
        };
        p2pManager.setDnsSdResponseListeners(dispatchChannel, dnsSdServiceResponseListener, txtRecordListener);
        dnsSdServiceRequest = WifiP2pDnsSdServiceRequest.newInstance();
        p2pManager.addServiceRequest(dispatchChannel, dnsSdServiceRequest, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                discover();
                Log.d(TAG, "onSuccess: add service successfully");
            }

            @Override
            public void onFailure(int reason) {
                Log.d(TAG, "onFailure: add service failed" + getReason(reason));

            }
        });

    }

    private void discover() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        p2pManager.discoverServices(dispatchChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess: discovery started successfully");
            }

            @Override
            public void onFailure(int reason) {
                Log.d(TAG, "onFailure: discovery failed" + reason);
            }
        });
    }

    public int getServer_port() {
        try {
            socket = new ServerSocket(0);
            return myPort = socket.getLocalPort();
        } catch (IOException e) {
            e.printStackTrace();
            return myPort = 9999;
        }

    }

    public String getReason(int reason) {
        switch (reason) {
            case WifiP2pManager.BUSY:
                return "Busy";
            case WifiP2pManager.P2P_UNSUPPORTED:
                return "P2p Unsupported";
            case WifiP2pManager.ERROR:
                return "Error";
            case WifiP2pManager.NO_SERVICE_REQUESTS:
                return "No Request";
        }
        return null;
    }

    public void removeService() {
        p2pManager.removeServiceRequest(dispatchChannel, dnsSdServiceRequest, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess: service removed successfully");
            }

            @Override
            public void onFailure(int reason) {
                Log.d(TAG, "onFailure: cant remove service " + reason);
            }
        });
    }

    public void setConnection(WifiP2pService service) {
        removeService();
        wifiP2pService = service;
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = service.getDevice().deviceAddress;
        config.wps.setup = WpsInfo.PBC;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        p2pManager.connect(dispatchChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(FindConnection.this, ServiceListAdapter.getDeviceStatus(device.status), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(getApplicationContext(), "Not Connected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addToList() {
        adapter.submitList(services);
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        Log.d(TAG, "onConnectionInfoAvailable: ");
        try {
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (info.groupFormed) {

            if (info.isGroupOwner) {
                int port = wifiP2pService.getPort();
                service.putExtra(PORT, port);
            } else {
                service.putExtra(IP_ADDRESS, info.groupOwnerAddress.getHostAddress());
                service.putExtra(PORT, myPort);
            }
            service.putParcelableArrayListExtra(FILE_TO_SEND, (ArrayList<FileItem>) fileToSend);
            startService(service);
            connectionConfirmed();
        }

    }

    @Override
    public void selectDevice(WifiP2pService service, int position) {
        setConnection(service);
    }

    private void register() {
        //Log.d(TAG, "register: " + (dispatchChannel == null) + (p2pManager == null));
        wifiBroadcastReceiver = new WifiBroadcastReceiver(p2pManager, dispatchChannel, this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        registerReceiver(wifiBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        register();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: called");
        unregisterReceiver(wifiBroadcastReceiver);
        super.onPause();
    }

    public static boolean disconnect(WifiP2pManager wifiP2pManager, WifiP2pManager.Channel channel) {
        final boolean[] removed = new boolean[1];
        wifiP2pManager.removeGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                removed[0] = true;
            }

            @Override
            public void onFailure(int reason) {
                removed[0] = false;
            }
        });
        return removed[0];
    }

    public void connectionConfirmed() {
        Intent connected = new Intent();
        Log.d(TAG, "connectionConfirmed: ");
        setResult(AppCompatActivity.RESULT_OK, connected);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                MainActivity.RequestPermission(FindConnection.this);
            } else {
                setLocalService();
            }
        }
    }
}
