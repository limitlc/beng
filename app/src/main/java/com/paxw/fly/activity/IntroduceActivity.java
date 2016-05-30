package com.paxw.fly.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;

import com.paxw.fly.R;

import net.youmi.android.spot.SpotManager;

public class IntroduceActivity extends BaseActivity implements OnClickListener{

	
	public void onCreate(Bundle bundle){
		super.onCreate(bundle) ;
		SpotManager.getInstance(this.getApplicationContext()).showSpotAds(this);

		
		
		//强制全屏
        //首先去掉title,就是没有title 那一行，但是还不是全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);    
        // 禁止屏幕休眠
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //去掉状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
       
        setContentView(R.layout.introduce);
        //介绍页面的开始按钮
        findViewById(R.id.begin).setOnClickListener(this);
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		if (v.getId() == R.id.begin) {
			Intent i = new Intent(this, mainActivity.class);
			startActivity(i);
			this.finish();
		}
		
		
	}
	
	
}
