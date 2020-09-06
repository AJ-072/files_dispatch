package com.aj.filesdispatch.Models;

import java.io.Serializable;

public class SentFileItem implements Serializable {
    private String fileName;
    private long fileSize;
    private String sender;
    private String type;
    private int progress;
    private String fileLoc;
    private boolean Completed = false;

    public SentFileItem(FileViewItem item) {
        this.fileName = item.getFileName();
        this.fileSize = item.getFileSize();
        this.type = item.getType();
        if (type.equals("Apks"))
            fileName = getFileName() + ".apk";
        sender = item.getSender();
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getProgress() {
        return progress;
    }

    public boolean isCompleted() {
        return Completed;
    }

    public String getFileLoc() {
        return fileLoc;
    }

    public void setFileLoc(String fileLoc) {
        this.fileLoc = fileLoc;
        Completed = true;
    }

    public String getFileName() {
        return fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public String getType() {
        return type;
    }

    public String getSender() {
        return sender;
    }
}
