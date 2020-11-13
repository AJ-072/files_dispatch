package com.aj.filesdispatch.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
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
import androidx.core.content.FileProvider;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aj.filesdispatch.Entities.FileItem;
import com.aj.filesdispatch.Entities.FileItemBuilder;
import com.aj.filesdispatch.Interface.AddItemToShare;
import com.aj.filesdispatch.Interface.OnItemClickToOpen;
import com.aj.filesdispatch.R;
import com.aj.filesdispatch.RecyclerAdapter.ImageAdapter;
import com.aj.filesdispatch.common.Converter;

import java.io.File;
import java.util.ArrayList;

public class CliImages extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, OnItemClickToOpen {

    public static final String IMAGES = "Images";
    private final static int MEDIASTORE_LOADER_ID = 101;
    private static final String TAG = "Cli_images";
    private RecyclerView contentRecyclerView;
    private GridLayoutManager layoutManager;
    private ImageAdapter imageAdapter;
    private ContentLoadingProgressBar contentLoading;
    private AppCompatTextView noContentText;
    private OnBackPressedCallback backPressedCallback;
    private ArrayList<FileItem> imageItems;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content, container, false);
        contentRecyclerView = view.findViewById(R.id.content_recycler);
        contentLoading = view.findViewById(R.id.progress_bar);
        noContentText = view.findViewById(R.id.no_content_text);
        contentLoading.setVisibility(View.VISIBLE);
        noContentText.setVisibility(View.GONE);
        noContentText.setText(getText(R.string.no_images));
        contentRecyclerView.setVisibility(View.GONE);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imageItems = new ArrayList<>();
        DisplayMetrics metrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        imageAdapter = new ImageAdapter(this, getActivity(), metrics.widthPixels, metrics.heightPixels);
        layoutManager = new GridLayoutManager(requireContext(), 3);
        LoaderManager.getInstance(this).initLoader(MEDIASTORE_LOADER_ID, null, this);
        contentRecyclerView.setLayoutManager(layoutManager);
        contentRecyclerView.setHasFixedSize(true);
        Log.d(TAG, "onCreateView: OncreateView");
        contentRecyclerView.setAdapter(imageAdapter);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.DATA,
                //MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.Files.FileColumns.SIZE,
                MediaStore.Files.FileColumns.DISPLAY_NAME
        };
        return new CursorLoader(
                requireContext(),
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                MediaStore.Files.FileColumns.DATE_ADDED + " DESC"
        );
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        contentLoading.setVisibility(View.GONE);
        if (data.getCount() > 0) {
            if (imageItems.size() < Math.min(data.getCount(), 30))
                for (int i = 0; i < Math.min(data.getCount(), 30); i++) {
                    data.moveToPosition(i);
                    imageItems.add(new FileItemBuilder(data.getString(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)))
                            .setFileName(data.getString(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)))
                            .setFileSize(data.getLong(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)))
                            .setFileType(IMAGES)
                            .setDateAdded(data.getLong(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED)))
                            .setFileUri(data.getString(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)))
                            .setShowDes(Converter.getFileDes(new File(data.getString(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)))))
                            .build());
                }
            imageAdapter.setImageList(imageItems);
            imageAdapter.setImageList(data);
            contentRecyclerView.setVisibility(View.VISIBLE);
            noContentText.setVisibility(View.GONE);
        } else {
            contentRecyclerView.setVisibility(View.GONE);
            noContentText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        imageAdapter.setImageList((Cursor) null);
        imageAdapter.setImageList((ArrayList<FileItem>) null);
    }

    @Override
    public void onResume() {
        super.onResume();
        backPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Log.d(TAG, "handleOnBackPressed: backpressed");
                new AlertDialog.Builder(CliImages.this.requireContext())
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
    public void OnClick(FileItem item) {
        File file = new File(String.valueOf(item.getFileUri()));
        Uri fileUri = FileProvider.getUriForFile(
                requireContext(),
                "com.aj.filesdispatch.fileProvider",
                file);
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(fileUri, "image/*");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }
}