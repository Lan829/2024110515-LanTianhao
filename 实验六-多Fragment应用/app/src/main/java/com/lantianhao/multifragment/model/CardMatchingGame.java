package com.lantianhao.multifragment.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 翻牌游戏模型（Model），同时支持 2 张牌与 3 张牌两种玩法。
 *
 * <p>每次选牌扣 1 分；选满 {@code cardsPerMatch} 张后判定：配对成功时
 * 加分并标记已配对，失败时扣 2 分并取消其它牌的选中状态。</p>
 */
public class CardMatchingGame {

    public static final int MISMATCH_PENALTY = 2;
    public static final int MATCH_BONUS = 4;
    public static final int COST_TO_CHOOSE = 1;

    private final int cardsPerMatch;
    private final boolean fixedDeck;
    private final List<Card> cards = new ArrayList<>();
    private int count;
    private int score;

    /**
     * 从一副新牌中随机抽取 count 张牌，按 cardsPerMatch 张一组进行配对。
     */
    public CardMatchingGame(int count, int cardsPerMatch) {
        this.count = count;
        this.cardsPerMatch = cardsPerMatch;
        this.fixedDeck = false;
        dealCards();
    }

    /**
     * 使用指定的牌构造一局游戏，便于单元测试固定牌面。
     */
    CardMatchingGame(List<Card> cards, int cardsPerMatch) {
        this.count = cards.size();
        this.cardsPerMatch = cardsPerMatch;
        this.fixedDeck = true;
        this.cards.addAll(cards);
    }

    private CardMatchingGame(GameRecord record) {
        this.count = record.getCards().size();
        this.cardsPerMatch = record.getCardsPerMatch();
        this.fixedDeck = false;
        this.score = record.getScore();
        this.cards.addAll(record.getCards());
    }

    /**
     * 从序列化记录恢复牌局。
     */
    public static CardMatchingGame restore(GameRecord record) {
        return new CardMatchingGame(record);
    }

    /**
     * 导出当前牌局，供对象流保存。
     */
    public GameRecord toRecord(int highestScore) {
        return new GameRecord(cardsPerMatch, score, highestScore, new ArrayList<>(cards));
    }

    /**
     * 重新开始一局：分数归零、清除选中/配对状态并重新发牌。
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

    public int getCardsPerMatch() {
        return cardsPerMatch;
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
     * 玩家点击第 index 张牌时调用，选满 cardsPerMatch 张后自动判定。
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

        if (chosenCards.size() == cardsPerMatch - 1) {
            int matchScore = card.match(chosenCards.toArray(new Card[0]));
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
