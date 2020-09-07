package com.aj.filesdispatch.Repository;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.aj.filesdispatch.DatabaseHelper.FileItemDatabase;
import com.aj.filesdispatch.Entities.FileItem;
import com.aj.filesdispatch.Interface.FileItemDao;
import com.aj.filesdispatch.common.Converter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AppListRepository {
    private FileItemDao fileItemDao;
    private LiveData<List<FileItem>> listLiveData;
    public static final String APPLICATION = "Applications";
    private static final String TAG = "FileItemRepository";
    private static Context context;
    private UpdateList updateList;

    public AppListRepository(Application application) {
        context = application.getApplicationContext();
        FileItemDatabase database = FileItemDatabase.getInstance(application);
        fileItemDao = database.fileItemDao();
        listLiveData = fileItemDao.getAllFilesItems(APPLICATION);
    }

    public LiveData<List<FileItem>> getListLiveData() {
        return listLiveData;
    }
    public synchronized void UpdateList(List<FileItem> items){
        if (updateList==null){
            updateList= new UpdateList(fileItemDao,items);
            updateList.execute();
        }
    }


    static class UpdateList extends AsyncTask<Void, Void, Void> {
        private FileItemDao fileItemDao;
        private List<FileItem> fileItems;

        UpdateList(FileItemDao fileItemDao, List<FileItem> fileItems) {
            this.fileItemDao = fileItemDao;
            this.fileItems = fileItems;
        }

        @Override
        protected Void doInBackground(Void... Void) {
            String packageName;
            Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            List<ResolveInfo> resolveInfo = context.getPackageManager().queryIntentActivities(intent, 0);
            if (fileItems == null || fileItems.size() == 0) {
                for (ResolveInfo resolve : resolveInfo) {
                    ActivityInfo activityInfo = resolve.activityInfo;
                    if ((resolve.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                        packageName = activityInfo.packageName;
                        File file = new File(Objects.requireNonNull(getSourceDir(packageName)));
                        fileItemDao.insertFileItem(new FileItem(packageName
                                , getAppName(packageName), file.length(),
                                file.getPath(), APPLICATION, file.lastModified(), Converter.SizeInGMK(file.length())));
                    }
                }
            } else {
                List<String> packageNames = new ArrayList<>();
                for (ResolveInfo resolve : resolveInfo) {
                    ActivityInfo activityInfo = resolve.activityInfo;
                    if ((resolve.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                        packageName = activityInfo.packageName;
                        packageNames.add(packageName);
                    }
                }
                for (FileItem item : fileItems) {
                    packageName = item.getFileId();
                    if (packageNames.contains(packageName)) {
                        File file = new File(Objects.requireNonNull(getSourceDir(packageName)));
                        if (item.getDateAdded() != file.lastModified()) {
                            fileItemDao.updateFileItem(new FileItem(packageName
                                    , getAppName(packageName), file.length(),
                                    file.getPath(), APPLICATION, file.lastModified(), Converter.SizeInGMK(file.length())));

                        }
                    } else {
                        fileItemDao.deleteFileItem(item);
                    }
                    packageNames.remove(packageName);
                }
                Log.d(TAG, "doInBackground: " + packageNames.size());
                if (packageNames.size() > 0) {
                    for (String pack : packageNames) {
                        File file = new File(Objects.requireNonNull(getSourceDir(pack)));
                        fileItemDao.insertFileItem(new FileItem(pack
                                , getAppName(pack), file.length(),
                                file.getPath(), APPLICATION, file.lastModified(), Converter.SizeInGMK(file.length())));
                    }
                }
            }
            return null;
        }
    }

    private static String getAppName(String pack_name) {
        try {
            return (String) context.getPackageManager().getApplicationLabel(context.getPackageManager().getApplicationInfo(pack_name, 0));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getSourceDir(String pack_name) {
        try {
            return context.getPackageManager().getApplicationInfo(pack_name, 0).sourceDir;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
