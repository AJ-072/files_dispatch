package com.aj.filesdispatch.RecyclerAdapter;

import android.content.Context;
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

public class AppAdapter extends ListAdapter<FileItem, AppAdapter.MyViewHolder> {
    private static final String TAG = "Adapter";
    private Context context;
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

    public AppAdapter(Context context, AddItemToShare onAppItemClick) {
        super(fileItemItemCallback);
        this.context = context;
        this.onAppItemClick = onAppItemClick;
    }


    @NonNull
    @Override
    public AppAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View fileView = LayoutInflater.from(context).inflate(R.layout.item_app_view, parent, false);
        return new MyViewHolder(fileView);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {
            Glide.with(context)
                    .load(getItem(position).getDrawable() == null ?
                            getItem(position).getDrawable(context.getPackageManager().getApplicationIcon(getItem(position).getFileId()))
                            : getItem(position).getDrawable())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .fitCenter()
                    .into(holder.appIcon);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        holder.appCheck.setVisibility(getItem(position).isChecked() ? View.VISIBLE : View.INVISIBLE);
        holder.appSize.setText(getItem(position).getShowDes());
        holder.appName.setText(getItem(position).getFileName());
        holder.itemView.setOnClickListener(view -> {
            Log.d(TAG, "onBindViewHolder: "+(onAppItemClick==null));
            getItem(position).setChecked(!getItem(position).isChecked());
            notifyItemChanged(position);
            onAppItemClick.onItemAdded(getItem(position));
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
