package com.example.andrew.helpfind.entity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.andrew.helpfind.CardDetailActivity;
import com.example.andrew.helpfind.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhouming on 2017/7/7.
 */

public class SimpleDataAdapter extends RecyclerView.Adapter<SimpleDataAdapter.ViewHolder> {

    private ArrayList<CardUser> mData;
    private Context mContext;

    public static final int TYPE_FOOTER = 1;
    public static final int TYPE_NORMAL = 2;

    private View mFooterView = null;

    public SimpleDataAdapter(ArrayList<CardUser> data, Context context) {
        this.mContext = context;
        this.mData = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mFooterView != null && viewType == TYPE_FOOTER) {
            return new SimpleDataAdapter.ViewHolder(mFooterView, mContext);
        }

        // or normal view
        View recItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.detail_activity_home_list1_1, parent, false);
        return new SimpleDataAdapter.ViewHolder(recItem, mContext);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_NORMAL) {
            // bind data from ViewHolder
            CardUser data = mData.get(position);
            holder.describe.setText(data.get_info());
            holder.title.setText(data.get_title());
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mFooterView == null) {
            return TYPE_NORMAL;
        }
        if (position == getItemCount() - 1) {
            return TYPE_FOOTER;
        }
        return TYPE_NORMAL;
    }

    @Override
    public int getItemCount() {
        int length = (mData == null ? 0 : mData.size());
        if(mFooterView == null){
            return length;
        }
        return length + 1;
    }

    public void setmFooterView(View mFooterView) {
        this.mFooterView = mFooterView;
        notifyItemInserted(getItemCount() - 1);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Nullable @BindView(R.id.tv_simple_title)
        TextView title;
        @Nullable @BindView(R.id.tv_simple_desc)
        TextView describe;

        private Context mContext;

        public ViewHolder(View itemView, Context mContext) {
            super(itemView);
            this.mContext = mContext;
            if (itemView == mFooterView) return;
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
