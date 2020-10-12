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

import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;

import com.aj.filesdispatch.DatabaseHelper.FileItemDatabase;
import com.aj.filesdispatch.Entities.FileItem;
import com.aj.filesdispatch.Entities.FileItemBuilder;
import com.aj.filesdispatch.Interface.FileItemDao;
import com.aj.filesdispatch.common.Converter;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AppListRepository {
    private FileItemDao fileItemDao;
    private LiveData<List<FileItem>> listLiveData;
    public static final String APPLICATION = "Applications";
    private static final String TAG = "FileItemRepository";
    private Context context;
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

    public synchronized void UpdateList(List<FileItem> items) {
        if (updateList == null) {
            updateList = new UpdateList(fileItemDao, items,context);
            updateList.execute();
        }
    }


    static class UpdateList extends AsyncTask<Void, Void, Void> {
        private FileItemDao fileItemDao;
        private List<FileItem> fileItems;
        private WeakReference<Context> contextWeakReference;
        UpdateList(FileItemDao fileItemDao, List<FileItem> fileItems, Context context) {
            this.fileItemDao = fileItemDao;
            this.fileItems = fileItems;
            contextWeakReference= new WeakReference<>(context);
        }

        @Override
        protected Void doInBackground(Void... Void) {
            String packageName;
            Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            List<ResolveInfo> resolveInfo = contextWeakReference.get().getPackageManager().queryIntentActivities(intent, 0);
            if (fileItems == null || fileItems.size() == 0) {
                for (ResolveInfo resolve : resolveInfo) {
                    ActivityInfo activityInfo = resolve.activityInfo;
                    if ((resolve.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                        packageName = activityInfo.packageName;
                        File file = new File(Objects.requireNonNull(getSourceDir(packageName,contextWeakReference.get())));
                        fileItemDao.insertFileItem(
                                new FileItemBuilder(packageName)
                                        .setFileName(getAppName(packageName,contextWeakReference.get()))
                                        .setFileSize(file.length())
                                        .setDateAdded(file.lastModified())
                                        .setFileType(APPLICATION)
                                        .setFileUri(getSourceDir(packageName,contextWeakReference.get()))
                                        .setShowDes(Converter.SizeInGMK(file.length()))
                                        .build());
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
                        File file = new File(Objects.requireNonNull(getSourceDir(packageName,contextWeakReference.get())));
                        if (item.getDateAdded() != file.lastModified()) {
                            fileItemDao.updateFileItem(
                                    new FileItemBuilder(packageName)
                                            .setFileName(getAppName(packageName,contextWeakReference.get()))
                                            .setFileSize(file.length())
                                            .setDateAdded(file.lastModified())
                                            .setFileType(APPLICATION)
                                            .setFileUri(getSourceDir(packageName,contextWeakReference.get()))
                                            .setShowDes(Converter.SizeInGMK(file.length()))
                                            .build());

                        }
                    } else {
                        fileItemDao.deleteFileItem(item);
                    }
                    packageNames.remove(packageName);
                }
                Log.d(TAG, "doInBackground: " + packageNames.size());
                if (packageNames.size() > 0) {
                    for (String pack : packageNames) {
                        File file = new File(Objects.requireNonNull(getSourceDir(pack,contextWeakReference.get())));
                        fileItemDao.insertFileItem(
                                new FileItemBuilder(pack)
                                .setFileName(getAppName(pack,contextWeakReference.get()))
                                .setFileSize(file.length())
                                .setDateAdded(file.lastModified())
                                .setFileType(APPLICATION)
                                .setFileUri(getSourceDir(pack,contextWeakReference.get()))
                                .setShowDes(Converter.SizeInGMK(file.length()))
                                .build());
                    }
                }
            }
            return null;
        }
    }

    private static String getAppName(String pack_name,Context context) {
        try {
            return (String) context.getPackageManager().getApplicationLabel(context.getPackageManager().getApplicationInfo(pack_name, 0));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getSourceDir(String pack_name,Context context) {
        try {
            return context.getPackageManager().getApplicationInfo(pack_name, 0).sourceDir;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
