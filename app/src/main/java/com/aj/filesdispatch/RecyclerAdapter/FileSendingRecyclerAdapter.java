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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.aj.filesdispatch.Entities.SentFileItem;
import com.aj.filesdispatch.Interface.setClickListener;
import com.aj.filesdispatch.R;
import com.aj.filesdispatch.common.Converter;

import static com.aj.filesdispatch.Activities.FindConnection.BUDDY_NAME;
import static com.aj.filesdispatch.ApplicationActivity.defaultPreference;
import static com.aj.filesdispatch.Fragments.CliDoc.DOCUMENT;
import static com.aj.filesdispatch.Fragments.CliFiles.FOLDER;
import static com.aj.filesdispatch.Fragments.CliImages.IMAGES;
import static com.aj.filesdispatch.Fragments.CliMusic.AUDIOS;
import static com.aj.filesdispatch.Fragments.CliVideos.VIDEOS;
import static com.aj.filesdispatch.Repository.AppListRepository.APPLICATION;

public class FileSendingRecyclerAdapter extends ListAdapter<SentFileItem, FileSendingRecyclerAdapter.ViewHolder> {
    private static final String TAG = "FileSendingRecyclerAdap";
    private static String me = defaultPreference.getString(BUDDY_NAME, null);
    private Context context;
    private setClickListener cancelListener;

    public FileSendingRecyclerAdapter(Activity activity) {
        super(diffUtil);
        context = activity;
        cancelListener = (setClickListener) activity;
    }

    private static final DiffUtil.ItemCallback<SentFileItem> diffUtil = new DiffUtil.ItemCallback<SentFileItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull SentFileItem oldItem, @NonNull SentFileItem newItem) {
            return oldItem.getSender().equals(newItem.getSender())
                    && oldItem.getFileName().equals(newItem.getFileName());
        }

        @Override
        public boolean areContentsTheSame(@NonNull SentFileItem oldItem, @NonNull SentFileItem newItem) {
            return oldItem.getFileName().equals(newItem.getFileName())
                    && oldItem.getProgress() == newItem.getProgress()
                    && oldItem.isCompleted() == newItem.isCompleted();
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(viewType, parent, false));
    }

    @Override
    public int getItemViewType(int position) {
        if (me.equals(getItem(position).getSender()))
            return R.layout.send_file_view;
        return R.layout.recieve_file_view;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int drawableId = R.drawable.ic_launcher_foreground;
        holder.mSize.setText(Converter.SizeInGMK(getItem(position).getFileSize()));
        holder.mIdView.setText(getItem(position).getFileName());
        holder.progressBar.setMax((int) getItem(position).getFileSize());
        holder.progressBar.setProgress((int) getItem(position).getProgress());
        switch (getItem(position).getFileType()) {
            case APPLICATION:
                drawableId = R.drawable.ic_android;
                break;
            case VIDEOS:
                drawableId = R.drawable.ic_play;
                break;
            case IMAGES:
                drawableId = R.drawable.ic_image;
                break;
            case AUDIOS:
                drawableId = R.drawable.ic_music_icon;
                break;
            case DOCUMENT:
                drawableId = R.drawable.ic_document_24;
                break;
            case FOLDER:
                drawableId = R.drawable.ic_file_doc;
                break;
        }
        holder.icon.setImageResource(drawableId);
        if (getItem(position).isCompleted()) {
            holder.progressBar.setVisibility(View.INVISIBLE);
            holder.cancel.setText(context.getText(R.string.open));
        }
        holder.cancel.setOnClickListener(v -> cancelListener.onClick(getItem(position)));
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