package com.lantianhao.multifragment.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 一局翻牌游戏的历史记录（Model），实现 Serializable，
 * 通过 openFileOutput + ObjectOutputStream 保存、openFileInput + ObjectInputStream 恢复。
 */
public class GameRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int cardsPerMatch;
    private final int score;
    private final int highestScore;
    private final List<Card> cards;

    public GameRecord(int cardsPerMatch, int score, int highestScore, List<Card> cards) {
        this.cardsPerMatch = cardsPerMatch;
        this.score = score;
        this.highestScore = highestScore;
        this.cards = new ArrayList<>(cards);
    }

    public int getCardsPerMatch() {
        return cardsPerMatch;
    }

    public int getScore() {
        return score;
    }

    public int getHighestScore() {
        return highestScore;
    }

    public List<Card> getCards() {
        return new ArrayList<>(cards);
    }
}
