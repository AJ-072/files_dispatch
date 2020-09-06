package com.aj.filesdispatch.Fragments;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.aj.filesdispatch.Interface.AddItemToShare;
import com.aj.filesdispatch.R;
import com.aj.filesdispatch.RecyclerAdapter.AppAdapter;
import com.aj.filesdispatch.ViewModels.FileItemViewModel;

import java.util.Objects;

public class CliApp extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG = "CliApp";
    private AddItemToShare appToShare;
    private RecyclerView recyclerView;
    private AppAdapter appAdapter;
    private FileItemViewModel viewModel;
    public CliApp(AddItemToShare appToShare) {
        this.appToShare = appToShare;
    }

    public CliApp() {

    }


    public static CliApp newInstance(String param1, String param2) {
        CliApp fragment = new CliApp();
        Bundle args = new Bundle();
        Log.d(TAG, "newInstance: valled");
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cli_app, container, false);
        return view;
    }
}