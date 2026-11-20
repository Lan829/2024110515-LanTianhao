package com.lantianhao.musicplayer.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * 时间格式化工具测试。
 */
public class TimeUtilsTest {

    @Test
    public void formatsZero() {
        assertEquals("00:00", TimeUtils.formatDuration(0));
    }

    @Test
    public void formatsSeconds() {
        assertEquals("00:01", TimeUtils.formatDuration(1000));
        assertEquals("01:05", TimeUtils.formatDuration(65000));
    }

    @Test
    public void formatsMinutes() {
        assertEquals("10:00", TimeUtils.formatDuration(600000));
    }

    @Test
    public void negativeValueTreatedAsZero() {
        assertEquals("00:00", TimeUtils.formatDuration(-500));
    }
}
