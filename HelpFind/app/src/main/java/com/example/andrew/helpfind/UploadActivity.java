package com.example.andrew.helpfind;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.SaveCallback;
import com.bumptech.glide.Glide;
import com.example.andrew.helpfind.entity.StaticData;
import com.example.andrew.helpfind.util.SelectPicPopupWindow;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UploadActivity extends AppCompatActivity implements View.OnClickListener{

    @Nullable @BindView(R.id.et_notice_title)
    EditText title;

    @Nullable @BindView(R.id.et_notice_describe)
    EditText describe;
    @Nullable @BindView(R.id.et_notice_que)
    EditText question;
    @Nullable @BindView(R.id.et_notice_ans)
    EditText answer;
    @Nullable @BindView(R.id.spinner_tag)
    Spinner tagSelector;
    @Nullable @BindView(R.id.iv_upload)
    ImageView img;
    @Nullable @BindView(R.id.btn_upload)
    Button btnUpload;
    @Nullable @BindView(R.id.add_address)
    EditText addAddress;
    @Nullable @BindView(R.id.cur_address)
    Button curAddress;
    @Nullable @BindView(R.id.cur_textadd)
    TextView curTextadd;
    @Nullable @BindView(R.id.icon)
    ImageView locate_icon;


    private Uri photoUri;
    private String picPath;
    private Bitmap bm;
    private byte[] mImageBytes;

    private String tag;

    private SelectPicPopupWindow menuWindow;
    private static final int SELECT_PIC_BY_TACK_PHOTO = 0;
    private static final int SELECT_PIC_BY_PICK_PHOTO = 1;

    // 定义5个记录当前时间的变量
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private int second;
    private Date time = null;
    public AMapLocationClient mLocationClient = null;

    public AMapLocationClientOption mLocationOption = null;
    public double jingdu,weidu;
    String add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
        localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);

        setContentView(R.layout.activity_upload);
        ButterKnife.bind(this);
        //声明AMapLocationClient类对象

//声明定位回调监听器

//初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
//设置定位回调监听
        AMapLocationListener mAMapLocationListener = new AMapLocationListener(){
            @Override
            public void onLocationChanged(AMapLocation amapLocation) {
                if (amapLocation != null) {
                    if (amapLocation.getErrorCode() == 0) {
//可在其中解析amapLocation获取相应内容。
                        weidu = amapLocation.getLatitude();//获取纬度
                        jingdu = amapLocation.getLongitude();//获取经度
                        add = amapLocation.getAddress();
                    }else {
                        //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                        Log.e("AmapError","location Error, ErrCode:"
                                + amapLocation.getErrorCode() + ", errInfo:"
                                + amapLocation.getErrorInfo());
                    }
                }
            }
        };
        mLocationClient.setLocationListener(mAMapLocationListener);
        //声明AMapLocationClientOption对象

//初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
//获取一次定位结果：
//该方法默认为false。
        mLocationOption.setOnceLocation(true);

//获取最近3s内精度最高的一次定位结果：
//设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        mLocationOption.setOnceLocationLatest(true);
        mLocationOption.setNeedAddress(true);
        mLocationOption.setHttpTimeOut(20000);
        mLocationOption.setLocationCacheEnable(true);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
