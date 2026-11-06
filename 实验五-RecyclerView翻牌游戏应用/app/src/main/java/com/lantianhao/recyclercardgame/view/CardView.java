package com.lantianhao.recyclercardgame.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import com.lantianhao.recyclercardgame.R;
import com.lantianhao.recyclercardgame.model.Card;

/**
 * 单张牌的显示控件（View）。
 *
 * <p>根据 {@link Card} 的选中、配对状态切换背景与文字：牌背显示问号，
 * 翻开后显示花色与点数（红桃、方块为红色，黑桃、梅花为黑色），
 * 已配对的牌变绿并降低透明度。</p>
 */
public class CardView extends AppCompatTextView {

    private Card card;

    public CardView(@NonNull Context context) {
        super(context);
        init();
    }

    private void init() {
        setGravity(Gravity.CENTER);
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        setTypeface(getTypeface(), Typeface.BOLD);
        setIncludeFontPadding(false);
        setPadding(dp(4), dp(4), dp(4), dp(4));
        setElevation(dp(2));
        setClickable(true);
        setFocusable(true);
    }

    /**
     * 绑定模型对象并刷新显示。
     */
    public void bind(Card card) {
        this.card = card;
        render();
    }

    /**
     * 按模型当前状态刷新牌面。
     */
    public void render() {
        if (card == null) {
            return;
        }
        if (card.isMatched()) {
            setText(faceText());
            setTextColor(faceColor());
            setBackgroundResource(R.drawable.card_face_matched);
            setAlpha(0.6f);
        } else if (card.isChosen()) {
            setText(faceText());
            setTextColor(faceColor());
            setBackgroundResource(R.drawable.card_face_chosen);
            setAlpha(1f);
        } else {
            setText(R.string.card_back_symbol);
            setTextColor(ContextCompat.getColor(getContext(), R.color.white));
            setBackgroundResource(R.drawable.card_back);
            setAlpha(1f);
        }
        setContentDescription(card.isChosen() || card.isMatched()
                ? getContext().getString(R.string.desc_card_face, faceText())
                : getContext().getString(R.string.desc_card_hidden));
    }

    private String faceText() {
        return getContext().getString(R.string.card_face_format, card.getSuit(), card.getRank());
    }

    private int faceColor() {
        String suit = card.getSuit();
        boolean red = Card.VALID_SUITS[0].equals(suit) || Card.VALID_SUITS[1].equals(suit);
        return ContextCompat.getColor(getContext(), red ? R.color.suit_red : R.color.suit_black);
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
