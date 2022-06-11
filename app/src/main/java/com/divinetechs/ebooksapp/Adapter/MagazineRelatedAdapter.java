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


public class MagazineRelatedAdapter extends RecyclerView.Adapter<MagazineRelatedAdapter.MyViewHolder> {

    private List<Result> RelatedList;
    Context mcontext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_bookname;
        ImageView iv_thumb;

        public MyViewHolder(View view) {
            super(view);
            txt_bookname = (TextView) view.findViewById(R.id.txt_bookname);
            iv_thumb = (ImageView) view.findViewById(R.id.iv_thumb);
        }
    }


    public MagazineRelatedAdapter(Context context, List<Result> RelatedList) {
        this.RelatedList = RelatedList;
        this.mcontext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.related_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.txt_bookname.setText("" + RelatedList.get(position).getTitle());

        Picasso.get().load(RelatedList.get(position).getImage()).into(holder.iv_thumb);

        holder.iv_thumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("click", "call");
                Intent intent = new Intent(mcontext, MagazineDetails.class);
                intent.putExtra("ID", RelatedList.get(position).getId());
                mcontext.startActivity(intent);
            }
        });

    }

    public void addBook(List<Result> items) {
        RelatedList.addAll(items);
        Log.e("RelatedList", "" + RelatedList.size());
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return RelatedList.size();
    }

}
