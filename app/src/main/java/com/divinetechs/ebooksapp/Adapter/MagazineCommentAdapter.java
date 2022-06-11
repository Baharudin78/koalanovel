package com.divinetechs.ebooksapp.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.divinetechs.ebooksapp.Model.CommentModel.Result;
import com.divinetechs.ebooksapp.R;
import com.divinetechs.ebooksapp.Utility.Utils;

import java.util.List;
import java.util.Random;

public class MagazineCommentAdapter extends RecyclerView.Adapter<MagazineCommentAdapter.MyViewHolder> {

    private List<Result> CommentList;
    Context mcontext;
    String from;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_user_name, txt_comment, txt_date, txt_tag;
        ImageView iv_thumb;

        public MyViewHolder(View view) {
            super(view);
            txt_user_name = view.findViewById(R.id.txt_user_name);
            txt_comment = view.findViewById(R.id.txt_comment);
            txt_date = view.findViewById(R.id.txt_date);
            iv_thumb = view.findViewById(R.id.iv_thumb);
            txt_tag = view.findViewById(R.id.txt_tag);
        }
    }


    public MagazineCommentAdapter(Context context, List<Result> CommentList, String from) {
        this.CommentList = CommentList;
        this.mcontext = context;
        this.from = from;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        holder.txt_user_name.setText("" + CommentList.get(position).getFullname());
        holder.txt_comment.setText("" + CommentList.get(position).getComment());
        holder.txt_date.setText("" + Utils.DateFormat2(CommentList.get(position).getCreatedAt()));
        holder.txt_tag.setText("" + CommentList.get(position).getFullname().charAt(0));

        holder.iv_thumb.setBackgroundColor(getRandomColor());

        holder.iv_thumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("click", "call");
//                Intent intent = new Intent(mcontext, BookDetails.class);
//                intent.putExtra("ID", CommentList.get(position).getBId());
//                mcontext.startActivity(intent);
            }
        });

    }

    public void addBook(List<Result> items) {
        CommentList.addAll(items);
        Log.e("CommentList", "" + CommentList.size());
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (from.equalsIgnoreCase("Max_5")) {
            return 5;
        } else {
            return CommentList.size();
        }
    }

    public int getRandomColor() {
        Random rnd = new Random();
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }

}
