package com.aj.filesdispatch.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.aj.filesdispatch.Entities.FileItem;
import com.aj.filesdispatch.Repository.AppListRepository;

import java.util.List;

public class AppListViewModel extends AndroidViewModel {
    private LiveData<List<FileItem>> fileItems;
    private AppListRepository repository;
    private static final String TAG = "FileItemViewModel";

    public AppListViewModel(@NonNull Application application) {
        super(application);
        repository = new AppListRepository(application);
        fileItems = repository.getListLiveData();
    }

    public LiveData<List<FileItem>> getFileItems() {
        return fileItems;
    }

    public void UpdateList(List<FileItem> fileItems) {
        repository.UpdateList(fileItems);
    }
}
