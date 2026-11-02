package com.lantianhao.cardgame.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Card 模型的单元测试。
 */
public class CardTest {

    @Test
    public void sameRankScoresFour() {
        Card heartAce = new Card("♥", "A");
        Card spadeAce = new Card("♠", "A");
        assertEquals(4, heartAce.match(new Card[]{spadeAce}));
    }

    @Test
    public void sameSuitScoresOne() {
        Card heartTwo = new Card("♥", "2");
        Card heartThree = new Card("♥", "3");
        assertEquals(1, heartTwo.match(new Card[]{heartThree}));
    }

    @Test
    public void differentSuitAndRankScoresZero() {
        Card heartTwo = new Card("♥", "2");
        Card spadeThree = new Card("♠", "3");
        assertEquals(0, heartTwo.match(new Card[]{spadeThree}));
    }

    @Test
    public void matchRequiresExactlyOneOtherCard() {
        Card card = new Card("♥", "A");
        Card sameRank = new Card("♠", "A");
        Card sameSuit = new Card("♥", "K");
        assertEquals(0, card.match(new Card[]{}));
        assertEquals(0, card.match(new Card[]{sameRank, sameSuit}));
        assertEquals(0, card.match(null));
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
