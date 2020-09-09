package com.aj.filesdispatch.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ShareCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.aj.filesdispatch.Entities.FileItem;
import com.aj.filesdispatch.Interface.AddItemToShare;
import com.aj.filesdispatch.Interface.OnBindToService;
import com.aj.filesdispatch.Entities.UserInfo;
import com.aj.filesdispatch.Entities.WifiP2pService;
import com.aj.filesdispatch.R;
import com.aj.filesdispatch.RecyclerAdapter.SelectedFileList;
import com.aj.filesdispatch.Services.DispatchService;
import com.aj.filesdispatch.common.pager;
import com.aj.filesdispatch.dispatchmanager.WifiBroadcastReceiver;
import com.aj.filesdispatch.nav_optn.About;
import com.aj.filesdispatch.nav_optn.HelpandFeed;
import com.aj.filesdispatch.nav_optn.Settings;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.aj.filesdispatch.Activities.logo.APP_SETTINGS;
import static com.aj.filesdispatch.ApplicationActivity.DARK_MODE;
import static com.aj.filesdispatch.ApplicationActivity.FILE_TO_SEND;
import static com.aj.filesdispatch.ApplicationActivity.sharedPreferences;
import static com.aj.filesdispatch.ApplicationActivity.wifiChannel;
import static com.aj.filesdispatch.ApplicationActivity.wifiP2pManager;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener, AdapterView.OnItemClickListener, AddItemToShare, OnBindToService {
    public static final int LOCATION = 201;
    private static final String CURRENT_TAB = "current_tab";
    public static int count = 0;
    public static final String TAG = "wifi";
    private Toolbar toolbar;
    public static final String LOCATION_PERMISSION_REPEAT = "NEVER_AGAIN";
    public static final int CONNECTED_DEVICE = 2;
    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawerLayout;
    private RecyclerView listRecycler;
    private NavigationView navigationView;
    private ViewPager viewPager;
    private Integer currentTab;
    private TabLayout tabLayout;
    private ServiceConnection connection;
    private Button send;
    private boolean isPermissionGranted = false, isConnected = false;
    private PagerAdapter adapter;
    private androidx.appcompat.app.AlertDialog.Builder alertDialog;
    private ProgressDialog progressDialog;
    private Dialog listDialog;
    private TextView tv;
    private TextView count_text;
    private Intent dispatchActivity, sendFileIntent;
    private Switch dms;
    private BroadcastReceiver wifiBroadcastReceiver;
    private DispatchService dispatchService = null;
    private Intent serviceIntent;
    public ArrayList<FileItem> fileToTransfer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Initialize();
        if (savedInstanceState != null) {
            currentTab = savedInstanceState.getInt(CURRENT_TAB, 3);
            fileToTransfer = savedInstanceState.getParcelableArrayList(FILE_TO_SEND);
        } else {
            currentTab = 3;
            fileToTransfer = new ArrayList<>();
        }

        navigationView.setNavigationItemSelectedListener(this);             //Object Click Listeners
        send.setOnClickListener(this);
        dms.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.getId() == R.id.dms) {
                DarkMode(isChecked);
            }
        });
        TabMenu();
        setNav();

        alertDialog = new androidx.appcompat.app.AlertDialog.Builder(this);

        viewPager.setCurrentItem(currentTab);

        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                dispatchService = ((DispatchService.DispatchBinder) service).getServices();
                isConnected = true;
                WifiInitialize();
                dispatchService.setBindActivity(MainActivity.this);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                dispatchService.setBindActivity(null);
                dispatchService = null;
                isConnected = false;
                unregisterReceiver(wifiBroadcastReceiver);
            }
        };

    }

    public void Initialize() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.display_name);
        send = findViewById(R.id.send);
        viewPager = findViewById(R.id.viewer);
        tabLayout = findViewById(R.id.tabLayout);
        ConstraintLayout container = findViewById(R.id.container);
        sendFileIntent = new Intent(MainActivity.this, FileSendingProgress.class);
        serviceIntent = new Intent(this, DispatchService.class);
        /*disConnect.setOnClickListener(v -> {
            if (isConnected) {
                stopService(new Intent(DispatchService.class.getName()));
                FindConnection.disconnect(wifiP2pManager, wifiChannel);
            }
            v.setVisibility(View.GONE);
        });*/
        sharedPreferences.edit().putBoolean(LOCATION_PERMISSION_REPEAT, false).apply();
        count_text = findViewById(R.id.selected_item_count);
        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.nav_header);
        dispatchActivity = new Intent(MainActivity.this, FindConnection.class);
        dms = navigationView.getMenu().findItem(R.id.dm).getActionView().findViewById(R.id.dms);
        dms.setChecked(sharedPreferences.getBoolean(DARK_MODE, false));
        tv = navigationView.getHeaderView(0).findViewById(R.id.versioncode);
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String vercode = "Version: " + packageInfo.versionName;
            tv.setText(vercode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ctd:
                break;
            case R.id.dm:
                DarkMode(!dms.isChecked());
                break;
            case R.id.about:
                startActivity(new Intent(this, About.class));
                overridePendingTransition(0, 0);
                break;
            case R.id.settings:
                startActivity(new Intent(this, Settings.class));
                overridePendingTransition(0, 0);
                break;
            case R.id.helpmenu:
                startActivity(new Intent(this, HelpandFeed.class));
                overridePendingTransition(0, 0);
                break;
            case R.id.invite:
                Intent builder = ShareCompat.IntentBuilder.from(this)
                        .setType("text/plain")
                        .setText(getText(R.string.share_invitation))
                        .getIntent();
                startActivity(builder);
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        bindService(serviceIntent, connection, 0);
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send:
                isPermissionGranted = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
                if (isPermissionGranted && !isConnected) {
                    Log.d(TAG, "onClick: not connected with permission");
                    dispatchActivity.putParcelableArrayListExtra(FILE_TO_SEND, fileToTransfer);
                    Log.d(TAG, "onClick: " + dispatchActivity.getParcelableArrayListExtra(FILE_TO_SEND));
                    startActivityForResult(dispatchActivity, CONNECTED_DEVICE);
                } else if (!isPermissionGranted) {
                    Log.d(TAG, "onClick: permission not granted");
                    RequestPermission(MainActivity.this);
                } else {
                    Log.d(TAG, "onClick: otherWise");
                    dispatchService.setTransferFile(fileToTransfer);
                    startActivity(sendFileIntent);
                }
                //setAlertDialog();
                break;

        }
    }

    public void WifiInitialize() {
        wifiBroadcastReceiver = new WifiBroadcastReceiver(wifiP2pManager, wifiChannel, this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        registerReceiver(wifiBroadcastReceiver, intentFilter);
    }

    private void TabMenu() {
        adapter = new pager(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, tabLayout.getTabCount(), this);
        //viewPager.onRestoreInstanceState(viewPager.onSaveInstanceState());
        viewPager.setAdapter(adapter);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTab = tab.getPosition();
                viewPager.setCurrentItem(tab.getPosition());
                Objects.requireNonNull(tab.getIcon()).setTint(ActivityCompat.getColor(MainActivity.this, R.color.focused));

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Objects.requireNonNull(tab.getIcon()).setTint(ActivityCompat.getColor(MainActivity.this, R.color.notFocused));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

    private void setNav() {
        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();
    }

    private void DarkMode(boolean mode) {
        int mode_val = mode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
        AppCompatDelegate.setDefaultNightMode(mode_val);
        sharedPreferences.edit().putBoolean(DARK_MODE, mode).apply();
        dms.setChecked(mode);
    }

    public void searchdialog(String msg) {
        progressDialog = ProgressDialog
                .show(this, null, msg, true, false);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(connection);
        Log.d(TAG, "onPause: paused");
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public static void RequestPermission(Activity activity) {
        Intent permission = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
            Log.d(TAG, "RequestPermission: repeat");
            sharedPreferences.edit().putBoolean(LOCATION_PERMISSION_REPEAT, true).apply();
            new AlertDialog.Builder(activity)
                    .setTitle("Permission Needed")
                    .setIcon(R.drawable.ic_logo)
                    .setMessage(R.string.repeat_request_location)
                    .setPositiveButton("OK", (dialog, which) ->
                            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION))
                    .setNegativeButton("No thanks", (dialog, which) -> dialog.dismiss())
                    .setCancelable(false)
                    .create().show();
        } else {
            if (!sharedPreferences.getBoolean(LOCATION_PERMISSION_REPEAT, false)) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION);
            } else {
                Log.d(TAG, "RequestPermission: manual");
                new AlertDialog.Builder(activity)
                        .setTitle("Permission Needed")
                        .setIcon(R.drawable.ic_logo)
                        .setMessage(R.string.open_app_settings_location)
                        .setPositiveButton("OK", (dialog, which) -> {
                            permission.setData(Uri.fromParts("package", activity.getPackageName(), null));
                            activity.startActivityForResult(permission, APP_SETTINGS);
                        }).setNegativeButton("No thanks", (dialog, which) -> {
                    dialog.dismiss();
                }).setCancelable(false)
                        .create().show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION) {
            int GrantedCode = grantResults[0];
            if (GrantedCode == PackageManager.PERMISSION_DENIED) {
                Log.d(TAG, "onRequestPermissionsResult: denied");
                RequestPermission(MainActivity.this);
            } else {
                isPermissionGranted = true;
                if (fileToTransfer.size() > 0)
                    dispatchActivity.putParcelableArrayListExtra(FILE_TO_SEND, fileToTransfer);
                Log.d(TAG, "onRequestPermissionsResult: starting service");
                startActivityForResult(dispatchActivity, CONNECTED_DEVICE);

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CONNECTED_DEVICE && data != null && resultCode == Activity.RESULT_OK) {
            WifiP2pService connectedDevice = data.getParcelableExtra("device");
            if (connectedDevice != null) {
                if (connectedDevice.getDevice() != null) {
                    isConnected = true;
                }
            }
            /*disConnect.setVisibility(View.VISIBLE);*/
            startActivity(sendFileIntent);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putParcelableArrayList(FILE_TO_SEND, fileToTransfer);
        outState.putInt(CURRENT_TAB, currentTab);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isConnected)
            getMenuInflater().inflate(R.menu.dispatch_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.showProgress) {
            startActivity(new Intent(MainActivity.this, FileSendingProgress.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemAdded(FileItem item) {
        if (item != null) {
            Log.d(TAG, "addAppList: added " + item.getFileName());
            if (!fileToTransfer.contains(item)) {
                fileToTransfer.add(item);
            } else {
                fileToTransfer.remove(item);
            }
            item.setChecked(fileToTransfer.contains(item));
            count = fileToTransfer.size();
        }
        setCount();
    }

    @Override
    public void onMultiItemAdded(List<FileItem> fileViewItems) {
        if (fileViewItems != null) {
            if (fileToTransfer == fileViewItems) {
                for (FileItem item : fileViewItems) {
                    item.setChecked(false);
                }
                fileToTransfer.clear();
            } else if (!fileToTransfer.containsAll(fileViewItems)) {
                fileToTransfer.addAll(fileViewItems);
            } else {
                fileToTransfer.removeAll(fileViewItems);
            }
            count = fileToTransfer.size();
        }
        setCount();
    }

    private void setCount() {
        if (count > 0) {
            count_text.setVisibility(View.VISIBLE);
            count_text.setText(String.valueOf(count));
        } else {
            if (listDialog != null && listDialog.isShowing())
                listDialog.cancel();
            count_text.setVisibility(View.GONE);
            setNav();
        }
    }

    private void setAlertDialog() {
        SelectedFileList fileList = new SelectedFileList(fileToTransfer, this);
        listRecycler = new RecyclerView(this);
        listRecycler.setLayoutManager(new LinearLayoutManager(this));
        listRecycler.setPadding(10, 5, 10, 0);
        listRecycler.setAdapter(fileList);
        listDialog = alertDialog.setView(listRecycler)
                .setPositiveButton(getText(R.string.close), (dialog, which) -> dialog.cancel())
                .setNeutralButton(("Remove All"), (dialog, which) -> {
                    onMultiItemAdded(fileToTransfer);
                    fileList.notifyDataSetChanged();
                    dialog.cancel();
                })
                .create();
        listDialog.show();
    }

    @Override
    public void setTotalProgress(int totalProgress) {

    }

    @Override
    public void setTotalSize(int total) {

    }

    @Override
    public void getConnectedDeviceInfo(UserInfo info) {
        isConnected = true;
        /* status.setText(String.format("Connected to %s !", info.getUserName()));*/
        //status.setCompoundDrawablesRelativeWithIntrinsicBounds();
    }
}
