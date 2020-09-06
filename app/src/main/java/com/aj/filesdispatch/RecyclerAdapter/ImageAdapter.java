package com.aj.filesdispatch.RecyclerAdapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.aj.filesdispatch.Interface.AddItemToShare;
import com.aj.filesdispatch.Interface.OnItemClickToOpen;
import com.aj.filesdispatch.Models.FileViewItem;
import com.aj.filesdispatch.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private Context activity;
    int width, height;
    private List<FileViewItem> imageList;
    private OnItemClickToOpen imageToOpen;
    private AddItemToShare imageToShare;
    private Cursor cursorData;
    private static final String TAG = "ImageAdapter";

    public ImageAdapter(OnItemClickToOpen imageToOpen, AddItemToShare imageToShare, int width, int height) {
        this.imageToOpen = imageToOpen;
        this.imageToShare = imageToShare;
        this.width = width;
        this.height = height;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        activity=parent.getContext();
        View fileView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_view, parent, false);
        return new ViewHolder(fileView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        cursorData.moveToPosition(position);
        if (imageList.size() > position && imageList.get(position) == null)
            imageList.set(position, new FileViewItem(cursorData, "Images"));
        else if (imageList.size() == position)
            imageList.add(position, new FileViewItem(cursorData, "Images"));
        else if (imageList.size()<position){
            Log.d(TAG, "onBindViewHolder: add null");
            imageList.addAll(Collections.nCopies(position-imageList.size(),null));
            imageList.add(position, new FileViewItem(cursorData, "Images"));
        }
        Glide.with(activity)
                .load("file://" + getUriFromMediaStore(position))
                .centerCrop()
                .into(holder.imageView);
        holder.imageView.setOnClickListener(v -> imageToOpen.OnClick(imageList.get(position)));
        holder.checkBox.setOnClickListener(v -> imageToShare.onItemAdded(imageList.get(position)));
        holder.checkBox.setChecked(imageList.get(position).isChecked());
    }

    @Override
    public int getItemCount() {
        return cursorData != null ? cursorData.getCount() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public AppCompatImageView imageView;
        public AppCompatCheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.picview);
            checkBox = itemView.findViewById(R.id.pic_check);
        }
    }

    public void setImageList(Cursor data) {
        Cursor old = swapCursor(data);
        if (old != null) {
            old.close();
        }
    }

    public Cursor swapCursor(Cursor data) {
        Cursor old;
        if (cursorData != data) {
            old = cursorData;
            cursorData = data;
            this.notifyDataSetChanged();
            return old;
        } else
            return null;
    }

    public void setImageList(ArrayList<FileViewItem> imageList){
        this.imageList=imageList;
    }

    private Bitmap getBitmapFromMediaStore(int position) {
        return MediaStore.Images.Thumbnails.getThumbnail(
                activity.getContentResolver(),
                imageList.get(position).getId(),
                MediaStore.Images.Thumbnails.MINI_KIND,
                null);
    }

    public Uri getUriFromMediaStore(int position) {
        return Uri.parse(imageList.get(position).getFileLoc());
    }
}
