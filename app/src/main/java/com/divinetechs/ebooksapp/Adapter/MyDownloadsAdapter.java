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

import androidx.recyclerview.widget.RecyclerView;

import com.divinetechs.ebooksapp.Activity.BookDetails;
import com.divinetechs.ebooksapp.Activity.MagazineDetails;
import com.divinetechs.ebooksapp.Interface.ItemClick;
import com.divinetechs.ebooksapp.Model.DownloadedItemModel;
import com.divinetechs.ebooksapp.R;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyDownloadsAdapter extends RecyclerView.Adapter<MyDownloadsAdapter.MyViewHolder> {

    private List<DownloadedItemModel> downloadList;
    Context mcontext;
    String from, where, currency_symbol;
    ItemClick itemClick;

    public MyDownloadsAdapter(Context context, List<DownloadedItemModel> downloadList, String from, String where,
                              ItemClick itemClick, String currency_symbol) {
        this.downloadList = downloadList;
        this.mcontext = context;
        this.from = from;
        this.where = where;
        this.itemClick = itemClick;
        this.currency_symbol = currency_symbol;
        Log.e("=>where", "" + where);
        Log.e("=>from", "" + from);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_book, txt_description, txt_book_author, txt_category, txt_price, txt_status;
        LinearLayout layout_read;
        ImageView iv_thumb;
        SimpleRatingBar simpleRatingBar;

        public MyViewHolder(View view) {
            super(view);
            txt_book = view.findViewById(R.id.txt_book);
            txt_description = view.findViewById(R.id.txt_description);
            txt_book_author = view.findViewById(R.id.txt_book_author);
            txt_category = view.findViewById(R.id.txt_category);
            txt_price = view.findViewById(R.id.txt_price);
            txt_status = view.findViewById(R.id.txt_status);
            iv_thumb = view.findViewById(R.id.iv_thumb);
            simpleRatingBar = view.findViewById(R.id.ratingbar);
            layout_read = view.findViewById(R.id.layout_read);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.mydownloadbook_item2, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        Picasso.get().load(downloadList.get(position).getImage()).into(holder.iv_thumb);
        holder.txt_book.setText("" + downloadList.get(position).getTitle());
        holder.txt_description.setText(Html.fromHtml(downloadList.get(position).getDescription()));
        holder.txt_book_author.setText(mcontext.getResources().getString(R.string.by) + " " + downloadList.get(position).getAuthorName());
        holder.txt_category.setText("" + downloadList.get(position).getCategoryName());
        if (downloadList.get(position).getPrice().equalsIgnoreCase("0")) {
            holder.txt_price.setText("" + mcontext.getResources().getString(R.string.free));
        } else {
            holder.txt_price.setText(currency_symbol + " " + downloadList.get(position).getPrice());
        }
        holder.simpleRatingBar.setRating(Float.parseFloat(downloadList.get(position).getAvgRating()));
        holder.txt_description.setVisibility(View.INVISIBLE);
        holder.txt_status.setText("" + mcontext.getResources().getString(R.string.downloaded));

        holder.iv_thumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("click", "call");
                Intent intent;
                if (from.equalsIgnoreCase("Books")) {
                    intent = new Intent(mcontext, BookDetails.class);
                } else {
                    intent = new Intent(mcontext, MagazineDetails.class);
                }
                intent.putExtra("ID", downloadList.get(position).getId());
                mcontext.startActivity(intent);
            }
        });

        holder.layout_read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClick.OnClick("" + downloadList.get(position).getId(), position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return downloadList.size();
    }

}
