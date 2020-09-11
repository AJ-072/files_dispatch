package com.aj.filesdispatch.RecyclerAdapter;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.aj.filesdispatch.Entities.FileItem;
import com.aj.filesdispatch.Entities.FileItemBuilder;
import com.aj.filesdispatch.Fragments.CliDoc;
import com.aj.filesdispatch.Interface.AddItemToShare;
import com.aj.filesdispatch.R;
import com.aj.filesdispatch.common.Converter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.aj.filesdispatch.Fragments.CliDoc.DOCUMENT;
import static com.aj.filesdispatch.Fragments.CliDoc.DOC_LOADER_ID;

public class DocAdapter extends RecyclerView.Adapter<DocAdapter.MyViewHolder> {
    private List<String> list = Arrays.asList("Archives", "Presentations", "Documents", "E-Books");
    private onDocclick onDocclick;
    private Context context;
    private static final String TAG = "DocAdapter";
    private int id;
    private AddItemToShare itemToShare;
    private List<FileItem> docItems = new ArrayList<>();
    private Cursor cursorData;

    public DocAdapter(DocAdapter.onDocclick onDocclick, int id, Activity activity) {
        this.onDocclick = onDocclick;
        this.id = id;
        this.itemToShare= (AddItemToShare) activity;
    }

    @Override
    public int getItemViewType(int position) {
        if (id == DOC_LOADER_ID)
            return R.layout.item_category_doc_view;
        else
            return R.layout.item_docfile_view;
    }

    @NonNull
    @Override
    public DocAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View fileView = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new DocAdapter.MyViewHolder(fileView);
    }

    @Override
    public void onBindViewHolder(@NonNull DocAdapter.MyViewHolder holder, final int position) {
        if (id == DOC_LOADER_ID) {
            holder.docovertext.setText(list.get(position));
            holder.view.setOnClickListener(v -> onDocclick.onDoc(position));
        } else {
            cursorData.moveToPosition(position);
            if (docItems.size() > position && docItems.get(position) == null)
                docItems.set(position, new FileItemBuilder(cursorData.getString(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)))
                        .setFileName(new File(cursorData.getString(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA))).getName())
                        .setFileSize(cursorData.getLong(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)))
                        .setFileType(DOCUMENT)
                        .setDateAdded(cursorData.getLong(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED)))
                        .setFileUri(cursorData.getString(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)))
                        .setShowDes(Converter.getFileDes(new File(cursorData.getString(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)))))
                        .build());
            else if (docItems.size() == position)
                docItems.add(position, new FileItemBuilder(cursorData.getString(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)))
                        .setFileName(new File(cursorData.getString(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA))).getName())
                        .setFileSize(cursorData.getLong(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)))
                        .setFileType(DOCUMENT)
                        .setDateAdded(cursorData.getLong(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED)))
                        .setFileUri(cursorData.getString(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)))
                        .setShowDes(Converter.getFileDes(new File(cursorData.getString(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)))))
                        .build());
            else if (docItems.size() < position) {
                Log.d(TAG, "onBindViewHolder: add null");
                docItems.addAll(Collections.nCopies(position - docItems.size(), null));
                docItems.add(position, new FileItemBuilder(cursorData.getString(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)))
                        .setFileName(new File(cursorData.getString(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA))).getName())
                        .setFileSize(cursorData.getLong(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)))
                        .setFileType(DOCUMENT)
                        .setDateAdded(cursorData.getLong(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED)))
                        .setFileUri(cursorData.getString(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)))
                        .setShowDes(Converter.getFileDes(new File(cursorData.getString(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)))))
                        .build());
            }
            String file_name = docItems.get(position).getFileName();
            holder.docovertext.setText(file_name);
            holder.file_size.setText(docItems.get(position).getShowDes());
            holder.file_icon.setImageDrawable(ActivityCompat.getDrawable(context, R.drawable.ic_document_24));
            holder.view.setOnClickListener(view -> {
                itemToShare.onItemAdded(docItems.get(position));
            });
        }
    }

    @Override
    public int getItemCount() {
        if (id == CliDoc.DOC_FILE_LOADER_ID)
            return cursorData != null ? cursorData.getCount() : 0;
        else
            return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView docovertext;
        TextView file_size;
        ImageView file_icon;
        View view;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            docovertext = itemView.findViewById(R.id.file_name);
            file_icon = itemView.findViewById(R.id.file_icon);
            file_size = itemView.findViewById(R.id.file_size);
        }
    }

    public void setData(Cursor data) {
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
            docItems.clear();
            this.notifyDataSetChanged();
            return old;
        } else
            return null;
    }

    public void setId(int id) {
        this.id = id;
        notifyDataSetChanged();
    }

    public interface onDocclick {
        void onDoc(int position);
    }
}
