package com.divinetechs.ebooksapp.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.divinetechs.ebooksapp.Activity.BookDetails;
import com.divinetechs.ebooksapp.Model.BookModel.Result;
import com.divinetechs.ebooksapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AuthorBookAdapter extends RecyclerView.Adapter<AuthorBookAdapter.MyViewHolder> {

    private List<Result> NewArrivalList;
    Context mcontext;

    public AuthorBookAdapter(Context context, List<Result> NewArrivalList) {
        this.NewArrivalList = NewArrivalList;
        this.mcontext = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_bookname;
        ImageView iv_thumb;

        public MyViewHolder(View view) {
            super(view);
            txt_bookname = view.findViewById(R.id.txt_bookname);
            iv_thumb = view.findViewById(R.id.iv_thumb);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.authorbook_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.txt_bookname.setText("" + NewArrivalList.get(position).getTitle());
        Picasso.get().load(NewArrivalList.get(position).getImage()).into(holder.iv_thumb);

        holder.iv_thumb.setOnClickListener(new View.OnClickListener() {
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
