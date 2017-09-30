package com.example.andrew.helpfind;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.example.andrew.helpfind.entity.CardUser;
import com.example.andrew.helpfind.entity.StaticData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Andrew on 2017/7/2.
 */

public class MapActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * 需要进行检测的权限数组
     */
    protected String[] needPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    };

    private static final int PERMISSON_REQUESTCODE = 0;

    /**
     * 判断是否需要检测，防止不停的弹框
     */
    private boolean isNeedCheck = true;

    @BindView(R.id.btn_person_center)
    ImageView btn_person_center;
    @BindView(R.id.btn_add_marker)
    ImageView btn_add_marker;
    @BindView(R.id.check)
    ImageView check;
    @BindView(R.id.collection)
    ImageView collection;
    @BindView(R.id.id_bmapView)
    MapView mMapView;
    @BindView(R.id.rg_map_mode)
    RadioGroup modeSwitcher;
    @BindView(R.id.rb_sate)
    RadioButton modeSatellite;
    @BindView(R.id.rb_normal)
    RadioButton modeNormal;
    @BindView(R.id.rb_night)
    RadioButton modeNight;

    private AMap mMap;
    public AMapLocationClientOption mLocationOption = null;
    private AMapLocationClient mLocationClient = null;

    private ArrayList<CardUser> things;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_map);

        ButterKnife.bind(this);

        mMapView.onCreate(savedInstanceState);

        initView();
        initData();
    }

    private void initView() {

        // initialize map controller
        if (mMap == null) mMap = mMapView.getMap();

        // setting location style
        MyLocationStyle myLocationStyle = new MyLocationStyle();

        myLocationStyle.interval(2000);  //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
        myLocationStyle.strokeColor(Color.BLACK);  // 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(100, 0, 0, 180));// 设置圆形的填充颜色
        myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细

        mMap.setMyLocationStyle(myLocationStyle);
        mMap.getUiSettings().setMyLocationButtonEnabled(true); // 设置默认定位按钮是否显示，非必需设置。
        mMap.setMyLocationEnabled(true); // 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        mMap.setMapType(AMap.MAP_TYPE_NORMAL); //有普通，卫星，夜间模式

        // setting location
        mLocationClient = new AMapLocationClient(getApplicationContext());
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationClient.setLocationOption(mLocationOption);

        // 设置定位回调监听
        AMapLocationListener mAMapLocationListener = new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation amapLocation) {
                if (amapLocation != null) {
                    if (amapLocation.getErrorCode() == 0) {
                        StaticData.updateGeoPoint(amapLocation, null);
                    } else {
                        Toast.makeText(MapActivity.this, "AmapError: location Error, ErrCode:"
                                + amapLocation.getErrorCode() + ", errInfo:"
                                + amapLocation.getErrorInfo(), Toast.LENGTH_SHORT).show();

                    }
                }
            }
        };

        // location listener setting
        mLocationClient.setLocationListener(mAMapLocationListener);

        // other click event settings
        btn_person_center.setOnClickListener(this);
        btn_add_marker.setOnClickListener(this);
        check.setOnClickListener(this);
        collection.setOnClickListener(this);

        modeSwitcher.check(modeNormal.getId()); // check normal mode

        // setting mode switcher change listener: normal, night, satellite
        modeSwitcher.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                if (checkedId == modeNormal.getId()) {
                    mMap.setMapType(AMap.MAP_TYPE_NORMAL); //有普通，卫星，夜间模式
                } else if (checkedId == modeNight.getId()) {
                    mMap.setMapType(AMap.MAP_TYPE_NIGHT);
                } else if (checkedId == modeSatellite.getId()) {
                    mMap.setMapType(AMap.MAP_TYPE_SATELLITE);
                }
            }
        });

        // locate to current location
        mLocationClient.startLocation();

    }

    private void initData() {
        // get latitude and longitude
        things = new ArrayList<>();
        AVQuery<AVObject> query = new AVQuery<>("Notice");
        query.selectKeys(Arrays.asList("objectId", "title", "address","geo","time","describe","latitude","longitude","img"));
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                for( AVObject avcpj : list) things.add(CardUser.newInstance(avcpj));
                Toast.makeText(MapActivity.this, String.format("获取到%d个数据", list.size()), Toast.LENGTH_SHORT).show();
                drawData();
            }
        });
