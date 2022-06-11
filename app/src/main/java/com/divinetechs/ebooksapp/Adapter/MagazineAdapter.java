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

import com.divinetechs.ebooksapp.Activity.MagazineDetails;
import com.divinetechs.ebooksapp.Model.MagazineModel.Result;
import com.divinetechs.ebooksapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MagazineAdapter extends RecyclerView.Adapter<MagazineAdapter.MyViewHolder> {

    private List<Result> magazineList;
    Context mcontext;
    String from;

    public MagazineAdapter(Context context, List<Result> magazineList, String from) {
        this.magazineList = magazineList;
        this.mcontext = context;
        this.from = from;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtMagazine;
        ImageView ivThumb;

        public MyViewHolder(View view) {
            super(view);
            txtMagazine = (TextView) view.findViewById(R.id.txtMagazine);
            ivThumb = (ImageView) view.findViewById(R.id.ivThumb);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (from.equalsIgnoreCase("Popular") || from.equalsIgnoreCase("MostView") ||
                from.equalsIgnoreCase("TopDownload")) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.magazine_item, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.magazine_item2, parent, false);
        }
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.txtMagazine.setText("" + magazineList.get(position).getTitle());

        Picasso.get().load(magazineList.get(position).getImage()).into(holder.ivThumb);

        holder.ivThumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("click", "" + position);
                Intent intent = new Intent(mcontext, MagazineDetails.class);
                intent.putExtra("ID", magazineList.get(position).getId());
                mcontext.startActivity(intent);
            }
        });
    }

    public void addBook(List<Result> items) {
        magazineList.addAll(items);
        Log.e("magazineList", "" + magazineList.size());
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return magazineList.size();
    }

}
