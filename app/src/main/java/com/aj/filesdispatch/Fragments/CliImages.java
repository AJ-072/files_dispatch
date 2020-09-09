package com.aj.filesdispatch.Fragments;

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
import androidx.core.content.FileProvider;
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


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String IMAGES = "Images";
    private final static int MEDIASTORE_LOADER_ID = 101;
    private static final String TAG = "Cli_images";
    private Context context;
    private RecyclerView imageRecycler;
    private GridLayoutManager layoutManager;
    private ImageAdapter imageAdapter;
    private ProgressBar imageLoading;
    private TextView noImageText;
    private OnBackPressedCallback backPressedCallback;
    private AddItemToShare itemToShare;
    private ArrayList<FileItem> imageItems;

    private String mParam1;
    private String mParam2;

    public CliImages() {
        // Required empty public constructor
    }

    public CliImages(AddItemToShare itemToShare) {
        this.itemToShare = itemToShare;
    }

    public static CliImages newInstance(String param1, String param2) {
        CliImages fragment = new CliImages();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageItems = new ArrayList<>();
        context = requireContext().getApplicationContext();
        DisplayMetrics metrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        imageAdapter = new ImageAdapter(this, itemToShare, metrics.widthPixels, metrics.heightPixels);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cli_images, container, false);

        imageLoading = view.findViewById(R.id.image_loading);
        imageRecycler = view.findViewById(R.id.image_view_list);
        imageRecycler.setVisibility(View.INVISIBLE);
        imageLoading.setVisibility(View.VISIBLE);
        noImageText = view.findViewById(R.id.no_images_text);
        layoutManager = new GridLayoutManager(context, 3);
        LoaderManager.getInstance(this).initLoader(MEDIASTORE_LOADER_ID, null, this);
        imageRecycler.setLayoutManager(layoutManager);
        imageRecycler.setHasFixedSize(true);
        Log.d(TAG, "onCreateView: OncreateView");
        imageRecycler.setAdapter(imageAdapter);
        return view;
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
                context,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                MediaStore.Files.FileColumns.DATE_ADDED + " DESC"
        );
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        imageLoading.setVisibility(View.GONE);
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
            imageRecycler.setVisibility(View.VISIBLE);
            noImageText.setVisibility(View.GONE);
        } else {
            imageRecycler.setVisibility(View.GONE);
            noImageText.setVisibility(View.VISIBLE);
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
                context,
                "com.aj.filesdispatch.fileProvider",
                file);
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(fileUri, "image/*");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }
}