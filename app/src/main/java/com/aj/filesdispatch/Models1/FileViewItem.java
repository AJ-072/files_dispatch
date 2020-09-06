package com.aj.filesdispatch.Models1;

import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import com.aj.filesdispatch.DatabaseHelper.AdToDatabase;
import com.aj.filesdispatch.common.Converter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileViewItem implements Parcelable {
    private String fileName;
    private String dateAdded;
    private String id;
    private long size;
    private String showSize;
    private String fileLoc;
    private String fileType;
    private Drawable drawable;
    private boolean checked;

    public FileViewItem(Cursor cursor, String id) {
        fileName = cursor.getString(cursor.getColumnIndexOrThrow(AdToDatabase.FILE_NAME));
        dateAdded = SimpleDateFormat.getDateInstance(DateFormat.SHORT).format(new Date(cursor.getLong(cursor.getColumnIndexOrThrow(AdToDatabase.FILE_ADDED_DATE)) * 1000));
        size = cursor.getLong(cursor.getColumnIndexOrThrow(AdToDatabase.FILE_SIZE));
        fileLoc = cursor.getString(cursor.getColumnIndexOrThrow(AdToDatabase.FILE_LOC));
        fileType = cursor.getString(cursor.getColumnIndexOrThrow(AdToDatabase.FILE_TYPE));
        this.id = id;
    }

    public void update(Cursor cursor) {
        fileName = cursor.getString(cursor.getColumnIndexOrThrow(AdToDatabase.FILE_NAME));
        dateAdded = SimpleDateFormat.getDateInstance(DateFormat.SHORT).format(new Date(cursor.getLong(cursor.getColumnIndexOrThrow(AdToDatabase.FILE_ADDED_DATE)) * 1000));
        size = cursor.getLong(cursor.getColumnIndexOrThrow(AdToDatabase.FILE_SIZE));
        fileLoc = cursor.getString(cursor.getColumnIndexOrThrow(AdToDatabase.FILE_LOC));
        fileType = cursor.getString(cursor.getColumnIndexOrThrow(AdToDatabase.FILE_TYPE));
        this.id = id;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public FileViewItem(Parcel in) {
        fileName = in.readString();
        dateAdded = in.readString();
        size = in.readLong();
        showSize = in.readString();
        fileLoc = in.readString();
        fileType = in.readString();
    }

    public String getFileName() {
        return fileName;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public long getSize() {
        return size;
    }

    public String getShowSize() {
        showSize = Converter.SizeInGMK(size);
        return showSize;
    }

    public String getFileLoc() {
        return fileLoc;
    }

    public String getFileType() {
        return fileType;
    }


    public String getId() {
        return id;
    }

    public FileViewItem setDrawable(Drawable drawable) {
        this.drawable=drawable;
        return this;
    }
    public Drawable getDrawable(){
        return drawable;
    }

    public static final Creator<FileViewItem> CREATOR = new Creator<FileViewItem>() {
        @Override
        public FileViewItem createFromParcel(Parcel in) {
            return new FileViewItem(in);
        }

        @Override
        public FileViewItem[] newArray(int size) {
            return new FileViewItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fileName);
        dest.writeString(dateAdded);
        dest.writeLong(size);
        dest.writeString(showSize);
        dest.writeString(fileLoc);
        dest.writeString(fileType);
    }
}

