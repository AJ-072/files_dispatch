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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aj.filesdispatch.R;
import com.aj.filesdispatch.RecyclerAdapter.DocAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CliDoc extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, DocAdapter.onDocclick {


    public static final int DOC_LOADER_ID = 201;
    public static final int DOC_FILE_LOADER_ID = 101;
    public static final String DOCUMENT = "Documents";
    private List<String> type = new ArrayList<>();
    public static int current_id = DOC_LOADER_ID;
    private static final String TAG = "Doc fragment";
    private ContentLoadingProgressBar contentLoading;
    private LoaderManager loaderManager;
    private OnBackPressedCallback OnBackPressedCallback;
    private AppCompatTextView noContentText;
    private DocAdapter adapter;
    private RecyclerView contentRecyclerView;
    private DividerItemDecoration itemDecoration;
    private RecyclerView.LayoutManager gridLayout, linearLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content, container, false);
        contentRecyclerView = view.findViewById(R.id.content_recycler);
        contentLoading = view.findViewById(R.id.progress_bar);
        noContentText = view.findViewById(R.id.no_content_text);
        contentLoading.setVisibility(View.VISIBLE);
        noContentText.setVisibility(View.GONE);
        noContentText.setText(getText(R.string.no_documents_found));
        contentRecyclerView.setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        itemDecoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(getResources().getDrawable(R.drawable.line_divider, requireContext().getTheme()));

        linearLayout = new LinearLayoutManager(requireContext());
        gridLayout = new GridLayoutManager(requireContext(), 3);

        adapter = new DocAdapter(this, current_id, getActivity());
        contentRecyclerView.setHasFixedSize(true);
        contentLoading.setVisibility(View.GONE);
        setList();
    }

    @Override
    public void onResume() {
        super.onResume();
        loaderManager = LoaderManager.getInstance(this);
        OnBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Log.d(TAG, "handleOnBackPressed: called");
                if (current_id == DOC_LOADER_ID) {
                    new AlertDialog.Builder(CliDoc.this.requireContext())
                            .setIcon(R.drawable.ic_logo)
                            .setTitle("Are you Sure!")
                            .setPositiveButton("yes", (dialog, which) -> {
                                dialog.dismiss();
                                this.remove();
                                requireActivity().getOnBackPressedDispatcher().onBackPressed();
                            })
                            .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                            .create().show();
                } else {
                    contentRecyclerView.removeItemDecoration(itemDecoration);
                    setList();
                }
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

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        Log.d(TAG, "onCreateLoader: called");
        StringBuilder selection;
        String[] projection = {
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.SIZE,
                MediaStore.Files.FileColumns.MIME_TYPE,
                MediaStore.Files.FileColumns.TITLE
        };
        selection = new StringBuilder(MediaStore.Files.FileColumns.DATA + " LIKE '%." + type.get(0) + "'");
        for (int i = 1; i < type.size(); i++) {
            String s = type.get(i);
            selection.append(" OR ").append(MediaStore.Files.FileColumns.DATA).append(" LIKE '%.").append(s).append("'");
        }
        return new CursorLoader(requireContext(),
                MediaStore.Files.getContentUri("external"),
                projection, selection.toString(), null,
                MediaStore.Files.FileColumns.DATE_ADDED + " DESC");
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        contentLoading.setVisibility(View.GONE);
        current_id = DOC_FILE_LOADER_ID;
        if (data != null && data.getCount() > 0) {
            setAdapter(linearLayout, current_id);
            adapter.setData(data);
            contentRecyclerView.addItemDecoration(itemDecoration);
            contentRecyclerView.setVisibility(View.VISIBLE);
            noContentText.setVisibility(View.GONE);
        } else {
            contentRecyclerView.setVisibility(View.GONE);
            noContentText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset: called");
        adapter.setData(null);
    }

    @Override
    public void onDoc(int position) {
        if (type != null)
            type.clear();
        switch (position) {
            case 0:
                type = Arrays.asList(getResources().getStringArray(R.array.Archives));
                break;
            case 1:
                type = Arrays.asList(getResources().getStringArray(R.array.Presentations));
                break;
            case 2:
                type = Arrays.asList(getResources().getStringArray(R.array.Documents));
                break;
            case 3:
                type = Arrays.asList(getResources().getStringArray(R.array.EBooks));
                break;

        }
        contentLoading.setVisibility(View.VISIBLE);
        loaderManager.restartLoader(DOC_FILE_LOADER_ID, null, this);

    }

    private void setAdapter(RecyclerView.LayoutManager layoutManager, int current_id) {
        contentRecyclerView.setLayoutManager(layoutManager);
        contentRecyclerView.setAdapter(adapter);
        adapter.setId(current_id);
    }

    private void setList() {
        current_id = DOC_LOADER_ID;
        type = null;
        contentRecyclerView.setVisibility(View.VISIBLE);
        noContentText.setVisibility(View.GONE);
        setAdapter(gridLayout, current_id);

    }
}