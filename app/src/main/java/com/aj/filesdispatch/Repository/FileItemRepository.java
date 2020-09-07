package com.aj.filesdispatch.Repository;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListAdapter;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.DiffUtil;

import com.aj.filesdispatch.DatabaseHelper.FileItemDatabase;
import com.aj.filesdispatch.Entities.FileItem;
import com.aj.filesdispatch.Interface.FileItemDao;

import java.io.File;
import java.util.List;
import java.util.Objects;

public class FileItemRepository {
    private FileItemDao fileItemDao;
    private LiveData<List<FileItem>> listLiveData;
    public static final String APPLICATION="Applications";
    private static final String TAG = "FileItemRepository";
    private static Context context;

    public FileItemRepository(Application application) {
        context=application.getApplicationContext();
        FileItemDatabase database = FileItemDatabase.getInstance(application);
        fileItemDao = database.fileItemDao();
        listLiveData = fileItemDao.getAllFilesItems(APPLICATION);
        //new UpdateList(fileItemDao).execute();
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

    private static class UpdateList extends AsyncTask<Void, Void, Void> {
        private FileItemDao fileItemDao;
        private List<FileItem> fileItems;

        UpdateList(FileItemDao fileItemDao,List<FileItem> fileItems) {
            this.fileItemDao = fileItemDao;
            this.fileItems=fileItems;
        }

        @Override
        protected Void doInBackground(Void... Void) {
            Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            List<ResolveInfo> resolveInfo = context.getPackageManager().queryIntentActivities(intent, 0);
            if (fileItems==null||fileItems.size()==0){
                for (ResolveInfo resolve : resolveInfo) {
                    ActivityInfo activityInfo = resolve.activityInfo;
                    if ((resolve.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                        String packageName = activityInfo.packageName;
                        File file = new File(Objects.requireNonNull(getSourceDir(packageName)));
                        fileItemDao.insertFileItem(new FileItem(packageName
                                ,getAppName(packageName),file.length(),
                                file.getPath(),APPLICATION,file.lastModified()));
                    }
                }
            }else{
                for (FileItem item:fileItems){
                    DiffUtil.ItemCallback<FileItem> itemItemCallback=new DiffUtil.ItemCallback<FileItem>() {
                        @Override
                        public boolean areItemsTheSame(@NonNull FileItem oldItem, @NonNull FileItem newItem) {
                            return false;
                        }

                        @Override
                        public boolean areContentsTheSame(@NonNull FileItem oldItem, @NonNull FileItem newItem) {
                            return false;
                        }
                    };
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

    public Drawable getAppIcon(String pack_name) {
        try {
            return context.getPackageManager().getApplicationIcon(pack_name);
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
