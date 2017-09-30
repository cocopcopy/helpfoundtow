package com.example.andrew.helpfind.entity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.Marker;
import com.bumptech.glide.Glide;
import com.example.andrew.helpfind.R;

/**
 * Created by zhouming on 2017/7/5.
 */

public class CustomInfoAdapter implements AMap.InfoWindowAdapter {

    private View infoWindow = null;
    private Context mContext;
    private int layoutResID;

    public CustomInfoAdapter(Context mContext, int layoutResID) {
        this.mContext = mContext;
        this.layoutResID = layoutResID;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        if (infoWindow == null) {
            infoWindow = LayoutInflater.from(mContext).inflate(layoutResID, null);
        }
        render(marker, infoWindow);
        return infoWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    private void render(Marker marker, View view) {
//        TextView title = (TextView) view.findViewById(R.id.tv_title);
//        TextView content = (TextView) view.findViewById(R.id.tv_describe);
//        ImageView image = (ImageView) view.findViewById(R.id.iv_object);

//        title.setText(marker.getTitle());
//        String[] temp = marker.getSnippet().split(";");
//        if (temp[0].length() > 20) content.setText(temp[0].substring(0, 20) + "...");
//        Glide.with(mContext).load(temp[1]).override(200, 200).into(image);
    }
}
