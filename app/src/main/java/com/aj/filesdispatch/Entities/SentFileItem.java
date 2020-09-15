package com.aj.filesdispatch.Entities;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import com.aj.filesdispatch.ApplicationActivity;

import java.io.Serializable;

import static com.aj.filesdispatch.Activities.FindConnection.BUDDY_NAME;
import static com.aj.filesdispatch.ApplicationActivity.defaultPreference;

public class SentFileItem implements Serializable, Item {
    private String fileName;
    private long fileSize;
    private String fileUri;
    private String fileType;
    private String showDes;
    private Drawable drawable;
    private String sender;
    private long progress;
    private boolean completed;

    public SentFileItem(FileItem item){
        setFileName(item.getFileName());
        setFileSize(item.getFileSize());
        setFileType(item.getFileType());
        setShowDes(item.getShowDes());
        sender= defaultPreference.getString(BUDDY_NAME, null);
    }

    public String getSender() {
        return sender;
    }

    public long getProgress() {
        return progress;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }

    public boolean isCompleted() {
        return completed;
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
        this.completed=true;
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

    public String getShowDes() {
        return this.showDes;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    @NonNull
    @Override
    public String toString() {
        return fileName;
    }
}
