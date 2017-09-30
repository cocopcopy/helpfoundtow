package com.example.andrew.helpfind.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andrew.helpfind.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhouming on 2017/7/7.
 */

public class AboutMeFragment extends Fragment implements View.OnClickListener{

    @BindView(R.id.tv_profile_email)
    TextView emailTo;
    @BindView(R.id.tv_profile_phone)
    TextView callTo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_about_me, container, false);
        ButterKnife.bind(this, v);
        initView();
        return v;
    }

    private void initView() {
        emailTo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_profile_email:
                Toast.makeText(getActivity(), "Email cannot use", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_profile_phone:
                Toast.makeText(getActivity(), "Phone call cannot use", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
