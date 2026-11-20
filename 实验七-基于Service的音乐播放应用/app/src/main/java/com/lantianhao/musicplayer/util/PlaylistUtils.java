package com.lantianhao.musicplayer.util;

/**
 * 播放列表索引工具：处理上一首 / 下一首的循环切换。
 */
public final class PlaylistUtils {

    private PlaylistUtils() {
    }

    public static int nextIndex(int currentIndex, int size) {
        if (size <= 0) {
            return -1;
        }
        return (currentIndex + 1) % size;
    }

    public static int previousIndex(int currentIndex, int size) {
        if (size <= 0) {
            return -1;
        }
        return (currentIndex - 1 + size) % size;
    }
}
