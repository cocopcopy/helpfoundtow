package com.example.andrew.helpfind.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.andrew.helpfind.LoginActivity;
import com.example.andrew.helpfind.MyCenterActivity;
import com.example.andrew.helpfind.R;
import com.example.andrew.helpfind.entity.StaticData;
import com.example.andrew.helpfind.util.StaticMethods;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_CANCELED;


public class MyCenterFragment extends Fragment implements View.OnClickListener {

	@BindView(R.id.cpj_login) TextView cpj_login;
	@BindView(R.id.cpj_now_lost) TextView cpj_now_lost;
	@BindView(R.id.cpj_focus) TextView cpj_focus;
	@BindView(R.id.cpj_now_found) TextView cpj_now_found;
	@BindView(R.id.cpj_ever_lost) TextView cpj_ever_lost;
	@BindView(R.id.cpj_ever_found) TextView cpj_ever_found;
	@BindView(R.id.cpj_contribution) TextView cpj_contribution;
	@BindView(R.id.cpj_friends) TextView cpj_friends;
	@BindView(R.id.cpj_given_thanks) TextView cpj_given_thanks;
	@BindView(R.id.cpj_receive_thanks) TextView cpj_receive_thanks;
	@BindView(R.id.cpj_settings) TextView cpj_settings;
	@BindView(R.id.cpj_more) TextView cpj_more;
	@BindView(R.id.cpj_logout) TextView cpj_logout;
	@BindView(R.id.index_my_list1_touxiang) ImageView cpj_me;
	@BindView(R.id.cpj_username) TextView cpj_username;
	@BindView(R.id.cpj_contribution_number) TextView cpj_contribution_number;

