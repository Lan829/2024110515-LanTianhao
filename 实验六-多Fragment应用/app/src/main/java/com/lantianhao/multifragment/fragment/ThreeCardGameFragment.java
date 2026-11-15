package com.lantianhao.multifragment.fragment;

import com.lantianhao.multifragment.R;
import com.lantianhao.multifragment.data.ObjectStore;

/**
 * 3 张牌翻牌游戏 Fragment：每次选 3 张牌判定配对。
 */
public class ThreeCardGameFragment extends BaseGameFragment {

    @Override
    protected int cardsPerMatch() {
        return 3;
    }

    @Override
    protected int titleRes() {
        return R.string.title_three_game;
    }

    @Override
    protected String gameFile() {
        return ObjectStore.GAME_THREE_FILE;
    }
}
