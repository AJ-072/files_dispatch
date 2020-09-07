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
    private LiveData<List<FileItem>> fileItems;
    private static final String TAG = "FileItemViewModel";

    public FileItemViewModel(@NonNull Application application) {
        super(application);
        FileItemRepository repository = new FileItemRepository(application);
        fileItems = repository.getListLiveData();
    }

    public LiveData<List<FileItem>> getFileItems() {
        return fileItems;
    }
}
