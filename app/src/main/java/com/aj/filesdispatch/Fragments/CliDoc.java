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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aj.filesdispatch.R;
import com.aj.filesdispatch.RecyclerAdapter.DocAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CliDoc extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, DocAdapter.onDocclick {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final int DOC_LOADER_ID = 201;
    public static final int DOC_FILE_LOADER_ID = 101;
    public static final String DOCUMENT= "Documents";
    private List<String> type = new ArrayList<>();
    public static int current_id = DOC_LOADER_ID;
    private static final String TAG = "Doc fragment";
    private ProgressBar documentLoading;
    private LoaderManager loaderManager;
    private OnBackPressedCallback OnBackPressedCallback;
    private TextView noDocText;
    private Context context;
    private DocAdapter adapter;
    private RecyclerView recyclerView;
    private DividerItemDecoration itemDecoration;
    private RecyclerView.LayoutManager gridLayout, linearLayout;
    private String mParam1;
    private String mParam2;

    public CliDoc() {
        // Required empty public constructor
    }

    public static CliDoc newInstance(String param1, String param2) {
        CliDoc fragment = new CliDoc();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new DocAdapter(this, current_id,getActivity());
        Log.d(TAG, "onCreate: ");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cli_doc, container, false);
        context = container.getContext();
        //LoaderManager.enableDebugLogging(true);
        recyclerView = view.findViewById(R.id.doc_over_view);
        documentLoading = view.findViewById(R.id.document_loading_progress);
        noDocText = view.findViewById(R.id.no_document_text);

        itemDecoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(getResources().getDrawable(R.drawable.line_divider, context.getTheme()));

        Log.d(TAG, "onCreateView: ");

        linearLayout = new LinearLayoutManager(context);
        gridLayout = new GridLayoutManager(context, 3);


        recyclerView.setHasFixedSize(true);
        documentLoading.setVisibility(View.GONE);
        setList();
        return view;
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
                            .setNegativeButton("No", (dialog, which) -> {
                                dialog.dismiss();
                            })
                            .create().show();
                } else {
                    recyclerView.removeItemDecoration(itemDecoration);
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
        return new CursorLoader(context,
                MediaStore.Files.getContentUri("external"),
                projection, selection.toString(), null,
                MediaStore.Files.FileColumns.DATE_ADDED + " DESC");
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        documentLoading.setVisibility(View.GONE);
        current_id = DOC_FILE_LOADER_ID;
        if (data != null && data.getCount() > 0) {
            setAdapter(linearLayout,current_id);
            adapter.setData(data);
            recyclerView.addItemDecoration(itemDecoration);
            recyclerView.setVisibility(View.VISIBLE);
            noDocText.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            noDocText.setVisibility(View.VISIBLE);
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
        documentLoading.setVisibility(View.VISIBLE);
        loaderManager.restartLoader(DOC_FILE_LOADER_ID, null, this);

    }

    private void setAdapter(RecyclerView.LayoutManager layoutManager,int current_id) {
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.setId(current_id);
    }

    private void setList() {
        current_id = DOC_LOADER_ID;
        type = null;
        recyclerView.setVisibility(View.VISIBLE);
        noDocText.setVisibility(View.GONE);
        setAdapter(gridLayout,current_id);

    }
}