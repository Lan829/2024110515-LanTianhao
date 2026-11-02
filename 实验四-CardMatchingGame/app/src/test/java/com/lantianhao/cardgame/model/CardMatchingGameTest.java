package com.lantianhao.cardgame.model;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * CardMatchingGame 模型的单元测试。
 *
 * <p>通过包级构造器固定牌面，保证计分逻辑可重复验证：
 * 选牌 -1 分，配对成功加 配对得分×4，配对失败 -2 分。</p>
 */
public class CardMatchingGameTest {

    private static CardMatchingGame gameOf(Card... cards) {
        return new CardMatchingGame(Arrays.asList(cards));
    }

    @Test
    public void choosingCardCostsOnePoint() {
        CardMatchingGame game = gameOf(new Card("♥", "A"), new Card("♠", "A"));
        game.chooseCardAtIndex(0);
        assertEquals(-CardMatchingGame.COST_TO_CHOOSE, game.getScore());
        assertTrue(game.cardAtIndex(0).isChosen());
    }

    @Test
    public void matchingRankAddsBonusAndMarksBothCards() {
        CardMatchingGame game = gameOf(new Card("♥", "A"), new Card("♠", "A"));
        game.chooseCardAtIndex(0);
        game.chooseCardAtIndex(1);

        // -1（第一次选牌）+ 4×4（点数相同）- 1（第二次选牌）= 14
        assertEquals(14, game.getScore());
        assertTrue(game.cardAtIndex(0).isMatched());
        assertTrue(game.cardAtIndex(1).isMatched());
    }

    @Test
    public void matchingSuitAddsBonus() {
        CardMatchingGame game = gameOf(new Card("♥", "2"), new Card("♥", "3"));
        game.chooseCardAtIndex(0);
        game.chooseCardAtIndex(1);

        // -1 + 1×4 - 1 = 2
        assertEquals(2, game.getScore());
    }

    @Test
    public void mismatchAppliesPenaltyAndUnchoosesOtherCard() {
        CardMatchingGame game = gameOf(new Card("♥", "2"), new Card("♠", "3"));
        game.chooseCardAtIndex(0);
        game.chooseCardAtIndex(1);

        // -1 - 2 - 1 = -4
        assertEquals(-4, game.getScore());
        assertFalse(game.cardAtIndex(0).isChosen());
        assertTrue(game.cardAtIndex(1).isChosen());
    }

    @Test
    public void tappingChosenCardAgainUnchoosesWithoutCost() {
        CardMatchingGame game = gameOf(new Card("♥", "2"), new Card("♠", "3"));
        game.chooseCardAtIndex(0);
        game.chooseCardAtIndex(0);

        assertEquals(-1, game.getScore());
        assertFalse(game.cardAtIndex(0).isChosen());
    }

    @Test
    public void matchedCardCannotBeChosenAgain() {
        CardMatchingGame game = gameOf(new Card("♥", "A"), new Card("♠", "A"));
        game.chooseCardAtIndex(0);
        game.chooseCardAtIndex(1);
        int scoreAfterMatch = game.getScore();

        game.chooseCardAtIndex(0);
        assertEquals(scoreAfterMatch, game.getScore());
        assertTrue(game.cardAtIndex(0).isMatched());
    }

    @Test
    public void gameIsOverOnlyWhenEveryCardIsMatched() {
        CardMatchingGame game = gameOf(new Card("♥", "A"), new Card("♠", "A"));
        assertFalse(game.isGameOver());

        game.chooseCardAtIndex(0);
        assertFalse(game.isGameOver());

        game.chooseCardAtIndex(1);
        assertTrue(game.isGameOver());
    }

    @Test
    public void randomGameDrawsRequestedNumberOfCards() {
        CardMatchingGame game = new CardMatchingGame(16);
        assertEquals(16, game.getCardCount());
        assertEquals(0, game.getScore());
        for (int i = 0; i < game.getCardCount(); i++) {
            assertFalse(game.cardAtIndex(i).isMatched());
        }
    }
}
