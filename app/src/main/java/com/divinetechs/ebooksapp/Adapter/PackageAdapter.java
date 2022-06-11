package com.divinetechs.ebooksapp.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.divinetechs.ebooksapp.Model.PackageModel.Result;
import com.divinetechs.ebooksapp.Interface.ItemClickListener;
import com.divinetechs.ebooksapp.R;

import java.util.List;

public class PackageAdapter extends RecyclerView.Adapter<PackageAdapter.MyViewHolder> {

    private List<Result> packageList;
    Context mcontext;
    String type, currency_symbol;
    ItemClickListener itemClickListener;

    public PackageAdapter(Context context, List<Result> packageList, String type,
                          ItemClickListener itemClickListener, String currency_symbol) {
        this.packageList = packageList;
        this.mcontext = context;
        this.type = type;
        this.itemClickListener = itemClickListener;
        this.currency_symbol = currency_symbol;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_title, txt_price;
        LinearLayout ly_package;

        public MyViewHolder(View view) {
            super(view);
            ly_package = view.findViewById(R.id.ly_package);
            txt_title = view.findViewById(R.id.txt_title);
            txt_price = view.findViewById(R.id.txt_price);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.package_item_frg, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.txt_title.setText("" + packageList.get(position).getPackageName());
        holder.txt_price.setText(currency_symbol + "" + packageList.get(position).getPrice());

        holder.ly_package.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("click", "" + position);
                itemClickListener.onItemClick(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return packageList.size();
    }

}
