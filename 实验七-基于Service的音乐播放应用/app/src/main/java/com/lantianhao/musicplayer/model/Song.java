package com.lantianhao.musicplayer.model;

import android.net.Uri;

/**
 * 一首音乐（Model），数据来自 MediaStore.Audio.Media。
 */
public class Song {

    private final long id;
    private final String title;
    private final String artist;
    private final long duration;
    private final Uri uri;

    public Song(long id, String title, String artist, long duration, Uri uri) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.duration = duration;
        this.uri = uri;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public long getDuration() {
        return duration;
    }

    public Uri getUri() {
        return uri;
    }
}
