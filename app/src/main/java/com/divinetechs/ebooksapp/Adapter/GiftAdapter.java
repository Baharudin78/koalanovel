package com.divinetechs.ebooksapp.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.divinetechs.ebooksapp.R;
import com.makeramen.roundedimageview.RoundedImageView;

public class GiftAdapter extends RecyclerView.Adapter<GiftAdapter.MyViewHolder> {

    Context mcontext;
    public static int selectedPos = RecyclerView.NO_POSITION;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_coins;
        RoundedImageView iv_thumb;
        LinearLayout ly_gift;

        public MyViewHolder(View view) {
            super(view);
            ly_gift = view.findViewById(R.id.ly_gift);
            iv_thumb = view.findViewById(R.id.iv_thumb);
            txt_coins = view.findViewById(R.id.txt_coins);
        }
    }


    public GiftAdapter(Context context) {
        this.mcontext = context;
    }

    @Override
    public GiftAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gift_items, parent, false);
        return new GiftAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GiftAdapter.MyViewHolder holder, int position) {
        holder.itemView.setSelected(selectedPos == position);

        holder.ly_gift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Click", "" + holder.getAdapterPosition());
                if (holder.getLayoutPosition() == RecyclerView.NO_POSITION) return;

                notifyItemChanged(selectedPos);
                selectedPos = holder.getLayoutPosition();
                notifyItemChanged(selectedPos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return 8;
    }

}
