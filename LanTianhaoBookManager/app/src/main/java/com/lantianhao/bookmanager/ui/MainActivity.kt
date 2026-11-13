package com.lantianhao.bookmanager.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.lantianhao.bookmanager.R
import com.lantianhao.bookmanager.data.BookFilter
import com.lantianhao.bookmanager.data.BookRepository
import com.lantianhao.bookmanager.data.FavoriteStore
import com.lantianhao.bookmanager.model.Book
import com.lantianhao.bookmanager.model.BookSorter
import com.lantianhao.bookmanager.model.SortOption
import com.lantianhao.bookmanager.util.MoneyUtils

/**
 * 第 1 个 Activity：书籍列表。
 * 数据来自代码中的 Book 数组，默认按价格从低到高排序；点击条目进入详情页。
 * 扩展了搜索、三种排序、收藏、只看收藏与统计信息。
 */
class MainActivity : AppCompatActivity() {

    private val allBooks: MutableList<Book> = mutableListOf()
    private lateinit var adapter: BookAdapter
    private lateinit var favoriteStore: FavoriteStore
    private lateinit var recyclerView: RecyclerView
    private lateinit var summaryText: TextView
    private lateinit var sortText: TextView
    private lateinit var emptyState: View
    private lateinit var searchInput: TextInputEditText
    private var sortOption = SortOption.PRICE_ASC
    private var favoritesOnly = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(R.string.main_title)

        favoriteStore = FavoriteStore(this)
        allBooks.addAll(BookRepository.createBookList())
        // 试题要求：对数组中的书籍按价格从低到高排序
        BookSorter.sort(allBooks, SortOption.PRICE_ASC)

        recyclerView = findViewById(R.id.recyclerView)
        summaryText = findViewById(R.id.summaryText)
        sortText = findViewById(R.id.sortText)
        emptyState = findViewById(R.id.emptyState)
        searchInput = findViewById(R.id.searchInput)

        adapter = BookAdapter(::openDetail, ::toggleFavorite)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        if (savedInstanceState != null) {
            sortOption = parseSortOption(savedInstanceState.getString(STATE_SORT))
            favoritesOnly = savedInstanceState.getBoolean(STATE_FAVORITES_ONLY)
            searchInput.setText(savedInstanceState.getString(STATE_QUERY, ""))
        }

        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                applyFilters()
            }
        })

        adapter.setFavoriteIsbns(favoriteStore.getFavorites())
        applyFilters()
    }

    override fun onResume() {
        super.onResume()
        if (!::adapter.isInitialized) {
            return
        }
        // 详情页可能修改了收藏状态，返回后同步星标
        adapter.setFavoriteIsbns(favoriteStore.getFavorites())
        adapter.refreshFavoriteMarks()
        if (favoritesOnly) {
            applyFilters()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(STATE_QUERY, currentQuery())
        outState.putString(STATE_SORT, sortOption.name)
        outState.putBoolean(STATE_FAVORITES_ONLY, favoritesOnly)
        super.onSaveInstanceState(outState)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.findItem(R.id.action_sort)?.subMenu
            ?.findItem(menuIdFor(sortOption))
            ?.isChecked = true

        menu.findItem(R.id.action_favorites)?.apply {
            isChecked = favoritesOnly
            setIcon(if (favoritesOnly) R.drawable.ic_star else R.drawable.ic_star_border)
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sort_price_asc -> {
                changeSortOption(SortOption.PRICE_ASC)
                return true
            }
            R.id.sort_price_desc -> {
                changeSortOption(SortOption.PRICE_DESC)
                return true
            }
            R.id.sort_title -> {
                changeSortOption(SortOption.TITLE)
                return true
            }
            R.id.action_favorites -> {
                favoritesOnly = !favoritesOnly
                item.isChecked = favoritesOnly
                item.setIcon(if (favoritesOnly) R.drawable.ic_star else R.drawable.ic_star_border)
                applyFilters()
                return true
            }
            R.id.action_about -> {
                showAboutDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openDetail(book: Book) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_BOOK, book)
        startActivity(intent)
    }

    private fun toggleFavorite(book: Book, position: Int) {
        favoriteStore.toggle(book.isbn)
        adapter.setFavoriteIsbns(favoriteStore.getFavorites())
        adapter.notifyItemChanged(position)
        if (favoritesOnly) {
            applyFilters()
        }
    }

    private fun changeSortOption(option: SortOption) {
        sortOption = option
        applyFilters()
        invalidateOptionsMenu()
    }

    private fun applyFilters() {
        if (!::adapter.isInitialized) {
            return
        }
        val visibleBooks = BookFilter.filter(
            allBooks, currentQuery(), favoritesOnly, favoriteStore.getFavorites()
        ).sortedWith(BookSorter.comparatorFor(sortOption))
        adapter.submitList(visibleBooks)

        var totalPrice = 0.0
        visibleBooks.forEach { totalPrice += it.price }
        val averagePrice = if (visibleBooks.isEmpty()) 0.0 else totalPrice / visibleBooks.size
        summaryText.text = getString(
            R.string.summary_format,
            visibleBooks.size,
            MoneyUtils.format(totalPrice),
            MoneyUtils.format(averagePrice)
        )

        val sortLabel = getString(sortLabelRes(sortOption))
        sortText.text = if (favoritesOnly) {
            getString(R.string.sort_with_favorites, sortLabel)
        } else {
            sortLabel
        }

        val isEmpty = visibleBooks.isEmpty()
        emptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
        recyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    private fun currentQuery(): String = searchInput.text?.toString().orEmpty()

    private fun sortLabelRes(option: SortOption): Int = when (option) {
        SortOption.PRICE_DESC -> R.string.sort_label_price_desc
        SortOption.TITLE -> R.string.sort_label_title
        SortOption.PRICE_ASC -> R.string.sort_label_price_asc
    }

    private fun menuIdFor(option: SortOption): Int = when (option) {
        SortOption.PRICE_DESC -> R.id.sort_price_desc
        SortOption.TITLE -> R.id.sort_title
        SortOption.PRICE_ASC -> R.id.sort_price_asc
    }

    private fun parseSortOption(name: String?): SortOption {
        if (name == null) return SortOption.PRICE_ASC
        return try {
            SortOption.valueOf(name)
        } catch (ignored: IllegalArgumentException) {
            SortOption.PRICE_ASC
        }
    }

    private fun showAboutDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.about_title)
            .setMessage(R.string.about_message)
            .setPositiveButton(R.string.ok, null)
            .show()
    }

    companion object {
        private const val STATE_QUERY = "state_query"
        private const val STATE_SORT = "state_sort"
        private const val STATE_FAVORITES_ONLY = "state_favorites_only"
    }
}
