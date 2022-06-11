package com.divinetechs.ebooksapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.divinetechs.ebooksapp.Model.VoucherModel.Result;
import com.divinetechs.ebooksapp.R;
import com.divinetechs.ebooksapp.Utility.Utils;

import java.util.List;

public class VouchersAdapter extends RecyclerView.Adapter<VouchersAdapter.MyViewHolder> {

    Context mcontext;
    List<Result> voucherList;

    public VouchersAdapter(Context context, List<Result> voucherList) {
        this.mcontext = context;
        this.voucherList = voucherList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtTitle, txtExpireDate, txtStartDate, txtPointTitle, txtPoints;

        public MyViewHolder(View view) {
            super(view);
            txtTitle = view.findViewById(R.id.txtTitle);
            txtExpireDate = view.findViewById(R.id.txtExpireDate);
            txtStartDate = view.findViewById(R.id.txtStartDate);
            txtPoints = view.findViewById(R.id.txtPoints);
            txtPointTitle = view.findViewById(R.id.txtPointTitle);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.vouchers_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.txtTitle.setText("" + voucherList.get(position).getTitle());
        holder.txtStartDate.setText("" + Utils.DateFormat2(voucherList.get(position).getCreatedAt()));
        holder.txtExpireDate.setText(mcontext.getResources().getString(R.string.expires_on) + " " + Utils.DateFormat(voucherList.get(position).getExpiryDate()));

        if (!voucherList.get(position).getPoints().equalsIgnoreCase("")) {
            holder.txtPoints.setText("+" + voucherList.get(position).getPoints());
        } else {
            holder.txtPoints.setText("0");
        }
    }

    @Override
    public int getItemCount() {
        return voucherList.size();
    }

}
