package com.lantianhao.bookmanager.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.lantianhao.bookmanager.R;
import com.lantianhao.bookmanager.data.BookFilter;
import com.lantianhao.bookmanager.data.BookRepository;
import com.lantianhao.bookmanager.data.FavoriteStore;
import com.lantianhao.bookmanager.model.Book;
import com.lantianhao.bookmanager.model.BookSorter;
import com.lantianhao.bookmanager.model.SortOption;
import com.lantianhao.bookmanager.util.MoneyUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 第 1 个 Activity：书籍列表。
 * 数据来自代码中的 Book 数组，默认按价格从低到高排序；点击条目进入详情页。
 * 扩展了搜索、三种排序、收藏、只看收藏与统计信息。
 */
public class MainActivity extends AppCompatActivity {

    private static final String STATE_QUERY = "state_query";
    private static final String STATE_SORT = "state_sort";
    private static final String STATE_FAVORITES_ONLY = "state_favorites_only";

    private final List<Book> allBooks = new ArrayList<>();
    private BookAdapter adapter;
    private FavoriteStore favoriteStore;
    private RecyclerView recyclerView;
    private TextView summaryText;
    private TextView sortText;
    private View emptyState;
    private TextInputEditText searchInput;
    private SortOption sortOption = SortOption.PRICE_ASC;
    private boolean favoritesOnly = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.main_title);
        }

        favoriteStore = new FavoriteStore(this);
        allBooks.addAll(BookRepository.createBookList());
        // 试题要求：对数组中的书籍按价格从低到高排序
        BookSorter.sort(allBooks, SortOption.PRICE_ASC);

        recyclerView = findViewById(R.id.recyclerView);
        summaryText = findViewById(R.id.summaryText);
        sortText = findViewById(R.id.sortText);
        emptyState = findViewById(R.id.emptyState);
        searchInput = findViewById(R.id.searchInput);

        adapter = new BookAdapter(this::openDetail, this::toggleFavorite);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        if (savedInstanceState != null) {
            sortOption = parseSortOption(savedInstanceState.getString(STATE_SORT));
            favoritesOnly = savedInstanceState.getBoolean(STATE_FAVORITES_ONLY);
            searchInput.setText(savedInstanceState.getString(STATE_QUERY, ""));
        }

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                applyFilters();
            }
        });

        adapter.setFavoriteIsbns(favoriteStore.getFavorites());
        applyFilters();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter == null) {
            return;
        }
        // 详情页可能修改了收藏状态，返回后同步星标
        adapter.setFavoriteIsbns(favoriteStore.getFavorites());
        adapter.refreshFavoriteMarks();
        if (favoritesOnly) {
            applyFilters();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_QUERY, currentQuery());
        outState.putString(STATE_SORT, sortOption.name());
        outState.putBoolean(STATE_FAVORITES_ONLY, favoritesOnly);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem sortItem = menu.findItem(R.id.action_sort);
        if (sortItem != null && sortItem.hasSubMenu()) {
            MenuItem checkedItem = sortItem.getSubMenu().findItem(menuIdFor(sortOption));
            if (checkedItem != null) {
                checkedItem.setChecked(true);
            }
        }

        MenuItem favoriteItem = menu.findItem(R.id.action_favorites);
        if (favoriteItem != null) {
            favoriteItem.setChecked(favoritesOnly);
            favoriteItem.setIcon(favoritesOnly ? R.drawable.ic_star : R.drawable.ic_star_border);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.sort_price_asc) {
            changeSortOption(SortOption.PRICE_ASC);
            return true;
        }
        if (itemId == R.id.sort_price_desc) {
            changeSortOption(SortOption.PRICE_DESC);
            return true;
        }
        if (itemId == R.id.sort_title) {
            changeSortOption(SortOption.TITLE);
            return true;
        }
        if (itemId == R.id.action_favorites) {
            favoritesOnly = !favoritesOnly;
            item.setChecked(favoritesOnly);
            item.setIcon(favoritesOnly ? R.drawable.ic_star : R.drawable.ic_star_border);
            applyFilters();
            return true;
        }
        if (itemId == R.id.action_about) {
            showAboutDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openDetail(Book book) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_BOOK, book);
        startActivity(intent);
    }

    private void toggleFavorite(Book book, int position) {
        favoriteStore.toggle(book.getIsbn());
        adapter.setFavoriteIsbns(favoriteStore.getFavorites());
        adapter.notifyItemChanged(position);
        if (favoritesOnly) {
            applyFilters();
        }
    }

    private void changeSortOption(SortOption option) {
        sortOption = option;
        applyFilters();
        invalidateOptionsMenu();
    }

    private void applyFilters() {
        if (adapter == null) {
            return;
        }
        List<Book> visibleBooks = BookFilter.filter(
                allBooks, currentQuery(), favoritesOnly, favoriteStore.getFavorites());
        BookSorter.sort(visibleBooks, sortOption);
        adapter.submitList(visibleBooks);

        double totalPrice = 0;
        for (Book book : visibleBooks) {
            totalPrice += book.getPrice();
        }
        double averagePrice = visibleBooks.isEmpty() ? 0 : totalPrice / visibleBooks.size();
        summaryText.setText(getString(R.string.summary_format,
                visibleBooks.size(), MoneyUtils.format(totalPrice), MoneyUtils.format(averagePrice)));

        String sortLabel = getString(sortLabelRes(sortOption));
        sortText.setText(favoritesOnly
                ? getString(R.string.sort_with_favorites, sortLabel)
                : sortLabel);

        boolean isEmpty = visibleBooks.isEmpty();
        emptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    private String currentQuery() {
        Editable text = searchInput.getText();
        return text == null ? "" : text.toString();
    }

    private int sortLabelRes(SortOption option) {
        switch (option) {
            case PRICE_DESC:
                return R.string.sort_label_price_desc;
            case TITLE:
                return R.string.sort_label_title;
            case PRICE_ASC:
            default:
                return R.string.sort_label_price_asc;
        }
    }

    private int menuIdFor(SortOption option) {
        switch (option) {
            case PRICE_DESC:
                return R.id.sort_price_desc;
            case TITLE:
                return R.id.sort_title;
            case PRICE_ASC:
            default:
                return R.id.sort_price_asc;
        }
    }

    private SortOption parseSortOption(String name) {
        if (name == null) {
            return SortOption.PRICE_ASC;
        }
        try {
            return SortOption.valueOf(name);
        } catch (IllegalArgumentException ignored) {
            return SortOption.PRICE_ASC;
        }
    }

    private void showAboutDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.about_title)
                .setMessage(R.string.about_message)
                .setPositiveButton(R.string.ok, null)
                .show();
    }
}
