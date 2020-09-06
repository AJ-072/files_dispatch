package com.aj.filesdispatch.DatabaseHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.aj.filesdispatch.Models1.FileViewItem;

import java.util.ArrayList;
import java.util.List;

public class AdToDatabase extends SQLiteOpenHelper {
    private static final String TABLE_NAME = "cacheViews";
    public static final String _ID = "ID";
    public static final String FILE_NAME = "File_name";
    public static final String FILE_SIZE = "File_size";
    public static final String FILE_TYPE = "Mime_type";
    public static final String FILE_LOC = "File_location";
    public static final String FILE_ADDED_DATE = "File_Added";

    public AdToDatabase(@Nullable Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlCommand = "CREATE TABLE " + TABLE_NAME + "(" + _ID + " TEXT PRIMARY KEY,"
                + FILE_NAME + " TEXT,"
                + FILE_SIZE + " LONG,"
                + FILE_ADDED_DATE + " LONG,"
                + FILE_TYPE + " TEXT,"
                + FILE_LOC + " TEXT)";
        db.execSQL(sqlCommand);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sqlCommand = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(sqlCommand);
        onCreate(db);
    }

    public void addItem(String ID_VALUE, String fileName, long size, String type, String fileLoc,long date) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(_ID, ID_VALUE);
        values.put(FILE_NAME, fileName);
        values.put(FILE_SIZE, size);
        values.put(FILE_TYPE, type);
        values.put(FILE_LOC, fileLoc);
        values.put(FILE_ADDED_DATE, date);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    // code to get the single file
    public FileViewItem getFile(String ID) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, new String[]{_ID, FILE_NAME,
                        FILE_SIZE, FILE_TYPE, FILE_LOC}, _ID + "=?",
                new String[]{String.valueOf(ID)}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            return new FileViewItem(cursor, null);
        } else return null;
    }

    public boolean isExist(String id) {
        return getFile(id) != null;
    }

    public Cursor getAllItems() {
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();

        return db.rawQuery(selectQuery, null);
    }

    public int updateItem(String ID, String fileName, long Size,long date) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FILE_NAME, fileName);
        values.put(FILE_SIZE, Size);
        values.put(FILE_ADDED_DATE, date);
        int result = db.update(TABLE_NAME, values, _ID + " = ?",
                new String[]{ID});
        db.close();
        // updating row
        return result;
    }
    public long getDateModified(String id){
        long date=0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor= db.query(TABLE_NAME,new String[]{FILE_ADDED_DATE}, _ID+" = '"+id+"'",null,null,null,null,null);
        if (cursor.moveToFirst()) date= cursor.getLong(cursor.getColumnIndexOrThrow(FILE_ADDED_DATE));
        cursor.close();
        return date;
    }

    public void deleteItem(FileViewItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, FILE_LOC + " = ?",
                new String[]{String.valueOf(item.getFileLoc())});
        db.close();
    }

    public void deleteItem(String ID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, _ID + " = ?",
                new String[]{ID});
        db.close();
    }

    public List<String> getPackages() {
        List<String> packages = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(true, TABLE_NAME, new String[]{_ID}, null, null, null, null, FILE_ADDED_DATE + " DESC", null);
        if (cursor.moveToFirst())
            do {
                packages.add(cursor.getString(cursor.getColumnIndexOrThrow(_ID)));
            } while (cursor.moveToNext());
        cursor.close();
        return packages;
    }

    public int getCount() {
        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

}
