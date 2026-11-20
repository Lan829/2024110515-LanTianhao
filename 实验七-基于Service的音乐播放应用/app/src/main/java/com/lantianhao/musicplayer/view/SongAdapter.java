package com.lantianhao.musicplayer.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lantianhao.musicplayer.R;
import com.lantianhao.musicplayer.model.Song;
import com.lantianhao.musicplayer.util.TimeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 歌曲列表适配器（RecyclerView.Adapter）。
 */
public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    public interface OnSongClickListener {
        void onSongClick(int position);
    }

    private final List<Song> songs = new ArrayList<>();
    private final OnSongClickListener listener;

    public SongAdapter(OnSongClickListener listener) {
        this.listener = listener;
    }

    public void submit(List<Song> newSongs) {
        songs.clear();
        songs.addAll(newSongs);
        notifyDataSetChanged();
    }

    public Song getItem(int position) {
        return songs.get(position);
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_song, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song song = songs.get(position);
        holder.title.setText(song.getTitle());
        holder.artist.setText(song.getArtist());
        holder.duration.setText(TimeUtils.formatDuration(song.getDuration()));
        holder.itemView.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                listener.onSongClick(adapterPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    static class SongViewHolder extends RecyclerView.ViewHolder {

        private final TextView title;
        private final TextView artist;
        private final TextView duration;

        SongViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.song_title);
            artist = itemView.findViewById(R.id.song_artist);
            duration = itemView.findViewById(R.id.song_duration);
        }
    }
}
