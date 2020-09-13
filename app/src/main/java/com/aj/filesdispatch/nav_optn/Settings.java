package com.aj.filesdispatch.nav_optn;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aj.filesdispatch.R;
import com.aj.filesdispatch.RecyclerAdapter.AvatarRecyclerView;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

import static com.aj.filesdispatch.Activities.FindConnection.AVATAR;
import static com.aj.filesdispatch.Activities.FindConnection.BUDDY_NAME;
import static com.aj.filesdispatch.ApplicationActivity.Avatars;
import static com.aj.filesdispatch.ApplicationActivity.OptnlAvatarName;
import static com.aj.filesdispatch.ApplicationActivity.OptnlUserName;
import static java.lang.String.format;

public class Settings extends AppCompatActivity implements AvatarRecyclerView.onItemClick {
    private View popUp;
    private ImageView avatarImage;
    private TextView username;
    private AlertDialog dialog;
    private LinearLayoutCompat this_user;
    private EditText editUserName;
    private int currentId;
    private RecyclerView recyclerView;
    private String currentUserName;
    private AvatarRecyclerView adapter;
    private WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
    private SharedPreferences defaultPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_container);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Settings");
        defaultPreference = PreferenceManager.getDefaultSharedPreferences(this);
        this_user = findViewById(R.id.this_user);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.settings_fragment, new SettingsFragment())
                .commit();
        loadUser();
        adapter = new AvatarRecyclerView(this, currentId);
        changeUser();
        this_user.setOnClickListener(v -> {
            dialog.show();
            Objects.requireNonNull(dialog.getWindow()).setAttributes(lp);
            editUserName.setText(currentUserName);
            editUserName.setSelection(currentUserName.length());
            recyclerView.scrollToPosition(Arrays.asList(Avatars).indexOf(currentId));

            Button Random= dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            Random.setOnClickListener(view -> createRandom());
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void changeUser() {
        popUp = getLayoutInflater().inflate(R.layout.change_user_view, null);
        popUp.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT));
        recyclerView = popUp.findViewById(R.id.avatar_icon_list);
        editUserName = popUp.findViewById(R.id.username);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1, RecyclerView.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);
        dialog = new AlertDialog.Builder(this)
                .setView(popUp)
                .setPositiveButton("Confirm", (dialog, which) -> {
                    defaultPreference.edit().putInt(AVATAR, currentId).apply();
                    if (editUserName.getText().toString().trim().length() != 0)
                        defaultPreference.edit().putString(BUDDY_NAME, editUserName.getText().toString().trim()).apply();
                    loadUser();
                })
                .setNegativeButton("Random",null)
                .setNeutralButton("Cancel", (dialog, which) -> dialog.cancel())
                .create();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


    }

    private void loadUser() {
        avatarImage = findViewById(R.id.this_avatar);
        username = findViewById(R.id.this_username);
        currentId = defaultPreference.getInt(AVATAR, OptnlAvatarName);
        currentUserName = defaultPreference.getString(BUDDY_NAME, OptnlUserName);
        avatarImage.setImageResource(currentId);
        username.setText(currentUserName);
    }

    private void createRandom() {
        currentUserName=String.format("User%s", format(Locale.ROOT, "%04d", new Random().nextInt(100000)));
        editUserName.setText(currentUserName);
        currentId = Avatars[new Random().nextInt(6)];
        recyclerView.scrollToPosition(Arrays.asList(Avatars).indexOf(currentId));
        adapter.setSelected(currentId);
    }

    @Override
    public void onClick(int id, ImageView imageView) {
        adapter.notifyDataSetChanged();
        currentId = id;
    }

}