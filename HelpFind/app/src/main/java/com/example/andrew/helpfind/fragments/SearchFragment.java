package com.example.andrew.helpfind.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
 * Created by andrew on 17-11-11.
 */

public class SearchFragment extends Fragment {
    private static final String LAST_POS = "LastPosition";

    @BindView(R.id.rv_list_nearby) RecyclerView _listNearBy; // list notice
    @BindView(R.id.sr_broadcast) SwipeRefreshLayout _swipeRefresh;
    @BindView(R.id.toolbar_search) SearchView searchView;
    @BindView(R.id.tv_result) LinearLayout resultHintView;

    private SubBroadcastAdapter mAdapter;
    private ArrayList<CardUser> mData;
    private int lastPos = 0;
    private String searchKey;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);


        if (null == mData) mData = new ArrayList<>();

        if (savedInstanceState != null) {
            lastPos = savedInstanceState.getInt(LAST_POS);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, v);

        initialView();

        setFooterView(_listNearBy);

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

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                resultHintView.setVisibility(View.GONE);
                _swipeRefresh.setVisibility(View.VISIBLE);
                search(s);
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        assert _swipeRefresh != null;
        _swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        _swipeRefresh.setFocusable(false);
        _swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadMore(10);
            }
        });
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

    private void search(String searchKey) {
        mData.clear();
        mAdapter.notifyDataSetChanged();

        AVQuery<AVObject> query = new AVQuery<>("Notice");
        query.whereContains("title", searchKey);
        query.orderByAscending("createdAt");
        query.include("user");
        query.include("user.user");

        this.searchKey = searchKey;

        _swipeRefresh.post(new Runnable() {
            @Override
            public void run() {
                _swipeRefresh.setRefreshing(true);
            }
        });

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
                    _swipeRefresh.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "没有更多数据了！", Toast.LENGTH_SHORT).show();
                    return ;
                }



                mAdapter.notifyDataSetChanged();
                lastPos += list.size();
            }
        });
    }

    private void loadMore(int num) {

        AVQuery<AVObject> query = new AVQuery<>("Notice");
        query.orderByAscending("createdAt");
        query.whereEqualTo("status","in");
        query.whereContains("title", this.searchKey);
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
