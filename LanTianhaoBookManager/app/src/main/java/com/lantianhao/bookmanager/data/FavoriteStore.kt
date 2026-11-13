package com.lantianhao.bookmanager.data

import android.content.Context
import android.content.SharedPreferences

/** 使用 SharedPreferences 持久化收藏的书籍 ISBN 集合。 */
class FavoriteStore(context: Context) {

    private val preferences: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getFavorites(): Set<String> =
        HashSet(preferences.getStringSet(KEY_FAVORITES, emptySet()) ?: emptySet())

    fun isFavorite(isbn: String): Boolean = getFavorites().contains(isbn)

    /** 切换收藏状态并写入本地。 */
    fun toggle(isbn: String) {
        val favorites = getFavorites().toMutableSet()
        if (!favorites.remove(isbn)) {
            favorites.add(isbn)
        }
        preferences.edit().putStringSet(KEY_FAVORITES, favorites).apply()
    }

    companion object {
        private const val PREFS_NAME = "book_favorites"
        private const val KEY_FAVORITES = "favorite_isbns"
    }
}
