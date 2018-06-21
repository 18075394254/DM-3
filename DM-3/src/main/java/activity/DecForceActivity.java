package activity;

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
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import com.example.user.dm_3.R;

import controller.BaseActivity;
import utils.BluetoothState;
import utils.MyService;

/**
 * Created by Administrator on 16-10-27.
 */
public class DecForceActivity extends BaseActivity {
    private Button btn_decForceZero,btn_decFuZai;
    private ImageView backimage;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String message = (String) msg.obj;
            if (message.equals("E1")) {
                btn_decForceZero.setTextColor(Color.BLACK);
                Toast.makeText(DecForceActivity.this, "零点标定成功", Toast.LENGTH_SHORT).show();
            } else if (message.equals("E2")) {
                btn_decFuZai.setTextColor(Color.BLACK);
                Toast.makeText(DecForceActivity.this, "负载标定成功", Toast.LENGTH_SHORT).show();
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
        initView();
    }

    private void initView() {
        btn_decForceZero=getView(R.id.btn_decDisZero);
        btn_decForceZero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBinder.sendMessage("E1", BluetoothState.DECFORCEACTIVITY);
                btn_decForceZero.setTextColor(Color.RED);
            }
        });

        btn_decFuZai=getView(R.id.btn_decFuZai);
        btn_decFuZai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBinder.sendMessage("E2", BluetoothState.DECFORCEACTIVITY);
                btn_decFuZai.setTextColor(Color.RED);
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
}
