package com.example.andrew.helpfind;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.LogInCallback;
import com.example.andrew.helpfind.entity.StaticData;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private static final int REQUEST_LOGIN = 1;

    @BindView(R.id.input_account)
    EditText _userName;
    @BindView(R.id.input_password)
    EditText _password;
    @BindView(R.id.btn_login)
    Button _loginButton;
    @BindView(R.id.link_signup)
    TextView _signupLink;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:

                    break;
                case 2:
                    _loginButton.setEnabled(true);
                    Toast.makeText(LoginActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
        localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);

        setContentView(R.layout.activity_login);

        checkLogin();

        ButterKnife.bind(this);

        // Login activity will accept data from SignUpActivity, we should check whether there has
        // data in Intent
        Intent extraIntent = getIntent();
        if (null != extraIntent && extraIntent.hasExtra(SignUpActivity.USER_NAME) &&
                extraIntent.hasExtra(SignUpActivity.PASS_TOKEN)) {
            _userName.setText(extraIntent.getStringExtra(SignUpActivity.USER_NAME));
            _password.setText(extraIntent.getStringExtra(SignUpActivity.PASS_TOKEN));
        }

        // Setting Click Event for Login Button
        _loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        // Setting click event for Signup Link
        _signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                // TODO: execute sign up successful logic at here
                this.finish();
            }
        } else if (requestCode == REQUEST_LOGIN) {
            if (resultCode == RESULT_OK) {
                this.finish();
            }
        }
    }
//
//    @Override
//    public void onBackPressed() {
//        moveTaskToBack(true);
//    }

    /**
     * Login action method
     * <p>
     * before a user login, we should check whether the valid of this login action
     * if login action is invalid: login failure
     * if login action is valid: do something
     */
    private void login() {
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        _loginButton.setEnabled(false);

        final String account = _userName.getText().toString();
        final String password = _password.getText().toString();

        AVUser.logInInBackground(account, password, new LogInCallback<AVUser>() {
            @Override
            public void done(AVUser avUser, AVException e) {

                _loginButton.setEnabled(true);

//                添加了EaseUI以后的登陆逻辑
//                if (avUser != null) {
//                    EMClient.getInstance().login(account.trim(), password.trim(), new EMCallBack() {
//                        @Override
//                        public void onSuccess() {
//                            progressDialog.dismiss();
//                            onLoginSuccess();
//                        }
//
//                        @Override
//                        public void onError(int i, String s) {
//                            onLoginFailed();
//                        }
//
//                        @Override
//                        public void onProgress(int i, String s) {
//
//                        }
//                    });
//                } else {
//                    onLoginFailed();
//                }

//                没有EaseUI的登陆逻辑
                progressDialog.dismiss();
                if (avUser != null) {
                    onLoginSuccess();
                } else {
                    onLoginFailed(e.getLocalizedMessage());
                }
            }
        });

    }

    /**
     * When user login success, jump to BroadcastSquareActivity
     */
    private void onLoginSuccess() {
        StaticData.setCurrentUser(AVUser.getCurrentUser());
        AVQuery<AVObject> query = new AVQuery<>("UserProfile");
        query.whereEqualTo("user", AVObject.createWithoutData("_User", AVUser.getCurrentUser().getObjectId()));
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                StaticData.setUserProfileId(list.get(0).getObjectId());
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void onLoginFailed(String msg) {
        Toast.makeText(getBaseContext(), "Login falied: " + msg, Toast.LENGTH_LONG).show();
    }

    private void checkLogin() {
        AVUser currentUser = AVUser.getCurrentUser();
        if (currentUser != null) {
            onLoginSuccess();
        }
    }

}
