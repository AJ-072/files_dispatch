package com.aj.filesdispatch.DatabaseHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.MediaStore;

import androidx.annotation.Nullable;

import com.aj.filesdispatch.Entities.FileItem;
import com.aj.filesdispatch.Entities.FileItemBuilder;
import com.aj.filesdispatch.Entities.SentFileItem;
import com.aj.filesdispatch.common.Converter;

import java.io.File;

import static com.aj.filesdispatch.Fragments.CliImages.IMAGES;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String TABLE_ITEMS = "History";
    public static final String _ID = "ID";
    public String ID_VALUE;
    public static final String FILE_NAME = "File_name";
    public static final String FILE_SIZE = "File_size";
    public static final String FILE_TYPE = "Mime_type";
    public static final String FILE_LOC = "File_location";
    public static final String FILE_ADDED_DATE = "File_Added";
    public static final String FILE_SENDER = "sender";

    public DatabaseHelper(@Nullable Context context, int version) {
        super(context, TABLE_ITEMS, null, version);

    }

    public void setIdValue(String idValue) {
        ID_VALUE = idValue;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlCommand = "CREATE TABLE " + TABLE_ITEMS + "(" + _ID + " TEXT,"
                + FILE_NAME + " TEXT,"
                + FILE_SIZE + " LONG,"
                + FILE_ADDED_DATE + " LONG,"
                + FILE_TYPE + " TEXT,"
                + FILE_LOC + " TEXT,"
                + FILE_SENDER + " TEXT)";
        db.execSQL(sqlCommand);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sqlCommand = "DROP TABLE IF EXISTS " + TABLE_ITEMS;
        db.execSQL(sqlCommand);
        onCreate(db);
    }

    public void addItem(FileItem item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FILE_NAME, item.getFileName());
        values.put(FILE_SIZE, item.getFileSize());
        values.put(FILE_TYPE, item.getFileType());
        values.put(FILE_LOC, item.getFileUri());
        values.put(FILE_SENDER, "ME");
        db.insert(TABLE_ITEMS, null, values);
        db.close();
    }

    public void addItem(SentFileItem item) {
        SQLiteDatabase db = this.getWritableDatabase();


        ContentValues values = new ContentValues();
        values.put(_ID, ID_VALUE);
        values.put(FILE_NAME, item.getFileName());
        values.put(FILE_SIZE, item.getFileSize());
        values.put(FILE_ADDED_DATE, System.currentTimeMillis());
        values.put(FILE_TYPE, item.getFileType());
        values.put(FILE_LOC, item.getFileUri());
        values.put(FILE_SENDER, item.getSender());
        db.insert(TABLE_ITEMS, null, values);
        db.close();
    }

    // code to get the single contact
    public FileItem getFile(String Loc) {
        SQLiteDatabase db = this.getReadableDatabase();
        FileItem item = null;

        Cursor cursorData = db.query(TABLE_ITEMS, new String[]{_ID, FILE_NAME,
                        FILE_SIZE, FILE_TYPE, FILE_LOC, FILE_SENDER}, FILE_LOC + "=?",
                new String[]{String.valueOf(Loc)}, null, null, null, null);
        if (cursorData.moveToFirst())
            item = new FileItemBuilder(cursorData.getString(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)))
                    .setFileName(cursorData.getString(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)))
                    .setFileSize(cursorData.getLong(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)))
                    .setFileType(IMAGES)
                    .setDateAdded(cursorData.getLong(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED)))
                    .setFileUri(cursorData.getString(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)))
                    .setShowDes(Converter.getFileDes(new File(cursorData.getString(cursorData.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)))))
                    .build();
        return item;
    }


    public Cursor getAllItems() {
        String selectQuery = "SELECT  * FROM " + TABLE_ITEMS;
        SQLiteDatabase db = this.getWritableDatabase();

        return db.rawQuery(selectQuery, null);
    }

    public int updateItem(FileItem item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FILE_NAME, item.getFileName());
        values.put(FILE_SIZE, item.getFileSize());
        int result = db.update(TABLE_ITEMS, values, FILE_LOC + " = ?",
                new String[]{String.valueOf(item.getFileUri())});
        db.close();
        // updating row
        return result;
    }

    public void deleteItem(FileItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ITEMS, FILE_LOC + " = ?",
                new String[]{String.valueOf(item.getFileUri())});
        db.close();
    }

    public int getCount() {
        String countQuery = "SELECT  * FROM " + TABLE_ITEMS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public Cursor getSenders() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(true, TABLE_ITEMS, new String[]{_ID}, null, null, null, null, FILE_ADDED_DATE + " DESC", null);
    }

    public Cursor getListForSender(String ID_VALUE) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_ITEMS, null, DatabaseHelper._ID + " = " + ID_VALUE, null, null, null, FILE_ADDED_DATE + " DESC");
    }
}
