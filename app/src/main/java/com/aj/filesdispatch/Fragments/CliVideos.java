package com.aj.filesdispatch.Fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aj.filesdispatch.Entities.FileItem;
import com.aj.filesdispatch.Entities.FileItemBuilder;
import com.aj.filesdispatch.R;
import com.aj.filesdispatch.RecyclerAdapter.VideoAdapter;
import com.aj.filesdispatch.common.Converter;

import java.io.File;
import java.util.ArrayList;

public class CliVideos extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "videos";
    public static final String VIDEOS = "Videos";
    private final static int MEDIASTORE_LOADER_ID = 0;
    private RecyclerView contentRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private AppCompatTextView noContentText;
    private ContentLoadingProgressBar contentLoading;
    private OnBackPressedCallback backPressedCallback;
    private VideoAdapter videoAdapter;
    private ArrayList<FileItem> videoItems;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content, container, false);
        contentRecyclerView = view.findViewById(R.id.content_recycler);
        contentLoading = view.findViewById(R.id.progress_bar);
        noContentText = view.findViewById(R.id.no_content_text);
        contentLoading.setVisibility(View.VISIBLE);
        noContentText.setVisibility(View.GONE);
        noContentText.setText(getText(R.string.no_videos_found));
        contentRecyclerView.setVisibility(View.GONE);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        videoItems = new ArrayList<>();
        videoAdapter = new VideoAdapter(getActivity());
        layoutManager = new LinearLayoutManager(requireContext());
        LoaderManager.getInstance(this).initLoader(MEDIASTORE_LOADER_ID, null, this);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(getResources().getDrawable(R.drawable.line_divider, getActivity().getTheme()));
        contentRecyclerView.addItemDecoration(itemDecoration);
        contentRecyclerView.setLayoutManager(layoutManager);
        contentRecyclerView.setHasFixedSize(true);
        Log.d(TAG, "onCreateView: OncreateView");
        contentRecyclerView.setAdapter(videoAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: called");
        backPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Log.d(TAG, "handleOnBackPressed: backpressed");
                new AlertDialog.Builder(CliVideos.this.requireContext())
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
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (backPressedCallback != null)
            backPressedCallback.remove();
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.SIZE,
                MediaStore.Files.FileColumns.MIME_TYPE,
                MediaStore.Files.FileColumns.TITLE
        };
        return new CursorLoader(
                requireContext(),
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
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
            if (videoItems.size() < Math.min(data.getCount(), 30))
                for (int i = 0; i < Math.min(data.getCount(), 30); i++) {
                    data.moveToPosition(i);
                    videoItems.add(new FileItemBuilder(data.getString(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)))
                            .setFileName(new File(data.getString(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA))).getName())
                            .setFileSize(data.getLong(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)))
                            .setFileType(VIDEOS)
                            .setDateAdded(data.getLong(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED)))
                            .setFileUri(data.getString(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)))
                            .setShowDes(Converter.getFileDes(new File(data.getString(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)))))
                            .build());
                }
            videoAdapter.setVideoItems(videoItems);
            noContentText.setVisibility(View.GONE);
            contentRecyclerView.setVisibility(View.VISIBLE);
            videoAdapter.setVideoItems(data);

        } else {
            noContentText.setVisibility(View.VISIBLE);
            contentRecyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        videoAdapter.setVideoItems((Cursor) null);
        videoAdapter.setVideoItems((ArrayList<FileItem>) null);
    }
}