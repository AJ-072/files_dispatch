package com.aj.filesdispatch.RecyclerAdapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.aj.filesdispatch.Interface.AddItemToShare;
import com.aj.filesdispatch.Models.FileViewItem;
import com.aj.filesdispatch.R;

import java.io.File;
import java.util.ArrayList;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.MyVH> {
    private static final String TAG = "Adapter";
    private ArrayList<FileViewItem> fileData = new ArrayList<>();
    private Context context;
    private OnItemClickToOpen fileToOpen;
    private AddItemToShare fileToShare;

    public FileAdapter(Context context, AddItemToShare fileToShare, OnItemClickToOpen fileToOpen) {
        this.context = context;
        this.fileToOpen = fileToOpen;
        this.fileToShare = fileToShare;
    }

    @NonNull
    @Override
    public MyVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_docfile_view, parent, false);
        return new MyVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyVH holder, int position) {

        holder.file_name.setText(fileData.get(position).getFileName());
        holder.file_check.setChecked(fileData.get(position).isChecked());
        holder.file_check.setVisibility(fileData.get(position).isChecked() ? View.VISIBLE : View.GONE);
        holder.file_size.setText(fileData.get(position).getFileDes());
        if (new File(fileData.get(position).getFileLoc()).isDirectory()) {
            holder.file_icon.setImageDrawable(ActivityCompat.getDrawable(context, R.drawable.ic_file_doc));
        } else {
            holder.file_icon.setImageDrawable(ActivityCompat.getDrawable(context, R.drawable.ic_document_24));

        }
        holder.view.setOnClickListener(v -> {
            fileToOpen.OnClick(fileData.get(position),position);
        });
        holder.view.setOnLongClickListener(v -> {
            fileToShare.onItemAdded(fileData.get(position));
            notifyItemChanged(position);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: " + fileData.size());
        return fileData.size();
    }


    public static class MyVH extends RecyclerView.ViewHolder {
        TextView file_name, file_size;
        ImageView file_icon;
        View view;
        CheckBox file_check;

        public MyVH(@NonNull View itemView) {
            super(itemView);
            file_name = itemView.findViewById(R.id.file_name);
            file_size = itemView.findViewById(R.id.file_size);
            file_icon = itemView.findViewById(R.id.file_icon);
            file_check = itemView.findViewById(R.id.file_checked);
            view = itemView;
        }
    }

    public void setData(ArrayList<FileViewItem> fileData) {
        this.fileData = fileData;
        this.notifyDataSetChanged();
    }

    public interface OnItemClickToOpen{
        void OnClick(FileViewItem item,int position);
    }
}
