package com.aj.filesdispatch.Entities;

public interface Item {
    void setFileName(String fileName);

    void setFileSize(long fileSize);

    void setFileType(String fileType);

    String getFileName();

    long getFileSize();

    String getFileType();

}
