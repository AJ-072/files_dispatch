package com.aj.filesdispatch.Repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.aj.filesdispatch.DatabaseHelper.FileItemDatabase;
import com.aj.filesdispatch.Entities.FileItem;
import com.aj.filesdispatch.Interface.FileItemDao;

import java.util.List;

public class FileItemRepository {
    private FileItemDao fileItemDao;
    private LiveData<List<FileItem>> listLiveData;

    public FileItemRepository(Application application) {
        FileItemDatabase database = FileItemDatabase.getInstance(application);
        fileItemDao = database.fileItemDao();
        listLiveData = fileItemDao.getAllFilesItems("Applications");
    }

    public void InsertFileItems(FileItem item) {
        new InsertFileItemsAsync(fileItemDao).execute(item);
    }

    public void updateFileItem(FileItem item) {
        new UpdateFileItemsAsync(fileItemDao).execute(item);
    }

    public void deleteFileItem(FileItem item) {
        new DeleteFileItemsAsync(fileItemDao).execute(item);
    }

    public void deleteAllFileItem() {
        new DeleteAllAsync(fileItemDao).execute();
    }

    public LiveData<List<FileItem>> getListLiveData() {
        return listLiveData;
    }

    private static class InsertFileItemsAsync extends AsyncTask<FileItem, Void, Void> {
        private FileItemDao fileItemDao;

        InsertFileItemsAsync(FileItemDao fileItemDao) {
            this.fileItemDao = fileItemDao;
        }

        @Override
        protected Void doInBackground(FileItem... fileItems) {
            fileItemDao.insertFileItem(fileItems[0]);
            return null;
        }
    }

    private static class UpdateFileItemsAsync extends AsyncTask<FileItem, Void, Void> {
        private FileItemDao fileItemDao;

        UpdateFileItemsAsync(FileItemDao fileItemDao) {
            this.fileItemDao = fileItemDao;
        }

        @Override
        protected Void doInBackground(FileItem... fileItems) {
            fileItemDao.updateFileItem(fileItems[0]);
            return null;
        }
    }

    private static class DeleteFileItemsAsync extends AsyncTask<FileItem, Void, Void> {
        private FileItemDao fileItemDao;

        DeleteFileItemsAsync(FileItemDao fileItemDao) {
            this.fileItemDao = fileItemDao;
        }

        @Override
        protected Void doInBackground(FileItem... fileItems) {
            fileItemDao.deleteFileItem(fileItems[0]);
            return null;
        }
    }

    private static class DeleteAllAsync extends AsyncTask<Void, Void, Void> {
        private FileItemDao fileItemDao;

        DeleteAllAsync(FileItemDao fileItemDao) {
            this.fileItemDao = fileItemDao;
        }

        @Override
        protected Void doInBackground(Void... Void) {
            fileItemDao.deleteAll();
            return null;
        }
    }

}
