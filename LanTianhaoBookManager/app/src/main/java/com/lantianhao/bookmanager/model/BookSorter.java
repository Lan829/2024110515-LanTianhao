package com.lantianhao.bookmanager.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/** 书籍排序工具：价格升序、价格降序、书名升序，价格相同时按书名稳定排序。 */
public final class BookSorter {

    private BookSorter() {
    }

    public static void sort(List<Book> books, SortOption option) {
        Collections.sort(books, comparatorFor(option));
    }

    public static Comparator<Book> comparatorFor(SortOption option) {
        Comparator<Book> byTitle = Comparator.comparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER);
        switch (option) {
            case PRICE_DESC:
                return Comparator.comparingDouble(Book::getPrice).reversed().thenComparing(byTitle);
            case TITLE:
                return byTitle;
            case PRICE_ASC:
            default:
                return Comparator.comparingDouble(Book::getPrice).thenComparing(byTitle);
        }
    }
}