//启动定位
        mLocationClient.startLocation();

        initView();

        initData();
    }

    //时间初始化
    private void initView() {
        curTextadd.setText(add);
        img.setOnClickListener(this);
        btnUpload.setOnClickListener(this);
        curAddress.setOnClickListener(this);
        locate_icon.setOnClickListener(this);

        tagSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {

                String[] tags = getResources().getStringArray(R.array.tag);
                tag = tags[pos];
                if(tag.equals("Found")){
                    question.setVisibility(View.VISIBLE);
                    answer.setVisibility(View.VISIBLE);
                }
                else if(tag.equals("Lost")){
                    question.setVisibility(View.GONE);
                    answer.setVisibility(View.GONE);
                }

                Toast.makeText(UploadActivity.this, "你点击的是:" + tags[pos], Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });


    }

    private void initData() {
        //获得当前时间
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        hour = c.get(Calendar.HOUR);
        minute = c.get(Calendar.MINUTE);
        second = c.get(Calendar.SECOND);
        time = StringToDate();
    }

    //时间格式转化
    private Date StringToDate() {
        String t = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Log.d("time", t);
        try {
            time = sd.parse(t);
        } catch (java.text.ParseException e) {
            System.out.println("输入的日期格式有误！");
            e.printStackTrace();
        }
        return time;
    }

    //点击情况
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_upload:
                menuWindow = new SelectPicPopupWindow(UploadActivity.this);
                menuWindow.setClick(itemsOnClick);
                menuWindow.showAtLocation(findViewById(R.id.main),
                        Gravity.BOTTOM| Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.btn_upload:
                upLoad();
                break;
            case R.id.cur_address:
                curTextadd.setText(add);
                addAddress.setText(add);
                break;
            case R.id.icon:
                curTextadd.setText(add);
                break;
            default:
                break;
        }

    }

    //数据上传
    private void upLoad() {
        setInput();
        if (!isValid()) return;
        AVObject notice = new AVObject("Notice");
        notice.put("title", title.getText().toString());
        notice.put("describe", describe.getText().toString());
        notice.put("time", time);


        // notice.put("geo", new AVGeoPoint(30.78373 + Math.random() * 50, 103 + Math.random() * 50));
        notice.put("geo", new AVGeoPoint(weidu, jingdu));
        notice.put("user", AVObject.createWithoutData("UserProfile", StaticData.getUserProfileId()));
        notice.put("img", new AVFile("pic", mImageBytes));
        notice.put("question", question.getText().toString());
        notice.put("answer", answer.getText().toString());
        notice.put("tag", tag);

        // notice.put("address", searchButton.getText().toString());
        notice.put("address", addAddress.getText().toString());

        final ProgressDialog progressDialog = new ProgressDialog(UploadActivity.this,
                ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("正在上传...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        notice.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                progressDialog.dismiss();
                if (e == null) {
                    Toast.makeText(UploadActivity.this, "添加成功", Toast.LENGTH_LONG).show();
                    UploadActivity.this.finish();
                } else {
                    Toast.makeText(UploadActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean isValid() {
        String _title = title.getText().toString();
        String _describe = describe.getText().toString();
        String _question = question.getText().toString();
        String _answer = answer.getText().toString();

        if (_title.equals("")) {
            title.setError("信息标题不能为空！");
            return false;
        }
        if (_describe.equals("")) {
            describe.setError("描述信息不能为空！");
            return false;
        }
//        if (_question.equals("")) {
//            question.setError("请输入问题！");
//            return false;
//        }
//        if (_answer.equals("")) {
//            answer.setError("请输入答案！");
//            return false;
//        }
        return true;
    }

    //关闭键盘
    private void setInput() {
        final InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        Timer timer = new Timer();
        timer.schedule(new TimerTask(){
            @Override
            public void run() {
                inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }, 1000);
    }

    private View.OnClickListener itemsOnClick = new View.OnClickListener(){
        //照片路径选择
        public void onClick(View v) {
            //隐藏弹出窗口
            menuWindow.dismiss();
            switch (v.getId()) {
                case R.id.btn_pop_camera://拍照
                    takePhoto();
                    break;
                case R.id.btn_pop_album://从相册中选取
                    pickPhoto();
                    break;
                default:
                    break;
            }
        }
    };


    //拍照片
    private void takePhoto() {
        String SDState = Environment.getExternalStorageState();
        if (SDState.equals(Environment.MEDIA_MOUNTED)) {

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            ContentValues contentValues = new ContentValues();
            photoUri = getContentResolver().insert(MediaStore.Images.Media
                    .EXTERNAL_CONTENT_URI, contentValues);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(intent, SELECT_PIC_BY_TACK_PHOTO);

        } else {
            Toast.makeText(UploadActivity.this, "内存条不存在", Toast.LENGTH_LONG).show();
        }
    }

    //从相册中选取
    private void pickPhoto() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,SELECT_PIC_BY_PICK_PHOTO);
    }

    @Override
    //处理一下图片选取的页面回调
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        //点击取消按钮
        if(resultCode == RESULT_CANCELED) {
            return;
        }
        doPhoto(requestCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    //照片加载到界面
    private void doPhoto(int requestCode, Intent data) {
        //
        if (requestCode == SELECT_PIC_BY_PICK_PHOTO) {
            if (data == null) {
                Toast.makeText(this, "选择图片文件出错", Toast.LENGTH_LONG).show();
                return;
            }
            photoUri = data.getData();

            if (photoUri == null) {
                Toast.makeText(this, "选择图片文件出错", Toast.LENGTH_LONG).show();
                return;
            } else {
                Glide.with(getApplicationContext()).load(photoUri).override(600, 600).fitCenter().into(img);
                try {
                    bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == SELECT_PIC_BY_TACK_PHOTO) {
            String[] poJo = {MediaStore.Images.Media.DATA};
            Cursor cursor = this.getContentResolver().query(photoUri, poJo, null, null, null);
            if (cursor != null) {
                int columnIndex = cursor.getColumnIndexOrThrow(poJo[0]);
                cursor.moveToFirst();
                picPath = cursor.getString(columnIndex);// 4.0以上的版本 cursor 会自动关闭
            }
            if (picPath != null && (picPath.endsWith(".png") ||
                    picPath.endsWith(".PNG") ||
                    picPath.endsWith(".jpg") ||
                    picPath.endsWith(".JPG"))) {
                BitmapFactory.Options option = new BitmapFactory.Options();
                //压缩图片
                option.inSampleSize = 15;
                bm = BitmapFactory.decodeFile(picPath, option);
                img.setImageBitmap(bm);
            }
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        mImageBytes = baos.toByteArray();
    }
}