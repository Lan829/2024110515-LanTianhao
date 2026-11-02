package com.lantianhao.cardgame.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 翻牌游戏模型（Model），参照实验附件中的 CardMatchingGame.kt 实现。
 *
 * <p>每次选牌先扣除 {@link #COST_TO_CHOOSE} 分；若与之前选中的牌配对成功，
 * 按 {@code 配对得分 × MATCH_BONUS} 加分并把两张牌标记为已配对；
 * 配对失败则扣除 {@link #MISMATCH_PENALTY} 分并取消另一张牌的选中状态。</p>
 */
public class CardMatchingGame {

    public static final int MISMATCH_PENALTY = 2;
    public static final int MATCH_BONUS = 4;
    public static final int COST_TO_CHOOSE = 1;

    private int score;
    private final List<Card> cards = new ArrayList<>();

    /**
     * 从一副新牌中随机抽取 count 张牌作为本局牌面。
     */
    public CardMatchingGame(int count) {
        Deck deck = new Deck();
        for (int i = 0; i < count; i++) {
            Card card = deck.drawRandomCard();
            if (card != null) {
                cards.add(card);
            }
        }
    }

    /**
     * 使用指定的牌构造一局游戏，便于单元测试固定牌面。
     */
    CardMatchingGame(List<Card> cards) {
        this.cards.addAll(cards);
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
     * 玩家点击第 index 张牌时调用。
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
        for (Card otherCard : cards) {
            if (otherCard.isChosen() && !otherCard.isMatched()) {
                int matchScore = card.match(new Card[]{otherCard});
                if (matchScore > 0) {
                    score += matchScore * MATCH_BONUS;
                    otherCard.setMatched(true);
                    card.setMatched(true);
                } else {
                    score -= MISMATCH_PENALTY;
                    otherCard.setChosen(false);
                }
                break;
            }
        }
        score -= COST_TO_CHOOSE;
        card.setChosen(true);
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
