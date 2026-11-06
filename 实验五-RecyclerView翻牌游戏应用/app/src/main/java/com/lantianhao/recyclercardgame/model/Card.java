package com.lantianhao.recyclercardgame.model;

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
     * 计算本牌与另外两张牌（共 3 张）的配对得分。
     *
     * <p>三张牌点数全部相同得 8 分；三张牌花色全部相同得 3 分；
     * 否则得 0 分。分数对应三张牌两两配对得分之和。</p>
     */
    public int match(Card[] otherCards) {
        if (otherCards == null || otherCards.length != 2) {
            return 0;
        }
        boolean sameRank = rank.equals(otherCards[0].rank)
                && rank.equals(otherCards[1].rank);
        if (sameRank) {
            return 8;
        }
        boolean sameSuit = suit.equals(otherCards[0].suit)
                && suit.equals(otherCards[1].suit);
        if (sameSuit) {
            return 3;
        }
        return 0;
    }

    @Override
    public String toString() {
        return suit + rank;
    }
}
