package com.aj.filesdispatch.RecyclerAdapter;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aj.filesdispatch.DatabaseHelper.DatabaseHelper;
import com.aj.filesdispatch.R;
import com.aj.filesdispatch.common.Converter;

class HistoryItemAdapter extends RecyclerView.Adapter<HistoryItemAdapter.ViewHolder> {
    private Cursor cursor;

    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_file_view,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        cursor.moveToPosition(position);
        holder.fileName.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.FILE_NAME)));
        holder.fileSize.setText(Converter.GetDate(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.FILE_SIZE))));
    }

    @Override
    public int getItemCount() {
        return cursor!=null?cursor.getCount():0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView fileName,fileSize;
        public final ImageView fileIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fileIcon=itemView.findViewById(R.id.file_icon);
            fileName= itemView.findViewById(R.id.file_name);
            fileSize=itemView.findViewById(R.id.file_size);
        }
    }
}
