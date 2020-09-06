package com.aj.filesdispatch.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.aj.filesdispatch.Entities.FileItem;
import com.aj.filesdispatch.Repository.FileItemRepository;

import java.util.List;

public class FileItemViewModel extends AndroidViewModel {
    private FileItemRepository repository;
    private LiveData<List<FileItem>> fileItems;

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

    public LiveData<List<FileItem>> getFileItems() {
        return fileItems;
    }
}
