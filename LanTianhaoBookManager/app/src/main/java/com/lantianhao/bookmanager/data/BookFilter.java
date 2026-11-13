package com.lantianhao.bookmanager.data;

import com.lantianhao.bookmanager.model.Book;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/** 书籍筛选工具：支持关键词搜索与“只看收藏”过滤，便于单元测试。 */
public final class BookFilter {

    private BookFilter() {
    }

    public static List<Book> filter(List<Book> books,
                                    String query,
                                    boolean favoritesOnly,
                                    Set<String> favoriteIsbns) {
        String keyword = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        List<Book> result = new ArrayList<>();
        for (Book book : books) {
            if (favoritesOnly && (favoriteIsbns == null
                    || !favoriteIsbns.contains(book.getIsbn()))) {
                continue;
            }
            if (!keyword.isEmpty() && !matches(book, keyword)) {
                continue;
            }
            result.add(book);
        }
        return result;
    }

    /** 书名、作者、出版社、ISBN、类别任一命中即视为匹配。 */
    public static boolean matches(Book book, String keyword) {
        String key = keyword == null ? "" : keyword.trim().toLowerCase(Locale.ROOT);
        if (key.isEmpty()) {
            return true;
        }
        return containsIgnoreCase(book.getTitle(), key)
                || containsIgnoreCase(book.getAuthor(), key)
                || containsIgnoreCase(book.getPublisher(), key)
                || containsIgnoreCase(book.getIsbn(), key)
                || containsIgnoreCase(book.getCategory(), key);
    }

    private static boolean containsIgnoreCase(String value, String keyword) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(keyword);
    }
}
