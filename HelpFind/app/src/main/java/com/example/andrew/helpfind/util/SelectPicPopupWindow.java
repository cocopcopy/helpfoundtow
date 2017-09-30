package com.example.andrew.helpfind.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;

import com.example.andrew.helpfind.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by liuyibo on 2017/6/27.
 */

public class SelectPicPopupWindow extends PopupWindow {
    private Button btnFromAlbum;
    private Button btnFromCamera;
    private Button btnCancel;

    private View popView;
    private Context mContext;

    public SelectPicPopupWindow(Context context) {
        super(context);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        popView = inflater.inflate(R.layout.window_selectpic, null);

        mContext = context;

        btnCancel = (Button) popView.findViewById(R.id.btn_pop_cancel);
        btnFromAlbum = (Button) popView.findViewById(R.id.btn_pop_album);
        btnFromCamera = (Button) popView.findViewById(R.id.btn_pop_camera);

        int width = context.getResources().getDisplayMetrics().widthPixels;
        int height = context.getResources().getDisplayMetrics().heightPixels;

        this.setContentView(popView);
        this.setWidth(width);
        this.setHeight(height);

        this.setAnimationStyle(R.style.Widget_AppCompat_Light_PopupMenu);
        this.setFocusable(true);


    }

    public void setClick(View.OnClickListener itemsOnClick) {
        btnFromAlbum.setOnClickListener(itemsOnClick);
        btnFromCamera.setOnClickListener(itemsOnClick);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
