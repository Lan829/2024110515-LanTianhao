package com.lantianhao.bookmanager.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import com.lantianhao.bookmanager.model.Book;

import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BookRepositoryTest {

    @Test
    public void repositoryContainsEnoughBooks() {
        Book[] books = BookRepository.getBooks();
        assertTrue(books.length >= 8);
        assertEquals(books.length, BookRepository.createBookList().size());
    }

    @Test
    public void everyBookHasCompleteInformation() {
        for (Book book : BookRepository.getBooks()) {
            assertFalse(book.getTitle().isEmpty());
            assertFalse(book.getAuthor().isEmpty());
            assertFalse(book.getPublisher().isEmpty());
            assertFalse(book.getIsbn().isEmpty());
            assertFalse(book.getCategory().isEmpty());
            assertFalse(book.getDescription().isEmpty());
            assertTrue(book.getPrice() > 0);
            assertTrue(book.getRating() >= 0 && book.getRating() <= 10);
            assertTrue(book.getPublishYear() >= 1900);
        }
    }

    @Test
    public void isbnIsUnique() {
        Set<String> isbns = new HashSet<>();
        for (Book book : BookRepository.getBooks()) {
            assertTrue("重复的 ISBN: " + book.getIsbn(), isbns.add(book.getIsbn()));
        }
    }

    @Test
    public void returnedArrayAndListAreCopies() {
        Book[] first = BookRepository.getBooks();
        Book[] second = BookRepository.getBooks();
        assertNotSame(first, second);
        first[0] = null;
        assertEquals(BookRepository.getBooks()[0].getIsbn(), second[0].getIsbn());

        List<Book> list = BookRepository.createBookList();
        list.clear();
        assertFalse(BookRepository.createBookList().isEmpty());
    }
}
