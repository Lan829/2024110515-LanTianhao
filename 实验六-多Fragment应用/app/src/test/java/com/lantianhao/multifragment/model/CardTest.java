package com.lantianhao.multifragment.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Card 模型的单元测试：同时覆盖 2 张牌与 3 张牌规则。
 */
public class CardTest {

    @Test
    public void twoCardsSameRankScoresFour() {
        Card heartAce = new Card("♥", "A");
        Card spadeAce = new Card("♠", "A");
        assertEquals(4, heartAce.match(new Card[]{spadeAce}));
    }

    @Test
    public void twoCardsSameSuitScoresOne() {
        Card heartTwo = new Card("♥", "2");
        Card heartThree = new Card("♥", "3");
        assertEquals(1, heartTwo.match(new Card[]{heartThree}));
    }

    @Test
    public void threeCardsSameRankScoresEight() {
        Card heartAce = new Card("♥", "A");
        Card spadeAce = new Card("♠", "A");
        Card clubAce = new Card("♣", "A");
        assertEquals(8, heartAce.match(new Card[]{spadeAce, clubAce}));
    }

    @Test
    public void threeCardsSameSuitScoresThree() {
        Card heartTwo = new Card("♥", "2");
        Card heartFive = new Card("♥", "5");
        Card heartKing = new Card("♥", "K");
        assertEquals(3, heartTwo.match(new Card[]{heartFive, heartKing}));
    }

    @Test
    public void mixedCardsScoreZero() {
        Card heartTwo = new Card("♥", "2");
        Card spadeTwo = new Card("♠", "2");
        Card clubNine = new Card("♣", "9");
        assertEquals(0, heartTwo.match(new Card[]{spadeTwo, clubNine}));
    }

    @Test
    public void matchRequiresOneOrTwoOtherCards() {
        Card card = new Card("♥", "A");
        Card sameRank = new Card("♠", "A");
        Card sameSuit = new Card("♥", "K");
        assertEquals(0, card.match(null));
        assertEquals(0, card.match(new Card[]{}));
        assertEquals(0, card.match(new Card[]{sameRank, sameSuit, sameRank}));
    }

    @Test
    public void toStringContainsSuitAndRank() {
        assertEquals("♥A", new Card("♥", "A").toString());
        assertEquals("♦10", new Card("♦", "10").toString());
    }

    @Test
    public void cardStartsUnchosenAndUnmatched() {
        Card card = new Card("♣", "Q");
        assertFalse(card.isChosen());
        assertFalse(card.isMatched());
    }
}
