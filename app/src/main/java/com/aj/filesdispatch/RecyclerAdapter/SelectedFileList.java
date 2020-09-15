package com.aj.filesdispatch.RecyclerAdapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.aj.filesdispatch.Entities.FileItem;
import com.aj.filesdispatch.Interface.AddItemToShare;
import com.aj.filesdispatch.R;
import com.aj.filesdispatch.common.Converter;

public class SelectedFileList extends ListAdapter<FileItem, SelectedFileList.selectedViewHolder> {
    private AddItemToShare removeFromList;
    private Activity activity;

    public SelectedFileList(Activity activity) {
        super(diffUtil);
        this.removeFromList = (AddItemToShare) activity;
        this.activity = activity;
    }

    private static final DiffUtil.ItemCallback<FileItem> diffUtil = new DiffUtil.ItemCallback<FileItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull FileItem oldItem, @NonNull FileItem newItem) {
            return oldItem.getFileId().equals(newItem.getFileId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull FileItem oldItem, @NonNull FileItem newItem) {
            return oldItem.getFileUri().equals(newItem.getFileUri())
                    && oldItem.getFileSize() == newItem.getFileSize()
                    && oldItem.getFileName().equals(newItem.getFileName());
        }
    };

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
        holder.label.setText(getItem(position).getFileName());
        holder.size.setText(Converter.SizeInGMK(getItem(position).getFileSize()));
        holder.button.setText(R.string.remove);
        holder.button.setOnClickListener(v -> {
            removeFromList.onItemAdded(getItem(position));
            notifyDataSetChanged();
        });
        holder.imageView.setImageDrawable(getItem(position).getDrawable());
    }

    public static class selectedViewHolder extends RecyclerView.ViewHolder {
        private final AppCompatImageView imageView;
        private final AppCompatTextView label, size;
        private final AppCompatButton button;

        public selectedViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.file_icon);
            label = itemView.findViewById(R.id.file_name);
            size = itemView.findViewById(R.id.file_size);
            button = itemView.findViewById(R.id.action_button);
        }
    }
}
