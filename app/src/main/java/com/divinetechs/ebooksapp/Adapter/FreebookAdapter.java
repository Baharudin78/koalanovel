package com.divinetechs.ebooksapp.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.divinetechs.ebooksapp.Activity.BookDetails;
import com.divinetechs.ebooksapp.Model.BookModel.Result;
import com.divinetechs.ebooksapp.R;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FreebookAdapter extends RecyclerView.Adapter<FreebookAdapter.MyViewHolder> {
    List<Result> freebookList;
    Context mcontext;
    String from;

    public FreebookAdapter(Context context, List<Result> freebookList, String from) {
        this.freebookList = freebookList;
        this.mcontext = context;
        this.from = from;
    }

    @NonNull
    @Override
    public FreebookAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        if (from.equalsIgnoreCase("Home")) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.freepaid_book_item, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.freepaid_book_item2, parent, false);
        }
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FreebookAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.txt_book_price.setVisibility(View.GONE);

        if (from.equalsIgnoreCase("Home")) {
            holder.txt_bookname.setText("" + freebookList.get(position + 2).getTitle());
            holder.txt_description.setText(Html.fromHtml(freebookList.get(position + 2).getDescription()));
            holder.txt_category.setText("" + freebookList.get(position + 2).getCategoryName());
            holder.ratingbar.setRating(Float.parseFloat(freebookList.get(position + 2).getAvgRating()));
            Picasso.get().load(freebookList.get(position + 2).getImage()).into(holder.iv_thumb);

            holder.lyBook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("click", "call");
                    Intent intent = new Intent(mcontext, BookDetails.class);
                    intent.putExtra("ID", freebookList.get(position + 2).getId());
                    mcontext.startActivity(intent);
                }
            });

        } else {
            holder.txt_bookname.setText("" + freebookList.get(position).getTitle());
            holder.txt_description.setText(Html.fromHtml(freebookList.get(position).getDescription()));
            holder.txt_category.setText("" + freebookList.get(position).getCategoryName());
            holder.ratingbar.setRating(Float.parseFloat(freebookList.get(position).getAvgRating()));
            Picasso.get().load(freebookList.get(position).getImage()).into(holder.iv_thumb);

            holder.lyBook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("click", "call");
                    Intent intent = new Intent(mcontext, BookDetails.class);
                    intent.putExtra("ID", freebookList.get(position).getId());
                    mcontext.startActivity(intent);
                }
            });
        }
    }

    public void addBook(List<Result> items) {
        freebookList.addAll(items);
        Log.e("freebookList", "" + freebookList.size());
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (from.equalsIgnoreCase("Home")) {
            return (freebookList.size()) - 2;
        } else {
            return freebookList.size();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_bookname, txt_description, txt_category, txt_book_price;
        ImageView iv_thumb;
        public SimpleRatingBar ratingbar;
        LinearLayout lyBook;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            lyBook = itemView.findViewById(R.id.lyBook);
            txt_bookname = itemView.findViewById(R.id.txt_bookname);
            txt_description = itemView.findViewById(R.id.txt_description);
            txt_category = itemView.findViewById(R.id.txt_category);
            txt_book_price = itemView.findViewById(R.id.txt_book_price);
            ratingbar = itemView.findViewById(R.id.ratingbar);
            iv_thumb = itemView.findViewById(R.id.iv_thumb);
        }
    }
}
