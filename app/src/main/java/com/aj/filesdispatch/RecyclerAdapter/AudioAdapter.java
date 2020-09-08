package com.aj.filesdispatch.RecyclerAdapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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

import com.aj.filesdispatch.Interface.AddItemToShare;
import com.aj.filesdispatch.Interface.OnItemClickToOpen;
import com.aj.filesdispatch.Models.FileViewItem;
import com.aj.filesdispatch.R;
import com.bumptech.glide.Glide;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.ViewHolder> {
    private static final String TAG = "FILE_ADAPTER";
    private AddItemToShare itemToShare;
    private OnItemClickToOpen itemClickToOpen;
    private Context context;
    private boolean isPlaying = false;
    private List<FileViewItem> audioList = new ArrayList<>();
    private Cursor cursorData;

    public AudioAdapter(AddItemToShare itemToShare, OnItemClickToOpen itemClickToOpen, Context context) {
        this.itemClickToOpen = itemClickToOpen;
        this.itemToShare = itemToShare;
        this.context = context;
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
            audioList.set(position, new FileViewItem(cursorData, "Audios"));
        else if (audioList.size() == position)
            audioList.add(position, new FileViewItem(cursorData, "Audios"));
        else if (audioList.size() < position) {
            Log.d(TAG, "onBindViewHolder: add null");
            audioList.addAll(Collections.nCopies(position - audioList.size(), null));
            audioList.add(position, new FileViewItem(cursorData, "Audios"));
        }
        String extension, encoder;
        String description = audioList.get(position).getFileDes();
        String fileName = audioList.get(position).getFileName();
        try {
            encoder = URLEncoder.encode(fileName, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            encoder = fileName;
        }
        extension = MimeTypeMap.getFileExtensionFromUrl(encoder).toUpperCase();
        audioList.get(position).setFileDrawable(writeOnDrawable(extension));
        Glide.with(context)
                .load(audioList.get(position).getFileDrawable())
                .fitCenter()
                .into(holder.audioIcon);
        holder.audioLabel.setText(fileName);
        holder.audioDes.setText(description);
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

    public void setAudioList(ArrayList<FileViewItem> audioList) {
        this.audioList = audioList;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    private Drawable writeOnDrawable(String extension) {
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_music_icon);
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
        paint.setColor(context.getResources().getColor(R.color.audioPlayer));
        Typeface typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);
        paint.setTextSize(23);
        paint.setFakeBoldText(true);
        paint.setTypeface(typeface);
        iconCanvas.drawText(extension, (float) icon.getWidth() * 7 / 12, (float) icon.getHeight() * 2 / 3, paint);
        return new BitmapDrawable(context.getResources(), icon);
    }
}

