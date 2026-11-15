package com.lantianhao.multifragment.fragment;

import com.lantianhao.multifragment.R;
import com.lantianhao.multifragment.data.ObjectStore;

/**
 * 2 张牌翻牌游戏 Fragment：每次选 2 张牌判定配对。
 */
public class TwoCardGameFragment extends BaseGameFragment {

    @Override
    protected int cardsPerMatch() {
        return 2;
    }

    @Override
    protected int titleRes() {
        return R.string.title_two_game;
    }

    @Override
    protected String gameFile() {
        return ObjectStore.GAME_TWO_FILE;
    }
}
