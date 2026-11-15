package com.lantianhao.multifragment.model;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * 对象序列化测试：GameRecord（含 Card 列表）经 ObjectOutputStream /
 * ObjectInputStream 往返后，分数、最高分与牌面状态保持不变。
 */
public class GameRecordTest {

    @Test
    public void gameRecordSurvivesSerializationRoundTrip() throws Exception {
        CardMatchingGame game = new CardMatchingGame(Arrays.asList(
                new Card("♥", "A"), new Card("♠", "A"), new Card("♣", "A")), 3);
        game.chooseCardAtIndex(0);
        GameRecord record = game.toRecord(20);

        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
        try (ObjectOutputStream objectOutput = new ObjectOutputStream(byteOutput)) {
            objectOutput.writeObject(record);
        }

        GameRecord restored;
        try (ObjectInputStream objectInput = new ObjectInputStream(
                new ByteArrayInputStream(byteOutput.toByteArray()))) {
            restored = (GameRecord) objectInput.readObject();
        }

        assertEquals(3, restored.getCardsPerMatch());
        assertEquals(record.getScore(), restored.getScore());
        assertEquals(20, restored.getHighestScore());
        assertEquals(3, restored.getCards().size());
        assertTrue(restored.getCards().get(0).isChosen());
        assertEquals("♥A", restored.getCards().get(0).toString());
    }

    @Test
    public void playerSurvivesSerializationRoundTrip() throws Exception {
        Player player = new Player();
        player.setName("兰天浩");
        player.setStudentId("2024110515");
        player.setGender("男");
        player.setPhone("138 0000 0000");
        player.setEmail("lan829@example.com");
        player.setHobbies("阅读、游戏");
        player.setHighScore(42);

        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
        try (ObjectOutputStream objectOutput = new ObjectOutputStream(byteOutput)) {
            objectOutput.writeObject(player);
        }

        Player restored;
        try (ObjectInputStream objectInput = new ObjectInputStream(
                new ByteArrayInputStream(byteOutput.toByteArray()))) {
            restored = (Player) objectInput.readObject();
        }

        assertEquals("兰天浩", restored.getName());
        assertEquals("2024110515", restored.getStudentId());
        assertEquals("阅读、游戏", restored.getHobbies());
        assertEquals(42, restored.getHighScore());
    }
}
