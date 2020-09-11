package com.aj.filesdispatch.RecyclerAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.aj.filesdispatch.Entities.FileItem;
import com.aj.filesdispatch.Entities.FileItemBuilder;
import com.aj.filesdispatch.Interface.AddItemToShare;
import com.aj.filesdispatch.R;
import com.aj.filesdispatch.common.Converter;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.aj.filesdispatch.Fragments.CliVideos.VIDEOS;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {
    private static final String TAG = "FILE_ADAPTER";
    private Activity activity;
    private List<FileItem> videoItems = new ArrayList<>();
    private AddItemToShare videoClick;
    private Cursor cursorData;

    public VideoAdapter(Activity activity) {
        this.activity = activity;
        this.videoClick = (AddItemToShare) activity;
    }


    @NonNull
    @Override
    public VideoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View fileView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_view, parent, false);
        return new ViewHolder(fileView);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoAdapter.ViewHolder holder, int position) {
        cursorData.moveToPosition(position);
        if (videoItems.size() > position && videoItems.get(position) == null)
            videoItems.set(position, new FileItemBuilder(cursorData.getString(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)))
                    .setFileName(new File(cursorData.getString(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA))).getName())
                    .setFileSize(cursorData.getLong(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)))
                    .setFileType(VIDEOS)
                    .setDateAdded(cursorData.getLong(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED)))
                    .setFileUri(cursorData.getString(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)))
                    .setShowDes(Converter.getFileDes(new File(cursorData.getString(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)))))
                    .build());
        else if (videoItems.size() == position)
            videoItems.add(position, new FileItemBuilder(cursorData.getString(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)))
                    .setFileName(new File(cursorData.getString(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA))).getName())
                    .setFileSize(cursorData.getLong(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)))
                    .setFileType(VIDEOS)
                    .setDateAdded(cursorData.getLong(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED)))
                    .setFileUri(cursorData.getString(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)))
                    .setShowDes(Converter.getFileDes(new File(cursorData.getString(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)))))
                    .build());
        else if (videoItems.size() < position) {
            Log.d(TAG, "onBindViewHolder: add null");
            videoItems.addAll(Collections.nCopies(position - videoItems.size(), null));
            videoItems.add(position, new FileItemBuilder(cursorData.getString(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)))
                    .setFileName(new File(cursorData.getString(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA))).getName())
                    .setFileSize(cursorData.getLong(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)))
                    .setFileType(VIDEOS)
                    .setDateAdded(cursorData.getLong(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED)))
                    .setFileUri(cursorData.getString(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)))
                    .setShowDes(Converter.getFileDes(new File(cursorData.getString(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)))))
                    .build());
        }

        String size = videoItems.get(position).getShowDes();
        String label = videoItems.get(position).getFileName();
        holder.viewLabel.setText(label);
        holder.viewDes.setText(size);
        holder.check_item.setOnClickListener(v -> {
            videoItems.get(position).setChecked(holder.check_item.isChecked());
            notifyItemChanged(position);
            videoClick.onItemAdded(videoItems.get(position));
        });
        //  holder.videoDuration.setText(getDur(cursorData.getString(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA))));
        //holder.viewImage.setImageBitmap(getBitmapFromMediaStore(position));
        Glide.with(activity)
                .load("file://" + getUriFromMediaStore(position))
                .placeholder(R.drawable.ic_play)
                .centerCrop()
                .into(holder.viewImage);
        holder.check_item.setChecked(videoItems.get(position).isChecked());
        holder.itemView.setOnClickListener(view -> {
            playVideo(position);
        });

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return cursorData != null ? cursorData.getCount() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView viewLabel, viewDes, videoDuration;
        public ImageView viewImage;
        public CheckBox check_item;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            viewLabel = itemView.findViewById(R.id.file_name);
            viewDes = itemView.findViewById(R.id.file_size);
            viewImage = itemView.findViewById(R.id.file_icon);
            check_item = itemView.findViewById(R.id.video_checkbox);
            videoDuration = itemView.findViewById(R.id.video_duration);

        }
    }


    public void setVideoItems(Cursor data) {
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
            videoItems.clear();
            this.notifyDataSetChanged();
            return old;
        } else
            return null;
    }

    public void setVideoItems(ArrayList<FileItem> videoItems) {
        this.videoItems = videoItems;
    }

    private Bitmap getBitmapFromMediaStore(int position) {
        return MediaStore.Video.Thumbnails.getThumbnail(
                activity.getContentResolver(),
                Long.parseLong(videoItems.get(position).getFileId()),
                MediaStore.Video.Thumbnails.MINI_KIND,
                null
        );
    }

    private Uri getUriFromMediaStore(int position) {
        return Uri.parse(videoItems.get(position).getFileUri());
    }

    /*@SuppressLint("DefaultLocale")
    private String getDur(String uri){
        MediaMetadataRetriever retriever= new MediaMetadataRetriever();
        retriever.setDataSource(activity, Uri.parse(uri));
        String duration= retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long dur= Long.parseLong(duration);
        retriever.release();
        *//*MediaPlayer mp =MediaPlayer.create(activity, Uri.parse(uri));
        int dur= mp.getDuration();
        mp.release();*//*
        return String.format("%d:%2d",TimeUnit.MILLISECONDS.toMinutes(dur),TimeUnit.MILLISECONDS.toSeconds(dur)-TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(dur)));
    }*/
    private void playVideo(int position) {
        File file = new File(String.valueOf(getUriFromMediaStore(position)));
        Uri fileUri = FileProvider.getUriForFile(
                activity,
                "com.aj.filesdispatch.fileProvider",
                file);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setDataAndType(fileUri, "video/*");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        activity.startActivity(intent);
    }
}


