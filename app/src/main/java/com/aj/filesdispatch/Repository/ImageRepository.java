package com.aj.filesdispatch.Repository;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.loader.content.AsyncTaskLoader;

import com.aj.filesdispatch.DatabaseHelper.FileItemDatabase;
import com.aj.filesdispatch.Entities.FileItem;
import com.aj.filesdispatch.Interface.FileItemDao;
import com.aj.filesdispatch.common.Converter;

import java.io.File;
import java.util.List;

class ImageRepository {
    private FileItemDao imageDao;
    private LiveData<List<FileItem>> imageLiveData;
    public static final String IMAGES = "Images";
    private static final String TAG = "FileItemRepository";
    private static Context context;
    private UpdateList updateList;

    public ImageRepository(Application application) {
        context = application.getApplicationContext();
        FileItemDatabase database = FileItemDatabase.getInstance(application);
        imageDao = database.fileItemDao();
        imageLiveData = imageDao.getAllFilesItems(IMAGES);
    }

    public LiveData<List<FileItem>> getListLiveData() {
        return imageLiveData;
    }

    public synchronized void UpdateList(List<FileItem> items) {
        if (updateList == null) {
            updateList = new UpdateList(context, imageDao, items);
            updateList.forceLoad();
        }
    }

    public static class UpdateList extends AsyncTaskLoader<Void> {
        private FileItemDao fileItemDao;
        private List<FileItem> fileItems;

        public UpdateList(@NonNull Context context, FileItemDao fileItemDao, List<FileItem> fileItems) {
            super(context);
            this.fileItemDao = fileItemDao;
            this.fileItems = fileItems;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Nullable
        @Override
        public Void loadInBackground() {
            ContentResolver resolver = getContext().getContentResolver();
            String[] projection = {
                    MediaStore.Files.FileColumns._ID,
                    MediaStore.Files.FileColumns.DATE_ADDED,
                    MediaStore.Files.FileColumns.DATA,
                    MediaStore.Files.FileColumns.SIZE,
                    MediaStore.Files.FileColumns.DISPLAY_NAME
            };
            Cursor cursor = resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    null,
                    null,
                    MediaStore.Files.FileColumns.DATE_ADDED + " DESC");
            if (cursor != null) {
                if (fileItems == null || fileItems.size() == 0) {
                    cursor.moveToFirst();
                    do {
                        fileItemDao.insertFileItem(
                                new FileItem(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)),
                                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)),
                                        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)),
                                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)),
                                        IMAGES, cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_MODIFIED)),
                                        Converter.getFileDes(new File(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA))))));
                    } while (cursor.moveToNext());
                    cursor.close();
                }else{
                }
            }
            return null;
        }
    }
}
