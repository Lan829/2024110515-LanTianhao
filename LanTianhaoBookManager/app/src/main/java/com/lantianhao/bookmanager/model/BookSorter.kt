package com.lantianhao.bookmanager.model

import java.util.Comparator

/** 书籍排序工具：价格升序、价格降序、书名升序，价格相同时按书名稳定排序。 */
object BookSorter {

    @JvmStatic
    fun sort(books: MutableList<Book>, option: SortOption) {
        books.sortWith(comparatorFor(option))
    }

    @JvmStatic
    fun comparatorFor(option: SortOption): Comparator<Book> {
        val byTitle = Comparator<Book> { first, second ->
            String.CASE_INSENSITIVE_ORDER.compare(first.title, second.title)
        }
        val byPrice = Comparator<Book> { first, second ->
            first.price.compareTo(second.price)
        }
        return when (option) {
            SortOption.PRICE_DESC -> byPrice.reversed().thenComparing(byTitle)
            SortOption.TITLE -> byTitle
            SortOption.PRICE_ASC -> byPrice.thenComparing(byTitle)
        }
    }
}
