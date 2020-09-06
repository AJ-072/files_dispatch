package com.aj.filesdispatch.RecyclerAdapter;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.SpeechRecognizer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aj.filesdispatch.DatabaseHelper.DatabaseHelper;
import com.aj.filesdispatch.Fragments.History;
import com.aj.filesdispatch.R;

import java.util.Objects;

import static com.aj.filesdispatch.DatabaseHelper.DatabaseHelper._ID;

public class HistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private RecyclerView.RecycledViewPool pool = new RecyclerView.RecycledViewPool();
    private Cursor cursor;
    private Context context;
    private Fragment fragment;
    public HistoryAdapter(Fragment fragment){
        this.fragment=fragment;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context=parent.getContext();
        return new HistoryRecycler(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_view, parent, false),fragment,parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        cursor.moveToPosition(position);
        HistoryRecycler recycler= ((HistoryRecycler) holder);
        recycler.textView.setText(cursor.getString(cursor.getColumnIndexOrThrow(_ID)));
        recycler.textView.setOnClickListener(v -> {
            recycler.recyclerView.setRecycledViewPool(pool);
            cursor.moveToPosition(position);
            recycler.setCursor(cursor.getString(cursor.getColumnIndexOrThrow(_ID)));
        });

    }

    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return cursor != null ? cursor.getCount() : 0;
    }

    public static class HistoryRecycler extends RecyclerView.ViewHolder implements LoaderManager.LoaderCallbacks<Cursor> {
        public final TextView textView;
        public final RecyclerView recyclerView;
        private Fragment fragment;
        private Context context;
        private String id_value;
        private HistoryItemAdapter adapter;

        public HistoryRecycler(@NonNull View itemView, Fragment fragment,Context context) {
            super(itemView);
            textView = itemView.findViewById(R.id.history_profile);
            recyclerView = itemView.findViewById(R.id.history_recycler);
            this.context=context;
            this.fragment=fragment;
        }

        private void setCursor(String ID){
            this.id_value=ID;
            adapter= new HistoryItemAdapter();
            recyclerView.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));
            recyclerView.setAdapter(adapter);
            LoaderManager.getInstance(fragment).initLoader(1,null,this);
        }

        @NonNull
        @Override
        public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
            return new AsyncTaskLoader<Cursor>(Objects.requireNonNull(context)) {
                @Override
                protected void onStartLoading() {
                    forceLoad();
                }

                @Nullable
                @Override
                public Cursor loadInBackground() {
                    return new DatabaseHelper(context,1).getListForSender(id_value);
                }
            };
        }

        @Override
        public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
            adapter.setCursor(data);
            recyclerView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onLoaderReset(@NonNull Loader<Cursor> loader) {
            adapter.setCursor(null);
        }
    }
}
