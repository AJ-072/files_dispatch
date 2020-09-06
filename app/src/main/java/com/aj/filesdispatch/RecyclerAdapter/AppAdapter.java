package com.aj.filesdispatch.RecyclerAdapter;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aj.filesdispatch.DataSource.AppListProvider;
import com.aj.filesdispatch.Interface.AddItemToShare;
import com.aj.filesdispatch.Models1.FileViewItem;
import com.aj.filesdispatch.R;
import com.aj.filesdispatch.common.Converter;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.aj.filesdispatch.DatabaseHelper.AdToDatabase._ID;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.MyViewHolder> {
    private static final String TAG = "Adapter";
    private Context context;
    private ArrayList<FileViewItem> applists;
    private Cursor cursor;
    AddItemToShare onAppItemClick;

    public AppAdapter(Context context, AddItemToShare onAppItemClick) {
        this.context = context;
        this.onAppItemClick = onAppItemClick;
    }

    @NonNull
    @Override
    public AppAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View fileView = LayoutInflater.from(context).inflate(R.layout.item_app_view, parent, false);
        Log.d(TAG, "onCreateViewHolder: "+getItemCount());
        return new MyViewHolder(fileView);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        cursor.moveToPosition(position);
        if (applists.size() > position && applists.get(position) == null) {
            Log.d(TAG, "onBindViewHolder: set");
            applists.set(position, new FileViewItem(cursor, cursor.getString(cursor.getColumnIndexOrThrow(_ID)))
                    .setDrawable(AppListProvider.getAppIcon(cursor.getString(cursor.getColumnIndexOrThrow(_ID)),context)));
        }else if (applists.size() == position) {
            Log.d(TAG, "onBindViewHolder: add");
            applists.add(position, new FileViewItem(cursor, cursor.getString(cursor.getColumnIndexOrThrow(_ID)))
                    .setDrawable(AppListProvider.getAppIcon(cursor.getString(cursor.getColumnIndexOrThrow(_ID)),context)));
        }else if (applists.size()<position){
            Log.d(TAG, "onBindViewHolder: add null");
            applists.addAll(Collections.nCopies(position-applists.size(),null));
            applists.add(position, new FileViewItem(cursor,cursor.getString(cursor.getColumnIndexOrThrow(_ID)))
                    .setDrawable(AppListProvider.getAppIcon(cursor.getString(cursor.getColumnIndexOrThrow(_ID)),context)));
        }
        holder.appSize.setText(applists.get(position).getShowSize());
        holder.appName.setText(applists.get(position).getFileName());
        /*Glide.with(context)
                .load(applists.get(position).getDrawable(AppListProvider.getAppIcon(applists.get(position).getId(),context)))
                .centerCrop()
                .into(holder.appIcon);*/
        holder.appIcon.setImageDrawable(applists.get(position).getDrawable());
        holder.itemView.setOnClickListener(v -> {
            applists.get(position).setChecked(!applists.get(position).isChecked());
            this.notifyItemChanged(position);
            //onAppItemClick.onItemAdded(applists.get(position));
        });
        Log.d(TAG, "onBindViewHolder: binding");
        holder.appCheck.setVisibility(applists.get(position).isChecked() ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return cursor == null ? 0 : cursor.getCount();
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

    public void setApplists(Cursor data) {
        Log.d(TAG, "setApplists: ");
        Cursor old = swapCursor(data);
        if (old != null) {
            old.close();
        }
    }

    public Cursor swapCursor(Cursor data) {
        Cursor old;
        if (cursor != data) {
            old = cursor;
            cursor = data;
            this.notifyDataSetChanged();
            return old;
        } else
            return null;
    }
    public void setApplists(ArrayList<FileViewItem> applists){
        this.applists=applists;
        this.notifyDataSetChanged();

    }
}
