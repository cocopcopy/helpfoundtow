package com.example.andrew.helpfind;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.GetCallback;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.andrew.helpfind.fragments.AboutMeFragment;
import com.example.andrew.helpfind.fragments.HistoryFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhouming on 2017/7/7.
 *
 * 该文件定义了第三用户信息显示活动
 */

public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String USER_FLAG = "UserFlag";
    public static final String USER_PROFILEID = "ProfileId";

    @BindView(R.id.iv_profile_photo)
    ImageView userPhoto;   // 用户头像
    @BindView(R.id.tv_profile_name)
    TextView userName;      // 用户名
    @BindView(R.id.tv_profile_history)
    TextView history;    // 显示历史记录的按钮
    @BindView(R.id.tv_profile_about)
    TextView userProfile;  // 显示用户相关信息的按钮
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.app_bar) AppBarLayout appbar;
    @BindView(R.id.toolbar_layout) CollapsingToolbarLayout collapsingToolbarLayout;

    private HistoryFragment hisFragment;
    private AboutMeFragment aboutMeFragment;
    private String objectId;

    private CollapsingToolbarLayoutState state;

    private int FLAG_FRAGMENT = 0;

    private enum CollapsingToolbarLayoutState {
        EXPANDED,
        COLLAPSED,
        INTERNEDIATE
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
        localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);

        setContentView(R.layout.activity_userprofile);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        String username = getIntent().getStringExtra(USER_FLAG);
        objectId = getIntent().getStringExtra(USER_PROFILEID);
        userName.setText(username);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        initView();

        if (savedInstanceState == null) {
            setDefaultFragment();
        } else {
            if (FLAG_FRAGMENT == 0) hisFragment = (HistoryFragment) getSupportFragmentManager().getFragment(savedInstanceState, "fragment");
            else aboutMeFragment = (AboutMeFragment) getSupportFragmentManager().getFragment(savedInstanceState, "fragment");
        }
    }

    private void initView() {
        history.setOnClickListener(this);
        userProfile.setOnClickListener(this);

        collapsingToolbarLayout.setCollapsedTitleGravity(Gravity.CENTER_HORIZONTAL);
        collapsingToolbarLayout.setTitleEnabled(false);

        AVQuery<AVObject> query = new AVQuery<>("UserProfile");
        query.getInBackground(objectId, new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                AVFile file = avObject.getAVFile("photo");
                if (file != null) {
                    Glide.with(getBaseContext()).load(file.getUrl())
                            .asBitmap().centerCrop().into(new BitmapImageViewTarget(userPhoto) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            userPhoto.setImageDrawable(circularBitmapDrawable);
                        }
                    });
                }
            }
        });

        appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset == 0) {
                    if (state != CollapsingToolbarLayoutState.EXPANDED) {
                        state = CollapsingToolbarLayoutState.EXPANDED;//修改状态标记为展开
                    }
                } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                    if (state != CollapsingToolbarLayoutState.COLLAPSED) {
                        state = CollapsingToolbarLayoutState.COLLAPSED;//修改状态标记为折叠
                        userName.setVisibility(View.GONE);
                        userPhoto.setVisibility(View.GONE);
                        collapsingToolbarLayout.setTitleEnabled(true);
                        collapsingToolbarLayout.setTitle(userName.getText());
                    }
                } else {
                    if (state != CollapsingToolbarLayoutState.INTERNEDIATE) {
                        state = CollapsingToolbarLayoutState.INTERNEDIATE;//修改状态标记为中间
                        userName.setVisibility(View.VISIBLE);
                        userPhoto.setVisibility(View.VISIBLE);
                        collapsingToolbarLayout.setTitleEnabled(false);
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();

        switch (FLAG_FRAGMENT) {
            case 0:
                history.setTextColor(ContextCompat.getColor(UserProfileActivity.this, R.color.colorDeepDivider));
                break;
            case 1:
                userProfile.setTextColor(ContextCompat.getColor(UserProfileActivity.this, R.color.colorDeepDivider));
                break;
        }

        switch (v.getId()) {
            case R.id.tv_profile_history:
                if (null == hisFragment) {
                    hisFragment = HistoryFragment.newInstance(objectId);
                    transaction.add(R.id.fl_profile, hisFragment);
                }
                FLAG_FRAGMENT = 0;
                hideFragment(transaction);
                transaction.show(hisFragment);
                history.setTextColor(ContextCompat.getColor(UserProfileActivity.this, R.color.colorCenter));
                break;
            case R.id.tv_profile_about:
                if (null == aboutMeFragment) {
                    aboutMeFragment = new AboutMeFragment();
                    transaction.add(R.id.fl_profile, aboutMeFragment);
                }
                FLAG_FRAGMENT = 1;
                hideFragment(transaction);
                transaction.show(aboutMeFragment);
                userProfile.setTextColor(ContextCompat.getColor(UserProfileActivity.this, R.color.colorCenter));
                break;
        }

        transaction.commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (FLAG_FRAGMENT == 1) getSupportFragmentManager().putFragment(outState, "fragment", aboutMeFragment);
        else getSupportFragmentManager().putFragment(outState, "fragment", hisFragment);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }
        return true;
    }

    private void setDefaultFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();

        if (FLAG_FRAGMENT == 0) {
            history.setTextColor(ContextCompat.getColor(UserProfileActivity.this, R.color.colorCenter));
            hisFragment = HistoryFragment.newInstance(objectId);
            transaction.add(R.id.fl_profile, hisFragment);
            transaction.commit();
            FLAG_FRAGMENT = 0;
        } else {
            userProfile.setTextColor(ContextCompat.getColor(UserProfileActivity.this, R.color.colorCenter));
            aboutMeFragment = new AboutMeFragment();
            transaction.add(R.id.fl_profile, aboutMeFragment);
            transaction.commit();
            FLAG_FRAGMENT = 1;
        }
    }

    private void hideFragment(FragmentTransaction transaction) {
        if (hisFragment != null) transaction.hide(hisFragment);
        if (aboutMeFragment != null) transaction.hide(aboutMeFragment);
    }
}
