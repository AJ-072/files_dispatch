package com.aj.filesdispatch.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aj.filesdispatch.R;
import com.aj.filesdispatch.RecyclerAdapter.AppAdapter;
import com.aj.filesdispatch.ViewModels.AppListViewModel;

import java.util.Objects;

public class CliApp extends Fragment {

    private static final String TAG = "CliApp";
    private RecyclerView contentRecyclerView;
    private AppAdapter appAdapter;
    private AppListViewModel viewModel;
    private ContentLoadingProgressBar contentLoading;
    private AppCompatTextView noContentText;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content, container, false);
        contentRecyclerView = view.findViewById(R.id.content_recycler);
        contentLoading = view.findViewById(R.id.progress_bar);
        noContentText = view.findViewById(R.id.no_content_text);
        contentLoading.setVisibility(View.VISIBLE);
        noContentText.setVisibility(View.GONE);
        noContentText.setText(getText(R.string.no_installed_apps));
        contentRecyclerView.setVisibility(View.GONE);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication())).get(AppListViewModel.class);
        contentRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        appAdapter = new AppAdapter(getActivity());
        contentRecyclerView.setAdapter(appAdapter);
        viewModel.getFileItems().observe(getViewLifecycleOwner(), fileItems -> {
            contentLoading.setVisibility(View.GONE);
            if (fileItems.size()==0){
                noContentText.setVisibility(View.VISIBLE);
                contentRecyclerView.setVisibility(View.GONE);
            }else{
                noContentText.setVisibility(View.GONE);
                contentRecyclerView.setVisibility(View.VISIBLE);
            }
            appAdapter.submitList(fileItems);
            viewModel.UpdateList(fileItems);

            Log.d(TAG, "onCreateView: " + fileItems.size());
        });
    }
}