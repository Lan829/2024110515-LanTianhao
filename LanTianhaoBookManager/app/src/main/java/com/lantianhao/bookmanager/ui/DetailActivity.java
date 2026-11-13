package com.lantianhao.bookmanager.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.lantianhao.bookmanager.R;
import com.lantianhao.bookmanager.data.FavoriteStore;
import com.lantianhao.bookmanager.model.Book;
import com.lantianhao.bookmanager.util.MoneyUtils;

import java.io.Serializable;

/** 第 2 个 Activity：展示被选中书籍的完整信息，并支持收藏与分享。 */
public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_BOOK = "extra_book";

    private Book book;
    private FavoriteStore favoriteStore;
    private MaterialButton favoriteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        book = readBookFromIntent(getIntent());
        if (book == null) {
            finish();
            return;
        }

        favoriteStore = new FavoriteStore(this);
        bindBook(book);
    }

    @SuppressWarnings("deprecation")
    private Book readBookFromIntent(Intent intent) {
        Serializable extra = intent == null ? null : intent.getSerializableExtra(EXTRA_BOOK);
        return extra instanceof Book ? (Book) extra : null;
    }

    private void bindBook(Book selectedBook) {
        TextView coverText = findViewById(R.id.coverText);
        TextView titleText = findViewById(R.id.titleText);
        TextView authorText = findViewById(R.id.authorText);
        TextView priceText = findViewById(R.id.priceText);
        TextView ratingText = findViewById(R.id.ratingText);
        RatingBar ratingBar = findViewById(R.id.ratingBar);
        TextView publisherValue = findViewById(R.id.publisherValue);
        TextView isbnValue = findViewById(R.id.isbnValue);
        TextView yearValue = findViewById(R.id.yearValue);
        TextView categoryValue = findViewById(R.id.categoryValue);
        TextView priceValue = findViewById(R.id.priceValue);
        TextView descriptionText = findViewById(R.id.descriptionText);
        favoriteButton = findViewById(R.id.favoriteButton);

        coverText.setText(selectedBook.getInitial());
        titleText.setText(selectedBook.getTitle());
        authorText.setText(getString(R.string.author_format, selectedBook.getAuthor()));
        String price = MoneyUtils.format(selectedBook.getPrice());
        priceText.setText(price);
        ratingBar.setRating(selectedBook.getRating() / 2f);
        ratingText.setText(getString(R.string.rating_format, selectedBook.getRating()));
        publisherValue.setText(selectedBook.getPublisher());
        isbnValue.setText(selectedBook.getIsbn());
        yearValue.setText(getString(R.string.year_format, selectedBook.getPublishYear()));
        categoryValue.setText(selectedBook.getCategory());
        priceValue.setText(price);
        descriptionText.setText(selectedBook.getDescription());

        favoriteButton.setOnClickListener(v -> {
            favoriteStore.toggle(selectedBook.getIsbn());
            updateFavoriteButton();
        });
        findViewById(R.id.shareButton).setOnClickListener(v -> shareBook(selectedBook));
        updateFavoriteButton();
    }

    private void updateFavoriteButton() {
        boolean favorite = favoriteStore.isFavorite(book.getIsbn());
        favoriteButton.setText(favorite ? R.string.favorited : R.string.favorite);
        favoriteButton.setIconResource(favorite ? R.drawable.ic_star : R.drawable.ic_star_border);
    }

    private void shareBook(Book selectedBook) {
        String shareText = getString(R.string.share_text_format,
                selectedBook.getTitle(),
                selectedBook.getAuthor(),
                selectedBook.getPublisher(),
                MoneyUtils.format(selectedBook.getPrice()),
                selectedBook.getIsbn(),
                selectedBook.getDescription());
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, selectedBook.getTitle());
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(sendIntent, getString(R.string.share_chooser)));
    }
}
