package com.aj.filesdispatch.Activities;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aj.filesdispatch.DatabaseHelper.DatabaseHelper;
import com.aj.filesdispatch.Entities.SentFileItem;
import com.aj.filesdispatch.Entities.UserInfo;
import com.aj.filesdispatch.Interface.OnBindToService;
import com.aj.filesdispatch.Interface.SendingFIleListener;
import com.aj.filesdispatch.Interface.setClickListener;
import com.aj.filesdispatch.R;
import com.aj.filesdispatch.RecyclerAdapter.FileSendingRecyclerAdapter;
import com.aj.filesdispatch.Services.DispatchService;
import com.aj.filesdispatch.common.Converter;

import java.util.List;
import java.util.Objects;

import static com.aj.filesdispatch.Enums.Action.ACTION_ADD;
import static com.aj.filesdispatch.Enums.Action.ACTION_FINISHED;
import static com.aj.filesdispatch.Enums.Action.ACTION_PAUSE;
import static com.aj.filesdispatch.Enums.Action.ACTION_REMOVE;
import static com.aj.filesdispatch.Enums.Action.ACTION_RESUME;

public class FileSendingProgress extends AppCompatActivity implements OnBindToService, SendingFIleListener, setClickListener {
    private FileSendingRecyclerAdapter adapter;
    private ProgressBar totalProgressBar;
    private static final String TAG = "FileSendingProgress";
    private ServiceConnection connection;
    private DispatchService dispatchService = null;
    private Intent serviceIntent;
    private static int count = 0, speed = 0;
    private DatabaseHelper helper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dispatch_file_list);
        totalProgressBar = findViewById(R.id.total_progress);
        RecyclerView sendFileView = findViewById(R.id.dispatchList);

        helper = new DatabaseHelper(this, 1);

        adapter = new FileSendingRecyclerAdapter(this);
        sendFileView.setLayoutManager(new LinearLayoutManager(this));
        sendFileView.setAdapter(adapter);

        serviceIntent = new Intent(this, DispatchService.class);
        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                dispatchService = ((DispatchService.DispatchBinder) service).getServices();
                dispatchService.setBindActivity(FileSendingProgress.this);
                onFileListChanged(dispatchService.getReceivedFile());
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                dispatchService.setBindActivity(null);
                dispatchService = null;
                Log.d(TAG, "onServiceDisconnected: ");
            }
        };

        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                Objects.requireNonNull(getSupportActionBar()).setTitle(Converter.SizeInGMK(count - speed) + "/s");
                speed = count;
                handler.postDelayed(this, 1000);
            }
        });
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: ");
        bindService(serviceIntent, connection, Service.BIND_ADJUST_WITH_ACTIVITY);
        super.onResume();
    }


    @Override
    protected void onPause() {
        unbindService(connection);
        super.onPause();
    }


    @Override
    public void setTotalProgress(int totalProgress) {
        count = totalProgress;
        runOnUiThread(() -> totalProgressBar.setProgress(totalProgress));
    }

    @Override
    public void setTotalSize(int total) {
        runOnUiThread(() -> totalProgressBar.setMax(total));
    }

    @Override
    public void getConnectedDeviceInfo(UserInfo info) {

    }

    @Override
    public void onFileRemoved(int position) {
        runOnUiThread(() -> adapter.notifyItemRemoved(position));
    }

    @Override
    public void onFileListChanged(List<SentFileItem> fileItems) {
        runOnUiThread(() -> {
            adapter.submitList(fileItems);
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onFileProgress(int position) {
        runOnUiThread(() -> adapter.notifyItemChanged(position));
    }

    @Override
    public void onErrorOccur(int position) {

    }

    @Override
    public void onClick(SentFileItem item,int position) {
        switch (item.getWhat()) {
            case ACTION_FINISHED:
                break;
            case ACTION_PAUSE:
                item.setWhat(ACTION_RESUME);
                break;
            case ACTION_RESUME:
                item.setWhat(ACTION_PAUSE);
                break;
            case ACTION_REMOVE:
                item.setWhat(ACTION_ADD);
                break;
            case ACTION_ADD:
                item.setWhat(ACTION_REMOVE);
                break;
        }
        if (item.getWhat() != ACTION_FINISHED) {
            runOnUiThread(() -> adapter.notifyItemChanged(position));
            dispatchService.changeAction(item);
        }
    }
}