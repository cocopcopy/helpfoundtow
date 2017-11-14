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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVCloudQueryResult;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.CloudQueryCallback;
import com.avos.avoscloud.DeleteCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.andrew.helpfind.entity.StaticData;

import java.util.List;

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
    private String entrance;
    private  boolean focus_status=false;
    private AVUser currentUser = AVUser.getCurrentUser();

    private Button btn_infodetail_certain;
    private  ImageView btn_focus_on;
    private  TextView question_view;
    private  TextView verification_question;
    private  String focus_objectId;

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
        entrance= extract.getStringExtra("entrance");
        btn_infodetail_certain=(Button) findViewById(R.id.btn_InfoDetail_certain);
        btn_focus_on=(ImageView) findViewById(R.id.btn_focus_on);
        question_view=(TextView) findViewById(R.id.question_view);
        verification_question=(TextView) findViewById(R.id.verification_quesion);



        //关注
        initfocus();
        //确认按钮
        initcertain();
        //验证问题
        initquestion();


        init();

        userPhoto.setOnClickListener(this);
        callButton.setOnClickListener(this);
    }
    public  void initquestion(){
        //从主列表寻找失主进入则验证问题可见
        if(entrance.equals("main")){
            AVObject todo = AVObject.createWithoutData("Notice", mId);
            todo.fetchInBackground(new GetCallback<AVObject>() {
                @Override
                public void done(AVObject avObject, AVException e) {
                    if(avObject.getString("tag").equals("Found")){
                        question_view.setVisibility(View.VISIBLE);
                        verification_question.setText(avObject.getString("question"));
                        verification_question.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

    }

    public  void  initfocus(){
        //从主列表和我的关注进入则显示关注按钮
        if(entrance.equals("main")||entrance.equals("Focus")){
            btn_focus_on.setVisibility(View.VISIBLE);
        }
        else return;


        //确认物品的关注状态
        if(currentUser!=null) {
            AVQuery<AVObject> query = new AVQuery<>("Focus");
            query.whereEqualTo("notice", AVObject.createWithoutData("Notice", mId));
            query.whereEqualTo("user", currentUser);
            query.include("notice");
            query.findInBackground(new FindCallback<AVObject>() {
                @Override
                public void done(List<AVObject> list, AVException e) {
                    if (e == null && list.size() > 0) {
                        focus_objectId = list.get(0).getObjectId();
                        btn_focus_on.setImageResource(R.drawable.focus_in);
                        focus_status = true;
                    }

                }
            });
        }
        //关注按钮点击事件
        btn_focus_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //判断登录状态
                if(currentUser==null){
                    Intent intent = new Intent(CardDetailActivity.this, LoginActivity.class);
                    startActivity(intent);
                    return;
                }


                //添加关注
                if (!focus_status) {
                    //创建Focus对象
                    final AVObject focus = new AVObject("Focus");
                    focus.put("notice", AVObject.createWithoutData("Notice", mId));
                    focus.put("user", currentUser);
                    focus.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e != null) {
                                Toast.makeText(getApplicationContext(), "关注失败",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "已关注",
                                        Toast.LENGTH_SHORT).show();
                                btn_focus_on.setImageResource(R.drawable.focus_in);
                                focus_status = true;
                                focus_objectId = focus.getObjectId();
                            }
                        }
                    });

                }

                //取消关注
                else {

                    AVObject focus = AVObject.createWithoutData("Focus", focus_objectId);
                    focus.deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e != null) {
                                Toast.makeText(getApplicationContext(), "取消失败",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "取消关注",
                                        Toast.LENGTH_SHORT).show();
                                btn_focus_on.setImageResource(R.drawable.focus_out);
                                focus_status = false;
                            }
                        }
                    });

                }

            }
        });

    }

    public void initcertain(){
        //从我的失物或我的拾物进入
        if(entrance.equals("Lost")||entrance.equals("Found")){
            btn_infodetail_certain.setVisibility(View.VISIBLE);
            if(entrance.equals("Lost")) btn_infodetail_certain.setText("确认找回");
            else  btn_infodetail_certain.setText("确认归还");

            //按钮点击后确认找回
            btn_infodetail_certain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final AVObject notice = AVObject.createWithoutData("Notice", mId);

                    // 修改 content
                    notice.put("status","out");

                    // 保存到云端
                    notice.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e!=null){
                                Toast.makeText(getApplicationContext(), "确认失败",
                                        Toast.LENGTH_SHORT).show();
                            }
                            else{
                                AVQuery<AVObject> avQuery = new AVQuery<>("Notice");
                                avQuery.include("user");
                                avQuery.getInBackground(mId, new GetCallback<AVObject>() {
                                    @Override
                                    public void done(AVObject avObject, AVException e) {
                                        if(avObject.getBoolean("findowner")) {
                                            final int conSore=currentUser.getInt("contributionScore")+10;
                                            currentUser.put("contributionScore",conSore);
                                            currentUser.saveInBackground(new SaveCallback() {
                                                @Override
                                                public void done(AVException e) {
                                                    if(e!=null) Toast.makeText(getApplicationContext(), "获取分数失败", Toast.LENGTH_SHORT).show();
                                                    else Toast.makeText(getApplicationContext(), "目前贡献分为:"+conSore, Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                        else
                                            Toast.makeText(getApplicationContext(), "没有用户确认！",
                                                    Toast.LENGTH_SHORT).show();
                                    }
                                });


                                Toast.makeText(getApplicationContext(), "确认成功",
                                        Toast.LENGTH_SHORT).show();
                                btn_infodetail_certain.setText("已确认");
                                btn_infodetail_certain.setEnabled(false);
                            }
                        }
                    });
                }
            });
        }
        //从我的关注进入
        else if(entrance.equals("Focus")){
            btn_infodetail_certain.setVisibility(View.VISIBLE);
            btn_infodetail_certain.setText("确认完成");
            btn_infodetail_certain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AVObject notice = AVObject.createWithoutData("Notice", mId);
                    notice.put("findowner",true);
                    notice.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e!=null){
                                Toast.makeText(getApplicationContext(), "确认失败",
                                        Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "确认成功",
                                        Toast.LENGTH_SHORT).show();
                                AVObject focus = AVObject.createWithoutData("Focus", focus_objectId);
                                focus.deleteInBackground(new DeleteCallback() {
                                    @Override
                                    public void done(AVException e) {
                                        if (e != null) {
                                            Toast.makeText(getApplicationContext(), "取消失败",
                                                    Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "取消关注",
                                                    Toast.LENGTH_SHORT).show();
                                            btn_focus_on.setImageResource(R.drawable.focus_out);
                                            focus_status = false;
                                        }
                                    }
                                });
                                btn_infodetail_certain.setText("已确认");
                                btn_infodetail_certain.setEnabled(false);
                            }
                        }
                    });
                }
            });
        }

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
