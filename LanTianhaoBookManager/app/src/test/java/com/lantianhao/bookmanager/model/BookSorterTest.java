package com.lantianhao.bookmanager.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BookSorterTest {

    private Book book(String title, String isbn, double price) {
        return new Book(title, "作者", "出版社", price, isbn,
                "类别", 2020, 8f, "简介");
    }

    private List<Book> sampleBooks() {
        return new ArrayList<>(Arrays.asList(
                book("Gamma", "isbn-3", 30.0),
                book("alpha", "isbn-1", 10.0),
                book("Beta", "isbn-2", 20.0)));
    }

    private List<String> titles(List<Book> books) {
        List<String> result = new ArrayList<>();
        for (Book book : books) {
            result.add(book.getTitle());
        }
        return result;
    }

    @Test
    public void sortsByPriceAscendingByDefaultRequirement() {
        List<Book> books = sampleBooks();
        BookSorter.sort(books, SortOption.PRICE_ASC);
        assertEquals(Arrays.asList("alpha", "Beta", "Gamma"), titles(books));
    }

    @Test
    public void sortsByPriceDescending() {
        List<Book> books = sampleBooks();
        BookSorter.sort(books, SortOption.PRICE_DESC);
        assertEquals(Arrays.asList("Gamma", "Beta", "alpha"), titles(books));
    }

    @Test
    public void sortsByTitleIgnoringCase() {
        List<Book> books = sampleBooks();
        BookSorter.sort(books, SortOption.TITLE);
        assertEquals(Arrays.asList("alpha", "Beta", "Gamma"), titles(books));
    }

    @Test
    public void equalPricesFallBackToTitleOrder() {
        List<Book> books = new ArrayList<>(Arrays.asList(
                book("Zebra", "isbn-a", 12.0),
                book("Apple", "isbn-b", 12.0)));
        BookSorter.sort(books, SortOption.PRICE_ASC);
        assertEquals(Arrays.asList("Apple", "Zebra"), titles(books));
    }

    @Test
    public void comparatorIsAvailableForEveryOption() {
        for (SortOption option : SortOption.values()) {
            assertNotNull(BookSorter.comparatorFor(option));
        }
    }
}
