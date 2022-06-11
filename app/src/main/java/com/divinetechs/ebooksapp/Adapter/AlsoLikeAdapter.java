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
import com.divinetechs.ebooksapp.Model.BookModel.Result;
import com.divinetechs.ebooksapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AlsoLikeAdapter extends RecyclerView.Adapter<AlsoLikeAdapter.MyViewHolder> {

    Context mcontext;
    private List<Result> alsoLikeList;

    public AlsoLikeAdapter(Context context, List<Result> alsoLikeList) {
        this.mcontext = context;
        this.alsoLikeList = alsoLikeList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout lyAlsoLike;
        public TextView txt_bookname, txt_chapters, txt_category;
        ImageView iv_thumb;

        public MyViewHolder(View view) {
            super(view);
            lyAlsoLike = view.findViewById(R.id.lyAlsoLike);
            txt_bookname = view.findViewById(R.id.txt_bookname);
            txt_chapters = view.findViewById(R.id.txt_chapters);
            txt_category = view.findViewById(R.id.txt_category);
            iv_thumb = view.findViewById(R.id.iv_thumb);
        }
    }

    @Override
    public AlsoLikeAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.also_like_item, parent, false);

        return new AlsoLikeAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final AlsoLikeAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.txt_bookname.setText("" + alsoLikeList.get(position).getTitle());
        holder.txt_chapters.setText(mcontext.getResources().getString(R.string.chapter) + " " + alsoLikeList.get(position).getChapterCount());
        holder.txt_category.setText("" + alsoLikeList.get(position).getCategoryName());

        Picasso.get().load(alsoLikeList.get(position).getImage()).into(holder.iv_thumb);

        holder.lyAlsoLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("click", "call");
                Intent intent = new Intent(mcontext, BookDetails.class);
                intent.putExtra("ID", alsoLikeList.get(position).getId());
                mcontext.startActivity(intent);
            }
        });

    }

    public void addBook(List<Result> items) {
        alsoLikeList.addAll(items);
        Log.e("alsoLikeList", "" + alsoLikeList.size());
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return alsoLikeList.size();
    }

}
