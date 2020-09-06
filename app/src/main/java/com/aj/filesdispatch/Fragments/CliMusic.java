package com.aj.filesdispatch.Fragments;

import android.content.Context;
import android.database.Cursor;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aj.filesdispatch.Models.FileViewItem;
import com.aj.filesdispatch.Interface.AddItemToShare;
import com.aj.filesdispatch.Interface.OnItemClickToOpen;
import com.aj.filesdispatch.R;
import com.aj.filesdispatch.RecyclerAdapter.AudioAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class CliMusic extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, OnItemClickToOpen, SeekBar.OnSeekBarChangeListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "CliMusic";
    private final static int MEDIASTORE_LOADER_ID = 101;
    private Context context;
    private RecyclerView audioRecycler;
    private View audioPlayerView;
    private TextView audioName;
    private SeekBar audioProgress;
    private ImageButton audioPlay;
    private RecyclerView.LayoutManager layoutManager;
    private MediaPlayer player;
    private ProgressBar musicLoading;
    private TextView noMusicText;
    private AlertDialog musicPlayer;
    private AudioAdapter audioAdapter;
    private LoaderManager loaderManager;
    private OnBackPressedCallback backPressedCallback;
    private AddItemToShare audioToShare;
    private ArrayList<FileViewItem> audioList;

    private String mParam1;
    private String mParam2;

    public CliMusic(AddItemToShare addAudioToShare) {
        this.audioToShare=addAudioToShare;
    }

    public CliMusic() {
        // Required empty public constructor
    }

    public static CliMusic newInstance(String param1, String param2) {
        CliMusic fragment = new CliMusic();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        audioList= new ArrayList<>();
        context= getContext();
        audioAdapter = new AudioAdapter(audioToShare,this, getContext());
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        loaderManager = LoaderManager.getInstance(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cli_music, container, false);
        initilise(view);

        return view;
    }

    private void initilise(View view){
        musicLoading=view.findViewById(R.id.audio_loading);
        noMusicText=view.findViewById(R.id.no_audio_text);
        audioRecycler = view.findViewById(R.id.music_list);
        layoutManager = new LinearLayoutManager(context);
        audioPlayerView = getLayoutInflater().inflate(R.layout.audio_player_layout, null, false);
        audioPlay = audioPlayerView.findViewById(R.id.play_button);
        audioName = audioPlayerView.findViewById(R.id.playing_audio_name);
        audioProgress = audioPlayerView.findViewById(R.id.play_progress);
        musicPlayer = new AlertDialog.Builder(context)
                .setView(audioPlayerView)
                .create();
        player = new MediaPlayer();
        player.setAudioAttributes(new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build());
        musicPlayer.setCanceledOnTouchOutside(false);
        loaderManager.initLoader(MEDIASTORE_LOADER_ID, null, this);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(getResources().getDrawable(R.drawable.line_divider, getActivity().getTheme()));
        audioRecycler.addItemDecoration(itemDecoration);
        audioRecycler.setLayoutManager(layoutManager);
        audioRecycler.setHasFixedSize(true);
        Log.d(TAG, "onCreateView: OncreateView");
        audioRecycler.setAdapter(audioAdapter);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        Log.d(TAG, "onCreateLoader: called");
        String[] projection = {
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.SIZE,
                MediaStore.Files.FileColumns.MIME_TYPE,
                MediaStore.Files.FileColumns.TITLE
        };
        return new CursorLoader(context,
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                MediaStore.Files.FileColumns.DATE_ADDED + " DESC");
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        musicLoading.setVisibility(View.GONE);
        if (data.getCount()>0){
            if (audioList.size()<Math.min(data.getCount(),30))
            for (int i=0;i<Math.min(data.getCount(),30);i++){
                data.moveToPosition(i);
                audioList.add(new FileViewItem(data,"Images"));
            }
            audioAdapter.setAudioList(audioList);
            audioRecycler.setVisibility(View.VISIBLE);
            noMusicText.setVisibility(View.GONE);
            audioAdapter.ChangeItems(data);
        }else{
            audioRecycler.setVisibility(View.INVISIBLE);
            noMusicText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        audioAdapter.ChangeItems(null);
        audioAdapter.setAudioList(null);
    }

    @Override
    public void  OnClick(FileViewItem item) {
        Uri uri=Uri.parse(item.getFileLoc());
        String name=item.getFileName();
        try {
            audioPlay.setBackground(ActivityCompat.getDrawable(context,R.drawable.ic_pause_circle));
            player.setDataSource(context, uri);
            audioName.setText(name);
            musicPlayer.show();
            musicPlayer.setOnDismissListener(dialog -> {
                audioAdapter.setPlaying(false);
                if (player.isPlaying())
                    player.stop();
                player.reset();
            });
            player.prepare();
            player.setOnPreparedListener(mp -> {
                audioProgress.setMax(player.getDuration());
                audioProgress.setProgress(player.getCurrentPosition());
                mp.start();
                audioPlay.setOnClickListener(v1 -> playButton(audioPlay));
                mp.setScreenOnWhilePlaying(true);
                setProgress();
                audioProgress.setOnSeekBarChangeListener(this);
            });
            player.setOnSeekCompleteListener(mp -> {
                setProgress();
            });
            player.setOnCompletionListener(mp -> {
                musicPlayer.dismiss();
            });
        } catch (IOException e) {
            Toast.makeText(context, "Can't play Audio", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void playButton(ImageButton play) {
        if (player.isPlaying()) {
            play.setBackground(ActivityCompat.getDrawable(context,R.drawable.ic_play_circle));
            player.pause();
        } else {
            play.setBackground(ActivityCompat.getDrawable(context,R.drawable.ic_pause_circle));
            player.start();
        }
        setProgress();
    }

    private void setProgress() {
        new Thread(() -> {
            int current_position = player.getCurrentPosition();
            int max = player.getDuration();
            audioProgress.setMax(max);
            while (current_position < max && player.isPlaying()) {
                try {
                    Thread.sleep(100);
                    current_position = player.getCurrentPosition();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
                audioProgress.setProgress(current_position);
            }
        }).start();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        player.seekTo(seekBar.getProgress());
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: called");
        backPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Log.d(TAG, "handleOnBackPressed: backpressed");
                new AlertDialog.Builder(CliMusic.this.requireContext())
                        .setIcon(R.drawable.ic_logo)
                        .setTitle("Are you Sure!")
                        .setPositiveButton("yes", (dialog, which) -> {
                            dialog.dismiss();
                            this.remove();
                            requireActivity().getOnBackPressedDispatcher().onBackPressed();
                        })
                        .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                        .create().show();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, backPressedCallback);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (player!=null&&player.isPlaying())
            playButton(audioPlay);
        backPressedCallback.setEnabled(false);
        Log.d(TAG, "onPause: called");
    }

    @Override
    public void onStop() {
        super.onStop();
        if (backPressedCallback != null)
            backPressedCallback.remove();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        if (player!=null){
            player.release();
        }
        super.onDestroyView();
    }
}