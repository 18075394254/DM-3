package activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;


import com.example.user.dm_3.R;

import java.util.ArrayList;

import controller.BaseActivity;
import controller.UserDatabase;
import model.User;

public class RegisterActivity extends BaseActivity {
	private EditText et_name;
	private EditText et_pwd;
	private EditText et_pwd2;
	UserDatabase helper;
	ArrayList<User> list;
	Boolean b=true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//强制为竖屏
		setContentView(R.layout.activity_register_avt);
		et_name=(EditText) findViewById(R.id.edit_user_register);
		et_pwd=(EditText) findViewById(R.id.edit_pwd_register);
		et_pwd2= (EditText) findViewById(R.id.edit_pwd_2 );
		helper=new UserDatabase(this);
		list = helper.getAllUsers();
		
	}
	public void click(View v){
		switch (v.getId()) {
		case R.id.btn_register:

			String name=et_name.getText().toString();
			String pwd=et_pwd.getText().toString();
			String pwd2=et_pwd2.getText().toString();
			if(!pwd.equals(pwd2)){
				Toast.makeText(RegisterActivity.this, "两次密码输入不一致，请重新设置", Toast.LENGTH_SHORT).show();
			}else {

				for (int i = 0; i < list.size(); i++) {
					if (list.get(i).getName().equals(name)) {
						Toast.makeText(this, "此用户名已被注册，请重新输入", Toast.LENGTH_SHORT).show();
						b = false;
					}
				}

				for (int i = 0; i < list.size(); i++) {
					Log.i("====", "name=" + list.get(i).getName());
				}
				if (b) {
					if (TextUtils.isEmpty(name) || TextUtils.isEmpty(pwd)) {

						Toast.makeText(RegisterActivity.this, "账号密码不能为空", Toast.LENGTH_SHORT).show();
					} else {
						if (name != null) {
							helper.add(name, pwd);
						}

						Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();

						startActivity(new Intent(RegisterActivity.this, LogActivity.class));

						finish();
					}

				}
			}
			break;

			case R.id.but_return:
				startActivity(new Intent(RegisterActivity.this, LogActivity.class));
				finish();
				break;

		default:
			break;
		}
	}
}
