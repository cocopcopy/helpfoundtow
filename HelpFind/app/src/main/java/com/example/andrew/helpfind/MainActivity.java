package com.example.andrew.helpfind;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.andrew.helpfind.fragments.BroadcastSquareFragment;
import com.example.andrew.helpfind.fragments.FakeFragment;
import com.example.andrew.helpfind.fragments.MyCenterFragment;
import com.example.andrew.helpfind.fragments.SearchFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Andrew on 2017/8/29.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";

    public static final String FRAGMENT_ID = "FragmentId";

    private BroadcastSquareFragment _broadcastFragment;
    //    EaseUI
//    private EaseConversationListFragment _conversationListFragment;
//    替代的_conversationListFragment，这是一个Fake
    private FakeFragment _conversationListFragment;
    private SearchFragment searchFragment;
    private MyCenterFragment _centerFragment;



    private int FLAG_FRAGMENT = 0;

    @BindView(R.id.iv_menu_broadcast) ImageView switcherBroadcast;
    @BindView(R.id.iv_menu_center) ImageView switcherMe;
    @BindView(R.id.tv_home) TextView homeHint;
    @BindView(R.id.tv_center) TextView centerHint;
    @BindView(R.id.iv_menu_conversation) ImageView switcherConversation;
    @BindView(R.id.tv_chat) TextView chatHint;
    @BindView(R.id.iv_menu_search) ImageView switcherSearch;
    @BindView(R.id.tv_search) TextView searchHint;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
        localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setResult(RESULT_OK, null); // tell login activity: you can die

        switcherBroadcast.setOnClickListener(this);
        switcherMe.setOnClickListener(this);
        switcherConversation.setOnClickListener(this);
        switcherSearch.setOnClickListener(this);


        FLAG_FRAGMENT = getIntent().getIntExtra(FRAGMENT_ID, -1);

        if (savedInstanceState == null) {
            setDefaultFragment();
        } else {
            if (FLAG_FRAGMENT <= 0) {
                _broadcastFragment = (BroadcastSquareFragment) getSupportFragmentManager().getFragment(savedInstanceState, "fragment");
            } else if (FLAG_FRAGMENT == 0) {
                _conversationListFragment = (FakeFragment) getSupportFragmentManager().getFragment(savedInstanceState, "fragment");
            } else if (FLAG_FRAGMENT == 2) {
                searchFragment = (SearchFragment) getSupportFragmentManager().getFragment(savedInstanceState, "fragment");
//                EaseUI
//                _conversationListFragment = (EaseConversationListFragment) getFragmentManager().getFragment(savedInstanceState, "fragment");
            } else if (FLAG_FRAGMENT == 3) {
                _centerFragment = (MyCenterFragment) getSupportFragmentManager().getFragment(savedInstanceState, "fragment");
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        if (FLAG_FRAGMENT == 3)
//            getSupportFragmentManager().putFragment(outState, "fragment", _centerFragment);
//        else if (FLAG_FRAGMENT == 0)
//            getSupportFragmentManager().putFragment(outState, "fragment", _broadcastFragment);
//        else if (FLAG_FRAGMENT == 1)
//            getSupportFragmentManager().putFragment(outState, "fragment", _conversationListFragment);
//        else if (FLAG_FRAGMENT == 2)
//            getSupportFragmentManager().putFragment(outState, "fragment", searchFragment);
//        else if (FLAG_FRAGMENT == 1) getFragmentManager().putFragment(outState, "fragment", _conversationListFragment); EaseUI
    }

    @Override
    public void onClick(View v) {

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();

        // 将当前的图标给gray掉
        switch (FLAG_FRAGMENT) {
            case 0:
                switcherBroadcast.setImageResource(R.drawable.home);
                homeHint.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorDective));
                break;
            case 1:
                switcherConversation.setImageResource(R.drawable.conversation);
                chatHint.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorDective));
            case 2:
                switcherSearch.setImageResource(R.drawable.search);
                searchHint.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorDective));
            case 3:
                switcherMe.setImageResource(R.drawable.center);
                centerHint.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorDective));
                break;
        }

        // 再把点击的图给active起来
        switch (v.getId()) {
            case R.id.iv_menu_broadcast:
                if (null == _broadcastFragment) {
                    _broadcastFragment = BroadcastSquareFragment.newInstance();
                    transaction.add(R.id.fl_activity_main, _broadcastFragment);
                }
                FLAG_FRAGMENT = 0;
                hideFragment(transaction);
                transaction.show(_broadcastFragment);
                homeHint.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorFontActive));
                switcherBroadcast.setImageResource(R.drawable.home_active);
                break;
            case R.id.iv_menu_conversation:
                if (null == _conversationListFragment) {
//                    _conversationListFragment = new EaseConversationListFragment();
//                    _conversationListFragment.setConversationListItemClickListener(new EaseConversationListFragment.EaseConversationListItemClickListener() {
//
//                        @Override
//                        public void onListItemClicked(EMConversation conversation) {
//                            startActivity(new Intent(MainActivity.this, ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, conversation.conversationId()));
//                        }
//                    });
                    _conversationListFragment = new FakeFragment();
                    transaction.add(R.id.fl_activity_main, _conversationListFragment);
                }
                FLAG_FRAGMENT = 1;
                hideFragment(transaction);
                transaction.show(_conversationListFragment);
                chatHint.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorFontActive));
                switcherConversation.setImageResource(R.drawable.conversation_active);
                break;
            case R.id.iv_menu_search:
                if (null == searchFragment) {
                    searchFragment = new SearchFragment();
                    transaction.add(R.id.fl_activity_main, searchFragment);
                }
                FLAG_FRAGMENT = 2;
                hideFragment(transaction);
                transaction.show(searchFragment);
                searchHint.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorFontActive));
                switcherSearch.setImageResource(R.drawable.search_active);
                break;
            case R.id.iv_menu_center:
                if (null == _centerFragment) {
                    _centerFragment = new MyCenterFragment();
                    transaction.add(R.id.fl_activity_main, _centerFragment);
                }
                FLAG_FRAGMENT = 3;
                hideFragment(transaction);
                transaction.show(_centerFragment);
                centerHint.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorFontActive));
                switcherMe.setImageResource(R.drawable.center_active);
                break;
        }

        transaction.commit();
    }

    private void setDefaultFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();

        if (FLAG_FRAGMENT <= 0) {
            switcherBroadcast.setImageResource(R.drawable.home_active);
            homeHint.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorFontActive));
            _broadcastFragment = BroadcastSquareFragment.newInstance();
            transaction.add(R.id.fl_activity_main, _broadcastFragment);
            transaction.commit();
            FLAG_FRAGMENT = 0;
        } else if (FLAG_FRAGMENT == 3) {
            switcherBroadcast.setImageResource(R.drawable.center_active);
            centerHint.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorFontActive));
            _centerFragment = new MyCenterFragment();
            transaction.add(R.id.fl_activity_main, _centerFragment);
            transaction.commit();
        } else if (FLAG_FRAGMENT == 1) {
            switcherConversation.setImageResource(R.drawable.conversation_active);
            chatHint.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorFontActive));
//            EaseUI
//            _conversationListFragment = new EaseConversationListFragment();
            _conversationListFragment = new FakeFragment();
            transaction.add(R.id.fl_activity_main, _conversationListFragment);
            transaction.commit();
        } else if (FLAG_FRAGMENT == 3) {
            switcherSearch.setImageResource(R.drawable.search_active);
            searchHint.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorFontActive));
            searchFragment = new SearchFragment();
            transaction.add(R.id.fl_activity_main, searchFragment);
            transaction.commit();
        }
    }

    private void hideFragment(FragmentTransaction transaction) {
        if (_broadcastFragment != null) transaction.hide(_broadcastFragment);
        if (_centerFragment != null) transaction.hide(_centerFragment);
        if (_conversationListFragment != null) transaction.hide(_conversationListFragment);
        if (searchFragment != null) transaction.hide(searchFragment);
    }
}
