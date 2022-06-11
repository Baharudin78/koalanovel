package com.divinetechs.ebooksapp.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.divinetechs.ebooksapp.Activity.BookDetails;
import com.divinetechs.ebooksapp.Interface.ItemClick;
import com.divinetechs.ebooksapp.Model.BookmarkModel.Result;
import com.divinetechs.ebooksapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.MyViewHolder> {

    private List<Result> NewArrivalList;
    Context mcontext;
    String from;
    ItemClick itemClick;

    public BookmarkAdapter(Context context, List<Result> NewArrivalList, String from, ItemClick itemClick) {
        this.NewArrivalList = NewArrivalList;
        this.mcontext = context;
        this.from = from;
        this.itemClick = itemClick;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_bookname, txt_view;
        ImageView iv_thumb;
        LinearLayout lyDelete;

        public MyViewHolder(View view) {
            super(view);
            txt_bookname = view.findViewById(R.id.txt_bookname);
            iv_thumb = view.findViewById(R.id.iv_thumb);
            txt_view = view.findViewById(R.id.txt_view);
            lyDelete = view.findViewById(R.id.lyDelete);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmark_item2, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.txt_view.setText("" + NewArrivalList.get(position).getReadcnt());
        holder.txt_bookname.setText("" + NewArrivalList.get(position).getTitle());

        Picasso.get().load(NewArrivalList.get(position).getImage()).into(holder.iv_thumb);

        holder.iv_thumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("click", "pos => " + position);
                Intent intent = new Intent(mcontext, BookDetails.class);
                intent.putExtra("ID", NewArrivalList.get(position).getId());
                mcontext.startActivity(intent);
            }
        });

        holder.lyDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("click", "" + position);
                itemClick.OnClick(NewArrivalList.get(position).getId(), position);
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
