package com.aj.filesdispatch.RecyclerAdapter;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.aj.filesdispatch.Entities.FileItem;
import com.aj.filesdispatch.Interface.AddItemToShare;
import com.aj.filesdispatch.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.concurrent.ThreadPoolExecutor;

public class AppAdapter extends ListAdapter<FileItem, AppAdapter.MyViewHolder> {
    private static final String TAG = "Adapter";
    private Activity activity;
    AddItemToShare onAppItemClick;
    private static DiffUtil.ItemCallback<FileItem> fileItemItemCallback = new DiffUtil.ItemCallback<FileItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull FileItem oldItem, @NonNull FileItem newItem) {
            return oldItem.getFileId().equals(newItem.getFileId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull FileItem oldItem, @NonNull FileItem newItem) {
            return oldItem.getFileId().equals(newItem.getFileId())
                    && oldItem.getDateAdded() == newItem.getDateAdded()
                    && oldItem.getFileSize() == newItem.getFileSize();
        }
    };

    public AppAdapter(Activity activity, AddItemToShare onAppItemClick) {
        super(fileItemItemCallback);
        this.activity = activity;
        this.onAppItemClick = onAppItemClick;
    }


    @NonNull
    @Override
    public AppAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View fileView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_app_view, parent, false);
        return new MyViewHolder(fileView);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if (getItem(position).getDrawable() == null)
            new Thread(() -> {
                try {
                    getItem(position).getDrawable(activity.getPackageManager().getApplicationIcon(getItem(position).getFileId()));
                    updateIcon(position);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }).start();
        Glide.with(activity)
                .load(getItem(position).getDrawable())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fitCenter()
                .into(holder.appIcon);
        holder.appCheck.setVisibility(getItem(position).isChecked() ? View.VISIBLE : View.INVISIBLE);
        holder.appSize.setText(getItem(position).getShowDes());
        holder.appName.setText(getItem(position).getFileName());
        holder.itemView.setOnClickListener(view -> {
            Log.d(TAG, "onBindViewHolder: " + (onAppItemClick == null));
            getItem(position).setChecked(!getItem(position).isChecked());
            notifyItemChanged(position);
            onAppItemClick.onItemAdded(getItem(position));
        });
    }
    private void updateIcon(int position){
        activity.runOnUiThread(() -> {
            notifyItemChanged(position);
        });
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView appIcon;
        private TextView appName;
        private TextView appSize;
        private ImageView appCheck;
        private View itemView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.app_icon);
            appName = itemView.findViewById(R.id.app_name);
            appSize = itemView.findViewById(R.id.app_size);
            appCheck = itemView.findViewById(R.id.app_checked);
            this.itemView = itemView;
        }
    }
}
