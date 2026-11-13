package com.lantianhao.bookmanager.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.lantianhao.bookmanager.model.Book;

import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BookFilterTest {

    private List<Book> books() {
        return BookRepository.createBookList();
    }

    @Test
    public void blankQueryKeepsAllBooks() {
        List<Book> result = BookFilter.filter(books(), "   ", false, Collections.emptySet());
        assertEquals(books().size(), result.size());
    }

    @Test
    public void matchesTitleIgnoringCase() {
        List<Book> result = BookFilter.filter(books(), "java", false, Collections.emptySet());
        assertEquals(1, result.size());
        assertEquals("Java编程思想（第4版）", result.get(0).getTitle());
    }

    @Test
    public void matchesAuthor() {
        List<Book> result = BookFilter.filter(books(), "余华", false, Collections.emptySet());
        assertEquals(1, result.size());
        assertEquals("活着", result.get(0).getTitle());
    }

    @Test
    public void matchesPublisher() {
        List<Book> result = BookFilter.filter(books(), "中信出版社", false, Collections.emptySet());
        assertEquals(2, result.size());
    }

    @Test
    public void matchesIsbnFragment() {
        Book target = books().get(0);
        List<Book> result = BookFilter.filter(books(), target.getIsbn().substring(0, 8),
                false, Collections.emptySet());
        assertTrue(result.contains(target));
    }

    @Test
    public void favoritesOnlyKeepsSelectedIsbn() {
        Book target = books().get(2);
        Set<String> favorites = new HashSet<>();
        favorites.add(target.getIsbn());

        List<Book> result = BookFilter.filter(books(), "", true, favorites);
        assertEquals(1, result.size());
        assertEquals(target.getIsbn(), result.get(0).getIsbn());
    }

    @Test
    public void favoritesAndKeywordWorkTogether() {
        Book favorite = books().get(0);
        Set<String> favorites = new HashSet<>();
        favorites.add(favorite.getIsbn());

        assertEquals(0, BookFilter.filter(books(), "不存在的关键词", true, favorites).size());

        List<Book> result = BookFilter.filter(books(), "Java", true, favorites);
        assertEquals(1, result.size());
    }

    @Test
    public void unknownKeywordReturnsEmptyList() {
        assertTrue(BookFilter.filter(books(), "###", false, Collections.emptySet()).isEmpty());
    }

    @Test
    public void matchesBlankKeywordAlwaysTrue() {
        assertTrue(BookFilter.matches(books().get(0), ""));
        assertFalse(BookFilter.matches(books().get(0), "完全无关"));
    }
}
