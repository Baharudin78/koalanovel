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
import com.divinetechs.ebooksapp.Model.BookModel.Result;
import com.divinetechs.ebooksapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ContinueReadAdapter extends RecyclerView.Adapter<ContinueReadAdapter.MyViewHolder> {

    private List<Result> ContinueReadList;
    Context mcontext;
    String from;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_bookname, txt_description, txt_category;
        ImageView iv_thumb;
        LinearLayout lyBook;

        public MyViewHolder(View view) {
            super(view);
            lyBook = view.findViewById(R.id.lyBook);
            txt_bookname = view.findViewById(R.id.txt_bookname);
            txt_description = itemView.findViewById(R.id.txt_description);
            txt_category = itemView.findViewById(R.id.txt_category);
            iv_thumb = view.findViewById(R.id.iv_thumb);
        }
    }


    public ContinueReadAdapter(Context context, List<Result> ContinueReadList, String from) {
        this.ContinueReadList = ContinueReadList;
        this.mcontext = context;
        this.from = from;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (from.equalsIgnoreCase("Home")) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.continue_read_item, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.continue_read_item2, parent, false);
        }
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        if (from.equalsIgnoreCase("Home")) {
            holder.txt_bookname.setText("" + ContinueReadList.get(position + 1).getTitle());
            holder.txt_description.setText(Html.fromHtml(ContinueReadList.get(position + 1).getDescription()));
            holder.txt_category.setText("" + ContinueReadList.get(position + 1).getCategoryName());
            Picasso.get().load(ContinueReadList.get(position + 1).getImage()).into(holder.iv_thumb);
            holder.lyBook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("click", "call");
                    Intent intent = new Intent(mcontext, BookDetails.class);
                    intent.putExtra("ID", ContinueReadList.get(position + 1).getId());
                    mcontext.startActivity(intent);
                }
            });

        } else {
            holder.txt_bookname.setText("" + ContinueReadList.get(position).getTitle());
            holder.txt_description.setText(Html.fromHtml(ContinueReadList.get(position).getDescription()));
            holder.txt_category.setText("" + ContinueReadList.get(position).getCategoryName());
            Picasso.get().load(ContinueReadList.get(position).getImage()).into(holder.iv_thumb);
            holder.lyBook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("click", "call");
                    Intent intent = new Intent(mcontext, BookDetails.class);
                    intent.putExtra("ID", ContinueReadList.get(position).getId());
                    mcontext.startActivity(intent);
                }
            });
        }

    }

    public void addBook(List<Result> items) {
        ContinueReadList.addAll(items);
        Log.e("ContinueReadList", "" + ContinueReadList.size());
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (from.equalsIgnoreCase("Home")) {
            return (ContinueReadList.size()) - 1;
        } else {
            return ContinueReadList.size();
        }
    }

}
