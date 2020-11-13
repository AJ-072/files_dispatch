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
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aj.filesdispatch.Entities.FileItem;
import com.aj.filesdispatch.Entities.FileItemBuilder;
import com.aj.filesdispatch.Interface.AddItemToShare;
import com.aj.filesdispatch.R;
import com.aj.filesdispatch.RecyclerAdapter.FileAdapter;
import com.aj.filesdispatch.common.Converter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static androidx.core.content.ContextCompat.getDrawable;
import static com.aj.filesdispatch.Fragments.CliDoc.DOCUMENT;

public class CliFiles extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<FileItem>>, FileAdapter.OnItemClickToOpen, AddItemToShare {

    private static final String TAG = "CliFiles";
    public static final int FILE_LOADER_ID = 101;
    public static final String FOLDER = "Folder";
    private RecyclerView contentRecyclerView;
    private AppCompatTextView noContentText;
    private ContentLoadingProgressBar contentLoading;
    private FileAdapter adapter;
    private File dir;
    private List<FileItem> selectedFiles = new ArrayList<>();
    private ArrayList<FileItem> fileData = new ArrayList<>();
    private AddItemToShare fileToShare;
    private LoaderManager loaderManager;
    private OnBackPressedCallback OnBackPressedCallback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fileToShare= (AddItemToShare) getActivity();
        View view = inflater.inflate(R.layout.fragment_content, container, false);
        contentRecyclerView = view.findViewById(R.id.content_recycler);
        contentLoading = view.findViewById(R.id.progress_bar);
        noContentText = view.findViewById(R.id.no_content_text);
        contentLoading.setVisibility(View.VISIBLE);
        noContentText.setVisibility(View.GONE);
        noContentText.setText(getText(R.string.no_file_found));
        contentRecyclerView.setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dir = new File(String.valueOf(Environment.getExternalStorageDirectory()));
        loaderManager = LoaderManager.getInstance(this);
        loaderManager.initLoader(FILE_LOADER_ID, null, this);
        RecyclerView.LayoutManager file_layoutManger = new LinearLayoutManager(requireContext());
        contentRecyclerView.setLayoutManager(file_layoutManger);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(Objects.requireNonNull(getDrawable(requireContext(), R.drawable.line_divider)));
        contentRecyclerView.addItemDecoration(itemDecoration);
        adapter = new FileAdapter(requireContext(), this, this);
        contentRecyclerView.setAdapter(adapter);
    }

    @NonNull
    @Override
    public Loader<ArrayList<FileItem>> onCreateLoader(int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<ArrayList<FileItem>>(requireContext()) {
            @Override
            protected void onStartLoading() {
                forceLoad();
            }

            @Override
            public ArrayList<FileItem> loadInBackground() {
                File[] files = dir.listFiles();
                fileData.clear();
                if (files != null) {
                    for (File f : files) {
                        boolean selected = false;
                        for (FileItem item : selectedFiles) {
                            if (item.getFileUri().equals(f.getPath())) {
                                fileData.add(item);
                                selected = true;
                                break;
                            }
                        }
                        if (!selected)
                            fileData.add(new FileItemBuilder(f.getPath())
                                    .setFileName(f.getName())
                                    .setFileSize(f.isFile() ? f.length() : 0)
                                    .setFileType(f.isFile() ? DOCUMENT : FOLDER)
                                    .setDateAdded(f.lastModified())
                                    .setFileUri(f.getPath())
                                    .setShowDes(Converter.getFileDes(f))
                                    .build());
                    }
                }
                return fileData;
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<FileItem>> loader, ArrayList<FileItem> data) {
        contentLoading.setVisibility(View.GONE);
        if (data.size() > 0) {
            adapter.setData(data);
            contentRecyclerView.setVisibility(View.VISIBLE);
            noContentText.setVisibility(View.INVISIBLE);
        } else {
            noContentText.setVisibility(View.VISIBLE);
            contentRecyclerView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<FileItem>> loader) {
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
    public void OnClick(FileItem item, int position) {
        File clicked_file = new File(item.getFileUri());
        if (clicked_file.isDirectory()) {
            if (item.isChecked()) {
                fileToShare.onItemAdded(item);
                selectedFiles.add(item);
                adapter.notifyItemChanged(position);
            } else {
                dir = clicked_file;
                loaderManager.restartLoader(FILE_LOADER_ID, null, this);
                contentLoading.setVisibility(View.VISIBLE);
            }
        } else {
            selectedFiles.add(item);
            fileToShare.onItemAdded(item);
            adapter.notifyItemChanged(position);
        }
    }

    @Override
    public void onItemAdded(FileItem item) {
        if (selectedFiles.contains(item))
            selectedFiles.remove(item);
        else

            selectedFiles.add(item);
        fileToShare.onItemAdded(item);
    }

    @Override
    public void onMultiItemAdded(List<FileItem> fileViewItems) {
        if (selectedFiles.containsAll(fileViewItems))
            selectedFiles.removeAll(fileViewItems);
        else

            selectedFiles.addAll(fileViewItems);
        fileToShare.onMultiItemAdded(fileViewItems);
    }
}