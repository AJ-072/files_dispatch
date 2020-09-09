package com.aj.filesdispatch.Entities;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

class SentFileItem implements Serializable, Item {
    private String fileName;
    private long fileSize;
    private String fileUri;
    private String fileType;
    private String showDes;
    private Drawable drawable;

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

    public void setShowDes(String showDes) {
        this.showDes = showDes;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
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

    public Drawable getDrawable() {
        return drawable;
    }
}
