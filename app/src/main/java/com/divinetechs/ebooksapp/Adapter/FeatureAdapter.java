package com.divinetechs.ebooksapp.Adapter;

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

import androidx.recyclerview.widget.RecyclerView;

import com.divinetechs.ebooksapp.Activity.BookDetails;
import com.divinetechs.ebooksapp.Model.BookModel.Result;
import com.divinetechs.ebooksapp.R;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FeatureAdapter extends RecyclerView.Adapter<FeatureAdapter.MyViewHolder> {

    private List<Result> NewArrivalList;
    Context mcontext;
    String from, currency_symbol;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_bookname, txt_view, txt_book_price, txt_description, txt_category;
        ImageView iv_thumb;
        SimpleRatingBar simpleRatingBar;
        LinearLayout lyBook;

        public MyViewHolder(View view) {
            super(view);
            lyBook = view.findViewById(R.id.lyBook);
            txt_bookname = view.findViewById(R.id.txt_bookname);
            iv_thumb = view.findViewById(R.id.iv_thumb);
            txt_view = view.findViewById(R.id.txt_view);
            txt_book_price = view.findViewById(R.id.txt_book_price);
            txt_description = view.findViewById(R.id.txt_description);
            txt_category = view.findViewById(R.id.txt_category);
            simpleRatingBar = view.findViewById(R.id.ratingbar);
        }
    }


    public FeatureAdapter(Context context, List<Result> NewArrivalList, String from, String currency_symbol) {
        this.NewArrivalList = NewArrivalList;
        this.mcontext = context;
        this.from = from;
        this.currency_symbol = currency_symbol;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (from.equalsIgnoreCase("Home")) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.feature_item, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.feature_item2, parent, false);
        }
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        holder.txt_view.setText("" + NewArrivalList.get(position).getReadcnt());
        holder.txt_bookname.setText("" + NewArrivalList.get(position).getTitle());

        if (!from.equalsIgnoreCase("Home")) {
            holder.txt_description.setText(Html.fromHtml(NewArrivalList.get(position).getDescription()));
            holder.txt_category.setText("" + NewArrivalList.get(position).getCategoryName());
        }

        if (NewArrivalList.get(position).getIsPaid().equalsIgnoreCase("1")) {
            holder.txt_book_price.setText(currency_symbol + " " + NewArrivalList.get(position).getPrice());
        } else {
            holder.txt_book_price.setText("" + mcontext.getResources().getString(R.string.free));
        }

        Picasso.get().load(NewArrivalList.get(position).getImage()).into(holder.iv_thumb);

        holder.simpleRatingBar.setRating(Float.parseFloat(NewArrivalList.get(position).getAvgRating()));

        holder.lyBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("click", "call");
                Intent intent = new Intent(mcontext, BookDetails.class);
                intent.putExtra("ID", NewArrivalList.get(position).getId());
                mcontext.startActivity(intent);
            }
        });
    }

    public void addBook(List<Result> items) {
        NewArrivalList.addAll(items);
        Log.e("NewArrivalList", "" + NewArrivalList.size());
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return NewArrivalList.size();
    }

}
