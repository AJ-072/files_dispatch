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
import com.aj.filesdispatch.Interface.setClickListener;
import com.aj.filesdispatch.Models.SentFileItem;
import com.aj.filesdispatch.R;
import com.aj.filesdispatch.common.Converter;

import java.util.ArrayList;
import java.util.List;

public class FileSendingRecyclerAdapter extends RecyclerView.Adapter<FileSendingRecyclerAdapter.ViewHolder> {
    private List<SentFileItem> TransferingFile = new ArrayList<>();
    private static final String TAG = "FileSendingRecyclerAdap";
    private static String me = ApplicationActivity.userName;
    private final String[] fileType;
    private Context context;
    private setClickListener cancelListener;

    public FileSendingRecyclerAdapter(Activity activity) {
        fileType = activity.getResources().getStringArray(R.array.FileType);
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
        holder.progressBar.setProgress(TransferingFile.get(position).getProgress());
        if (TransferingFile.get(position).getType().equals(fileType[0]))
            drawableId = R.drawable.ic_android;
        else if (TransferingFile.get(position).getType().equals(fileType[1]))
            drawableId= R.drawable.ic_play;
        else if (TransferingFile.get(position).getType().equals(fileType[2]))
            drawableId=R.drawable.ic_image;
        else if (TransferingFile.get(position).getType().equals(fileType[3]))
            drawableId=R.drawable.ic_music_icon;
        else if (TransferingFile.get(position).getType().equals(fileType[4]))
            drawableId=R.drawable.ic_document_24;
        else if (TransferingFile.get(position).getType().equals(fileType[5]))
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