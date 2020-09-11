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

    public pager(@NonNull FragmentManager fm, int behavior, int tab) {
        super(fm, behavior);
        this.tabNum = tab;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new CliMusic();
            case 1:
                return new CliVideos();
            case 2:
                return new CliImages();
            case 4:
                return new CliDoc();
            case 5:
                return new CliFiles();
            case 6:
                return new History();
            case 3:
            default:
                return new CliApp();
        }
    }

    @Override
    public int getCount() {
        return tabNum;
    }
}

