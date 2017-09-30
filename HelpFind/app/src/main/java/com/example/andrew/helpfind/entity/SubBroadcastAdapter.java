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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.andrew.helpfind.CardDetailActivity;
import com.example.andrew.helpfind.MapActivity;
import com.example.andrew.helpfind.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Andrew on 2017/8/29.
 */

public class SubBroadcastAdapter extends RecyclerView.Adapter<SubBroadcastAdapter.ViewHolder> {
    private ArrayList<CardUser> mData;
    private Context mContext;

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_FOOTER = 1;
    public static final int TYPE_NORMAL = 2;

    private View mHeaderView = null;
    private View mFooterView = null;

    public SubBroadcastAdapter(ArrayList<CardUser> data, Context context) {
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
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        if (mHeaderView != null && viewType == TYPE_HEADER) {
            return new ViewHolder(mHeaderView, mContext);
        }
        if (mFooterView != null && viewType == TYPE_FOOTER) {
            return new ViewHolder(mFooterView, mContext);
        }

        // or normal view
        View recItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nearby_list, parent, false);
        return new ViewHolder(recItem, mContext);
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
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_NORMAL) {
            // bind data from ViewHolder
            position = (mHeaderView == null ? position : position - 1);
            CardUser data = mData.get(position);

            holder.userName.setText(data.get_userName());
            holder.info.setText(data.get_info());
            holder.title.setText(data.get_title());
            holder.address.setText(data.getAddress());
            holder.date.setText(data.getDateStringLike());


            final ImageView imageView = holder.userPhoto;

            String userPhotoUrl = mData.get(position).get_userPhotoURL();
            String infoImgUrl = mData.get(position).get_infoImgUrl();

            if (userPhotoUrl != null) {
                Glide.with(mContext).load(userPhotoUrl)
                        .asBitmap().into(new BitmapImageViewTarget(imageView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        imageView.setImageDrawable(circularBitmapDrawable);
                    }
                });
            } else {
//                Glide.with(mContext).load(R.drawable.defaultimg).asBitmap().into(new BitmapImageViewTarget(imageView) {
//                    @Override
//                    protected void setResource(Bitmap resource) {
//                        RoundedBitmapDrawable circularBitmapDrawable =
//                                RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
//                        circularBitmapDrawable.setCircular(true);
//                        imageView.setImageDrawable(circularBitmapDrawable);
//                    }
//                });

            }

            if (infoImgUrl != null) {
                Glide.with(mContext).load(infoImgUrl).centerCrop().into(holder.infoImg);
            }

        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && mHeaderView != null) return TYPE_HEADER;
        if (position == getItemCount() - 1 && mFooterView != null) return TYPE_FOOTER;
        return TYPE_NORMAL;
    }

    @Override
    public int getItemCount() {
        int length = (mData == null ? 0 : mData.size());
        if(mHeaderView == null && mFooterView == null){
            return length;
        }else if(mHeaderView == null && mFooterView != null){
            return length + 1;
        }else if (mHeaderView != null && mFooterView == null){
            return length + 1;
        }else {
            return length + 2;
        }
    }

    //    设置头部进入地图的item
    public void setmHeaderView(View mHeaderView) {
        this.mHeaderView = mHeaderView;
        Button btnOpenMap = (Button) this.mHeaderView.findViewById(R.id.btn_open_map);
        btnOpenMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: open map
                Intent intent = new Intent(mContext, MapActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });
        notifyItemInserted(0);
    }

    //    设置尾部item
    public void setmFooterView(View mFooterView) {
        this.mFooterView = mFooterView;
        notifyItemInserted(getItemCount() - 1);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Nullable
        @BindView(R.id.iv_card) ImageView userPhoto;  // 发布人的头像
        @BindView(R.id.tv_card) TextView userName;    // 发布人的用户名
        @BindView(R.id.tv_card_title) TextView title; // 发布信息的标题
        @BindView(R.id.tv_card_info) TextView info;   // 发布信息的描述信息
        @BindView(R.id.iv_info) ImageView infoImg;    // 发布信息的图像
        @BindView(R.id.tv_address) TextView address;
        @BindView(R.id.tv_date) TextView date;

        private Context mContext;

        public ViewHolder(View itemView, Context mContext) {
            super(itemView);
            this.mContext = mContext;
            if (itemView == mHeaderView || itemView == mFooterView) return;
            itemView.setOnClickListener(this);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onClick(View v) {
            int current = getAdapterPosition();
            current = (mHeaderView == null ? current : current - 1);
            Intent intent = new Intent(mContext, CardDetailActivity.class);
            intent.putExtra(CardDetailActivity.INFO_ID, mData.get(current).get_id());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        }
    }
}
