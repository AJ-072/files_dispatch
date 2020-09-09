package com.aj.filesdispatch.Entities;

import android.graphics.drawable.Drawable;

interface ItemInterface {
    void setFileName(String fileName);

    void setFileSize(long fileSize);

    void setFileType(String fileType);

    String getFileName();

    long getFileSize();

    String getFileType();

}
