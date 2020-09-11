package com.aj.filesdispatch.RecyclerAdapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aj.filesdispatch.ApplicationActivity;
import com.aj.filesdispatch.Entities.SentFileItem;
import com.aj.filesdispatch.Interface.setClickListener;
import com.aj.filesdispatch.R;
import com.aj.filesdispatch.common.Converter;

import java.util.ArrayList;
import java.util.List;

import static com.aj.filesdispatch.Activities.FindConnection.BUDDY_NAME;
import static com.aj.filesdispatch.ApplicationActivity.defaultPreference;
import static com.aj.filesdispatch.Fragments.CliDoc.DOCUMENT;
import static com.aj.filesdispatch.Fragments.CliFiles.FOLDER;
import static com.aj.filesdispatch.Fragments.CliImages.IMAGES;
import static com.aj.filesdispatch.Fragments.CliMusic.AUDIOS;
import static com.aj.filesdispatch.Fragments.CliVideos.VIDEOS;
import static com.aj.filesdispatch.Repository.AppListRepository.APPLICATION;

public class FileSendingRecyclerAdapter extends RecyclerView.Adapter<FileSendingRecyclerAdapter.ViewHolder> {
    private List<SentFileItem> TransferingFile = new ArrayList<>();
    private static final String TAG = "FileSendingRecyclerAdap";
    private static String me = defaultPreference.getString(BUDDY_NAME, null);
    private Context context;
    private setClickListener cancelListener;

    public FileSendingRecyclerAdapter(Activity activity) {
        context=activity;
        cancelListener= (setClickListener) activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(viewType, parent, false));
    }

    @Override
    public int getItemViewType(int position) {
        if (me.equals(TransferingFile.get(position).getSender()))
            return R.layout.send_file_view;
        return R.layout.recieve_file_view;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int drawableId = R.drawable.ic_launcher_foreground;
        holder.mSize.setText(Converter.SizeInGMK(TransferingFile.get(position).getFileSize()));
        holder.mIdView.setText(TransferingFile.get(position).getFileName());
        holder.progressBar.setMax((int) TransferingFile.get(position).getFileSize());
        holder.progressBar.setProgress((int) TransferingFile.get(position).getProgress());
        if (TransferingFile.get(position).getFileType().equals(APPLICATION))
            drawableId = R.drawable.ic_android;
        else if (TransferingFile.get(position).getFileType().equals(VIDEOS))
            drawableId= R.drawable.ic_play;
        else if (TransferingFile.get(position).getFileType().equals(IMAGES))
            drawableId=R.drawable.ic_image;
        else if (TransferingFile.get(position).getFileType().equals(AUDIOS))
            drawableId=R.drawable.ic_music_icon;
        else if (TransferingFile.get(position).getFileType().equals(DOCUMENT))
            drawableId=R.drawable.ic_document_24;
        else if (TransferingFile.get(position).getFileType().equals(FOLDER))
            drawableId=R.drawable.ic_file_doc;
        holder.icon.setImageResource(drawableId);
        if (TransferingFile.get(position).isCompleted()) {
            holder.progressBar.setVisibility(View.INVISIBLE);
            holder.cancel.setText(context.getText(R.string.open));
        }
        holder.cancel.setOnClickListener(v -> cancelListener.onClick(TransferingFile.get(position)));
    }

    @Override
    public int getItemCount() {
        return TransferingFile != null ? TransferingFile.size() : 0;
    }

    @MainThread
    public void setFilePacks(List<SentFileItem> TransferingFile) {
        this.TransferingFile = TransferingFile;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final ImageView icon;
        public final TextView mSize;
        public final ProgressBar progressBar;
        public final Button cancel;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            icon = view.findViewById(R.id.file_icon);
            mIdView = view.findViewById(R.id.file_name);
            mSize = view.findViewById(R.id.file_size);
            progressBar = view.findViewById(R.id.file_progress);
            cancel = view.findViewById(R.id.file_cancel);
        }
    }
}