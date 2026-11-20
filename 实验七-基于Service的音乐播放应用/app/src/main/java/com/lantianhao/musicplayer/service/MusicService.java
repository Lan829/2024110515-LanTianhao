package com.lantianhao.musicplayer.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.lantianhao.musicplayer.R;
import com.lantianhao.musicplayer.controller.MainActivity;
import com.lantianhao.musicplayer.model.Song;
import com.lantianhao.musicplayer.util.PlaylistUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 音乐播放服务：所有播放相关功能（播放、暂停、停止、上一首、下一首、
 * 拖动进度）都在服务中通过 MediaPlayer 实现，Activity 通过 Binder 获取状态。
 */
public class MusicService extends Service {

    public static final String CHANNEL_ID = "music_playback";
    private static final int NOTIFICATION_ID = 1001;

    private final IBinder binder = new MusicBinder();
    private final List<Song> playlist = new ArrayList<>();
    private MediaPlayer player;
    private int currentIndex = -1;
    private boolean prepared;

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        player = new MediaPlayer();
        player.setOnCompletionListener(mp -> next());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        if (player != null) {
            player.release();
            player = null;
        }
        NotificationManagerCompat.from(this).cancel(NOTIFICATION_ID);
        super.onDestroy();
    }

    public void setPlaylist(List<Song> songs) {
        playlist.clear();
        playlist.addAll(songs);
        if (playlist.isEmpty()) {
            currentIndex = -1;
            prepared = false;
        } else if (currentIndex < 0 || currentIndex >= playlist.size()) {
            currentIndex = 0;
        }
    }

    public List<Song> getPlaylist() {
        return new ArrayList<>(playlist);
    }

    public Song getCurrentSong() {
        if (currentIndex >= 0 && currentIndex < playlist.size()) {
            return playlist.get(currentIndex);
        }
        return null;
    }

    /**
     * 播放指定位置的歌曲。
     */
    public void playSong(int index) {
        if (index < 0 || index >= playlist.size()) {
            return;
        }
        currentIndex = index;
        Song song = playlist.get(index);
        try {
            prepared = false;
            player.reset();
            player.setDataSource(getApplicationContext(), song.getUri());
            player.setOnPreparedListener(mp -> {
                prepared = true;
                mp.start();
                showNotification(song);
            });
            player.prepareAsync();
        } catch (IOException | IllegalStateException e) {
            prepared = false;
        }
    }

    /**
     * 播放 / 暂停切换；没有播放过时从第一首开始。
     */
    public void playOrPause() {
        if (playlist.isEmpty()) {
            return;
        }
        if (currentIndex < 0) {
            playSong(0);
            return;
        }
        if (!prepared) {
            playSong(currentIndex);
            return;
        }
        if (player.isPlaying()) {
            player.pause();
        } else {
            player.start();
        }
    }

    public void stop() {
        if (player != null) {
            try {
                player.stop();
            } catch (IllegalStateException ignored) {
                // 未准备完成时 stop 可能抛出异常。
            }
            prepared = false;
        }
        NotificationManagerCompat.from(this).cancel(NOTIFICATION_ID);
    }

    public void next() {
        if (playlist.isEmpty()) {
            return;
        }
        int nextIndex = PlaylistUtils.nextIndex(currentIndex, playlist.size());
        if (nextIndex >= 0) {
            playSong(nextIndex);
        }
    }

    public void previous() {
        if (playlist.isEmpty()) {
            return;
        }
        int previousIndex = PlaylistUtils.previousIndex(currentIndex, playlist.size());
        if (previousIndex >= 0) {
            playSong(previousIndex);
        }
    }

    public void seekTo(int positionMs) {
        if (player != null && prepared) {
            player.seekTo(positionMs);
        }
    }

    public boolean isPlaying() {
        return player != null && prepared && player.isPlaying();
    }

    public int getPosition() {
        if (player != null && prepared) {
            return player.getCurrentPosition();
        }
        return 0;
    }

    public int getDuration() {
        if (player != null && prepared) {
            return player.getDuration();
        }
        return 0;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    getString(R.string.notification_channel_name),
                    NotificationManager.IMPORTANCE_LOW);
            channel.setDescription(getString(R.string.notification_channel_description));
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    /**
     * 新歌曲开始播放时，在通知栏显示歌曲名与艺术家。
     */
    private void showNotification(Song song) {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_music_note)
                .setContentTitle(song.getTitle())
                .setContentText(song.getArtist())
                .setContentIntent(contentIntent)
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .build();
        NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, notification);
    }
}
