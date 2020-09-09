package com.aj.filesdispatch.Entities;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Objects;


@Entity(tableName = "file_item_table")
public class FileItem implements Item , Parcelable {

    @NonNull
    @PrimaryKey
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

    protected FileItem(Parcel in) {
        fileId = Objects.requireNonNull(in.readString());
        fileName = in.readString();
        fileSize = in.readLong();
        fileUri = in.readString();
        fileType = in.readString();
        dateAdded = in.readLong();
        showDes = in.readString();
        checked = in.readByte() != 0;
    }

    public static final Creator<FileItem> CREATOR = new Creator<FileItem>() {
        @Override
        public FileItem createFromParcel(Parcel in) {
            return new FileItem(in);
        }

        @Override
        public FileItem[] newArray(int size) {
            return new FileItem[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(fileId);
        parcel.writeString(fileName);
        parcel.writeLong(fileSize);
        parcel.writeString(fileUri);
        parcel.writeString(fileType);
        parcel.writeLong(dateAdded);
        parcel.writeString(showDes);
        parcel.writeByte((byte) (checked ? 1 : 0));
    }
}