	private int RESULT_LOAD_IMAGE = 0;
	private int RESULT_CAMERA_IMAGE = 1;
	private String mCurrentPhotoPath;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.index_my, container, false);
		ButterKnife.bind(this, view);
		initView();

		cpj_contribution_number.setText(""+AVUser.getCurrentUser().getInt("contributionScore"));
		return view;
	}

	private void initView() {
		// TODO Auto-generated method stub

		if (AVUser.getCurrentUser() != null) {
			resetToLogin();
			if (StaticData.getUserImgLink() != null)
				Glide.with(getActivity()).load(StaticData.getUserImgLink()).asBitmap()
				.centerCrop().into(new BitmapImageViewTarget(cpj_me) {
					@Override
					protected void setResource(Bitmap resource) {
						RoundedBitmapDrawable circularBitmapDrawable =
								RoundedBitmapDrawableFactory.create(getResources(), resource);
						circularBitmapDrawable.setCircular(true);
						cpj_me.setImageDrawable(circularBitmapDrawable);
					}
				});
		} else {
			resetToLogout();
		}

		cpj_login.setOnClickListener(this);
		cpj_now_lost.setOnClickListener(this);
		cpj_focus.setOnClickListener(this);
		cpj_now_found.setOnClickListener(this);
		cpj_ever_lost.setOnClickListener(this);
		cpj_ever_found.setOnClickListener(this);
		cpj_contribution.setOnClickListener(this);
		cpj_friends.setOnClickListener(this);
		cpj_given_thanks.setOnClickListener(this);
		cpj_receive_thanks.setOnClickListener(this);
		cpj_settings.setOnClickListener(this);
		cpj_more.setOnClickListener(this);
		cpj_logout.setOnClickListener(this);
		cpj_me.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.index_my_list1_touxiang:
				// 设置用户头像，首先动态确定权限
				StaticMethods.settingPhoto(R.id.center_tool, getActivity(), this);
				break;
			case R.id.cpj_login:
				login();
				break;
			case R.id.cpj_now_lost:
				launchActivity(MyCenterActivity.NOW_LOST);
				break;
			case R.id.cpj_focus:
				launchActivity(MyCenterActivity.FOCUS);
				break;
			case R.id.cpj_now_found:
				launchActivity(MyCenterActivity.NOW_FIND);
				break;
			case R.id.cpj_ever_lost:
				launchActivity(MyCenterActivity.EVER_LOST);
				break;
			case R.id.cpj_ever_found:
				launchActivity(MyCenterActivity.EVER_FIND);
				break;
			case R.id.cpj_contribution:
				Toast.makeText(getActivity(), "Contribution", Toast.LENGTH_SHORT).show();
				break;
			case R.id.cpj_receive_thanks:
				Toast.makeText(getActivity(), "Thanks", Toast.LENGTH_SHORT).show();
				break;
			case R.id.cpj_given_thanks:
				Toast.makeText(getActivity(), "Gives", Toast.LENGTH_SHORT).show();
				break;
			case R.id.cpj_settings:
				Toast.makeText(getActivity(), "Settings", Toast.LENGTH_SHORT).show();
				break;
			case R.id.cpj_more:
				Toast.makeText(getActivity(), "More", Toast.LENGTH_SHORT).show();
				break;
			case R.id.cpj_logout:
				logout();
			default:
				break;
		}
	}


	/**
	 * Launch different activity with different keyword
	 * @param keyWord
     */
	private void launchActivity(String keyWord) {
		if (AVUser.getCurrentUser() == null) {
			Intent intent = new Intent(getActivity(), LoginActivity.class);
			startActivity(intent);
		} else {
			Intent intent = new Intent(getActivity(), MyCenterActivity.class);
			intent.putExtra(MyCenterActivity.KEYWORD, keyWord);
			startActivity(intent);
		}
	}

	private Handler handler= new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 1:
					AVUser.logOut();
					resetToLogout();
					break;
				default:
					break;
			}
		}
	};

	private void logout() {
		if (AVUser.getCurrentUser() == null) return;
//		EMClient.getInstance().logout(false, new EMCallBack() {
//
//			@Override
//			public void onSuccess() {
				Message message = new Message();
				message.what = 1;
				handler.sendMessage(message);
//			}
//
//			@Override
//			public void onProgress(int progress, String status) {
//
//			}
//
//			@Override
//			public void onError(int code, String error) {
//
//			}
//		});

	}

	private void login() {
		if (AVUser.getCurrentUser() != null) return;
		Intent intent = new Intent(getActivity(), LoginActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	private void resetToLogout() {
		cpj_login.setVisibility(View.VISIBLE);
		cpj_username.setVisibility(View.GONE);
		cpj_logout.setVisibility(View.GONE);
		cpj_me.setImageResource(R.drawable.me);
	}

	private void resetToLogin() {
		cpj_login.setVisibility(View.GONE);
		cpj_username.setVisibility(View.VISIBLE);
		cpj_username.setText(AVUser.getCurrentUser().getUsername());
		cpj_logout.setVisibility(View.VISIBLE);
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == RESULT_CANCELED) {
			return;
		}

		StaticMethods.doPhoto(getContext(), cpj_me, requestCode, data, new Runnable() {
			@Override
			public void run() {
				final ProgressDialog progressDialog = new ProgressDialog(getActivity(),
						ProgressDialog.STYLE_SPINNER);
				progressDialog.setIndeterminate(true);
				progressDialog.setMessage("正在上传...");
				progressDialog.setCanceledOnTouchOutside(false);
				progressDialog.show();

				// update data
				AVObject userProfile = AVObject.createWithoutData("UserProfile", StaticData.getUserProfileId());
				userProfile.put("photo", new AVFile("pic", StaticData.mImageBytes));
				userProfile.saveInBackground(new SaveCallback() {
					@Override
					public void done(AVException e) {
						progressDialog.dismiss();
						if (e == null) {
							Toast.makeText(getActivity(), "添加成功",Toast.LENGTH_LONG).show();
//							getActivity().finish();
						} else {
							Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
						}
					}
				});
			}
		});
	}
}
