package com.aj.filesdispatch.Fragments;

import android.os.Bundle;
import android.os.Environment;
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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aj.filesdispatch.Interface.AddItemToShare;
import com.aj.filesdispatch.Models.FileViewItem;
import com.aj.filesdispatch.R;
import com.aj.filesdispatch.RecyclerAdapter.FileAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static androidx.core.content.ContextCompat.getDrawable;

public class CliFiles extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<FileViewItem>>, FileAdapter.OnItemClickToOpen, AddItemToShare {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "CliFiles";
    public static final int FILE_LOADER_ID = 101;
    private RecyclerView fileRecycler;
    private TextView noFileText;
    private ProgressBar filesLoading;
    private FileAdapter adapter;
    private File dir;
    private List<FileViewItem> selectedFiles = new ArrayList<>();
    private ArrayList<FileViewItem> fileData = new ArrayList<>();
    private AddItemToShare fileToShare;
    private LoaderManager loaderManager;
    private ViewGroup container;
    private OnBackPressedCallback OnBackPressedCallback;

    private String mParam1;
    private String mParam2;

    public CliFiles() {
        // Required empty public constructor
    }

    public CliFiles(AddItemToShare fileToShare) {
        this.fileToShare = fileToShare;
    }

    public static CliFiles newInstance(String param1, String param2) {
        CliFiles fragment = new CliFiles();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.container = container;
        View view = inflater.inflate(R.layout.fragment_cli_files, container, false);
        fileRecycler = view.findViewById(R.id.file_recycler);
        noFileText = view.findViewById(R.id.no_file_text);
        filesLoading = view.findViewById(R.id.file_loading);
        dir = new File(String.valueOf(Environment.getExternalStorageDirectory()));
        loaderManager = LoaderManager.getInstance(this);
        loaderManager.initLoader(FILE_LOADER_ID, null, this);
        RecyclerView.LayoutManager file_layoutManger = new LinearLayoutManager(container.getContext());
        fileRecycler.setLayoutManager(file_layoutManger);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(container.getContext(), DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(getDrawable(container.getContext(), R.drawable.line_divider));
        fileRecycler.addItemDecoration(itemDecoration);
        adapter = new FileAdapter(container.getContext(), this, this);
        fileRecycler.setAdapter(adapter);
        return view;
    }

    @NonNull
    @Override
    public Loader<ArrayList<FileViewItem>> onCreateLoader(int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<ArrayList<FileViewItem>>(container.getContext()) {
            @Override
            protected void onStartLoading() {
                forceLoad();
            }

            @Override
            public ArrayList<FileViewItem> loadInBackground() {
                File[] files = dir.listFiles();
                fileData.clear();
                if (files != null) {
                    for (File f : files) {
                        boolean selected = false;
                        for (FileViewItem item : selectedFiles) {
                            if (item.getFileLoc().equals(f.getPath())) {
                                fileData.add(item);
                                selected = true;
                                break;
                            }
                        }
                        if (!selected)
                            fileData.add(new FileViewItem(f));
                    }
                }
                return fileData;
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<FileViewItem>> loader, ArrayList<FileViewItem> data) {
        filesLoading.setVisibility(View.GONE);
        if (data.size() > 0) {
            adapter.setData(data);
            fileRecycler.setVisibility(View.VISIBLE);
            noFileText.setVisibility(View.INVISIBLE);
        } else {
            noFileText.setVisibility(View.VISIBLE);
            fileRecycler.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<FileViewItem>> loader) {
        Log.d(TAG, "onLoaderReset: called");
        adapter.setData(null);
    }

    @Override
    public void onResume() {
        adapter.notifyDataSetChanged();
        super.onResume();
        loaderManager = LoaderManager.getInstance(this);
        OnBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Log.d(TAG, "handleOnBackPressed: before " + dir);
                if (dir.equals(new File(String.valueOf(Environment.getExternalStorageDirectory())))) {
                    new AlertDialog.Builder(CliFiles.this.requireContext())
                            .setIcon(R.drawable.ic_logo)
                            .setTitle("Are you Sure!")
                            .setPositiveButton("yes", (dialog, which) -> {
                                dialog.dismiss();
                                this.remove();
                                requireActivity().getOnBackPressedDispatcher().onBackPressed();
                            })
                            .setNegativeButton("No", (dialog, which) -> {
                                dialog.dismiss();
                            })
                            .create().show();

                } else {
                    dir = new File(Objects.requireNonNull(dir.getParent()));
                    loaderManager.restartLoader(FILE_LOADER_ID, null, CliFiles.this);
                }
                Log.d(TAG, "handleOnBackPressed: after " + dir);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, OnBackPressedCallback);
    }

    @Override
    public void onPause() {
        super.onPause();
        OnBackPressedCallback.setEnabled(false);
        Log.d(TAG, "onPause: called");
    }

    @Override
    public void onStop() {
        super.onStop();
        if (OnBackPressedCallback != null)
            OnBackPressedCallback.remove();
    }

    @Override
    public void OnClick(FileViewItem item, int position) {
        File clicked_file = new File(item.getFileLoc());
        if (clicked_file.isDirectory()) {
            if (item.isChecked()) {
                fileToShare.onItemAdded(item);
                selectedFiles.add(item);
                adapter.notifyItemChanged(position);
            } else {
                dir = clicked_file;
                loaderManager.restartLoader(FILE_LOADER_ID, null, this);
                filesLoading.setVisibility(View.VISIBLE);
            }
        } else {
            selectedFiles.add(item);
            fileToShare.onItemAdded(item);
            adapter.notifyItemChanged(position);
        }
    }

    @Override
    public void onItemAdded(FileViewItem item) {
        if (selectedFiles.contains(item))
            selectedFiles.remove(item);
        else

            selectedFiles.add(item);
        fileToShare.onItemAdded(item);
    }

    @Override
    public void onMultiItemAdded(List<FileViewItem> fileViewItems) {
        if (selectedFiles.containsAll(fileViewItems))
            selectedFiles.removeAll(fileViewItems);
        else

            selectedFiles.addAll(fileViewItems);
        fileToShare.onMultiItemAdded(fileViewItems);
    }
}