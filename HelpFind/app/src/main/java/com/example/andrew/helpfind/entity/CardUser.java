package com.example.andrew.helpfind.entity;

import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by zhouming on 2017/6/21.
 */

public class CardUser {
    private String id;
    private String userName;
    private String title;
    private String info;
    private String userPhotoUrl;
    private String infoImgUrl;
    private String address;
    private String date;

    private AVGeoPoint geo;

    public CardUser() {}

    public CardUser(String id, String _userName, String title, String _info,
                    String userPhotoUrl, String infoImgUrl) {
        this.id = id;
        this.userName = _userName;
        this.title = title;
        this.info = _info;
        this.userPhotoUrl = userPhotoUrl;
        this.infoImgUrl = infoImgUrl;
    }

    public CardUser(String id, String title, String info) {
        this.id = id;
        this.title = title;
        this.info = info;
    }

    public static CardUser newInstanceFull(AVObject avObject) {
        AVObject userProfile = avObject.getAVObject("user");
        AVUser user = userProfile.getAVUser("user");

        String infoImgUrl = null;
        if (avObject.getAVFile("img") != null) infoImgUrl = avObject.getAVFile("img").getUrl();
        String userImgUrl = null;
        if (userProfile.getAVFile("photo") != null) userImgUrl = userProfile.getAVFile("photo").getUrl();

        CardUser item = new CardUser();
        item.id = avObject.getObjectId();
        item.userName = user.getString("username");
        item.userPhotoUrl = userImgUrl;
        item.title = avObject.getString("title");
        item.info = avObject.getString("describe");
        item.infoImgUrl = infoImgUrl;
        item.address = avObject.getString("address");

        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        item.date = df.format(avObject.getDate("time"));

        return item;
    }

    public static CardUser newInstance(AVObject avObject) {
        CardUser item = new CardUser();
        item.id = avObject.getObjectId();
        item.title = avObject.getString("title");
        item.info = avObject.getString("describe");

        // 填充info图片
        AVFile file = avObject.getAVFile("img");
        if (file != null) item.infoImgUrl = file.getUrl();

        item.geo = avObject.getAVGeoPoint("geo");
        return item;
    }

    public String get_userName() {
        return userName;
    }

    public String get_info() {
        return info;
    }

    public String get_userPhotoURL() {
        return userPhotoUrl;
    }

    public String get_id() {
        return id;
    }

    public String get_title() {
        return title;
    }

    public String get_infoImgUrl() {
        return infoImgUrl;
    }

    public double getLat() {
        return geo.getLatitude();
    }

    public double getLon() {
        return geo.getLongitude();
    }

    public String getAddress() { return address; }

    public String getDateStringLike() { return date; }
}
