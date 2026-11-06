package com.lantianhao.recyclercardgame.model;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Deck 模型的单元测试。
 */
public class DeckTest {

    @Test
    public void newDeckContainsFiftyTwoCards() {
        assertEquals(52, new Deck().size());
    }

    @Test
    public void drawRemovesCardFromDeck() {
        Deck deck = new Deck();
        Card card = deck.drawRandomCard();
        assertNotNull(card);
        assertEquals(51, deck.size());
    }

    @Test
    public void drawnCardsAreAllDifferent() {
        Deck deck = new Deck();
        Set<String> seen = new HashSet<>();
        for (int i = 0; i < 52; i++) {
            Card card = deck.drawRandomCard();
            assertNotNull(card);
            assertTrue("重复抽到了 " + card, seen.add(card.toString()));
        }
        assertEquals(52, seen.size());
    }

    @Test
    public void drawFromEmptyDeckReturnsNull() {
        Deck deck = new Deck();
        for (int i = 0; i < 52; i++) {
            assertNotNull(deck.drawRandomCard());
        }
        assertEquals(0, deck.size());
        assertNull(deck.drawRandomCard());
    }

    @Test
    public void deckCoversEverySuitAndRank() {
        Deck deck = new Deck();
        Set<String> suits = new HashSet<>();
        Set<String> ranks = new HashSet<>();
        for (int i = 0; i < 52; i++) {
            Card card = deck.drawRandomCard();
            suits.add(card.getSuit());
            ranks.add(card.getRank());
        }
        assertEquals(Card.VALID_SUITS.length, suits.size());
        assertEquals(Card.RANK_STRINGS.length, ranks.size());
    }
}
