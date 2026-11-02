package com.lantianhao.cardgame.data;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 最高分的本地存储。个人信息按实验要求无需持久化，最高分则写入
 * SharedPreferences，重新打开应用后主界面仍能显示。
 */
public final class HighScoreStore {

    private static final String PREFS_NAME = "card_game_prefs";
    private static final String KEY_HIGH_SCORE = "high_score";

    private HighScoreStore() {
    }

    public static int getHighScore(Context context) {
        return preferences(context).getInt(KEY_HIGH_SCORE, 0);
    }

    /**
     * 当局得分高于历史最高分时保存并返回 true。
     */
    public static boolean saveIfHigher(Context context, int score) {
        if (score <= getHighScore(context)) {
            return false;
        }
        preferences(context).edit().putInt(KEY_HIGH_SCORE, score).apply();
        return true;
    }

    private static SharedPreferences preferences(Context context) {
        return context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
}
