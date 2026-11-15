package com.lantianhao.multifragment.model;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * CardMatchingGame 的单元测试：2 张牌与 3 张牌两种玩法、reset 与记录恢复。
 */
public class CardMatchingGameTest {

    private static CardMatchingGame twoCardGame(Card... cards) {
        return new CardMatchingGame(Arrays.asList(cards), 2);
    }

    private static CardMatchingGame threeCardGame(Card... cards) {
        return new CardMatchingGame(Arrays.asList(cards), 3);
    }

    @Test
    public void twoCardMatchingRankAddsBonus() {
        CardMatchingGame game = twoCardGame(new Card("♥", "A"), new Card("♠", "A"));
        game.chooseCardAtIndex(0);
        game.chooseCardAtIndex(1);

        // -1（第一次选牌）+ 4×4（同点）- 1（第二次选牌）= 14
        assertEquals(14, game.getScore());
        assertTrue(game.cardAtIndex(0).isMatched());
        assertTrue(game.cardAtIndex(1).isMatched());
        assertTrue(game.isGameOver());
    }

    @Test
    public void twoCardMismatchAppliesPenalty() {
        CardMatchingGame game = twoCardGame(new Card("♥", "2"), new Card("♠", "3"));
        game.chooseCardAtIndex(0);
        game.chooseCardAtIndex(1);

        assertEquals(-4, game.getScore());
        assertFalse(game.cardAtIndex(0).isChosen());
        assertTrue(game.cardAtIndex(1).isChosen());
    }

    @Test
    public void threeCardMatchingRankAddsBonus() {
        CardMatchingGame game = threeCardGame(
                new Card("♥", "A"), new Card("♠", "A"), new Card("♣", "A"));
        game.chooseCardAtIndex(0);
        game.chooseCardAtIndex(1);
        game.chooseCardAtIndex(2);

        // -3（三次选牌）+ 8×4（三张同点）= 29
        assertEquals(29, game.getScore());
        assertTrue(game.isGameOver());
    }

    @Test
    public void threeCardMatchingSuitAddsBonus() {
        CardMatchingGame game = threeCardGame(
                new Card("♥", "2"), new Card("♥", "5"), new Card("♥", "K"));
        game.chooseCardAtIndex(0);
        game.chooseCardAtIndex(1);
        game.chooseCardAtIndex(2);

        assertEquals(9, game.getScore());
    }

    @Test
    public void threeCardMismatchKeepsOnlyLastCardChosen() {
        CardMatchingGame game = threeCardGame(
                new Card("♥", "2"), new Card("♠", "5"), new Card("♣", "9"));
        game.chooseCardAtIndex(0);
        game.chooseCardAtIndex(1);
        game.chooseCardAtIndex(2);

        assertEquals(-5, game.getScore());
        assertFalse(game.cardAtIndex(0).isChosen());
        assertFalse(game.cardAtIndex(1).isChosen());
        assertTrue(game.cardAtIndex(2).isChosen());
    }

    @Test
    public void resetClearsScoreAndCardStates() {
        CardMatchingGame game = threeCardGame(
                new Card("♥", "A"), new Card("♠", "A"), new Card("♣", "A"));
        game.chooseCardAtIndex(0);
        game.chooseCardAtIndex(1);
        game.chooseCardAtIndex(2);
        assertTrue(game.getScore() > 0);

        game.reset();

        assertEquals(0, game.getScore());
        assertEquals(3, game.getCardCount());
        for (int i = 0; i < game.getCardCount(); i++) {
            assertFalse(game.cardAtIndex(i).isChosen());
            assertFalse(game.cardAtIndex(i).isMatched());
        }
    }

    @Test
    public void randomTwoCardGameUsesRequestedCount() {
        CardMatchingGame game = new CardMatchingGame(24, 2);
        assertEquals(24, game.getCardCount());
        assertEquals(2, game.getCardsPerMatch());
        assertEquals(0, game.getScore());
    }

    @Test
    public void recordRestoresScoreCardsAndHighestScore() {
        CardMatchingGame game = twoCardGame(new Card("♥", "A"), new Card("♠", "A"));
        game.chooseCardAtIndex(0);

        GameRecord record = game.toRecord(12);
        CardMatchingGame restored = CardMatchingGame.restore(record);

        assertEquals(game.getScore(), restored.getScore());
        assertEquals(2, restored.getCardCount());
        assertEquals(2, restored.getCardsPerMatch());
        assertTrue(restored.cardAtIndex(0).isChosen());
        assertEquals(12, record.getHighestScore());
    }
}
