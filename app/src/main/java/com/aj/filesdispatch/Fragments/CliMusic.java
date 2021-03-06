package com.aj.filesdispatch.Fragments;

import android.database.Cursor;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aj.filesdispatch.Entities.FileItem;
import com.aj.filesdispatch.Entities.FileItemBuilder;
import com.aj.filesdispatch.Interface.OnItemClickToOpen;
import com.aj.filesdispatch.R;
import com.aj.filesdispatch.RecyclerAdapter.AudioAdapter;
import com.aj.filesdispatch.common.Converter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class CliMusic extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, OnItemClickToOpen, SeekBar.OnSeekBarChangeListener {

    private static final String TAG = "CliMusic";
    private final static int MEDIASTORE_LOADER_ID = 101;
    public static final String AUDIOS = "Audios";
    private RecyclerView contentRecyclerView;
    private View audioPlayerView;
    private TextView audioName;
    private AppCompatSeekBar audioProgress;
    private AppCompatImageButton audioPlay;
    private RecyclerView.LayoutManager layoutManager;
    private MediaPlayer player;
    private ContentLoadingProgressBar contentLoading;
    private AppCompatTextView noContentText;
    private AlertDialog musicPlayer;
    private AudioAdapter audioAdapter;
    private LoaderManager loaderManager;
    private OnBackPressedCallback backPressedCallback;
    private ArrayList<FileItem> audioList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_content, container, false);
        contentRecyclerView = view.findViewById(R.id.content_recycler);
        contentLoading = view.findViewById(R.id.progress_bar);
        noContentText = view.findViewById(R.id.no_content_text);
        contentLoading.setVisibility(View.VISIBLE);
        noContentText.setVisibility(View.GONE);
        noContentText.setText(getText(R.string.no_audios_found));
        contentRecyclerView.setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        audioList = new ArrayList<>();
        audioAdapter = new AudioAdapter(getActivity(), this);
        loaderManager = LoaderManager.getInstance(this);
        initilise();
    }

    private void initilise() {
        layoutManager = new LinearLayoutManager(requireContext());
        audioPlayerView = getLayoutInflater().inflate(R.layout.audio_player_layout, null, false);
        audioPlay = audioPlayerView.findViewById(R.id.play_button);
        audioName = audioPlayerView.findViewById(R.id.playing_audio_name);
        audioProgress = audioPlayerView.findViewById(R.id.play_progress);
        musicPlayer = new AlertDialog.Builder(requireContext())
                .setView(audioPlayerView)
                .create();
        player = new MediaPlayer();
        player.setAudioAttributes(new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build());
        musicPlayer.setCanceledOnTouchOutside(false);
        loaderManager.initLoader(MEDIASTORE_LOADER_ID, null, this);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(getResources().getDrawable(R.drawable.line_divider, getActivity().getTheme()));
        contentRecyclerView.addItemDecoration(itemDecoration);
        contentRecyclerView.setLayoutManager(layoutManager);
        contentRecyclerView.setHasFixedSize(true);
        Log.d(TAG, "onCreateView: OncreateView");
        contentRecyclerView.setAdapter(audioAdapter);
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
                MediaStore.Files.FileColumns.DISPLAY_NAME
        };
        return new CursorLoader(requireContext(),
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                MediaStore.Files.FileColumns.DATE_ADDED + " DESC");
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        contentLoading.setVisibility(View.GONE);
        if (data.getCount() > 0) {
            if (audioList.size() < Math.min(data.getCount(), 30))
                for (int i = 0; i < Math.min(data.getCount(), 30); i++) {
                    data.moveToPosition(i);
                    audioList.add(new FileItemBuilder(data.getString(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)))
                            .setFileName(data.getString(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)))
                            .setFileSize(data.getLong(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)))
                            .setFileType(AUDIOS)
                            .setDateAdded(data.getLong(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED)))
                            .setFileUri(data.getString(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)))
                            .setShowDes(Converter.getFileDes(new File(data.getString(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)))))
                            .build());
                }
            audioAdapter.setAudioList(audioList);
            contentRecyclerView.setVisibility(View.VISIBLE);
            noContentText.setVisibility(View.GONE);
            audioAdapter.ChangeItems(data);
        } else {
            contentRecyclerView.setVisibility(View.INVISIBLE);
            noContentText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        audioAdapter.ChangeItems(null);
        audioAdapter.setAudioList(null);
    }

    @Override
    public void OnClick(FileItem item) {
        Uri uri = Uri.parse(item.getFileUri());
        String name = item.getFileName();
        try {
            audioPlay.setBackground(ActivityCompat.getDrawable(requireContext(), R.drawable.ic_pause_circle));
            player.setDataSource(requireContext(), uri);
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
            Toast.makeText(requireContext(), "Can't play Audio", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void playButton(ImageButton play) {
        if (player.isPlaying()) {
            play.setBackground(ActivityCompat.getDrawable(requireContext(), R.drawable.ic_play_circle));
            player.pause();
        } else {
            play.setBackground(ActivityCompat.getDrawable(requireContext(), R.drawable.ic_pause_circle));
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
        if (player != null && player.isPlaying())
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
        if (player != null) {
            player.release();
        }
        super.onDestroyView();
    }
}