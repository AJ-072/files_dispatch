package com.aj.filesdispatch.Entities;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class FileItem {

    @PrimaryKey
    private String fileId;
    private String fileName;
    private long fileSize;
    private String fileUri;

    @Ignore
    private boolean checked=false;
    @Ignore
    private String showDes;

    public FileItem(String fileId, String fileName, long fileSize, String fileUri) {
        this.fileId = fileId;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileUri = fileUri;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
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
