package com.lantianhao.recyclercardgame.model;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * CardMatchingGame 模型的单元测试（三张牌玩法）。
 *
 * <p>通过包级构造器固定牌面，验证三张牌配对、失败扣分、重置与结束判定。</p>
 */
public class CardMatchingGameTest {

    private static CardMatchingGame gameOf(Card... cards) {
        return new CardMatchingGame(Arrays.asList(cards));
    }

    @Test
    public void choosingCardsCostsOnePointEach() {
        CardMatchingGame game = gameOf(
                new Card("♥", "A"), new Card("♠", "A"), new Card("♣", "A"));
        game.chooseCardAtIndex(0);
        game.chooseCardAtIndex(1);
        assertEquals(-2, game.getScore());
        assertTrue(game.cardAtIndex(0).isChosen());
        assertTrue(game.cardAtIndex(1).isChosen());
    }

    @Test
    public void threeSameRankCardsMatchAndAddBonus() {
        CardMatchingGame game = gameOf(
                new Card("♥", "A"), new Card("♠", "A"), new Card("♣", "A"));
        game.chooseCardAtIndex(0);
        game.chooseCardAtIndex(1);
        game.chooseCardAtIndex(2);

        // -3（三次选牌）+ 8×4（三张同点）= 29
        assertEquals(29, game.getScore());
        assertTrue(game.cardAtIndex(0).isMatched());
        assertTrue(game.cardAtIndex(1).isMatched());
        assertTrue(game.cardAtIndex(2).isMatched());
        assertTrue(game.isGameOver());
    }

    @Test
    public void threeSameSuitCardsMatchAndAddBonus() {
        CardMatchingGame game = gameOf(
                new Card("♥", "2"), new Card("♥", "5"), new Card("♥", "K"));
        game.chooseCardAtIndex(0);
        game.chooseCardAtIndex(1);
        game.chooseCardAtIndex(2);

        // -3（三次选牌）+ 3×4（三张同花）= 9
        assertEquals(9, game.getScore());
        assertTrue(game.isGameOver());
    }

    @Test
    public void mismatchAppliesPenaltyAndKeepsOnlyLastCardChosen() {
        CardMatchingGame game = gameOf(
                new Card("♥", "2"), new Card("♠", "5"), new Card("♣", "9"));
        game.chooseCardAtIndex(0);
        game.chooseCardAtIndex(1);
        game.chooseCardAtIndex(2);

        // -3（三次选牌）- 2（配对失败）= -5
        assertEquals(-5, game.getScore());
        assertFalse(game.cardAtIndex(0).isChosen());
        assertFalse(game.cardAtIndex(1).isChosen());
        assertTrue(game.cardAtIndex(2).isChosen());
        assertFalse(game.isGameOver());
    }

    @Test
    public void tappingChosenCardAgainUnchoosesWithoutCost() {
        CardMatchingGame game = gameOf(
                new Card("♥", "2"), new Card("♠", "5"), new Card("♣", "9"));
        game.chooseCardAtIndex(0);
        game.chooseCardAtIndex(0);

        assertEquals(-1, game.getScore());
        assertFalse(game.cardAtIndex(0).isChosen());
    }

    @Test
    public void matchedCardsCannotBeChosenAgain() {
        CardMatchingGame game = gameOf(
                new Card("♥", "A"), new Card("♠", "A"), new Card("♣", "A"));
        game.chooseCardAtIndex(0);
        game.chooseCardAtIndex(1);
        game.chooseCardAtIndex(2);
        int scoreAfterMatch = game.getScore();

        game.chooseCardAtIndex(0);
        assertEquals(scoreAfterMatch, game.getScore());
        assertTrue(game.cardAtIndex(0).isMatched());
    }

    @Test
    public void resetClearsScoreAndCardStates() {
        CardMatchingGame game = gameOf(
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
        assertFalse(game.isGameOver());
    }

    @Test
    public void randomGameCanResetWithTwentyFourCards() {
        CardMatchingGame game = new CardMatchingGame(24);
        assertEquals(24, game.getCardCount());
        game.chooseCardAtIndex(0);
        game.reset();

        assertEquals(0, game.getScore());
        assertEquals(24, game.getCardCount());
        for (int i = 0; i < game.getCardCount(); i++) {
            assertFalse(game.cardAtIndex(i).isChosen());
            assertFalse(game.cardAtIndex(i).isMatched());
        }
    }
}
