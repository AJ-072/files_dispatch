package com.aj.filesdispatch.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.aj.filesdispatch.ApplicationActivity;

import java.io.File;

public class FileData implements Parcelable {
    private String sender;
    private String fileName;
    private long fileSize;
    private String type;


    public FileData(File file) {
        this.fileName = file.getName();
        this.fileSize = file.length();
        sender = ApplicationActivity.userName;
    }

    protected FileData(Parcel in) {
        sender = in.readString();
        fileName = in.readString();
        fileSize = in.readLong();
        type = in.readString();
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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

    public void setType(String type) {
        this.type = type;
    }

    public String getSender() {
        return sender;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(sender);
        dest.writeString(fileName);
        dest.writeLong(fileSize);
        dest.writeString(type);
    }
}
