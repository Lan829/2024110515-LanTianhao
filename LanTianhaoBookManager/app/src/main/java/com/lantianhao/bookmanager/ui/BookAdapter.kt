package com.lantianhao.bookmanager.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lantianhao.bookmanager.R
import com.lantianhao.bookmanager.model.Book
import com.lantianhao.bookmanager.util.MoneyUtils

/** 书籍列表适配器：负责列表项渲染、点击进入详情以及收藏按钮回调。 */
class BookAdapter(
    private val bookClickListener: (Book) -> Unit,
    private val favoriteClickListener: (Book, Int) -> Unit
) : ListAdapter<Book, BookAdapter.BookViewHolder>(DIFF_CALLBACK) {

    private var favoriteIsbns: Set<String> = emptySet()

    /** 更新收藏集合（不主动刷新，由调用方决定刷新范围）。 */
    fun setFavoriteIsbns(isbns: Set<String>?) {
        favoriteIsbns = isbns?.toSet() ?: emptySet()
    }

    /** 从详情页返回时刷新全部星标状态。 */
    fun refreshFavoriteMarks() {
        notifyItemRangeChanged(0, itemCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_book, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = getItem(position)
        holder.bind(book, favoriteIsbns.contains(book.isbn))
    }

    inner class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val coverText: TextView = itemView.findViewById(R.id.coverText)
        private val titleText: TextView = itemView.findViewById(R.id.titleText)
        private val metaText: TextView = itemView.findViewById(R.id.metaText)
        private val priceText: TextView = itemView.findViewById(R.id.priceText)
        private val categoryText: TextView = itemView.findViewById(R.id.categoryText)
        private val favoriteButton: ImageButton = itemView.findViewById(R.id.favoriteButton)

        fun bind(book: Book, favorite: Boolean) {
            val context = itemView.context
            coverText.text = book.initial
            titleText.text = book.title
            metaText.text = context.getString(
                R.string.book_meta_format, book.author, book.publisher
            )
            priceText.text = MoneyUtils.format(book.price)
            categoryText.text = book.category
            favoriteButton.setImageResource(
                if (favorite) R.drawable.ic_star else R.drawable.ic_star_border
            )
            favoriteButton.contentDescription = context.getString(
                if (favorite) R.string.cd_unfavorite else R.string.cd_favorite,
                book.title
            )

            itemView.setOnClickListener { bookClickListener(book) }
            favoriteButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    favoriteClickListener(book, position)
                }
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Book>() {
            override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean =
                oldItem.isbn == newItem.isbn

            override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean =
                oldItem == newItem
        }
    }
}
