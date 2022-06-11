package com.divinetechs.ebooksapp.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.divinetechs.ebooksapp.Activity.AuthorBookList;
import com.divinetechs.ebooksapp.Model.AuthorModel.Result;
import com.divinetechs.ebooksapp.R;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AuthorAdapter extends RecyclerView.Adapter<AuthorAdapter.MyViewHolder> {

    private List<Result> AuthorList;
    Context mcontext;
    String from;

    public AuthorAdapter(Context context, List<Result> AuthorList, String from) {
        this.AuthorList = AuthorList;
        this.mcontext = context;
        this.from = from;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_bookname;
        RoundedImageView iv_thumb;

        public MyViewHolder(View view) {
            super(view);
            txt_bookname = (TextView) view.findViewById(R.id.txt_bookname);
            iv_thumb = (RoundedImageView) view.findViewById(R.id.iv_thumb);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;

        if (from.equalsIgnoreCase("Home")) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.author_item, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.author_item2, parent, false);
        }

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.txt_bookname.setText("" + AuthorList.get(position).getName());

        Picasso.get().load(AuthorList.get(position).getImage()).into(holder.iv_thumb);

        holder.iv_thumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("click", "call");
                Intent intent = new Intent(mcontext, AuthorBookList.class);
                intent.putExtra("a_id", AuthorList.get(position).getId());
                intent.putExtra("a_name", AuthorList.get(position).getName());
                intent.putExtra("a_bio", AuthorList.get(position).getAddress());
                intent.putExtra("a_image", AuthorList.get(position).getImage());
                intent.putExtra("a_address", AuthorList.get(position).getAddress());
                mcontext.startActivity(intent);
            }
        });

    }

    public void addBook(List<Result> items) {
        AuthorList.addAll(items);
        Log.e("AuthorList", "" + AuthorList.size());
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return AuthorList.size();
    }

}
