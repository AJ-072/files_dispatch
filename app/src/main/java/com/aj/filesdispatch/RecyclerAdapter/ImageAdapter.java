package com.aj.filesdispatch.RecyclerAdapter;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.aj.filesdispatch.Entities.FileItem;
import com.aj.filesdispatch.Entities.FileItemBuilder;
import com.aj.filesdispatch.Interface.AddItemToShare;
import com.aj.filesdispatch.Interface.OnItemClickToOpen;
import com.aj.filesdispatch.R;
import com.aj.filesdispatch.common.Converter;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.aj.filesdispatch.Fragments.CliImages.IMAGES;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private Activity activity;
    int width, height;
    private List<FileItem> imageList;
    private OnItemClickToOpen imageToOpen;
    private AddItemToShare imageToShare;
    private ExecutorService threadPool = Executors.newFixedThreadPool(10);
    private Cursor cursorData;
    private static final String TAG = "ImageAdapter";

    public ImageAdapter(OnItemClickToOpen imageToOpen, Activity activity, int width, int height) {
        this.imageToOpen = imageToOpen;
        this.imageToShare = (AddItemToShare) activity;
        this.activity=activity;
        this.width = width;
        this.height = height;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View fileView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_view, parent, false);
        return new ViewHolder(fileView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        //holder.imageView.setImageDrawable(ActivityCompat.getDrawable(activity,R.color.focusedShade));
        cursorData.moveToPosition(position);
        if (imageList.size() > position && imageList.get(position) == null) {
            imageList.set(position, new FileItemBuilder(cursorData.getString(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)))
                    .setFileName(cursorData.getString(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)))
                    .setFileSize(cursorData.getLong(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)))
                    .setFileType(IMAGES)
                    .setDateAdded(cursorData.getLong(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED)))
                    .setFileUri(cursorData.getString(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)))
                    .setShowDes(Converter.getFileDes(new File(cursorData.getString(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)))))
                    .build());
        }
        else if (imageList.size() == position) {
            imageList.add(position, new FileItemBuilder(cursorData.getString(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)))
                    .setFileName(cursorData.getString(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)))
                    .setFileSize(cursorData.getLong(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)))
                    .setFileType(IMAGES)
                    .setDateAdded(cursorData.getLong(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED)))
                    .setFileUri(cursorData.getString(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)))
                    .setShowDes(Converter.getFileDes(new File(cursorData.getString(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)))))
                    .build());
        }
        else if (imageList.size() < position) {
            Log.d(TAG, "onBindViewHolder: add null");
            imageList.addAll(Collections.nCopies(position - imageList.size(), null));
            imageList.add(position, new FileItemBuilder(cursorData.getString(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)))
                    .setFileName(cursorData.getString(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)))
                    .setFileSize(cursorData.getLong(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)))
                    .setFileType(IMAGES)
                    .setDateAdded(cursorData.getLong(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED)))
                    .setFileUri(cursorData.getString(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)))
                    .setShowDes(Converter.getFileDes(new File(cursorData.getString(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)))))
                    .build());
        }
        if (imageList.get(position).getDrawable() == null) {
            threadPool.submit(() -> {
                imageList.get(position).setDrawable(getBitmapFromMediaStore(position));
                activity.runOnUiThread(() -> Glide.with(activity)
                        .load(imageList.get(position).getDrawable())
                        .centerCrop()
                        .into(holder.imageView));
            });
        }
        else
            Glide.with(activity)
                    .load(imageList.get(position).getDrawable())
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
            imageView = itemView.findViewById(R.id.image_item);
            checkBox = itemView.findViewById(R.id.image_check);
        }
    }

    public void setImageList(Cursor cursorData) {
        Cursor old = swapCursor(cursorData);
        if (old != null) {
            old.close();
        }
    }

    public Cursor swapCursor(Cursor cursorData) {
        Cursor old;
        if (this.cursorData != cursorData) {
            old = this.cursorData;
            this.cursorData = cursorData;
            this.notifyDataSetChanged();
            return old;
        } else
            return null;
    }

    public void setImageList(ArrayList<FileItem> imageList) {
        this.imageList = imageList;
    }

    private Drawable getBitmapFromMediaStore(int position) {
        return new BitmapDrawable(activity.getResources(),MediaStore.Images.Thumbnails.getThumbnail(
                activity.getContentResolver(),
                Long.parseLong(imageList.get(position).getFileId()),
                MediaStore.Images.Thumbnails.MINI_KIND,
                null));

    }
}
