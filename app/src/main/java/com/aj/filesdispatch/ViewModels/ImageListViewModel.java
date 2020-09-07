package com.aj.filesdispatch.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.aj.filesdispatch.Entities.FileItem;
import com.aj.filesdispatch.Repository.AppListRepository;

import java.util.List;

class ImageListViewModel extends AndroidViewModel {
    private LiveData<List<FileItem>> imageList;
    private AppListRepository repository;
    private static final String TAG = "FileItemViewModel";

    public ImageListViewModel(@NonNull Application application) {
        super(application);
        repository = new AppListRepository(application);
        imageList = repository.getListLiveData();
    }

    public LiveData<List<FileItem>> getFileItems() {
        return imageList;
    }

    public void UpdateList(List<FileItem> fileItems) {
        repository.UpdateList(fileItems);
    }
}
