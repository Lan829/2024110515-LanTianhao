package com.lantianhao.musicplayer.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * 播放列表上一首/下一首索引测试。
 */
public class PlaylistUtilsTest {

    @Test
    public void nextIndexLoopsToStart() {
        assertEquals(1, PlaylistUtils.nextIndex(0, 3));
        assertEquals(2, PlaylistUtils.nextIndex(1, 3));
        assertEquals(0, PlaylistUtils.nextIndex(2, 3));
    }

    @Test
    public void previousIndexLoopsToEnd() {
        assertEquals(2, PlaylistUtils.previousIndex(0, 3));
        assertEquals(0, PlaylistUtils.previousIndex(1, 3));
        assertEquals(1, PlaylistUtils.previousIndex(2, 3));
    }

    @Test
    public void emptyPlaylistReturnsMinusOne() {
        assertEquals(-1, PlaylistUtils.nextIndex(0, 0));
        assertEquals(-1, PlaylistUtils.previousIndex(0, 0));
    }
}
