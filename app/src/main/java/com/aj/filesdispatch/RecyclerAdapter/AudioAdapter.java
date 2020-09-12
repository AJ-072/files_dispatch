package com.aj.filesdispatch.RecyclerAdapter;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.aj.filesdispatch.Entities.FileItem;
import com.aj.filesdispatch.Entities.FileItemBuilder;
import com.aj.filesdispatch.Interface.AddItemToShare;
import com.aj.filesdispatch.Interface.OnItemClickToOpen;
import com.aj.filesdispatch.R;
import com.aj.filesdispatch.common.Converter;
import com.bumptech.glide.Glide;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.aj.filesdispatch.Fragments.CliMusic.AUDIOS;

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.ViewHolder> {
    private static final String TAG = "FILE_ADAPTER";
    private AddItemToShare itemToShare;
    private OnItemClickToOpen itemClickToOpen;
    private Activity activity;
    private ExecutorService threadPool = Executors.newFixedThreadPool(4);
    private boolean isPlaying = false;
    private List<FileItem> audioList = new ArrayList<>();
    private Cursor cursorData;

    public AudioAdapter(Activity activity, OnItemClickToOpen itemClickToOpen) {
        this.itemClickToOpen = itemClickToOpen;
        this.itemToShare = (AddItemToShare) activity;
        this.activity = activity;
    }


    @NonNull
    @Override
    public AudioAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View fileView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_audio_view, parent, false);
        return new ViewHolder(fileView);
    }


    @Override
    public void onBindViewHolder(@NonNull AudioAdapter.ViewHolder holder, int position) {
        cursorData.moveToPosition(position);
        if (audioList.size() > position && audioList.get(position) == null)
            audioList.set(position, new FileItemBuilder(cursorData.getString(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)))
                    .setFileName(cursorData.getString(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)))
                    .setFileSize(cursorData.getLong(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)))
                    .setFileType(AUDIOS)
                    .setDateAdded(cursorData.getLong(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED)))
                    .setFileUri(cursorData.getString(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)))
                    .setShowDes(Converter.getFileDes(new File(cursorData.getString(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)))))
                    .build());
        else if (audioList.size() == position)
            audioList.add(position, new FileItemBuilder(cursorData.getString(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)))
                    .setFileName(cursorData.getString(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)))
                    .setFileSize(cursorData.getLong(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)))
                    .setFileType(AUDIOS)
                    .setDateAdded(cursorData.getLong(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED)))
                    .setFileUri(cursorData.getString(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)))
                    .setShowDes(Converter.getFileDes(new File(cursorData.getString(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)))))
                    .build());
        else if (audioList.size() < position) {
            Log.d(TAG, "onBindViewHolder: add null");
            audioList.addAll(Collections.nCopies(position - audioList.size(), null));
            audioList.add(position, new FileItemBuilder(cursorData.getString(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)))
                    .setFileName(cursorData.getString(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)))
                    .setFileSize(cursorData.getLong(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)))
                    .setFileType(AUDIOS)
                    .setDateAdded(cursorData.getLong(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED)))
                    .setFileUri(cursorData.getString(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)))
                    .setShowDes(Converter.getFileDes(new File(cursorData.getString(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)))))
                    .build());
        }
        if (audioList.get(position).getDrawable() == null) {
            threadPool.submit(() -> {
                String extension, encoder;
                String fileName = audioList.get(position).getFileName();
                try {
                    encoder = URLEncoder.encode(fileName, "UTF-8").replace("+", "%20");
                } catch (UnsupportedEncodingException e) {
                    encoder = fileName;
                }
                extension = MimeTypeMap.getFileExtensionFromUrl(encoder).toUpperCase();
                audioList.get(position).setDrawable(writeOnDrawable(extension));
                activity.runOnUiThread(() -> Glide.with(activity)
                        .load(audioList.get(position).getDrawable())
                        .fitCenter()
                        .into(holder.audioIcon));
                //updateIcon(position);
            });
        } else
            Glide.with(activity)
                    .load(audioList.get(position).getDrawable())
                    .fitCenter()
                    .into(holder.audioIcon);
        holder.audioLabel.setText(audioList.get(position).getFileName());
        holder.audioDes.setText(audioList.get(position).getShowDes());
        holder.audioView.setOnClickListener(v -> {
            if (!isPlaying) {
                isPlaying = true;
                itemClickToOpen.OnClick(audioList.get(position));
            }
        });
        holder.audioCheck.setOnClickListener(v -> {
            itemToShare.onItemAdded(audioList.get(position));
            audioList.get(position).setChecked(holder.audioCheck.isChecked());
            this.notifyItemChanged(position);
        });
        holder.audioCheck.setChecked(audioList.get(position).isChecked());
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private void updateIcon(int position) {
        activity.runOnUiThread(() -> notifyItemChanged(position));
    }

    @Override
    public int getItemCount() {
        return cursorData != null ? cursorData.getCount() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView audioLabel, audioDes;
        public ImageView audioIcon;
        public CheckBox audioCheck;
        private View audioView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            audioLabel = itemView.findViewById(R.id.audio_name);
            audioDes = itemView.findViewById(R.id.audio_des);
            audioView = itemView;
            audioCheck = itemView.findViewById(R.id.audio_check);
            audioIcon = itemView.findViewById(R.id.audio_icon);
        }
    }

    public void ChangeItems(Cursor data) {
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
            audioList.clear();
            this.notifyDataSetChanged();
            return old;
        } else
            return null;
    }

    public void setAudioList(ArrayList<FileItem> audioList) {
        this.audioList = audioList;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    private Drawable writeOnDrawable(String extension) {
        Drawable drawable = ContextCompat.getDrawable(activity, R.drawable.ic_music_icon);
        Bitmap drawableIcon = null;
        if (drawable != null) {
            drawableIcon = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(drawableIcon);
            drawable.setBounds(0, 0, drawableIcon.getWidth(), drawableIcon.getHeight());
            drawable.draw(canvas);
        }
        assert drawableIcon != null;
        Bitmap icon = Bitmap.createBitmap(drawableIcon).copy(Bitmap.Config.ARGB_8888, true);
        Canvas iconCanvas = new Canvas(icon);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(activity.getResources().getColor(R.color.audioPlayer));
        Typeface typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);
        paint.setTextSize(23);
        paint.setFakeBoldText(true);
        paint.setTypeface(typeface);
        iconCanvas.drawText(extension, (float) icon.getWidth() * 7 / 12, (float) icon.getHeight() * 2 / 3, paint);
        return new BitmapDrawable(activity.getResources(), icon);
    }
}

