package com.aj.filesdispatch.Fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aj.filesdispatch.DataSource.AppListProvider;
import com.aj.filesdispatch.DatabaseHelper.AdToDatabase;
import com.aj.filesdispatch.Interface.AddItemToShare;
import com.aj.filesdispatch.Models1.FileViewItem;
import com.aj.filesdispatch.R;
import com.aj.filesdispatch.RecyclerAdapter.AppAdapter;

import java.util.ArrayList;
import java.util.Collections;

import static com.aj.filesdispatch.DatabaseHelper.DatabaseHelper.FILE_ADDED_DATE;
import static com.aj.filesdispatch.DatabaseHelper.DatabaseHelper._ID;

public class CliApp extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG = "AppsInstalled";
    public static final int CACHE_LOAD = 72;
    public static final int RElOAD = 101;
    private RecyclerView appRecyler;
    private ProgressBar appLoadig;
    private AppAdapter adapter;
    private TextView noAppText;
    private Context context;
    private OnBackPressedCallback backPressedCallback;
    private AddItemToShare appToShare;
    private ArrayList<FileViewItem> appList;
    private LoaderManager loaderManager;
    private String mParam1;
    private String mParam2;
    private Cursor data;

    public CliApp(AddItemToShare appToShare) {
        this.appToShare = appToShare;
    }

    public CliApp() {

    }


    public static CliApp newInstance(String param1, String param2) {
        CliApp fragment = new CliApp();
        Bundle args = new Bundle();
        Log.d(TAG, "newInstance: valled");
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appList = new ArrayList<>();
        context = getContext();
        loaderManager = LoaderManager.getInstance(this);
        adapter = new AppAdapter(context, appToShare);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cli_app, container, false);
        appRecyler = view.findViewById(R.id.app_recycler);
        appLoadig = view.findViewById(R.id.app_loading);
        noAppText = view.findViewById(R.id.no_app_installed);
        loaderManager.initLoader(CACHE_LOAD, null, this);
        loaderManager.restartLoader(RElOAD, null, this);
        appRecyler.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context, 4);
        appRecyler.setLayoutManager(layoutManager);

        appRecyler.setAdapter(adapter);
        return view;
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        if (id == CACHE_LOAD)
            return new AsyncTaskLoader<Cursor>(this.context) {
                @Override
                protected void onStartLoading() {
                    forceLoad();
                }

                @Nullable
                @Override
                public Cursor loadInBackground() {
                    return new AdToDatabase(getContext()).getAllItems();
                }
            };
        else
            return new AppListProvider(context);

    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        appLoadig.setVisibility(View.INVISIBLE);
            if (appRecyler != null)
                if (data != null && data.getCount() > 0) {
                    Log.d(TAG, "onLoadFinished: cached data");
                    appRecyler.setVisibility(View.VISIBLE);
                    for (int i = 0; i < (Math.min(data.getCount(), 50)); i++) {
                        if (data.moveToPosition(i))
                            appList.add(new FileViewItem(data, data.getString(data.getColumnIndexOrThrow(_ID))).setDrawable(AppListProvider.getAppIcon(data.getString(data.getColumnIndexOrThrow(_ID)), context)));
                    }
                    adapter.setApplists(data);
                    adapter.setApplists(appList);
                } else {
                    noAppText.setVisibility(View.VISIBLE);
                }
        /*} else {
            Log.d(TAG, "onLoadFinished: new data");
            if (this.data != null&&data!=null) {
                Log.d(TAG, "onLoadFinished: new data not null");
                if (data.moveToFirst() && this.data.moveToFirst()) {
                    Log.d(TAG, "onLoadFinished: move to first");
                    do {
                        if (data.getString(data.getColumnIndexOrThrow(_ID)).equals(this.data.getString(this.data.getColumnIndexOrThrow(_ID)))) {
                            Log.d(TAG, "onLoadFinished: equal");
                            if (!(data.getString(data.getColumnIndexOrThrow(FILE_ADDED_DATE)).equals(this.data.getString(this.data.getColumnIndexOrThrow(FILE_ADDED_DATE))))) {
                                Log.d(TAG, "onLoadFinished: update");
                                updateData(data);
                            }
                        } else {
                            Log.d(TAG, "onLoadFinished: data not present");
                            appList.add(data.getPosition(), new FileViewItem(data, data.getString(data.getColumnIndexOrThrow(_ID))));
                        }
                    } while (data.moveToNext() && this.data.moveToNext());
                    Log.d(TAG, "onLoadFinished: exist loop");
                    if (data.getCount() != this.data.getCount()) {
                        Log.d(TAG, "onLoadFinished: count unequal");
                        if (data.getCount() > this.data.getCount()) {
                            Log.d(TAG, "onLoadFinished: new data added");
                            for (int i = this.data.getCount(); i < data.getCount(); i++) {
                                Log.d(TAG, "onLoadFinished: adding");
                                data.moveToPosition(i);
                                appList.add(new FileViewItem(data, data.getString(data.getColumnIndexOrThrow(_ID))));
                                adapter.notifyItemInserted(i);
                            }
                        } else
                            for (int i = data.getCount(); i < this.data.getCount(); i++) {
                                Log.d(TAG, "onLoadFinished: data removing");
                                appList.remove(i);
                                adapter.notifyItemRemoved(i);
                            }

                    }
                }
            }else if (data==null){
                Log.d(TAG, "onLoadFinished: new data is null");
                adapter.setApplists((Cursor) null);
                adapter.setApplists((ArrayList<FileViewItem>) null);
                noAppText.setVisibility(View.VISIBLE);
            }else{
                Log.d(TAG, "onLoadFinished: old data is null");
                appRecyler.setVisibility(View.VISIBLE);
                for (int i = 0; i < (Math.min(data.getCount(), 50)); i++) {
                    if (data.moveToPosition(i))
                        appList.add(new FileViewItem(data, data.getString(data.getColumnIndexOrThrow(_ID))).setDrawable(AppListProvider.getAppIcon(data.getString(data.getColumnIndexOrThrow(_ID)), context)));
                }
                adapter.setApplists(data);
                adapter.setApplists(appList);
            }
*/

            /*if (data != null && data.getCount() > 0) {
                appRecyler.setVisibility(View.VISIBLE);
                for (int i = 0; i < (Math.min(data.getCount(), 30)); i++) {
                    if (data.moveToPosition(i))
                        appList.add(new FileViewItem(data, data.getString(data.getColumnIndexOrThrow(_ID))).setDrawable(AppListProvider.getAppIcon(data.getString(data.getColumnIndexOrThrow(_ID)), context)));
                }
                adapter.setApplists(data);
                adapter.setApplists(appList);
            } else {
                noAppText.setVisibility(View.VISIBLE);
            }*/
       // }

    }

    private void updateData(Cursor data) {
        int i = data.getPosition();
        if (appList.size() > i) {
            if (appList.get(i) != null) {
                appList.get(i).update(data);
            } else
                appList.set(i, new FileViewItem(data, data.getString(data.getColumnIndexOrThrow(_ID)))
                        .setDrawable(AppListProvider.getAppIcon(data.getString(data.getColumnIndexOrThrow(AdToDatabase._ID)),context)));
        } else if (appList.size() == i) {
            appList.add(i, new FileViewItem(data, data.getString(data.getColumnIndexOrThrow(_ID)))
                    .setDrawable(AppListProvider.getAppIcon(data.getString(data.getColumnIndexOrThrow(AdToDatabase._ID)),context)));
        } else {
            appList.addAll(Collections.nCopies(appList.size() - i, null));
            appList.add(i, new FileViewItem(data, data.getString(data.getColumnIndexOrThrow(_ID)))
                    .setDrawable(AppListProvider.getAppIcon(data.getString(data.getColumnIndexOrThrow(AdToDatabase._ID)),context)));
        }
        adapter.notifyItemChanged(i);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        adapter.setApplists((Cursor) null);
        adapter.setApplists((ArrayList<com.aj.filesdispatch.Models1.FileViewItem>) null);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: called");
        backPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Log.d(TAG, "handleOnBackPressed: backpressed");
                new AlertDialog.Builder(CliApp.this.requireContext())
                        .setIcon(R.drawable.ic_logo)
                        .setTitle("Are you Sure!")
                        .setPositiveButton("yes", (dialog, which) -> {
                            dialog.dismiss();
                            this.remove();
                            requireActivity().getOnBackPressedDispatcher().onBackPressed();
                        })
                        .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                        .create().show();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, backPressedCallback);
    }

    @Override
    public void onPause() {
        super.onPause();
        backPressedCallback.setEnabled(false);
        Log.d(TAG, "onPause: called");
    }

    @Override
    public void onStop() {
        super.onStop();
        if (backPressedCallback != null)
            backPressedCallback.remove();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        //outState.putParcelableArrayList(TAG, (ArrayList<? extends Parcelable>) data);
        super.onSaveInstanceState(outState);
    }
}