package com.example.andrew.helpfind.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.example.andrew.helpfind.R;
import com.example.andrew.helpfind.entity.CardUser;
import com.example.andrew.helpfind.entity.SubBroadcastAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.gmariotti.recyclerview.adapter.AlphaAnimatorAdapter;

/**
 * Created by Andrew on 2017/8/29.
 */

public class SubBroadcastFragment extends Fragment {
    private static final String LAST_POS = "LastPosition";
    private static final String ARG_VIEW_PAGER_TYPE = "ViewpagerType";

    private int currentType;

    @BindView(R.id.rv_list_nearby) RecyclerView _listNearBy; // list notice
    @BindView(R.id.sr_broadcast) SwipeRefreshLayout _swipeRefresh;

    private SubBroadcastAdapter mAdapter;
    private ArrayList<CardUser> mData;
    private int lastPos = 0;

    public static SubBroadcastFragment newInstance(int type) {
        Bundle args = new Bundle();
        args.putInt(ARG_VIEW_PAGER_TYPE, type);
        SubBroadcastFragment fragment = new SubBroadcastFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        currentType = getArguments().getInt(ARG_VIEW_PAGER_TYPE, 0);

        if (null == mData) mData = new ArrayList<>();

        if (savedInstanceState != null) {
            lastPos = savedInstanceState.getInt(LAST_POS);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sub_broadcast, container, false);
        ButterKnife.bind(this, v);

        initialView();
        if (currentType == 1) {
            setHeaderView(_listNearBy);
        }
        setFooterView(_listNearBy);

        if (lastPos == 0) initialData();

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(LAST_POS, lastPos);
    }

    /**
     * 初始化视图，包括已登用户的基本信息和RecyclerView的初始化
     */
    private void initialView() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);

        _listNearBy.setLayoutManager(mLayoutManager);

        mAdapter = new SubBroadcastAdapter(mData, getContext());

        AlphaAnimatorAdapter animatorAdapter = new AlphaAnimatorAdapter(mAdapter, _listNearBy);

        _listNearBy.setAdapter(animatorAdapter);

        // 为刷新控件添加下拉刷新事件监听
        assert _swipeRefresh != null;
        _swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        _swipeRefresh.setFocusable(false);
        _swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMore(10);
            }
        });
    }

    /**
     * 初始化招领数据
     */
    private void initialData() {
        _swipeRefresh.post(new Runnable() {
            @Override
            public void run() {
                _swipeRefresh.setRefreshing(true);
            }
        });
        getMore(15);
    }

    /**
     * Setting header view for RecyclerView
     *
     * header view contains a button which links to a open map interface
     * @param recyclerView
     */
    private void setHeaderView(RecyclerView recyclerView) {
        View header = LayoutInflater.from(getContext())
                .inflate(R.layout.broadcast_list_header, recyclerView, false);
        mAdapter.setmHeaderView(header);
    }

    /**
     * Footer view for RecyclerView
     *
     * footer view contains a TextView which display loading animation, loading history
     * @param recyclerView
     */
    private void setFooterView(RecyclerView recyclerView) {
        View footer = LayoutInflater.from(getContext())
                .inflate(R.layout.broadcast_list_footer, recyclerView, false);
        mAdapter.setmFooterView(footer);
    }

    /**
     * 从服务器拉取更多数据
     *
     * @param num 指定数量
     */
    private void getMore(final int num) {
        String dataType = "Found";
        if (currentType == 1) dataType = "Lost";

        //TODO: get more data
        AVQuery<AVObject> query = new AVQuery<>("Notice");
        query.orderByAscending("createdAt");
        query.whereEqualTo("tag", dataType);
        query.whereEqualTo("status","in");
        query.limit(num);
        query.skip(lastPos);
        query.include("user");
        query.include("user.user");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e != null) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                for (AVObject avObject : list) {
                    CardUser cardUser = CardUser.newInstanceFull(avObject);
                    mData.add(0, cardUser);
                }

                _swipeRefresh.setRefreshing(false);
                if (list.size() == 0) {
                    Toast.makeText(getActivity(), "没有更多数据了！", Toast.LENGTH_SHORT).show();
                    return ;
                }

                mAdapter.notifyDataSetChanged();
                lastPos += list.size();
            }
        });
    }
}
