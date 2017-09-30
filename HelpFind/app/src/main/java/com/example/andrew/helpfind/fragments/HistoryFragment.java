package com.example.andrew.helpfind.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.example.andrew.helpfind.R;
import com.example.andrew.helpfind.entity.CardUser;
import com.example.andrew.helpfind.entity.HisAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.gmariotti.recyclerview.adapter.AlphaAnimatorAdapter;

/**
 * Created by zhouming on 2017/7/7.
 *
 * 在UserProfileActivity中被动态按钮调用而显示的Fragment，列出当前用户发布过的招领信息
 */

public class HistoryFragment extends Fragment {
    private String userProfileId;

    @Nullable
    @BindView(R.id.rv_profile_history) RecyclerView _listNearBy; // list notice

    private HisAdapter mAdapter;
    private ArrayList<CardUser> mData;
    private int lastPos = 0;

    public static HistoryFragment newInstance(String userprofileId) {
        HistoryFragment fragment = new HistoryFragment();
        fragment.userProfileId = userprofileId;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        if (null == mData) mData = new ArrayList<>();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_history, container, false);
        ButterKnife.bind(this, v);

        initialView();
        if (lastPos == 0) initialData();

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * Initial RecyclerView
     */
    private void initialView() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getBaseContext(),
                LinearLayoutManager.VERTICAL, false);

        _listNearBy.setLayoutManager(mLayoutManager);

        mAdapter = new HisAdapter(mData, getActivity().getBaseContext());

        AlphaAnimatorAdapter animatorAdapter = new AlphaAnimatorAdapter(mAdapter, _listNearBy);

        _listNearBy.setAdapter(animatorAdapter);
    }

    private void initialData() {
        getMore();
    }

    /**
     * This method is designed for getting data for RecyclerView
     *
     * both load data from disk and get data from website, called by getMore and loadMore
     * @return
     */
    private void getMore() {
        //TODO: get more data
        AVQuery<AVObject> query = new AVQuery<>("Notice");
        query.orderByAscending("createdAt");
        query.include("user");
        query.include("user.user");
        query.whereEqualTo("user", AVObject.createWithoutData("UserProfile", userProfileId));
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e != null) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                for (AVObject avObject : list) {
                    AVObject userProfile = avObject.getAVObject("user");
                    AVUser user = userProfile.getAVUser("user");

                    String infoImgUrl = null;
                    if (avObject.getAVFile("img") != null) infoImgUrl = avObject.getAVFile("img").getUrl();

                    String userImgUrl = null;
                    if (userProfile.getAVFile("photo") != null) userImgUrl = userProfile.getAVFile("photo").getUrl();

                    CardUser cardUser = new CardUser(
                            avObject.getObjectId(), user.getString("username"),
                            avObject.getString("title"), avObject.getString("describe"),
                            userImgUrl, infoImgUrl);
                    mData.add(0, cardUser);
                }

                if (list.size() == 0) {
                    Toast.makeText(getActivity().getBaseContext(), "没有更多数据了！", Toast.LENGTH_SHORT).show();
                    return ;
                }
                mAdapter.notifyDataSetChanged();
            }
        });
    }
}
