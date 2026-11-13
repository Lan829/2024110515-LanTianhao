package com.lantianhao.bookmanager.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class BookTest {

    private Book sampleBook() {
        return new Book("测试之书", "测试作者", "测试出版社", 42.50,
                "9780000000001", "计算机", 2024, 8.8f, "这是一段测试简介。");
    }

    @Test
    public void exposesAllFields() {
        Book book = sampleBook();
        assertEquals("测试之书", book.getTitle());
        assertEquals("测试作者", book.getAuthor());
        assertEquals("测试出版社", book.getPublisher());
        assertEquals(42.50, book.getPrice(), 0.0001);
        assertEquals("9780000000001", book.getIsbn());
        assertEquals("计算机", book.getCategory());
        assertEquals(2024, book.getPublishYear());
        assertEquals(8.8f, book.getRating(), 0.0001f);
        assertEquals("这是一段测试简介。", book.getDescription());
    }

    @Test
    public void initialIsFirstCharacterOfTitle() {
        assertEquals("测", sampleBook().getInitial());
        assertEquals("书", new Book("", "作者", "出版社", 1,
                "isbn", "类别", 2020, 5f, "").getInitial());
    }

    @Test
    public void equalityUsesIsbn() {
        Book sameIsbn = new Book("另一个书名", "另一个作者", "另一个出版社", 99,
                "9780000000001", "文学", 2020, 9f, "另一段简介");
        assertEquals(sampleBook(), sameIsbn);
        assertEquals(sampleBook().hashCode(), sameIsbn.hashCode());

        Book otherIsbn = new Book("测试之书", "测试作者", "测试出版社", 42.50,
                "9780000000002", "计算机", 2024, 8.8f, "这是一段测试简介。");
        assertNotEquals(sampleBook(), otherIsbn);
    }

    @Test
    public void canBeSerializedForIntent() throws Exception {
        Book original = sampleBook();
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream output = new ObjectOutputStream(bytes)) {
            output.writeObject(original);
        }

        Book restored;
        try (ObjectInputStream input =
                     new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            restored = (Book) input.readObject();
        }

        assertTrue(restored instanceof Book);
        assertEquals(original.getIsbn(), restored.getIsbn());
        assertEquals(original.getTitle(), restored.getTitle());
        assertEquals(original.getPrice(), restored.getPrice(), 0.0001);
    }
}
