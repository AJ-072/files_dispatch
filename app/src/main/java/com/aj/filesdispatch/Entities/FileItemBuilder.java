package com.aj.filesdispatch.Entities;

import android.graphics.drawable.Drawable;

public class FileItemBuilder {
    FileItem fileItem;

    public FileItemBuilder(String ID) {
        fileItem = new FileItem(ID);
    }

    public FileItemBuilder setFileName(String fileName) {
        fileItem.setFileName(fileName);
        return this;
    }

    public FileItemBuilder setFileSize(long fileSize) {
        fileItem.setFileSize(fileSize);
        return this;
    }

    public FileItemBuilder setFileUri(String fileUri) {
        fileItem.setFileUri(fileUri);
        return this;
    }

    public FileItemBuilder setFileType(String fileType) {
        fileItem.setFileType(fileType);
        return this;
    }

    public FileItemBuilder setDateAdded(long dateAdded) {
        fileItem.setDateAdded(dateAdded);
        return this;
    }

    public FileItemBuilder setShowDes(String showDes) {
        fileItem.setShowDes(showDes);
        return this;
    }

    public FileItemBuilder setDrawable(Drawable drawable) {
        fileItem.setDrawable(drawable);
        return this;
    }

    public FileItem build() {
        return fileItem;
    }
}
