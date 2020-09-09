package com.aj.filesdispatch.RecyclerAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aj.filesdispatch.Entities.FileItem;
import com.aj.filesdispatch.Interface.AddItemToShare;
import com.aj.filesdispatch.R;
import com.aj.filesdispatch.common.Converter;

import java.util.List;

public class SelectedFileList extends RecyclerView.Adapter<SelectedFileList.selectedViewHolder> {
    private List<FileItem> selectedFiles;
    private AddItemToShare removeFromList;

    public SelectedFileList(List<FileItem> selectedFiles, AddItemToShare removeFromList) {
        this.selectedFiles = selectedFiles;
        this.removeFromList = removeFromList;
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.item_history_file_view;
    }

    @NonNull
    @Override
    public selectedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new selectedViewHolder(LayoutInflater.from(parent.getContext().getApplicationContext()).inflate(viewType, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull selectedViewHolder holder, int position) {
        holder.label.setText(selectedFiles.get(position).getFileName());
        holder.size.setText(Converter.SizeInGMK(selectedFiles.get(position).getFileSize()));
        holder.button.setText(R.string.remove);
        holder.button.setOnClickListener(v -> {
            removeFromList.onItemAdded(selectedFiles.get(position));
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return selectedFiles.size();
    }

    public static class selectedViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView label, size;
        private final Button button;

        public selectedViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.file_icon);
            label = itemView.findViewById(R.id.file_name);
            size = itemView.findViewById(R.id.file_size);
            button = itemView.findViewById(R.id.action_button);
        }
    }
}
