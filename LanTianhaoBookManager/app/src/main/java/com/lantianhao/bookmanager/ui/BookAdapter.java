package com.lantianhao.bookmanager.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.lantianhao.bookmanager.R;
import com.lantianhao.bookmanager.model.Book;
import com.lantianhao.bookmanager.util.MoneyUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/** 书籍列表适配器：负责列表项渲染、点击进入详情以及收藏按钮回调。 */
public class BookAdapter extends ListAdapter<Book, BookAdapter.BookViewHolder> {

    public interface OnBookClickListener {
        void onBookClick(Book book);
    }

    public interface OnFavoriteClickListener {
        void onFavoriteClick(Book book, int position);
    }

    private static final DiffUtil.ItemCallback<Book> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Book>() {
                @Override
                public boolean areItemsTheSame(@NonNull Book oldItem, @NonNull Book newItem) {
                    return Objects.equals(oldItem.getIsbn(), newItem.getIsbn());
                }

                @Override
                public boolean areContentsTheSame(@NonNull Book oldItem, @NonNull Book newItem) {
                    return oldItem.equals(newItem);
                }
            };

    private final OnBookClickListener bookClickListener;
    private final OnFavoriteClickListener favoriteClickListener;
    private Set<String> favoriteIsbns = Collections.emptySet();

    public BookAdapter(OnBookClickListener bookClickListener,
                       OnFavoriteClickListener favoriteClickListener) {
        super(DIFF_CALLBACK);
        this.bookClickListener = bookClickListener;
        this.favoriteClickListener = favoriteClickListener;
    }

    /** 更新收藏集合（不主动刷新，由调用方决定刷新范围）。 */
    public void setFavoriteIsbns(Set<String> isbns) {
        favoriteIsbns = isbns == null ? Collections.emptySet() : new HashSet<>(isbns);
    }

    /** 从详情页返回时刷新全部星标状态。 */
    public void refreshFavoriteMarks() {
        notifyItemRangeChanged(0, getItemCount());
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        holder.bind(getItem(position), favoriteIsbns.contains(getItem(position).getIsbn()));
    }

    final class BookViewHolder extends RecyclerView.ViewHolder {

        private final TextView coverText;
        private final TextView titleText;
        private final TextView metaText;
        private final TextView priceText;
        private final TextView categoryText;
        private final ImageButton favoriteButton;

        BookViewHolder(@NonNull View itemView) {
            super(itemView);
            coverText = itemView.findViewById(R.id.coverText);
            titleText = itemView.findViewById(R.id.titleText);
            metaText = itemView.findViewById(R.id.metaText);
            priceText = itemView.findViewById(R.id.priceText);
            categoryText = itemView.findViewById(R.id.categoryText);
            favoriteButton = itemView.findViewById(R.id.favoriteButton);
        }

        void bind(final Book book, boolean favorite) {
            coverText.setText(book.getInitial());
            titleText.setText(book.getTitle());
            metaText.setText(itemView.getContext().getString(
                    R.string.book_meta_format, book.getAuthor(), book.getPublisher()));
            priceText.setText(MoneyUtils.format(book.getPrice()));
            categoryText.setText(book.getCategory());
            favoriteButton.setImageResource(
                    favorite ? R.drawable.ic_star : R.drawable.ic_star_border);
            favoriteButton.setContentDescription(itemView.getContext().getString(
                    favorite ? R.string.cd_unfavorite : R.string.cd_favorite, book.getTitle()));

            itemView.setOnClickListener(v -> bookClickListener.onBookClick(book));
            favoriteButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    favoriteClickListener.onFavoriteClick(book, position);
                }
            });
        }
    }
}
