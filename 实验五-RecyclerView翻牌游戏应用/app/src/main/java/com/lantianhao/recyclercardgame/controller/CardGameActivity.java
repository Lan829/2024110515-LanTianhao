package com.lantianhao.recyclercardgame.controller;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lantianhao.recyclercardgame.R;
import com.lantianhao.recyclercardgame.data.HighScoreStore;
import com.lantianhao.recyclercardgame.model.CardMatchingGame;
import com.lantianhao.recyclercardgame.view.CardAdapter;

/**
 * 翻牌游戏 Activity（Controller）：使用 RecyclerView 展示 24 张牌
 * （横屏时 32 张），通过 {@link CardAdapter} 接收点击回调并同步模型与界面。
 */
public class CardGameActivity extends AppCompatActivity implements CardAdapter.OnCardClickListener {

    private RecyclerView cardList;
    private TextView scoreValue;

    private CardMatchingGame game;
    private CardAdapter adapter;
    private int cardCount;
    private int spanCount;
    private boolean gameOverAnnounced;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_game);

        cardCount = getResources().getInteger(R.integer.card_count);
        spanCount = getResources().getInteger(R.integer.span_count);

        cardList = findViewById(R.id.card_list);
        scoreValue = findViewById(R.id.score_value);

        game = new CardMatchingGame(cardCount);
        cardList.setLayoutManager(new GridLayoutManager(this, spanCount));
        cardList.setHasFixedSize(true);
        adapter = new CardAdapter(game, this);
        cardList.setAdapter(adapter);
        cardList.post(this::updateCardHeight);

        findViewById(R.id.button_reset).setOnClickListener(v -> restartGame());
        findViewById(R.id.button_exit).setOnClickListener(v -> finish());

        updateScore();
    }

    /**
     * 按当前方向的行数平均分配 RecyclerView 高度，保证牌面规整。
     */
    private void updateCardHeight() {
        int height = cardList.getHeight();
        if (height <= 0 || spanCount <= 0) {
            return;
        }
        int rows = (cardCount + spanCount - 1) / spanCount;
        adapter.setItemHeight(height / rows);
    }

    @Override
    public void onCardClick(int position) {
        game.chooseCardAtIndex(position);
        adapter.notifyDataSetChanged();
        updateScore();

        if (game.isGameOver() && !gameOverAnnounced) {
            gameOverAnnounced = true;
            Toast.makeText(this,
                    getString(R.string.toast_game_over, game.getScore()),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void restartGame() {
        game.reset();
        gameOverAnnounced = false;
        adapter.notifyDataSetChanged();
        updateScore();
    }

    private void updateScore() {
        scoreValue.setText(getString(R.string.score_format, game.getScore()));
        HighScoreStore.saveIfHigher(this, game.getScore());
    }
}
