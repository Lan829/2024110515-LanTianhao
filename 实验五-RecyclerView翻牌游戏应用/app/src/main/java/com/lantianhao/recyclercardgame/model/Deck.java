package com.lantianhao.recyclercardgame.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 一副 52 张的扑克牌（Model），参照实验附件中的 Deck.kt 实现。
 */
public class Deck {

    private final List<Card> cards = new ArrayList<>();
    private final Random random = new Random();

    public Deck() {
        for (String suit : Card.VALID_SUITS) {
            for (String rank : Card.RANK_STRINGS) {
                cards.add(new Card(suit, rank));
            }
        }
    }

    /**
     * 随机抽取一张牌，牌堆为空时返回 null。
     */
    public Card drawRandomCard() {
        if (cards.isEmpty()) {
            return null;
        }
        return cards.remove(random.nextInt(cards.size()));
    }

    public int size() {
        return cards.size();
    }
}
