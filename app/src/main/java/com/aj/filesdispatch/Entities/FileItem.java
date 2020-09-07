package com.aj.filesdispatch.Entities;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;


@Entity(tableName = "file_item_table")
public class FileItem {

    @PrimaryKey
    @NonNull
    private String fileId;
    private String fileName;
    private long fileSize;
    private String fileUri;
    private String fileType;
    private long dateAdded;
    @Ignore
    private boolean checked = false;
    @Ignore
    private String showDes;

    @Ignore
    private Drawable drawable;

    public FileItem(String fileId, String fileName, long fileSize, String fileUri, String fileType, long dateAdded) {
        this.fileId = fileId;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileUri = fileUri;
        this.fileType = fileType;
        this.dateAdded = dateAdded;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public String getFileType() {
        return fileType;
    }

    public long getDateAdded() {
        return dateAdded;
    }

    public String getFileId() {
        return fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public String getFileUri() {
        return fileUri;
    }

    public boolean isChecked() {
        return checked;
    }

    public String getShowDes() {
        return showDes;
    }
}
