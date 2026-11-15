package com.lantianhao.multifragment.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lantianhao.multifragment.R;
import com.lantianhao.multifragment.data.ObjectStore;
import com.lantianhao.multifragment.model.CardMatchingGame;
import com.lantianhao.multifragment.model.GameRecord;
import com.lantianhao.multifragment.view.CardAdapter;

/**
 * 翻牌游戏 Fragment 基类：负责 RecyclerView、得分、最高分与重置，
 * 并通过 {@link ObjectStore} 把牌局序列化保存，退出应用后重新打开可恢复。
 */
public abstract class BaseGameFragment extends Fragment implements CardAdapter.OnCardClickListener {

    /** 每次配对需要的牌数（2 或 3）。 */
    protected abstract int cardsPerMatch();

    /** 游戏标题。 */
    @StringRes
    protected abstract int titleRes();

    /** 保存历史记录的文件名。 */
    protected abstract String gameFile();

    private CardMatchingGame game;
    private CardAdapter adapter;
    private RecyclerView cardList;
    private TextView scoreValue;
    private TextView highestValue;
    private int highestScore;
    private int cardCount;
    private int spanCount;
    private boolean gameOverAnnounced;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_game, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cardList = view.findViewById(R.id.card_list);
        scoreValue = view.findViewById(R.id.score_value);
        highestValue = view.findViewById(R.id.highest_value);
        ((TextView) view.findViewById(R.id.game_title)).setText(titleRes());

        cardCount = getResources().getInteger(R.integer.card_count);
        spanCount = getResources().getInteger(R.integer.span_count);

        GameRecord record = ObjectStore.read(requireContext(), gameFile());
        if (record != null) {
            game = CardMatchingGame.restore(record);
            highestScore = Math.max(0, record.getHighestScore());
        } else {
            game = new CardMatchingGame(cardCount, cardsPerMatch());
            highestScore = 0;
        }

        cardList.setLayoutManager(new GridLayoutManager(requireContext(), spanCount));
        adapter = new CardAdapter(game, this);
        cardList.setAdapter(adapter);
        cardList.post(this::updateCardHeight);

        view.findViewById(R.id.button_reset).setOnClickListener(v -> restartGame());
        updateScore();
    }

    @Override
    public void onPause() {
        super.onPause();
        saveRecord();
    }

    @Override
    public void onCardClick(int position) {
        game.chooseCardAtIndex(position);
        adapter.notifyDataSetChanged();
        updateScore();

        if (game.isGameOver() && !gameOverAnnounced) {
            gameOverAnnounced = true;
            Toast.makeText(requireContext(),
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
        if (game.getScore() > highestScore) {
            highestScore = game.getScore();
        }
        scoreValue.setText(getString(R.string.score_format, game.getScore()));
        highestValue.setText(getString(R.string.highest_score_format, highestScore));
        saveRecord();
    }

    private void saveRecord() {
        if (game != null) {
            ObjectStore.write(requireContext(), gameFile(), game.toRecord(highestScore));
        }
    }

    private void updateCardHeight() {
        int height = cardList.getHeight();
        if (height <= 0 || spanCount <= 0) {
            return;
        }
        int rows = (game.getCardCount() + spanCount - 1) / spanCount;
        adapter.setItemHeight(height / rows);
    }

    /**
     * 测试与调试用：当前显示的最高分。
     */
    protected int getHighestScore() {
        return highestScore;
    }
}
