package com.example.andrew.helpfind;

import android.app.Application;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.example.andrew.helpfind.entity.StaticData;

import java.util.List;

/**
 * Created by Andrew on 2017/8/29.
 */

public class LeanCloudApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

//        EaseUI的逻辑
//        EaseUI.getInstance().init(this,null);
//        EMClient.getInstance().setDebugMode(true);

        // 初始化参数依次为 this, AppId, AppKey
        AVOSCloud.initialize(this,"eD0HOw4NQN1Wz29Nq7sa1rz6-gzGzoHsz","eWJEUjTyMoYASze32sN1SBuk");
        AVOSCloud.setDebugLogEnabled(true);



        if (AVUser.getCurrentUser() != null) {
            // get current user
            StaticData.setCurrentUser(AVUser.getCurrentUser());
            AVQuery<AVObject> query = new AVQuery<>("UserProfile");
            query.whereEqualTo("user", AVObject.createWithoutData("_User", AVUser.getCurrentUser().getObjectId()));
            query.findInBackground(new FindCallback<AVObject>() {
                @Override
                public void done(List<AVObject> list, AVException e) {
                    StaticData.setUserProfileId(list.get(0).getObjectId());
                }
            });
        }

    }
}
