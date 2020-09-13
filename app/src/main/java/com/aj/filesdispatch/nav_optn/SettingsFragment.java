package com.aj.filesdispatch.nav_optn;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aj.filesdispatch.R;
import com.aj.filesdispatch.RecyclerAdapter.AvatarRecyclerView;

import java.util.Objects;

import static com.aj.filesdispatch.Activities.FindConnection.AVATAR;
import static com.aj.filesdispatch.Activities.FindConnection.BUDDY_NAME;
import static com.aj.filesdispatch.ApplicationActivity.OptnlAvatarName;
import static com.aj.filesdispatch.ApplicationActivity.OptnlUserName;

public class SettingsFragment extends PreferenceFragmentCompat {
    private Preference setUser;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        setUser = findPreference("setUser");
    }

}