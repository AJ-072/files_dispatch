package com.aj.filesdispatch.Fragments;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aj.filesdispatch.DatabaseHelper.DatabaseHelper;
import com.aj.filesdispatch.R;
import com.aj.filesdispatch.RecyclerAdapter.HistoryAdapter;

public class History extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "History";
    private OnBackPressedCallback backPressedCallback;
    private TextView noHistory;
    private RecyclerView historyRecycler;
    private ProgressBar historyProgress;
    private HistoryAdapter adapter;

    private String mParam1;
    private String mParam2;

    public History() {
        // Required empty public constructor
    }

    public static History newInstance(String param1, String param2) {
        History fragment = new History();
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
        View view = inflater.inflate(R.layout.fragment_cli_history, container, false);
        historyRecycler=view.findViewById(R.id.history_recycler_view);
        noHistory=view.findViewById(R.id.no_history_text);
        historyProgress=view.findViewById(R.id.history_progress);
        historyRecycler.setLayoutManager(new LinearLayoutManager(this.getContext(),RecyclerView.HORIZONTAL,false));
        adapter= new HistoryAdapter(this);
        LoaderManager.getInstance(this).initLoader(1,null,this);
        historyRecycler.setAdapter(adapter);
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
                new AlertDialog.Builder(History.this.requireContext())
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

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<Cursor>(requireContext()) {

            @Override
            protected void onStartLoading() {
                forceLoad();
            }

            @Nullable
            @Override
            public Cursor loadInBackground() {
                DatabaseHelper helper= new DatabaseHelper(getContext(), 1);
                if (helper.getReadableDatabase()==null)
                    return null;
                else
                    return helper.getSenders();
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        adapter.setCursor(data);
        historyProgress.setVisibility(View.GONE);
        if (data!=null&&data.getCount()>0) {
            historyRecycler.setVisibility(View.VISIBLE);
            noHistory.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        adapter.setCursor(null);
    }
}
