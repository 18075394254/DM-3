package activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;


import com.example.user.dm_3.R;

import controller.BaseActivity;

public class SplashScreenActivity extends BaseActivity
{
	//延时两秒
	private static final long DELAY_MILLIS = 2000;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		//去掉标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//if (MyApplication.getWindowHeight() < MyApplication.getWindowWidth()){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//强制为竖屏
		//}
		setContentView(R.layout.activity_startup);
		
		Handler jumpHandle = new Handler();
		jumpHandle.postDelayed(new Runnable() {
			@Override
			public void run() {
				//跳转到
				GoHome();
			}
		}, DELAY_MILLIS);
	}
	
	public void GoHome() 
	{
		//跳转到登录界面
		Intent intent = new Intent(this, LogActivity.class);
		startActivity(intent);
		this.finish();	
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
		{
//land
		}
		else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
		{
//port
		}
	}
}
