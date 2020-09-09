package com.aj.filesdispatch.Entities;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;


@Entity(tableName = "file_item_table")
public class FileItem implements Item {

    @PrimaryKey
    @NonNull
    private String fileId;
    private String fileName;
    private long fileSize;
    private String fileUri;
    private String fileType;
    private long dateAdded;
    private String showDes;

    @Ignore
    private boolean checked = false;
    @Ignore
    private Drawable drawable = null;

    public FileItem(@NonNull String fileId) {
        this.fileId = fileId;
    }

    @Override
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public void setFileUri(String fileUri) {
        this.fileUri = fileUri;
    }

    @Override
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public void setDateAdded(long dateAdded) {
        this.dateAdded = dateAdded;
    }

    public void setShowDes(String showDes) {
        this.showDes = showDes;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @NonNull
    public String getFileId() {
        return fileId;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public long getFileSize() {
        return fileSize;
    }

    @Override
    public String getFileType() {
        return fileType;
    }

    public String getFileUri() {
        return fileUri;
    }

    public String getShowDes() {
        return this.showDes;
    }

    public Drawable getDrawable(Drawable drawable) {
        if (this.drawable == null)
            this.drawable = drawable;
        return this.drawable;
    }


    public Drawable getDrawable() {
        return drawable;
    }

    public long getDateAdded() {
        return dateAdded;
    }

    public boolean isChecked() {
        return checked;
    }
}
