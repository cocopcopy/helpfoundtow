package com.example.andrew.helpfind.entity;

import com.amap.api.location.AMapLocation;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVUser;

/**
 * Created by Andrew on 2017/8/29.
 */

public class StaticData {
    private static AVGeoPoint geoPoint = new AVGeoPoint();
    private static String address;
    private static AVUser currentUser;
    private static String userImgLink;
    private static String userProfileId;

    public static byte[] mImageBytes;

    public static void updateGeoPoint(AMapLocation location, Runnable callback) {
        geoPoint.setLatitude(location.getLatitude());
        geoPoint.setLatitude(location.getLongitude());
        address = location.getAddress();
        if (callback != null) callback.run();
    }

    public static void setCurrentUser(AVUser usr) {
        currentUser = usr;
    }

    public static AVUser getCurrentUser() {
        return currentUser;
    }

    public static String getUserImgLink() {
        return userImgLink;
    }

    public static void setUserImgLink(String url) {
        userImgLink = url;
    }

    public static void setUserProfileId(String id) {
        userProfileId = id;
    }

    public static String getUserProfileId() {
        return userProfileId;
    }
}
