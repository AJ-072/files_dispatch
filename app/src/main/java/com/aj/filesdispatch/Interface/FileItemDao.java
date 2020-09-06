package com.aj.filesdispatch.Interface;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.aj.filesdispatch.Entities.FileItem;

import java.util.List;

@Dao
interface FileItemDao {
    @Insert
    void insertFileItem(FileItem item);

    @Update
    void updateFileItem(FileItem item);

    @Delete
    void deleteFileItem(FileItem item);

    @Query("DELETE FROM file_item_table")
    void deleteAll(FileItem item);

    @Query("SELECT * FROM file_item_table WHERE fileId =  ORDER BY dateAdded DESC")
    LiveData<List<FileItem>> getAllFilesItems(String i);
}
