package com.lantianhao.musicplayer.controller;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.lantianhao.musicplayer.R;
import com.lantianhao.musicplayer.model.Song;
import com.lantianhao.musicplayer.service.MusicService;
import com.lantianhao.musicplayer.util.MusicLoader;
import com.lantianhao.musicplayer.util.TimeUtils;
import com.lantianhao.musicplayer.view.SongAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 音乐播放主界面：申请音频权限、使用 ContentResolver 获取歌曲列表，
 * 绑定 MusicService 后通过 Binder 获取歌曲名、播放进度并控制播放；
 * 使用 Handler 每 500ms 刷新 SeekBar。
 */
public class MainActivity extends AppCompatActivity implements SongAdapter.OnSongClickListener {

    private static final long PROGRESS_INTERVAL_MS = 500L;

    private final List<Song> songs = new ArrayList<>();
    private MusicService musicService;
    private boolean bound;

    private TextView currentTitle;
    private TextView currentArtist;
    private TextView currentTime;
    private TextView totalTime;
    private TextView songCount;
    private TextView emptyView;
    private SeekBar seekBar;
    private ImageButton playPauseButton;
    private RecyclerView songList;

    private SongAdapter adapter;
    private boolean userSeeking;

    private final Handler progressHandler = new Handler(Looper.getMainLooper());
    private final Runnable progressRunnable = new Runnable() {
        @Override
        public void run() {
            updateProgress();
            progressHandler.postDelayed(this, PROGRESS_INTERVAL_MS);
        }
    };

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicService = ((MusicService.MusicBinder) service).getService();
            musicService.setPlaylist(songs);
            bound = true;
            updateProgress();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
            musicService = null;
        }
    };

    private final ActivityResultLauncher<String[]> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                    result -> loadSongs());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();
        setupList();
        setupControls();
        requestPermissions();
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(new Intent(this, MusicService.class), connection, Context.BIND_AUTO_CREATE);
        progressHandler.post(progressRunnable);
    }

    @Override
    protected void onStop() {
        super.onStop();
        progressHandler.removeCallbacks(progressRunnable);
        if (bound) {
            unbindService(connection);
            bound = false;
            musicService = null;
        }
    }

    private void bindViews() {
        currentTitle = findViewById(R.id.current_title);
        currentArtist = findViewById(R.id.current_artist);
        currentTime = findViewById(R.id.current_time);
        totalTime = findViewById(R.id.total_time);
        songCount = findViewById(R.id.song_count);
        emptyView = findViewById(R.id.empty_view);
        seekBar = findViewById(R.id.seek_bar);
        playPauseButton = findViewById(R.id.button_play_pause);
        songList = findViewById(R.id.song_list);
    }

    private void setupList() {
        adapter = new SongAdapter(this);
        songList.setLayoutManager(new LinearLayoutManager(this));
        songList.setAdapter(adapter);
    }

    private void setupControls() {
        findViewById(R.id.button_previous).setOnClickListener(v -> {
            if (musicService != null) {
                musicService.previous();
            }
        });
        playPauseButton.setOnClickListener(v -> {
            if (musicService != null) {
                musicService.playOrPause();
            }
        });
        findViewById(R.id.button_stop).setOnClickListener(v -> {
            if (musicService != null) {
                musicService.stop();
            }
        });
        findViewById(R.id.button_next).setOnClickListener(v -> {
            if (musicService != null) {
                musicService.next();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    currentTime.setText(TimeUtils.formatDuration(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                userSeeking = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                userSeeking = false;
                if (musicService != null) {
                    musicService.seekTo(seekBar.getProgress());
                }
                updateProgress();
            }
        });
    }

    private void requestPermissions() {
        List<String> permissions = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.READ_MEDIA_AUDIO);
            permissions.add(Manifest.permission.POST_NOTIFICATIONS);
        } else {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        List<String> missing = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                missing.add(permission);
            }
        }
        if (missing.isEmpty()) {
            loadSongs();
        } else {
            permissionLauncher.launch(missing.toArray(new String[0]));
        }
    }

    private void loadSongs() {
        songs.clear();
        songs.addAll(MusicLoader.load(this));
        adapter.submit(songs);
        updateEmptyState();
        if (musicService != null) {
            musicService.setPlaylist(songs);
        }
        if (songs.isEmpty()) {
            Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
        }
    }

    private void updateEmptyState() {
        emptyView.setVisibility(songs.isEmpty() ? android.view.View.VISIBLE
                : android.view.View.GONE);
        songCount.setText(getString(R.string.song_count, songs.size()));
    }

    @Override
    public void onSongClick(int position) {
        if (musicService != null) {
            musicService.playSong(position);
            updateProgress();
        }
    }

    private void updateProgress() {
        if (musicService == null) {
            return;
        }
        Song song = musicService.getCurrentSong();
        if (song != null) {
            currentTitle.setText(song.getTitle());
            currentArtist.setText(song.getArtist());
        }

        int duration = musicService.getDuration();
        int position = musicService.getPosition();
        if (!userSeeking) {
            int max = Math.max(duration, 1);
            seekBar.setMax(max);
            seekBar.setProgress(Math.min(position, max));
        }
        currentTime.setText(TimeUtils.formatDuration(position));
        totalTime.setText(TimeUtils.formatDuration(duration));

        boolean playing = musicService.isPlaying();
        playPauseButton.setImageResource(playing ? R.drawable.ic_pause : R.drawable.ic_play);
        playPauseButton.setContentDescription(
                getString(playing ? R.string.desc_pause : R.string.desc_play));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_about) {
            showAboutDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAboutDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.about_title)
                .setMessage(getString(R.string.about_message, getString(R.string.app_name)))
                .setPositiveButton(R.string.action_ok, null)
                .show();
    }
}
