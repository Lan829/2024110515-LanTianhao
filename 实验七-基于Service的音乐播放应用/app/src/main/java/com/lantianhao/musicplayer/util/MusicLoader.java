package com.lantianhao.musicplayer.util;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.lantianhao.musicplayer.R;
import com.lantianhao.musicplayer.model.Song;

import java.util.ArrayList;
import java.util.List;

/**
 * 使用 ContentResolver 查询 MediaStore 中的音乐列表。
 */
public final class MusicLoader {

    private MusicLoader() {
    }

    public static List<Song> load(Context context) {
        List<Song> songs = new ArrayList<>();
        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION,
        };
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";

        try (Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection, selection, null, sortOrder)) {
            if (cursor == null) {
                return songs;
            }
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
            int titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
            int artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
            int durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);

            while (cursor.moveToNext()) {
                long id = cursor.getLong(idColumn);
                String title = cursor.getString(titleColumn);
                String artist = cursor.getString(artistColumn);
                long duration = cursor.getLong(durationColumn);
                if (title == null || title.isEmpty()) {
                    title = context.getString(R.string.unknown_song);
                }
                if (artist == null || artist.isEmpty()
                        || MediaStore.UNKNOWN_STRING.equals(artist)) {
                    artist = context.getString(R.string.unknown_artist);
                }
                Uri uri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
                songs.add(new Song(id, title, artist, duration, uri));
            }
        } catch (SecurityException | IllegalArgumentException ignored) {
            // 未授权或媒体库不可用时返回空列表。
        }
        return songs;
    }
}
