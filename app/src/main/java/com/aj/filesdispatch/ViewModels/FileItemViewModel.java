package com.aj.filesdispatch.ViewModels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.aj.filesdispatch.Entities.FileItem;
import com.aj.filesdispatch.Repository.FileItemRepository;

import java.util.List;

public class FileItemViewModel extends AndroidViewModel {
    private FileItemRepository repository;
    private LiveData<List<FileItem>> fileItems;
    private static final String TAG = "FileItemViewModel";

    public FileItemViewModel(@NonNull Application application) {
        super(application);
        repository = new FileItemRepository(application);
        fileItems = repository.getListLiveData();
    }

    public void insert(FileItem item) {
        repository.InsertFileItems(item);
    }

    public void update(FileItem item) {
        repository.updateFileItem(item);
    }

    public void delete(FileItem item) {
        repository.deleteFileItem(item);
    }

    public void deleteAll() {
        repository.deleteAllFileItem();
    }

    public void UpdateList(List<FileItem> items){
        repository.UpdateListItems(items);
        Log.d(TAG, "UpdateList: "+items.size());
    }

    public LiveData<List<FileItem>> getFileItems() {
        return fileItems;
    }
}
