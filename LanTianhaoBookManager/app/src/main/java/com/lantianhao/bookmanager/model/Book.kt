package com.lantianhao.bookmanager.model

import java.io.Serializable

/**
 * 书籍实体类。
 * 包含试题要求的书名、作者、出版社、价格，并扩展 ISBN、类别、出版年份、评分与简介。
 * 实现 Serializable，便于通过 Intent 在 Activity 之间传递。
 */
data class Book(
    val title: String,
    val author: String,
    val publisher: String,
    val price: Double,
    val isbn: String,
    val category: String,
    val publishYear: Int,
    val rating: Float,
    val description: String
) : Serializable {

    /** 封面占位文字：取书名首字。 */
    val initial: String
        get() = if (title.isEmpty()) "书" else title.substring(0, 1)

    /** 以 ISBN 作为书籍的唯一标识，便于 DiffUtil 与收藏去重。 */
    override fun equals(other: Any?): Boolean =
        this === other || (other is Book && other.isbn == isbn)

    override fun hashCode(): Int = isbn.hashCode()

    companion object {
        private const val serialVersionUID: Long = 1L
    }
}
