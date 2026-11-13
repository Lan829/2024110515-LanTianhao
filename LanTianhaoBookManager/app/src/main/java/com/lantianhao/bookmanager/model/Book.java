package com.lantianhao.bookmanager.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * 书籍实体类。
 * 包含试题要求的书名、作者、出版社、价格，并扩展了 ISBN、类别、出版年份、评分与简介。
 * 实现 Serializable，便于通过 Intent 在 Activity 之间传递。
 */
public class Book implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String title;
    private final String author;
    private final String publisher;
    private final double price;
    private final String isbn;
    private final String category;
    private final int publishYear;
    private final float rating;
    private final String description;

    public Book(String title,
                String author,
                String publisher,
                double price,
                String isbn,
                String category,
                int publishYear,
                float rating,
                String description) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.price = price;
        this.isbn = isbn;
        this.category = category;
        this.publishYear = publishYear;
        this.rating = rating;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getPublisher() {
        return publisher;
    }

    public double getPrice() {
        return price;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getCategory() {
        return category;
    }

    public int getPublishYear() {
        return publishYear;
    }

    public float getRating() {
        return rating;
    }

    public String getDescription() {
        return description;
    }

    /** 封面占位文字：取书名首字。 */
    public String getInitial() {
        return title == null || title.isEmpty() ? "书" : title.substring(0, 1);
    }

    /** 以 ISBN 作为书籍的唯一标识，便于 DiffUtil 与收藏去重。 */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Book)) {
            return false;
        }
        Book book = (Book) o;
        return Objects.equals(isbn, book.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }

    @Override
    public String toString() {
        return "Book{" + title + ", " + author + ", " + price + "}";
    }
}
