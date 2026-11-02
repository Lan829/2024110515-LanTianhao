package com.lantianhao.cardgame.model;

/**
 * 一张扑克牌（Model）。
 *
 * <p>参照实验附件中的 Card.kt 实现：牌面由花色 suit 与点数 rank 组成，
 * 并记录“是否被选中”“是否已配对”两个状态。配对规则为：</p>
 * <ul>
 *   <li>点数相同：得 4 分；</li>
 *   <li>花色相同：得 1 分；</li>
 *   <li>都不相同：0 分。</li>
 * </ul>
 */
public class Card {

    /** 所有点数。 */
    public static final String[] RANK_STRINGS = {
            "A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"
    };

    /** 所有花色。 */
    public static final String[] VALID_SUITS = {"♥", "♦", "♠", "♣"};

    private final String suit;
    private final String rank;
    private boolean chosen;
    private boolean matched;

    public Card(String suit, String rank) {
        this.suit = suit;
        this.rank = rank;
    }

    public String getSuit() {
        return suit;
    }

    public String getRank() {
        return rank;
    }

    public boolean isChosen() {
        return chosen;
    }

    public void setChosen(boolean chosen) {
        this.chosen = chosen;
    }

    public boolean isMatched() {
        return matched;
    }

    public void setMatched(boolean matched) {
        this.matched = matched;
    }

    /**
     * 计算本牌与另一张牌（数组长度为 1）的配对得分。
     */
    public int match(Card[] otherCards) {
        int score = 0;
        if (otherCards != null && otherCards.length == 1) {
            Card otherCard = otherCards[0];
            if (otherCard.rank.equals(rank)) {
                score = 4;
            } else if (otherCard.suit.equals(suit)) {
                score = 1;
            }
        }
        return score;
    }

    @Override
    public String toString() {
        return suit + rank;
    }
}
