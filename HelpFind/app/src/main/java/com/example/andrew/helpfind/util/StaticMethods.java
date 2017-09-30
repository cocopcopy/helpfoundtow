package com.example.andrew.helpfind.util;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.andrew.helpfind.R;
import com.example.andrew.helpfind.entity.StaticData;

import java.io.ByteArrayOutputStream;

/**
 * Created by zhouming on 2017/6/21.
 */

public class StaticMethods {
    public static final int SELECT_PIC_BY_TACK_PHOTO = 10;
    public static final int SELECT_PIC_BY_PICK_PHOTO = 11;

    private static Uri absoluteURL;

    public static boolean lackPermissions(Context mContext, String... permissions) {
        for (String permission: permissions) {
            if (lackPermission(mContext, permission)) {
                return true;
            }
        }
        return false;
    }

    public static boolean lackPermission(Context mContext, String permission) {
        return ContextCompat.checkSelfPermission(mContext, permission) == PackageManager.PERMISSION_DENIED;
    }

    public static void settingPhoto(int resourceId, final Activity context, final Fragment fragContext) {
        final SelectPicPopupWindow menuWindow = new SelectPicPopupWindow(fragContext.getContext());

        menuWindow.setClick(new View.OnClickListener() {
            //照片路径选择
            public void onClick(View v) {
                if (v.getId() == R.id.btn_pop_camera) {//拍照
                    takePhoto(menuWindow, fragContext);
                } else if (v.getId() == R.id.btn_pop_album) {
                    pickPhoto(menuWindow, fragContext);
                }
            }
        });

        menuWindow.showAtLocation(context.findViewById(resourceId), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    //拍照片
    private static void takePhoto(SelectPicPopupWindow menuWindow, Fragment fragContext) {
        String SDState = Environment.getExternalStorageState();
        if (SDState.equals(Environment.MEDIA_MOUNTED)) {

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            ContentValues contentValues = new ContentValues();
            absoluteURL = fragContext.getContext().getContentResolver().insert(MediaStore.Images.Media
                    .EXTERNAL_CONTENT_URI, contentValues);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, absoluteURL);
            fragContext.startActivityForResult(intent, SELECT_PIC_BY_TACK_PHOTO);
        } else {
            Toast.makeText(fragContext.getContext(), "内存条不存在", Toast.LENGTH_LONG).show();
        }
        menuWindow.dismiss();
    }

    //从相册中选取
    private static void pickPhoto(SelectPicPopupWindow menuWindow, Fragment fragContext) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        fragContext.startActivityForResult(intent, SELECT_PIC_BY_PICK_PHOTO);
        menuWindow.dismiss();
    }

    //照片加载到界面
    public static void doPhoto(Context context, ImageView img, int requestCode, Intent data, Runnable callback) {
        Uri photoUri = null;
        Bitmap bm = null;

        if (requestCode == SELECT_PIC_BY_PICK_PHOTO) {

            if (data == null) {
                Toast.makeText(context, "选择图片文件出错", Toast.LENGTH_LONG).show();
                return;
            }

            photoUri = data.getData();

            if (photoUri == null) {
                Toast.makeText(context, "选择图片文件出错", Toast.LENGTH_LONG).show();
                return;
            } else {
                Glide.with(context.getApplicationContext()).load(photoUri).override(200, 200).centerCrop().into(img);
                try {
                    bm = MediaStore.Images.Media.getBitmap(context.getContentResolver(), photoUri);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == SELECT_PIC_BY_TACK_PHOTO) {
            String[] poJo = {MediaStore.Images.Media.DATA};
            String picPath = null;
            Cursor cursor = context.getContentResolver().query(absoluteURL, poJo, null, null, null);
            if (cursor != null) {
                int columnIndex = cursor.getColumnIndexOrThrow(poJo[0]);
                cursor.moveToFirst();
                picPath = cursor.getString(columnIndex);
            }
            if (picPath != null && (picPath.endsWith(".png") ||
                    picPath.endsWith(".PNG") ||
                    picPath.endsWith(".jpg") ||
                    picPath.endsWith(".JPG"))) {
                BitmapFactory.Options option = new BitmapFactory.Options();
                option.inSampleSize = 15;
                bm = BitmapFactory.decodeFile(picPath, option);
                Log.d("photo", "upload1");
                img.setImageBitmap(bm);
                Log.d("photo", "upload2");
            }
        } else {
            Toast.makeText(context, "Unexcepted request code!", Toast.LENGTH_LONG).show();
            return;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        StaticData.mImageBytes = baos.toByteArray();
        callback.run();
    }
}
