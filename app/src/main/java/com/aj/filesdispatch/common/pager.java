package com.aj.filesdispatch.common;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.aj.filesdispatch.Fragments.CliFiles;
import com.aj.filesdispatch.Fragments.History;
import com.aj.filesdispatch.Activities.MainActivity;
import com.aj.filesdispatch.Fragments.CliApp;
import com.aj.filesdispatch.Fragments.CliMusic;
import com.aj.filesdispatch.Fragments.CliDoc;
import com.aj.filesdispatch.Fragments.CliImages;
import com.aj.filesdispatch.Fragments.CliVideos;

public class pager extends androidx.fragment.app.FragmentPagerAdapter {
    private int tabNum;
    private MainActivity activity;
    private static final String TAG = "pager";

    public pager(@NonNull FragmentManager fm, int behavior, int tab, MainActivity activity) {
        super(fm, behavior);
        this.tabNum = tab;
        this.activity = activity;
        Log.d(TAG, "pager: "+(activity==null));
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new CliMusic(activity);
            case 1:
                return new CliVideos(activity);
            case 2:
                return new CliImages(activity);
            case 4:
                return new CliDoc();
            case 5:
                return new CliFiles(activity);
            case 6:
                return new History();
            case 3:
            default:
                return new CliApp(activity);
        }
    }

    @Override
    public int getCount() {
        return tabNum;
    }
}

