package com.divinetechs.ebooksapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.divinetechs.ebooksapp.Model.BannerModel.Result;
import com.divinetechs.ebooksapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import es.dmoral.toasty.Toasty;

public class BannerAdapter extends PagerAdapter {

    private Context context;
    private List<Result> mBennerList;

    public BannerAdapter(Context context, List<Result> itemChannels) {
        this.context = context;
        this.mBennerList = itemChannels;
    }

    @Override
    public int getCount() {
        return mBennerList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        View imageLayout = LayoutInflater.from(container.getContext()).inflate(R.layout.banner_item_row, container, false);

        ImageView imageView = imageLayout.findViewById(R.id.image);

        Picasso.get().load(mBennerList.get(position).getImage()).into(imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("position", "" + position);
                Log.e("url", "" + mBennerList.get(position).getUrl());

                if (!TextUtils.isEmpty(mBennerList.get(position).getUrl())) {
                    Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("" + mBennerList.get(position).getUrl()));
                    context.startActivity(viewIntent);
                }
            }
        });

        container.addView(imageLayout, 0);
        return imageLayout;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        (container).removeView((View) object);
    }

}