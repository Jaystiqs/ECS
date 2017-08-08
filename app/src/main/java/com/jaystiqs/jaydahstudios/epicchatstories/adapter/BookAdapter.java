package com.jaystiqs.jaydahstudios.epicchatstories.adapter;

/**
 * Created by Jaystiqs on 8/7/2017.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaystiqs.jaydahstudios.epicchatstories.R;
import com.jaystiqs.jaydahstudios.epicchatstories.model.Book;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {

    private List<Book> bookList;

    //Provide a reference to the views for each data item
    //Complex data items may need more than one view per item, and
    //you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder{
        //each data item is just a string in this case
        public TextView tvRank,tvBookName,tvDescription,tvWriter;
        public ImageView ivAlbumCover;

        public ViewHolder(View v) {
            super(v);
            tvRank = (TextView)v.findViewById(R.id.tv_rank);
            tvBookName = (TextView) v.findViewById(R.id.tv_song_name);
            tvDescription = (TextView) v.findViewById(R.id.tv_singer);
            tvWriter = (TextView) v.findViewById(R.id.tv_year);
            ivAlbumCover = (ImageView) v.findViewById(R.id.iv_album_cover);

        }
    }

    //Provide a suitable constructor
    public BookAdapter(List<Book> bookList){
        this.bookList = bookList;
    }

    //Create new views (invoked by the layout manager)
    @Override
    public BookAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //Creating a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_list_item,parent,false);

        //set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    //Replace the contents of a view (invoked by the layout manager
    @Override
    public void onBindViewHolder(BookAdapter.ViewHolder holder, int position) {

        // - get element from arraylist at this position
        // - replace the contents of the view with that element

        Book book = bookList.get(position);
        holder.tvRank.setText(String.valueOf(book.getRank()));
        holder.tvBookName.setText(book.getName());
        holder.tvDescription.setText(book.getDescription());
        holder.tvWriter.setText(book.getWriter());
        holder.ivAlbumCover.setImageResource(book.getPic());
        holder.tvWriter.setText("ECS Team.");
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }
}