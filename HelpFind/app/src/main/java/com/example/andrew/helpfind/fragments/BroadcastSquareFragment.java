package com.example.andrew.helpfind.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.andrew.helpfind.LoginActivity;
import com.example.andrew.helpfind.R;
import com.example.andrew.helpfind.UploadActivity;
import com.example.andrew.helpfind.entity.BroadVPAdapter;
import com.example.andrew.helpfind.entity.StaticData;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Andrew on 2017/8/29.
 */

public class BroadcastSquareFragment extends Fragment {

    @BindView(R.id.tv_broadcast) TextView _makeBroadcast;  // link to DistributeFragment
    @BindView(R.id.tl_broad_tab) TabLayout mTabLayout;
    @BindView(R.id.vp_broad) ViewPager mViewPager;
    @BindView(R.id.iv_broadcast_me) ImageView _me; // link to CenterFragment

    private Context mContext;

    public static BroadcastSquareFragment newInstance() {
        BroadcastSquareFragment fragment = new BroadcastSquareFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_broadcastsquare, container, false);
        ButterKnife.bind(this, v);

        initialView();

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // 这里把原来的发布改成了menu选项
        inflater.inflate(R.menu.menu_search, menu);
        final MenuItem itemNewPost = menu.findItem(R.id.new_post);

        // 设置makepost的点击时间,能够跳到upload界面
        itemNewPost.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (StaticData.getCurrentUser() == null) {
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getContext().startActivity(intent);
                } else {
                    Intent intent = new Intent(getContext(), UploadActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getContext().startActivity(intent);
                }
                return true;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * 初始化视图，包括已登用户的基本信息和RecyclerView的初始化
     */
    private void initialView() {

        // 此处装载一个view paper，包含两个子fragment
        mViewPager.setAdapter(new BroadVPAdapter(getContext(), getChildFragmentManager()));
        mTabLayout.setupWithViewPager(mViewPager);


        // 用户头像设定逻辑，有点瑕疵，我需要给头像一个缩放，而不是让头像变形
        if (StaticData.getCurrentUser() != null) {
            String userId = StaticData.getCurrentUser().getObjectId();
            AVQuery<AVObject> query = new AVQuery<>("UserProfile");
            query.whereEqualTo("user", AVObject.createWithoutData("_User", userId));
            query.findInBackground(new FindCallback<AVObject>() {
                @Override
                public void done(List<AVObject> list, AVException e) {
                    if (list.size() == 0) return;
                    AVFile file = list.get(0).getAVFile("photo");
                    if (file != null) {
                        String imgUrl = file.getUrl();
                        StaticData.setUserImgLink(imgUrl);
                        Glide.with(getActivity()).load(imgUrl).asBitmap()
                                .centerCrop()
                                .into(new BitmapImageViewTarget(_me) {
                                    @Override
                                    protected void setResource(Bitmap resource) {
                                        RoundedBitmapDrawable circularBitmapDrawable =
                                                RoundedBitmapDrawableFactory.create(getActivity().getResources(), resource);
                                        circularBitmapDrawable.setCircular(true);
                                        _me.setImageDrawable(circularBitmapDrawable);
                                    }
                                });
                    }
                }
            });
        }

        _makeBroadcast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StaticData.getCurrentUser() == null) {
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getContext().startActivity(intent);
                } else {
                    Intent intent = new Intent(getContext(), UploadActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getContext().startActivity(intent);
                }

            }
        });
    }
}
