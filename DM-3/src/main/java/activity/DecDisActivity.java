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
import android.widget.TextView;
import android.widget.Toast;


import com.example.user.dm_3.R;

import controller.BaseActivity;
import utils.BluetoothState;
import utils.MyService;

/**
 * Created by Administrator on 16-10-27.
 */
public class DecDisActivity extends BaseActivity {
    private Button btn_decDisZero,btn_decDisFuZai,btn_resetDisFuZai;
    private TextView textDisValue;

    private ImageView backimage;
    String string = "当前位移量：";
    //表示位移量
    private int disValue =0;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String message = (String) msg.obj;
            if (message.equals("F1")) {
                btn_decDisZero.setTextColor(Color.BLACK);
                Toast.makeText(DecDisActivity.this, "零点标定成功！", Toast.LENGTH_SHORT).show();
            } else if (message.equals("F2")) {
                textDisValue.setText(string+disValue);
                btn_decDisFuZai.setTextColor(Color.BLACK);
                Toast.makeText(DecDisActivity.this, "负载标定成功，继续加1mm位移！", Toast.LENGTH_SHORT).show();
            }else if (message.equals("F3")){
                Toast.makeText(DecDisActivity.this, "复位成功，重新标定！", Toast.LENGTH_SHORT).show();
                textDisValue.setText(string + "null");
                btn_decDisFuZai.setTextColor(Color.BLACK);
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
        setContentView(R.layout.activity_decdis);
        bindIntent = new Intent(this, MyService.class);
        bindService(bindIntent, connection, BIND_AUTO_CREATE);

        receiver = new MyReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction("android.intent.action.decForceActivity");
        DecDisActivity.this.registerReceiver(receiver, filter);
        initView();
    }

    private void initView() {
        textDisValue = getView(R.id.disValue);
        textDisValue.setText(string+"null");
        btn_decDisZero=getView(R.id.btn_decDisZero);
        btn_decDisZero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBinder.sendMessage("F1", BluetoothState.DECFORCEACTIVITY);
                btn_decDisZero.setTextColor(Color.RED);
            }
        });

        btn_decDisFuZai=getView(R.id.btn_decDisFuZai);
        btn_decDisFuZai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disValue++;
                mBinder.sendMessage("F2", BluetoothState.DECFORCEACTIVITY);
                btn_decDisFuZai.setTextColor(Color.RED);
            }
        });


        btn_resetDisFuZai=getView(R.id.btn_resetDisFuzai);
        btn_resetDisFuZai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBinder.sendMessage("F3", BluetoothState.DECFORCEACTIVITY);
                btn_resetDisFuZai.setTextColor(Color.RED);
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
