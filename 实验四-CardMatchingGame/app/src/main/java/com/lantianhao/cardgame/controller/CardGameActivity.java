package com.lantianhao.cardgame.controller;

import android.os.Bundle;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.lantianhao.cardgame.R;
import com.lantianhao.cardgame.data.HighScoreStore;
import com.lantianhao.cardgame.model.CardMatchingGame;
import com.lantianhao.cardgame.view.CardView;

import java.util.ArrayList;
import java.util.List;

/**
 * 翻牌游戏 Activity（Controller）：接收用户点击，调用 {@link CardMatchingGame}
 * 处理游戏逻辑，并把模型状态同步到 {@link CardView} 牌面。
 *
 * <p>“重置游戏”会用新牌重新开始一局；“退出”返回主界面。</p>
 */
public class CardGameActivity extends AppCompatActivity {

    private static final int CARD_COLUMNS = 4;
    private static final int CARD_COUNT = 16;

    private GridLayout cardGrid;
    private TextView scoreValue;
    private final List<CardView> cardViews = new ArrayList<>();

    private CardMatchingGame game;
    private boolean gameOverAnnounced;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_game);
        cardGrid = findViewById(R.id.card_grid);
        scoreValue = findViewById(R.id.score_value);
        cardGrid.setAlignmentMode(GridLayout.ALIGN_BOUNDS);
        cardGrid.setUseDefaultMargins(false);

        findViewById(R.id.button_reset).setOnClickListener(v -> startNewGame());
        findViewById(R.id.button_exit).setOnClickListener(v -> finish());

        startNewGame();
    }

    /**
     * 重新开始一局游戏。
     */
    private void startNewGame() {
        game = new CardMatchingGame(CARD_COUNT);
        gameOverAnnounced = false;
        buildBoard();
        updateScore();
    }

    /**
     * 根据模型中的牌生成 4×4 的牌面控件。
     */
    private void buildBoard() {
        cardGrid.removeAllViews();
        cardViews.clear();

        int margin = dp(4);
        for (int index = 0; index < game.getCardCount(); index++) {
            final int cardIndex = index;
            CardView cardView = new CardView(this);
            cardView.bind(game.cardAtIndex(index));
            // tag 供 UI 测试定位第 index 张牌
            cardView.setTag(cardIndex);
            cardView.setOnClickListener(v -> onCardClicked(cardIndex));

            GridLayout.LayoutParams params = new GridLayout.LayoutParams(
                    GridLayout.spec(index / CARD_COLUMNS, 1f),
                    GridLayout.spec(index % CARD_COLUMNS, 1f));
            params.width = 0;
            params.height = 0;
            params.setMargins(margin, margin, margin, margin);
            cardGrid.addView(cardView, params);
            cardViews.add(cardView);
        }
    }

    private void onCardClicked(int index) {
        game.chooseCardAtIndex(index);
        syncBoard();
        updateScore();

        if (game.isGameOver() && !gameOverAnnounced) {
            gameOverAnnounced = true;
            Toast.makeText(this,
                    getString(R.string.toast_game_over, game.getScore()),
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 把模型状态同步到每一张牌。
     */
    private void syncBoard() {
        for (CardView cardView : cardViews) {
            cardView.render();
        }
    }

    private void updateScore() {
        scoreValue.setText(getString(R.string.score_format, game.getScore()));
        HighScoreStore.saveIfHigher(this, game.getScore());
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
