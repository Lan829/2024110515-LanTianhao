package com.lantianhao.recyclercardgame.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 翻牌游戏模型（Model），在实验四基础上扩展为“三张牌”玩法。
 *
 * <p>玩家依次选中 3 张牌：三张点数相同或花色相同时配对成功，
 * 按 {@code 配对得分 × MATCH_BONUS} 加分；否则扣分并取消前两张牌的选中状态。
 * 每次选牌仍扣除 {@link #COST_TO_CHOOSE} 分。</p>
 */
public class CardMatchingGame {

    public static final int MISMATCH_PENALTY = 2;
    public static final int MATCH_BONUS = 4;
    public static final int COST_TO_CHOOSE = 1;
    /** 每局每次配对需要的牌数。 */
    public static final int CARDS_PER_MATCH = 3;

    private int score;
    private final int count;
    private final boolean fixedDeck;
    private final List<Card> cards = new ArrayList<>();

    /**
     * 从一副新牌中随机抽取 count 张牌作为本局牌面。
     */
    public CardMatchingGame(int count) {
        this.count = count;
        this.fixedDeck = false;
        dealCards();
    }

    /**
     * 使用指定的牌构造一局游戏，便于单元测试固定牌面。
     */
    CardMatchingGame(List<Card> cards) {
        this.count = cards.size();
        this.fixedDeck = true;
        this.cards.addAll(cards);
    }

    /**
     * 重新开始一局：分数归零，随机牌局重新发牌；测试用的固定牌面只清空状态。
     */
    public void reset() {
        score = 0;
        for (Card card : cards) {
            card.setChosen(false);
            card.setMatched(false);
        }
        if (!fixedDeck) {
            dealCards();
        }
    }

    private void dealCards() {
        cards.clear();
        Deck deck = new Deck();
        for (int i = 0; i < count; i++) {
            Card card = deck.drawRandomCard();
            if (card != null) {
                cards.add(card);
            }
        }
    }

    public int getScore() {
        return score;
    }

    public int getCardCount() {
        return cards.size();
    }

    public Card cardAtIndex(int index) {
        return cards.get(index);
    }

    /**
     * 玩家点击第 index 张牌时调用。选满 3 张后自动判定配对结果。
     */
    public void chooseCardAtIndex(int index) {
        Card card = cardAtIndex(index);
        if (card.isMatched()) {
            return;
        }
        if (card.isChosen()) {
            card.setChosen(false);
            return;
        }

        List<Card> chosenCards = new ArrayList<>();
        for (Card otherCard : cards) {
            if (otherCard.isChosen() && !otherCard.isMatched()) {
                chosenCards.add(otherCard);
            }
        }

        if (chosenCards.size() == CARDS_PER_MATCH - 1) {
            int matchScore = card.match(new Card[]{chosenCards.get(0), chosenCards.get(1)});
            if (matchScore > 0) {
                score += matchScore * MATCH_BONUS;
                for (Card chosenCard : chosenCards) {
                    chosenCard.setChosen(false);
                    chosenCard.setMatched(true);
                }
                card.setMatched(true);
            } else {
                score -= MISMATCH_PENALTY;
                for (Card chosenCard : chosenCards) {
                    chosenCard.setChosen(false);
                }
                card.setChosen(true);
            }
        } else {
            card.setChosen(true);
        }
        score -= COST_TO_CHOOSE;
    }

    /**
     * 是否所有牌都已完成配对。
     */
    public boolean isGameOver() {
        for (Card card : cards) {
            if (!card.isMatched()) {
                return false;
            }
        }
        return true;
    }
}
