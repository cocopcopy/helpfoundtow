package com.example.andrew.helpfind;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.example.andrew.helpfind.entity.CardUser;
import com.example.andrew.helpfind.entity.SimpleDataAdapter;
import com.example.andrew.helpfind.entity.StaticData;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.gmariotti.recyclerview.adapter.AlphaAnimatorAdapter;

public class MyCenterActivity extends Activity implements OnClickListener {

	@BindView(R.id.cpj_back)
    ImageView btnBack;
	@BindView(R.id.cpj_search)
    ImageView btnSearch;
	@BindView(R.id.rv_simple) RecyclerView simpleList;
	@BindView(R.id.sr_simple) SwipeRefreshLayout refreshLayout;

	public static final String KEYWORD = "KEYWORD";
	public static final String NOW_FIND = "Found";
	public static final String NOW_LOST = "Lost";
	public static final String EVER_LOST = "EverLost";
	public static final String EVER_FIND = "EverFind";

	private String FLAG;
	private int lastPos = 0;

	private ArrayList<CardUser> mData;
	private SimpleDataAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.detail_activity_home_list1);
		ButterKnife.bind(this);

		Intent intent = getIntent();
		FLAG = intent.getStringExtra(KEYWORD);

		mData = new ArrayList<>();

		initView();
		initData();

		setFooterView(simpleList);
		if (lastPos == 0) initialData();
		
	}

	private void initView() {
		RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getBaseContext(),
				LinearLayoutManager.VERTICAL, false);
		simpleList.setLayoutManager(mLayoutManager);
		mAdapter = new SimpleDataAdapter(mData, getBaseContext());
		AlphaAnimatorAdapter animatorAdapter = new AlphaAnimatorAdapter(mAdapter, simpleList);
		simpleList.setAdapter(animatorAdapter);

		assert refreshLayout != null;
		refreshLayout.setColorSchemeResources(R.color.colorPrimary);
		refreshLayout.setFocusable(false);
		refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				getMore(10);
			}
		});

		btnBack.setOnClickListener(this);
		btnSearch.setOnClickListener(this);
	}

	private void initData() {
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.cpj_back:
			finish();
			break;
		case R.id.cpj_search:
			Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show();
			break;
		default:
			break;
		}
	}

	private void getMore(final int num) {
		//TODO: get more data
		if(FLAG.equals("Found")||FLAG.equals("Lost")) {
			AVQuery<AVObject> query = new AVQuery<>("Notice");
			query.whereEqualTo("tag", FLAG);
			query.whereEqualTo("status", "in");
			query.orderByAscending("createdAt");
			query.limit(num);
			query.skip(lastPos);
			query.whereEqualTo("user", AVObject.createWithoutData("UserProfile", StaticData.getUserProfileId()));
			query.findInBackground(new FindCallback<AVObject>() {
				@Override
				public void done(List<AVObject> list, AVException e) {
					if (e != null) {
						Toast.makeText(MyCenterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
						return;
					}

					for (AVObject avObject : list) {
						CardUser cardUser = new CardUser(avObject.getObjectId(), avObject.getString("title"),
								avObject.getString("describe"));
						mData.add(0, cardUser);
					}

					refreshLayout.setRefreshing(false);
					if (list.size() == 0) {
						Toast.makeText(getBaseContext(), "没有更多数据了！", Toast.LENGTH_SHORT).show();
						return;
					}

					mAdapter.notifyDataSetChanged();
					lastPos += list.size();
				}
			});
		}
		 else if(FLAG.equals("EverFind")) {
			AVQuery<AVObject> query = new AVQuery<>("Notice");
			query.whereEqualTo("tag", "Found");
			query.whereEqualTo("status", "out");
			query.orderByAscending("createdAt");
			query.limit(num);
			query.skip(lastPos);
			query.whereEqualTo("user", AVObject.createWithoutData("UserProfile", StaticData.getUserProfileId()));
			query.findInBackground(new FindCallback<AVObject>() {
				@Override
				public void done(List<AVObject> list, AVException e) {
					if (e != null) {
						Toast.makeText(MyCenterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
						return;
					}

					for (AVObject avObject : list) {
						CardUser cardUser = new CardUser(avObject.getObjectId(), avObject.getString("title"),
								avObject.getString("describe"));
						mData.add(0, cardUser);
					}

					refreshLayout.setRefreshing(false);
					if (list.size() == 0) {
						Toast.makeText(getBaseContext(), "没有更多数据了！", Toast.LENGTH_SHORT).show();
						return;
					}

					mAdapter.notifyDataSetChanged();
					lastPos += list.size();
				}
			});
		}

		else if(FLAG.equals("EverLost")) {
			AVQuery<AVObject> query = new AVQuery<>("Notice");
			query.whereEqualTo("tag", "Lost");
			query.whereEqualTo("status", "out");
			query.orderByAscending("createdAt");
			query.limit(num);
			query.skip(lastPos);
			query.whereEqualTo("user", AVObject.createWithoutData("UserProfile", StaticData.getUserProfileId()));
			query.findInBackground(new FindCallback<AVObject>() {
				@Override
				public void done(List<AVObject> list, AVException e) {
					if (e != null) {
						Toast.makeText(MyCenterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
						return;
					}

					for (AVObject avObject : list) {
						CardUser cardUser = new CardUser(avObject.getObjectId(), avObject.getString("title"),
								avObject.getString("describe"));
						mData.add(0, cardUser);
					}

					refreshLayout.setRefreshing(false);
					if (list.size() == 0) {
						Toast.makeText(getBaseContext(), "没有更多数据了！", Toast.LENGTH_SHORT).show();
						return;
					}

					mAdapter.notifyDataSetChanged();
					lastPos += list.size();
				}
			});
		}

	}

	/**
	 * Footer view for RecyclerView
	 *
	 * footer view contains a TextView which display loading animation, loading history
	 * @param recyclerView
	 */
	private void setFooterView(RecyclerView recyclerView) {
		View footer = LayoutInflater.from(getBaseContext())
				.inflate(R.layout.broadcast_list_footer, recyclerView, false);
		mAdapter.setmFooterView(footer);
	}

	private void initialData() {
		simpleList.post(new Runnable() {
			@Override
			public void run() {
				refreshLayout.setRefreshing(true);
			}
		});
		getMore(15);
	}
}
