package com.aj.filesdispatch.Fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
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
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aj.filesdispatch.Entities.FileItem;
import com.aj.filesdispatch.Entities.FileItemBuilder;
import com.aj.filesdispatch.Interface.AddItemToShare;
import com.aj.filesdispatch.R;
import com.aj.filesdispatch.RecyclerAdapter.VideoAdapter;
import com.aj.filesdispatch.common.Converter;

import java.io.File;
import java.util.ArrayList;

public class CliVideos extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "videos";
    public static final String VIDEOS = "Videos";
    private final static int MEDIASTORE_LOADER_ID = 0;
    private Context context;
    private RecyclerView videoRecycler;
    private RecyclerView.LayoutManager layoutManager;
    private TextView noVideoText;
    private ProgressBar videoLoading;
    private OnBackPressedCallback backPressedCallback;
    private VideoAdapter videoAdapter;
    private ArrayList<FileItem> videoItems;

    private String mParam1;
    private String mParam2;

    public CliVideos() {
        // Required empty public constructor
    }

    public static CliVideos newInstance(String param1, String param2) {
        CliVideos fragment = new CliVideos();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        videoItems = new ArrayList<>();
        context = requireContext().getApplicationContext();
        videoAdapter = new VideoAdapter(getActivity());
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cli_videos, container, false);
        videoRecycler = view.findViewById(R.id.video_list);
        noVideoText = view.findViewById(R.id.no_videos_text);
        videoLoading = view.findViewById(R.id.video_loading);
        layoutManager = new LinearLayoutManager(context);
        LoaderManager.getInstance(this).initLoader(MEDIASTORE_LOADER_ID, null, this);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(getResources().getDrawable(R.drawable.line_divider, getActivity().getTheme()));
        videoRecycler.addItemDecoration(itemDecoration);
        videoRecycler.setLayoutManager(layoutManager);
        videoRecycler.setHasFixedSize(true);
        Log.d(TAG, "onCreateView: OncreateView");
        videoRecycler.setAdapter(videoAdapter);
        return view;
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
                context,
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                MediaStore.Files.FileColumns.DATE_ADDED + " DESC"
        );
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        videoLoading.setVisibility(View.GONE);
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
            noVideoText.setVisibility(View.GONE);
            videoRecycler.setVisibility(View.VISIBLE);
            videoAdapter.setVideoItems(data);

        } else {
            noVideoText.setVisibility(View.VISIBLE);
            videoRecycler.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        videoAdapter.setVideoItems((Cursor) null);
        videoAdapter.setVideoItems((ArrayList<FileItem>) null);
    }
}