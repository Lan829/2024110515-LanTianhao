package com.lantianhao.bookmanager.data;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/** 使用 SharedPreferences 持久化收藏的书籍 ISBN 集合。 */
public class FavoriteStore {

    private static final String PREFS_NAME = "book_favorites";
    private static final String KEY_FAVORITES = "favorite_isbns";

    private final SharedPreferences preferences;

    public FavoriteStore(Context context) {
        preferences = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public Set<String> getFavorites() {
        return new HashSet<>(preferences.getStringSet(KEY_FAVORITES, Collections.emptySet()));
    }

    public boolean isFavorite(String isbn) {
        return getFavorites().contains(isbn);
    }

    /** 切换收藏状态并写入本地。 */
    public void toggle(String isbn) {
        Set<String> favorites = getFavorites();
        if (!favorites.remove(isbn)) {
            favorites.add(isbn);
        }
        preferences.edit().putStringSet(KEY_FAVORITES, favorites).apply();
    }
}
