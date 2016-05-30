package com.paxw.fly.activity;

import android.app.Activity;

import com.umeng.analytics.MobclickAgent;

/**
 * Created by lichuang on 2016/5/30.
 */
public class BaseActivity extends Activity {
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

}
