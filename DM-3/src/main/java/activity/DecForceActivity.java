package activity;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.user.dm_3.R;

import java.text.SimpleDateFormat;
import java.util.Date;

import controller.BaseActivity;
import utils.BluetoothState;
import utils.MyService;

/**
 * Created by Administrator on 16-10-27.
 */
public class DecForceActivity extends BaseActivity {
    private Button btn_startDec,btn_next,btn_reset;
    private TextView text_tishi;
    private EditText et_tishi;
    private ImageView backimage;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String message = (String) msg.obj;
            Log.i("mtag", "handler接收的数据为 ：" + message);
            if (message.equals("E1")) {
                btn_startDec.setClickable(true);
                if (text_tishi.getText().equals(getResources().getString(R.string.kongzai))) {
                    et_tishi.setText("点击下一步进行50N负载标定！");
                    Toast.makeText(DecForceActivity.this, "空载标定成功", Toast.LENGTH_SHORT).show();
                    btn_startDec.setTextColor(Color.BLACK);
                    btn_startDec.setText("空载标定成功");
                    btn_next.setVisibility(View.VISIBLE);

                }else if(text_tishi.getText().equals(getResources().getString(R.string.fuzai50N))){
                    et_tishi.setText("点击下一步进行100N负载标定！");
                    Toast.makeText(DecForceActivity.this, "50N负载标定成功", Toast.LENGTH_SHORT).show();
                    btn_startDec.setTextColor(Color.BLACK);
                    btn_startDec.setText("50N负载标定成功");
                    btn_next.setVisibility(View.VISIBLE);

                }else if(text_tishi.getText().equals(getResources().getString(R.string.fuzai100N))){
                    et_tishi.setText("负载标定完成，可以进行测试！");
                    Toast.makeText(DecForceActivity.this, "100N负载标定成功", Toast.LENGTH_SHORT).show();
                    btn_startDec.setText("标定完成，返回上一界面");
                    btn_startDec.setTextColor(Color.BLACK);
                }
            }else if (message.equals("E2")){
                btn_startDec.setClickable(true);
                btn_reset.setClickable(true);
                text_tishi.setText(R.string.kongzai);
                et_tishi.setText("点击按钮开始空载标定");
                btn_reset.setTextColor(Color.BLACK);
                btn_startDec.setText("开始标定");
                btn_startDec.setTextColor(Color.BLACK);

            }else{
                Toast.makeText(DecForceActivity.this, "标定异常，非标定指令！", Toast.LENGTH_SHORT).show();

            }
        }
    };

    private MyService.DiscoveryBinder mBinder;


    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //获取Service端的Messenger
            mBinder = (MyService.DiscoveryBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }


    };
    private Intent bindIntent;
    private MyReceiver receiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//强制为竖屏
        setContentView(R.layout.activity_decforce);
        bindIntent = new Intent(this, MyService.class);
        bindService(bindIntent, connection, BIND_AUTO_CREATE);

        receiver = new MyReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction("android.intent.action.decForceActivity");
        DecForceActivity.this.registerReceiver(receiver, filter);

        IntentFilter filter1=new IntentFilter();
        filter1.addAction("android.bluetooth.device.action.ACL_CONNECTED");
        filter1.addAction("android.bluetooth.device.action.ACL_DISCONNECTED");
        DecForceActivity.this.registerReceiver(mReceiver, filter1);



        initView();
    }

    private void initView() {
        text_tishi = getView(R.id.text_tishi);
        et_tishi = getView(R.id.et_tishi);
        btn_startDec = getView(R.id.btn_startDec);
        btn_next = getView(R.id.btn_next);
        btn_startDec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn_startDec.getText().equals("标定完成，返回上一界面")){
                    finish();
                }else {
                    mBinder.sendMessage("E1", BluetoothState.DECFORCEACTIVITY);
                    btn_startDec.setTextColor(Color.RED);
                    btn_startDec.setClickable(false);
                }
            }
        });
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btn_next.setVisibility(View.GONE);
                btn_startDec.setText("开始标定");
                if (text_tishi.getText().equals(getResources().getString(R.string.kongzai))) {

                    et_tishi.setText("手动加到50N负载进行标定！");
                    text_tishi.setText(getResources().getString(R.string.fuzai50N));


                } else if (text_tishi.getText().equals(getResources().getString(R.string.fuzai50N))){

                    et_tishi.setText("手动加到100N负载进行标定！");
                    text_tishi.setText(getResources().getString(R.string.fuzai100N));


                }
            }
        });

        btn_reset = getView(R.id.btn_reset);
        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btn_reset.setTextColor(Color.RED);
                btn_reset.setClickable(false);
            }
        });

        backimage= (ImageView) findViewById(R.id.back);
        backimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }




    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        unbindService(connection);
        unregisterReceiver(mReceiver);
    }

    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if (intent.getAction().equals("android.intent.action.decForceActivity")) {
                Bundle bundle = intent.getExtras();
                String message1 = bundle.getString("msg");
                Message message = new Message();
                message.what = 0;
                message.obj = message1;
                handler.sendMessage(message);

            }
        }
    }
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive (Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d("aaa", device.getName() + " ACTION_ACL_CONNECTED");
            } else if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
                Log.d("aaa", " ACTION_ACL_DISCONNECTED");
                //String message1="蓝牙断开连接";
                //handler.obtainMessage(2, 1, -1, message1).sendToTarget();
                DecForceActivity.this.finish();
            }
        }

    };
}
