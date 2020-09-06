package com.aj.filesdispatch.Entities;

import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "file_item_table")
public class FileItem{

    @PrimaryKey
    private String fileId;
    private String fileName;
    private long fileSize;
    private String fileUri;
    private long dateAdded;
    @Ignore
    private boolean checked=false;
    @Ignore
    private String showDes;

    public FileItem(String fileId, String fileName, long fileSize, String fileUri,long dateAdded) {
        this.fileId = fileId;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileUri = fileUri;
        this.dateAdded=dateAdded;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
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
