package com.lantianhao.multifragment.model;

import java.io.Serializable;

/**
 * 一张扑克牌（Model），实现 Serializable 以便通过对象流保存牌面历史。
 *
 * <p>配对规则按“本牌 + 其它牌”的张数区分：</p>
 * <ul>
 *   <li>其它牌 1 张（2 张牌玩法）：点数相同 4 分，花色相同 1 分；</li>
 *   <li>其它牌 2 张（3 张牌玩法）：三张点数相同 8 分，三张花色相同 3 分；</li>
 *   <li>其它情况 0 分。</li>
 * </ul>
 */
public class Card implements Serializable {

    private static final long serialVersionUID = 1L;

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
     * 计算本牌与若干张其它牌的配对得分。
     */
    public int match(Card[] otherCards) {
        if (otherCards == null || otherCards.length == 0 || otherCards.length > 2) {
            return 0;
        }
        boolean sameRank = true;
        boolean sameSuit = true;
        for (Card otherCard : otherCards) {
            sameRank = sameRank && rank.equals(otherCard.rank);
            sameSuit = sameSuit && suit.equals(otherCard.suit);
        }
        if (sameRank) {
            return otherCards.length == 1 ? 4 : 8;
        }
        if (sameSuit) {
            return otherCards.length == 1 ? 1 : 3;
        }
        return 0;
    }

    @Override
    public String toString() {
        return suit + rank;
    }
}
