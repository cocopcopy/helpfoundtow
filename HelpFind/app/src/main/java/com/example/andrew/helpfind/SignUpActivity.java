package com.example.andrew.helpfind;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
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
import com.avos.avoscloud.SignUpCallback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Andrew on 2017/8/29.
 */

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    public static final String USER_NAME = "username";
    public static final String PASS_TOKEN = "password";

    private ProgressDialog progressDialog;

    @BindView(R.id.input_sign_account) EditText _account;
    @BindView(R.id.input_sign_email) EditText _email;
    @BindView(R.id.input_sign_password) EditText _password;
    @BindView(R.id.btn_signup) Button _signup;
    @BindView(R.id.link_login) TextView _loginLink;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
        localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);

        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        progressDialog = new ProgressDialog(SignUpActivity.this,
                ProgressDialog.STYLE_SPINNER);

        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Create account...");
        progressDialog.setCanceledOnTouchOutside(false);

        // Setting click event for Sign up button
        _signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: execute sign up action at here
                signup();
            }
        });

        // Setting link to event for Sign in link
        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: implement link to sign in action at here
                finish();  // kill current activity, back to sign in activity
            }
        });
    }

    /**
     * Sign up method implemented for sign up action
     *
     * accept three params:
     * 1) account name
     * 2) email address
     * 3) password input
     *
     * before we submit the sign up action, we need verify the valid of account (exactly the email
     * address)
     */
    private void signup() {
        // Disable the signup button for misusing
        _signup.setEnabled(false);

        final Map<String, String> params = new HashMap<>();
        params.put("username", _account.getText().toString());
        params.put("password", _password.getText().toString());
        params.put("email", _email.getText().toString());

        progressDialog.show();

        checkValidAndSignUp(params);
    }

    /**
     * On sign up success, we back to login activity and fill sign up information automatically for
     * our user
     */
    private void onSignupSuccess(final String userName) {
        AVQuery<AVUser> userQuery = new AVQuery<>("_User");
        userQuery.whereEqualTo("username", userName);
        userQuery.findInBackground(new FindCallback<AVUser>() {
            @Override
            public void done(List<AVUser> list, AVException e) {
                if (list == null || list.size() == 0) return;
                String userID = list.get(0).getObjectId();
                AVObject userProfile = new AVObject("UserProfile");
                userProfile.put("user", AVObject.createWithoutData("_User", userID));
                userProfile.saveInBackground();

                _signup.setEnabled(true);
                progressDialog.dismiss();
                setResult(RESULT_OK, null);
                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                intent.putExtra(USER_NAME, _account.getText().toString());
                intent.putExtra(PASS_TOKEN, _password.getText().toString());
                startActivity(intent);
                finish();
            }
        });
    }

    private void onSignupFailed(String message) {
        _signup.setEnabled(true);
        progressDialog.dismiss();
        Toast.makeText(getBaseContext(), "Sign up failed: " + message, Toast.LENGTH_LONG).show();
    }

    /**
     * Valid method offers a way to verify the validation of current sign up account information
     *
     * @return true if valid, or false if invalid
     */
    private boolean isValid(Map<String, String> params) {

        String account = params.get("username");
        String email = params.get("email");
        String password = params.get("password");

        if (account.isEmpty() || account.length() < 6) {
            _account.setError("用户名长度不能少于6位");
            _account.setFocusable(true);
            return false;
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _email.setError("必须是：account@domain.com样式");
            return false;
        }

        if (password.isEmpty() || password.length() < 6) {
            _password.setError("密码长度不能少于6位");
            return false;
        }
        return true;
    }

    /**
     * Check current sign up action whether valid, and do callback
     * @param params information of sign up
     */
    private void checkValidAndSignUp(final Map<String, String> params) {
        if (isValid(params)) {
            // check whether has same user
            AVUser user = new AVUser();
            user.setEmail(params.get("email"));
            user.setUsername(params.get("username"));
            user.setPassword(params.get("password"));
            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(AVException e) {
                    if (e == null) {
//                        使用EaseUI时的逻辑
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
//                                    EMClient.getInstance().createAccount(_account.getText().toString().trim(),_password.getText().toString().trim());
//                                    EMClient.getInstance().logout(true);
                                    progressDialog.dismiss();
                                    Message message = new Message();
                                    message.what = 1;
                                    handler.sendMessage(message);
                                } catch (Exception e) {
                                    Log.e("Test", "注册失败" + "," + e.getMessage());
                                    progressDialog.dismiss();
                                    Message message = new Message();
                                    message.what = 2;
                                    message.obj = e.getMessage();
                                    handler.sendMessage(message);
                                }
                            }
                        }).start();
                    } else {
                        onSignupFailed(e.getMessage());
                    }
                }
            });
        } else {
            onSignupFailed("注册失败");
        }
    }

    private Handler handler= new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    onSignupSuccess(_account.getText().toString());
                    break;
                case 2:
                    onSignupFailed((String)msg.obj);
                    break;
                default:
                    break;
            }
        }
    };
}
