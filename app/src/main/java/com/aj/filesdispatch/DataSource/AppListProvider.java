package com.aj.filesdispatch.DataSource;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.aj.filesdispatch.DatabaseHelper.AdToDatabase;

import java.io.File;
import java.util.List;
import java.util.Objects;

public class AppListProvider extends AsyncTaskLoader<Cursor> {

    public AppListProvider(@NonNull Context context) {
        super(context.getApplicationContext());
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public Cursor loadInBackground() {

        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        List<ResolveInfo> resolveInfo = getContext().getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo resolve : resolveInfo) {
            ActivityInfo activityInfo = resolve.activityInfo;
            if ((resolve.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                String packageName = activityInfo.packageName;
                File file = new File(Objects.requireNonNull(getSourceDir(packageName)));
            }
        }
        return null;
    }

    private String getAppName(String pack_name) {
        try {
            return (String) getContext().getPackageManager().getApplicationLabel(getContext().getPackageManager().getApplicationInfo(pack_name, 0));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Drawable getAppIcon(String pack_name, Context context) {
        try {
            return context.getPackageManager().getApplicationIcon(pack_name);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getSourceDir(String pack_name) {
        try {
            return getContext().getPackageManager().getApplicationInfo(pack_name, 0).sourceDir;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
