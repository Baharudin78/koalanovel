package com.divinetechs.ebooksapp.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.divinetechs.ebooksapp.Model.WalletHistoryModel.Result;
import com.divinetechs.ebooksapp.R;
import com.divinetechs.ebooksapp.Utility.Utils;

import java.util.List;

public class WalletAdapter extends RecyclerView.Adapter<WalletAdapter.MyViewHolder> {

    private List<Result> RewardList;
    Context mcontext;
    String currency_symbol;

    public WalletAdapter(Context context, List<Result> RewardList, String currency_symbol) {
        this.RewardList = RewardList;
        this.mcontext = context;
        this.currency_symbol = currency_symbol;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_title, txt_date, txt_desc, txt_price;

        public MyViewHolder(View view) {
            super(view);
            txt_title = view.findViewById(R.id.txt_title);
            txt_date = view.findViewById(R.id.txt_date);
            txt_desc = view.findViewById(R.id.txt_desc);
            txt_price = view.findViewById(R.id.txt_price);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.reward_item_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.txt_price.setText(currency_symbol + "" + RewardList.get(position).getAmount());
        holder.txt_title.setText("Payment ID :- " + RewardList.get(position).getPaymentId());
        holder.txt_date.setText("" + Utils.DateFormat2(RewardList.get(position).getCreatedAt()));
    }

    public void addData(List<Result> items) {
        RewardList.addAll(items);
        Log.e("RewardList", "" + RewardList.size());
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return RewardList.size();
    }

}
