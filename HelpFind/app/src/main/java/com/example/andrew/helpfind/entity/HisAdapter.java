package com.example.andrew.helpfind.entity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.andrew.helpfind.CardDetailActivity;
import com.example.andrew.helpfind.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhouming on 2017/7/8.
 *
 * 用户显示历史动态的Adapter
 */

public class HisAdapter extends RecyclerView.Adapter<HisAdapter.ViewHolder> {
    private ArrayList<CardUser> mData;
    private Context mContext;

    public HisAdapter(ArrayList<CardUser> data, Context context) {
        this.mContext = context;
        this.mData = data;
    }

    /**
     * Create ViewHolder, if current view's type is HeaderView or FooterView, return
     * ViewHolder directly
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public HisAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        // or normal view
        View recItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nearby_list, parent, false);
        return new HisAdapter.ViewHolder(recItem, mContext);
    }

    /**
     * Bind view
     *
     * this method return view type for current value pointed by position, but HeaderView and
     * FooterView need not be bound
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(HisAdapter.ViewHolder holder, int position) {
        // bind data from ViewHolder
        CardUser data = mData.get(position);
        holder.userName.setText(data.get_userName());
        holder.info.setText(data.get_info());
        holder.title.setText(data.get_title());

        final ImageView imageView = holder.userPhoto;

        String userImgUrl = mData.get(position).get_userPhotoURL();
        String infoImgUrl = mData.get(position).get_infoImgUrl();

        if (userImgUrl != null) {
            Glide.with(mContext).load(userImgUrl)
                    .asBitmap().override(90, 90).into(new BitmapImageViewTarget(imageView) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    imageView.setImageDrawable(circularBitmapDrawable);
                }
            });
        } else {
            Glide.with(mContext).load(R.drawable.defaultimg).asBitmap().override(90, 90).into(new BitmapImageViewTarget(imageView) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    imageView.setImageDrawable(circularBitmapDrawable);
                }
            });
        }

        if (infoImgUrl != null) {
            Glide.with(mContext).load(infoImgUrl).centerCrop().into(holder.infoImg);
        }
    }

    @Override
    public int getItemCount() {
        return (mData == null ? 0 : mData.size());
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Nullable
        @BindView(R.id.iv_card)
        ImageView userPhoto;
        @Nullable @BindView(R.id.tv_card)
        TextView userName;
        @Nullable @BindView(R.id.tv_card_title)
        TextView title;
        @Nullable @BindView(R.id.tv_card_info)
        TextView info;
        @Nullable @BindView(R.id.iv_info)
        ImageView infoImg;

        private Context mContext;

        public ViewHolder(View itemView, Context mContext) {
            super(itemView);
            this.mContext = mContext;
            itemView.setOnClickListener(this);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onClick(View v) {
            int current = getAdapterPosition();
            Intent intent = new Intent(mContext, CardDetailActivity.class);
            intent.putExtra(CardDetailActivity.INFO_ID, mData.get(current).get_id());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        }
    }
}
