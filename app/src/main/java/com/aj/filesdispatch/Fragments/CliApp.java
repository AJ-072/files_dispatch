package com.aj.filesdispatch.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aj.filesdispatch.Interface.AddItemToShare;
import com.aj.filesdispatch.R;
import com.aj.filesdispatch.RecyclerAdapter.AppAdapter;
import com.aj.filesdispatch.ViewModels.AppListViewModel;

public class CliApp extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG = "CliApp";
    private AddItemToShare appToShare;
    private RecyclerView recyclerView;
    private AppAdapter appAdapter;
    private AppListViewModel viewModel;
    private ProgressBar appLoader;

    public CliApp(AddItemToShare appToShare) {
        this.appToShare = appToShare;
    }

    public CliApp() {

    }


    public static CliApp newInstance(String param1, String param2) {
        CliApp fragment = new CliApp();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(AppListViewModel.class);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cli_app, container, false);
        recyclerView = view.findViewById(R.id.app_recycler);
        appLoader = view.findViewById(R.id.app_loading);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        appAdapter = new AppAdapter(getActivity(), appToShare);
        recyclerView.setAdapter(appAdapter);
        viewModel.getFileItems().observe(getViewLifecycleOwner(), fileItems -> {
            appAdapter.submitList(fileItems);
            viewModel.UpdateList(fileItems);
            appLoader.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            Log.d(TAG, "onCreateView: " + fileItems.size());
        });
        return view;
    }
}