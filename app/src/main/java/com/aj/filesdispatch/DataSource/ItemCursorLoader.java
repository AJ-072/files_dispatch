package com.aj.filesdispatch.DataSource;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.aj.filesdispatch.Models.FileViewItem;

import java.util.ArrayList;
import java.util.List;

public class ItemCursorLoader extends AsyncTaskLoader<List<FileViewItem>> {
    private List<FileViewItem> itemList = new ArrayList<>();
    private Uri uri;
    private String selection, sortOrder;
    private String[] projection, selectionArgs;
    private String type;
    private static final String TAG = "ItemCursorLoader";

    public ItemCursorLoader(@NonNull Context context, String type, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        super(context.getApplicationContext());
        this.uri = uri;
        this.selection = selection;
        this.sortOrder = sortOrder;
        this.projection = projection;
        this.selectionArgs = selectionArgs;
        this.type = type;
    }

    @Override
    protected void onStartLoading() {
        if (itemList.isEmpty()) {
            forceLoad();
            Log.d(TAG, "onStartLoading: forceLoad");
        } else {
            deliverResult(itemList);
            Log.d(TAG, "onStartLoading: deliver Result");
        }
    }

    @Nullable
    @Override
    public List<FileViewItem> loadInBackground() {
        ContentResolver resolver = getContext().getContentResolver();
        Cursor cursor = resolver.query(uri, projection, selection, selectionArgs, sortOrder);
        if (cursor != null) {
            cursor.moveToFirst();
            while (cursor.moveToNext()) {
                Log.d(TAG, "loadInBackground: looping");
                itemList.add(new FileViewItem(cursor, type));
            }
            cursor.close();
        }
        Log.d(TAG, "loadInBackground: " + itemList.size());
        return itemList;
    }
}
