package com.example.andrew.helpfind;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

//import com.hyphenate.easeui.ui.EaseChatFragment;

/**
 * Created by copy on 2017/6/28.
 */

public class ChatActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

//        EaseUI
//        EaseChatFragment chatFragment=new EaseChatFragment();
//        chatFragment.setArguments(getIntent().getExtras());
//        getFragmentManager().beginTransaction().add(R.id.ec_layout_container,chatFragment).commit();
    }
}
