package com.aj.filesdispatch.DatabaseHelper;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.aj.filesdispatch.Entities.FileItem;
import com.aj.filesdispatch.Interface.FileItemDao;

@Database(entities = {FileItem.class}, version = 1)
public abstract class FileItemDatabase extends RoomDatabase {

    private static FileItemDatabase instance;

    public abstract FileItemDao fileItemDao();

    public static FileItemDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (FileItemDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext()
                            , FileItemDatabase.class, "file_item_database")
                            .fallbackToDestructiveMigration()
                            .addCallback(fileItemCallback)
                            .build();
                }
            }
        }
        return instance;
    }

    private static RoomDatabase.Callback fileItemCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
        }
    };
}
