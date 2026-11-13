package com.lantianhao.bookmanager.data

import com.lantianhao.bookmanager.model.Book
import java.util.Locale

/** 书籍筛选工具：支持关键词搜索与“只看收藏”过滤，便于单元测试。 */
object BookFilter {

    @JvmStatic
    fun filter(
        books: List<Book>,
        query: String?,
        favoritesOnly: Boolean,
        favoriteIsbns: Set<String>?
    ): List<Book> {
        val keyword = query?.trim()?.lowercase(Locale.ROOT).orEmpty()
        return books.filter { book ->
            (!favoritesOnly || favoriteIsbns?.contains(book.isbn) == true) &&
                (keyword.isEmpty() || matches(book, keyword))
        }
    }

    /** 书名、作者、出版社、ISBN、类别任一命中即视为匹配。 */
    @JvmStatic
    fun matches(book: Book, keyword: String?): Boolean {
        val key = keyword?.trim()?.lowercase(Locale.ROOT).orEmpty()
        if (key.isEmpty()) return true
        return listOf(book.title, book.author, book.publisher, book.isbn, book.category)
            .any { it.lowercase(Locale.ROOT).contains(key) }
    }
}
