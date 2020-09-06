package com.aj.filesdispatch.Models;

import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;

import com.aj.filesdispatch.DataSource.AppListProvider;
import com.aj.filesdispatch.common.Converter;

import java.io.File;

import static com.aj.filesdispatch.DatabaseHelper.AdToDatabase.FILE_LOC;
import static com.aj.filesdispatch.DatabaseHelper.DatabaseHelper._ID;

public class FileViewItem extends FileData implements Parcelable {
    private String fileLoc;
    private boolean checked = false;
    private String fileDes;
    private Drawable fileDrawable;
    private long id;

    public FileViewItem(Cursor cursor,String type) {
        super(new File(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA))));
        this.fileDes = Converter.getFileDes(new File(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA))));
        this.fileLoc = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA));
        this.id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID));
        this.setType(type);
    }

    public FileViewItem(Cursor cursor) {
        super(new File(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA))));
        this.fileDes = Converter.getFileDes(new File(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA))));
        this.fileLoc = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA));
        this.id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID));
    }
    public FileViewItem(String type,Cursor cursor,Drawable icon) {
        super(new File(cursor.getString(cursor.getColumnIndexOrThrow(FILE_LOC))));
        this.fileDes = Converter.getFileDes(new File(cursor.getString(cursor.getColumnIndexOrThrow(FILE_LOC))));
        this.fileLoc = cursor.getString(cursor.getColumnIndexOrThrow(FILE_LOC));
        this.id = cursor.getLong(cursor.getColumnIndexOrThrow(_ID));
        setType(type);
        setFileDrawable(icon);
    }

    public FileViewItem(File file, String fileName, Drawable fileIcon) {
        super(file);
        this.fileDes = Converter.SizeInGMK(file.length());
        this.fileLoc = file.getPath();
        this.fileDrawable = fileIcon;
        super.setFileName(fileName);
        this.setType("Apks");
    }

    public FileViewItem(File file) {
        super(file);
        this.fileDes = Converter.getFileDes(file);
        this.fileLoc = file.getPath();
        this.setType("Files");
    }

    protected FileViewItem(Parcel in) {
        super(in);
        fileLoc = in.readString();
        checked = in.readByte() != 0;
        fileDes = in.readString();
        id = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(fileLoc);
        dest.writeByte((byte) (checked ? 1 : 0));
        dest.writeString(fileDes);
        dest.writeLong(id);
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

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getFileLoc() {
        return fileLoc;
    }

    public boolean isChecked() {
        return checked;
    }

    public String getFileDes() {
        return fileDes;
    }

    public Drawable getFileDrawable() {
        return fileDrawable;
    }

    public void setFileDrawable(Drawable fileDrawable) {
        this.fileDrawable = fileDrawable;
    }

    public long getId() {
        return id;
    }

}
