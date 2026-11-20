package com.lantianhao.musicplayer.util;

import java.util.Locale;

/**
 * 时间格式化工具：把毫秒转换为 mm:ss。
 */
public final class TimeUtils {

    private TimeUtils() {
    }

    public static String formatDuration(long millis) {
        if (millis < 0) {
            millis = 0;
        }
        long totalSeconds = millis / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }
}
