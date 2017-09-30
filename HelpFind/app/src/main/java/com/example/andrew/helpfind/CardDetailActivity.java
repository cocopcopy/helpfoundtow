package com.example.andrew.helpfind;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhouming on 2017/6/27.
 * 该活动
 */

public class CardDetailActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String META_INFO_DETIAL = "InfoTag";
    public static final String INFO_ID = "InfoID";

    @BindView(R.id.iv_infodetail_user)
    ImageView userPhoto;
    @BindView(R.id.tv_infodetail_title)
    TextView title;
    @BindView(R.id.tv_infodetail_info)
    TextView info;
    @BindView(R.id.toolbar_card) Toolbar toolbar;
    @BindView(R.id.iv_infodetail_call)
    ImageView callButton;
    @BindView(R.id.iv_infodetail)
    ImageView infoImg;
    @BindView(R.id.tv_infodetail_address) TextView addressName;

    private String mId;
    private String userProfileId;
    private String userName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
        localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);

        setContentView(R.layout.activity_carddetail);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }


        userPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: link to user's info page

            }
        });

        Intent extract = getIntent();
        mId = extract.getStringExtra(INFO_ID);

        init();

        userPhoto.setOnClickListener(this);
        callButton.setOnClickListener(this);
    }

    public void init() {
        AVQuery<AVObject> query = new AVQuery<>("Notice");
        query.include("user");
        query.include("user.user");
        query.getInBackground(mId, new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                // get current instance
                AVFile img = avObject.getAVFile("img");
                AVObject userProfile = avObject.getAVObject("user");
                AVUser user = userProfile.getAVUser("user");
                AVFile photo =  userProfile.getAVFile("photo");

                userProfileId = userProfile.getObjectId();
                userName = userProfile.getString("userName");
                // album
                String infoImgUrl = null;
                if (img != null) infoImgUrl = img.getUrl();

                // user photo
                if (photo != null) {
                    Glide.with(getBaseContext()).load(photo.getUrl()).asBitmap().centerCrop()
                            .into(new BitmapImageViewTarget(userPhoto) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    RoundedBitmapDrawable circularBitmapDrawable =
                                            RoundedBitmapDrawableFactory.create(getResources(), resource);
                                    circularBitmapDrawable.setCircular(true);
                                    userPhoto.setImageDrawable(circularBitmapDrawable);
                                }
                            });
                }

                // setting grid style for album
                if (infoImgUrl != null) Glide.with(getBaseContext()).load(infoImgUrl).centerCrop().into(infoImg);

                title.setText(avObject.getString("title"));
                info.setText(avObject.getString("describe"));
                addressName.setText(avObject.getString("address"));
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_infodetail_user:
                // switch to user profile
                lunchUserProfile(userName, userProfileId);
                break;
            case R.id.iv_infodetail_call:
//                联系方式这里改成直接呼叫手机？
                Intent intent=new Intent(CardDetailActivity.this, ChatActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }
        return true;
    }

    private void lunchUserProfile(String username, String userId) {
        Intent intent = new Intent(CardDetailActivity.this, UserProfileActivity.class);
        intent.putExtra(UserProfileActivity.USER_FLAG, username);
        intent.putExtra(UserProfileActivity.USER_PROFILEID, userId);
        startActivity(intent);
    }
}
