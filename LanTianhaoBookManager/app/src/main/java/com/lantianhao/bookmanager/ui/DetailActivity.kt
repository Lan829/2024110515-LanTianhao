package com.lantianhao.bookmanager.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.lantianhao.bookmanager.R
import com.lantianhao.bookmanager.data.FavoriteStore
import com.lantianhao.bookmanager.model.Book
import com.lantianhao.bookmanager.util.MoneyUtils

/** 第 2 个 Activity：展示被选中书籍的完整信息，并支持收藏与分享。 */
class DetailActivity : AppCompatActivity() {

    private lateinit var book: Book
    private lateinit var favoriteStore: FavoriteStore
    private lateinit var favoriteButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val selectedBook = readBookFromIntent(intent)
        if (selectedBook == null) {
            finish()
            return
        }
        book = selectedBook
        favoriteStore = FavoriteStore(this)
        bindBook(book)
    }

    @Suppress("DEPRECATION")
    private fun readBookFromIntent(intent: Intent?): Book? =
        intent?.getSerializableExtra(EXTRA_BOOK) as? Book

    private fun bindBook(selectedBook: Book) {
        val coverText = findViewById<TextView>(R.id.coverText)
        val titleText = findViewById<TextView>(R.id.titleText)
        val authorText = findViewById<TextView>(R.id.authorText)
        val priceText = findViewById<TextView>(R.id.priceText)
        val ratingText = findViewById<TextView>(R.id.ratingText)
        val ratingBar = findViewById<RatingBar>(R.id.ratingBar)
        val publisherValue = findViewById<TextView>(R.id.publisherValue)
        val isbnValue = findViewById<TextView>(R.id.isbnValue)
        val yearValue = findViewById<TextView>(R.id.yearValue)
        val categoryValue = findViewById<TextView>(R.id.categoryValue)
        val priceValue = findViewById<TextView>(R.id.priceValue)
        val descriptionText = findViewById<TextView>(R.id.descriptionText)
        favoriteButton = findViewById(R.id.favoriteButton)

        coverText.text = selectedBook.initial
        titleText.text = selectedBook.title
        authorText.text = getString(R.string.author_format, selectedBook.author)
        val price = MoneyUtils.format(selectedBook.price)
        priceText.text = price
        ratingBar.rating = selectedBook.rating / 2f
        ratingText.text = getString(R.string.rating_format, selectedBook.rating)
        publisherValue.text = selectedBook.publisher
        isbnValue.text = selectedBook.isbn
        yearValue.text = getString(R.string.year_format, selectedBook.publishYear)
        categoryValue.text = selectedBook.category
        priceValue.text = price
        descriptionText.text = selectedBook.description

        favoriteButton.setOnClickListener {
            favoriteStore.toggle(selectedBook.isbn)
            updateFavoriteButton()
        }
        findViewById<View>(R.id.shareButton).setOnClickListener {
            shareBook(selectedBook)
        }
        updateFavoriteButton()
    }

    private fun updateFavoriteButton() {
        val favorite = favoriteStore.isFavorite(book.isbn)
        favoriteButton.setText(if (favorite) R.string.favorited else R.string.favorite)
        favoriteButton.setIconResource(
            if (favorite) R.drawable.ic_star else R.drawable.ic_star_border
        )
    }

    private fun shareBook(selectedBook: Book) {
        val shareText = getString(
            R.string.share_text_format,
            selectedBook.title,
            selectedBook.author,
            selectedBook.publisher,
            MoneyUtils.format(selectedBook.price),
            selectedBook.isbn,
            selectedBook.description
        )
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, selectedBook.title)
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        startActivity(Intent.createChooser(sendIntent, getString(R.string.share_chooser)))
    }

    companion object {
        const val EXTRA_BOOK = "extra_book"
    }
}