//        mMap.setInfoWindowAdapter(new CustomInfoAdapter(MapActivity.this, R.layout.layout_infowindow));
        mMap.setOnInfoWindowClickListener(new AMap.OnInfoWindowClickListener(){

            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent = new Intent(MapActivity.this, CardDetailActivity.class);
                intent.putExtra(CardDetailActivity.INFO_ID, marker.getSnippet().split(";")[2]);
                startActivity(intent);
            }
        });
    }

    /**
     * Draw marker by using data in things
     */
    private void drawData() {
        //TODO: using things draw
        for (CardUser ele: things) {
            LatLng latLng = new LatLng(ele.getLat(), ele.getLon());
            // set marker with animation
            String info = ele.get_info();
            if (info != null && info.length() > 20) info = info.substring(0, 20);
            mMap.addMarker(new MarkerOptions().position(latLng)
                    .title(ele.get_title()).snippet(info + ";" + ele.get_userPhotoURL() + ";" + ele.get_id()));
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.btn_person_center:
                Intent intent = new Intent(MapActivity.this, MainActivity.class);
                intent.putExtra(MainActivity.FRAGMENT_ID, 3);
                startActivity(intent);
                finish();
                break;
            case R.id.check:
                break;
            case R.id.collection:
                break;
            case R.id.btn_add_marker:
                break;

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
        if(isNeedCheck){
            checkPermissions(needPermissions);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     *
     * @since 2.5.0
     * requestPermissions方法是请求某一权限，
     */
    private void checkPermissions(String... permissions) {
        List<String> needRequestPermissonList = findDeniedPermissions(permissions);
        if (null != needRequestPermissonList
                && needRequestPermissonList.size() > 0) {
            ActivityCompat.requestPermissions(this,
                    needRequestPermissonList.toArray(
                            new String[needRequestPermissonList.size()]),
                    PERMISSON_REQUESTCODE);
        }
    }

    /**
     * 获取权限集中需要申请权限的列表
     *
     * @param permissions
     * @return
     * @since 2.5.0
     * checkSelfPermission方法是在用来判断是否app已经获取到某一个权限
     * shouldShowRequestPermissionRationale方法用来判断是否
     * 显示申请权限对话框，如果同意了或者不在询问则返回false
     */
    private List<String> findDeniedPermissions(String[] permissions) {
        List<String> needRequestPermissonList = new ArrayList<String>();
        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(this,
                    perm) != PackageManager.PERMISSION_GRANTED) {
                needRequestPermissonList.add(perm);
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this, perm)) {
                    needRequestPermissonList.add(perm);
                }
            }
        }
        return needRequestPermissonList;
    }

    /**
     * 检测是否所有的权限都已经授权
     * @param grantResults
     * @return
     * @since 2.5.0
     *
     */
    private boolean verifyPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 申请权限结果的回调方法
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] paramArrayOfInt) {
        if (requestCode == PERMISSON_REQUESTCODE) {
            if (!verifyPermissions(paramArrayOfInt)) {
                showMissingPermissionDialog();
                isNeedCheck = false;
            }
        }
    }

    /**
     * 显示提示信息
     *
     * @since 2.5.0
     *
     */
    private void showMissingPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("当前应用缺少必要权限。请点击\"设置\"-\"权限\"-打开所需权限。");

        // 拒绝, 退出应用
        builder.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

        builder.setPositiveButton("设置",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startAppSettings();
                    }
                });

        builder.setCancelable(false);

        builder.show();
    }

    /**
     *  启动应用的设置
     *
     * @since 2.5.0
     *
     */
    private void startAppSettings() {
        Intent intent = new Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
