package com.divinetechs.ebooksapp.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.divinetechs.ebooksapp.Activity.BookByCategory;
import com.divinetechs.ebooksapp.Model.CategoryModel.Result;
import com.divinetechs.ebooksapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Random;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {

    private List<Result> CategoryList;
    Context mcontext;
    String from;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_bookname, txt_tag;
        ImageView iv_thumb;
        LinearLayout ly_category;

        public MyViewHolder(View view) {
            super(view);
            txt_bookname = (TextView) view.findViewById(R.id.txt_bookname);
            iv_thumb = (ImageView) view.findViewById(R.id.iv_thumb);
            txt_tag = (TextView) view.findViewById(R.id.txt_tag);
            ly_category = view.findViewById(R.id.ly_category);
        }
    }


    public CategoryAdapter(Context context, List<Result> CategoryList, String from) {
        this.CategoryList = CategoryList;
        this.mcontext = context;
        this.from = from;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (from.equalsIgnoreCase("Home")) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.category_item, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.category_item2, parent, false);
        }
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.txt_bookname.setText("" + CategoryList.get(position).getName());
        holder.txt_tag.setText("" + CategoryList.get(position).getName().charAt(0));

        Picasso.get().load(CategoryList.get(position).getImage())
                .placeholder(R.drawable.test)
                .into(holder.iv_thumb);

        holder.ly_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("click", "call");
                Intent intent = new Intent(mcontext, BookByCategory.class);
                intent.putExtra("cat_id", CategoryList.get(position).getId());
                intent.putExtra("cat_name", CategoryList.get(position).getName());
                intent.putExtra("cat_image", CategoryList.get(position).getImage());
                mcontext.startActivity(intent);
            }
        });

    }

    public void addBook(List<Result> items) {
        CategoryList.addAll(items);
        Log.e("CategoryList", "" + CategoryList.size());
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return CategoryList.size();
    }

    public int getRandomColor() {
        Random rnd = new Random();
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }

}
