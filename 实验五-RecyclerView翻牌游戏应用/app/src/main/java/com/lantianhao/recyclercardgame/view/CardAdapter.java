package com.lantianhao.recyclercardgame.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lantianhao.recyclercardgame.R;
import com.lantianhao.recyclercardgame.model.CardMatchingGame;

/**
 * 翻牌游戏适配器（Adapter）。
 *
 * <p>从 {@link RecyclerView.Adapter} 派生，重载
 * {@link #onCreateViewHolder}、{@link #onBindViewHolder} 与 {@link #getItemCount}，
 * 并通过 {@link OnCardClickListener} 把牌面点击回调给 Controller。</p>
 */
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    /**
     * 牌面点击回调，由 CardGameActivity 实现。
     */
    public interface OnCardClickListener {
        void onCardClick(int position);
    }

    private final CardMatchingGame game;
    private final OnCardClickListener listener;
    private int itemHeight;

    public CardAdapter(CardMatchingGame game, OnCardClickListener listener) {
        this.game = game;
        this.listener = listener;
    }

    /**
     * 设置每张牌的高度（像素），用于适配横竖屏不同牌数。
     */
    public void setItemHeight(int itemHeight) {
        this.itemHeight = itemHeight;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        View itemView = holder.itemView;
        if (itemHeight > 0 && itemView.getLayoutParams().height != itemHeight) {
            ViewGroup.LayoutParams params = itemView.getLayoutParams();
            params.height = itemHeight;
            itemView.setLayoutParams(params);
        }
        holder.cardView.bind(game.cardAtIndex(position));
        itemView.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                listener.onCardClick(adapterPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return game.getCardCount();
    }

    static class CardViewHolder extends RecyclerView.ViewHolder {

        private final CardView cardView;

        CardViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view);
        }
    }
}
