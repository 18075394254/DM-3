package activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.example.user.dm_3.R;

import java.util.ArrayList;

import controller.UserDatabase;
import model.User;


public class LogActivity extends Activity{
	private EditText et_name;
	private EditText et_pwd;
	private CheckBox box;
	UserDatabase helper;
	ArrayList<User> list;
	Boolean b=false;
	SharedPreferences sharedPreferences;
	SharedPreferences.Editor editor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//强制为竖屏
		setContentView(R.layout.activity_login_avt);

		et_name=(EditText) findViewById(R.id.edit_userName);
		et_pwd=(EditText) findViewById(R.id.edit_password);
		box= (CheckBox) findViewById(R.id.checkBox);
		ImageView image = (ImageView) findViewById(R.id.logo);             //使用ImageView显示logo
		image.setImageResource(R.drawable.logo);
		
		helper=new UserDatabase(this);
		list = helper.getAllUsers();
		Log.i("===","user"+list.size());

		/*if (list.size()==0){
			helper.add("Administrator","123456");
		}*/
		for(int i=0;i<list.size();i++){
			Log.i("wp123","user"+list.get(i));
		}
		if(list.size()==1){
			et_name.setText("Administrator");
			et_pwd.setText("123456");
			box.setChecked(true);
		}
		else{
			sharedPreferences=getSharedPreferences("user",Activity.MODE_PRIVATE);
			if(sharedPreferences != null) {
				String name = sharedPreferences.getString("name", "");
				Boolean flag=sharedPreferences.getBoolean("isChecked",false);
				et_name.setText(name);
				Log.i("====","sharepreference  "+name);
				if(flag) {
					box.setChecked(true);
					String pwd = sharedPreferences.getString("pwd", "");
					et_pwd.setText(pwd);
					Log.i("====", "sharepreference  " + pwd);
				}else{
					box.setChecked(false);
					et_pwd.setText("");
				}

			}
		}




	}
	public void click(View v){
		switch (v.getId()) {
		case R.id.but_login:
			
			//获取输入框中的信息
			String name=et_name.getText().toString();
			String pwd=et_pwd.getText().toString();
			User user=new User(name, pwd);
			//验证账号密码
			//判断账号密码是否为空
			if(TextUtils.isEmpty(name)||TextUtils.isEmpty(pwd)){
				
				Toast.makeText(LogActivity.this, "账号密码不能为空", Toast.LENGTH_SHORT).show();
			}else {
				

				for(int i=0;i<list.size();i++){
					if(list.get(i).getName().equals(name) && list.get(i).getPwd().equals(pwd)){
						
						Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
						startActivity(new Intent(LogActivity.this, MainActivity.class));
						SharedPreferences sp= getSharedPreferences("user",
								Activity.MODE_PRIVATE);
						editor=sp.edit();
						editor.putString("name",name);
						editor.putString("pwd",pwd);
						editor.putBoolean("isChecked",box.isChecked());
						editor.commit();
						finish();
						b=true;
					}
				}
				if(b==false) {
					Toast.makeText(this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
				}
				}

			break;
		case R.id.but_register:
			
			startActivityForResult(new Intent(LogActivity.this, RegisterActivity.class), 0);
			finish();
			break;
		}
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
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
