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
import android.widget.Toast;

import com.example.user.dm_3.R;

import controller.BaseActivity;
import utils.MyService;

/**
 * Created by Administrator on 16-10-26.
 */
public class DeclareActivity extends BaseActivity {
    private Button btn_decForce,btn_decDis,btn_backMain;
    private MyService.DiscoveryBinder mBinder;

    //标定零点的处理
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String message = (String) msg.obj;
            if(message.equals("H1")) {
                btn_decForce.setTextColor(Color.BLACK);
                Toast.makeText(DeclareActivity.this,"零点标定已经完成！",Toast.LENGTH_SHORT).show();
            }
        }
    };
    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //获取Service端的Messenger
            mBinder =(MyService.DiscoveryBinder)service;
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
        setContentView(R.layout.activity_declare);
        bindIntent = new Intent(this, MyService.class);
        bindService(bindIntent, connection, BIND_AUTO_CREATE);

        receiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.ceshiActivity");
        DeclareActivity.this.registerReceiver(receiver, filter);

        btn_decForce=getView(R.id.btn_decDisZero);
        btn_decDis=getView(R.id.btn_decDis);
        btn_backMain=getView(R.id.btn_backMain);
        btn_decForce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DeclareActivity.this,DecForceActivity.class));
            }
        });

        btn_decDis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DeclareActivity.this,DecDisActivity.class));
            }
        });
      /*  btn_decZero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBinder.sendMessage("H1", BluetoothState.CESHIACTIVITY);
                btn_decZero.setTextColor(Color.RED);
            }
        });*/
        btn_backMain.setOnClickListener(new View.OnClickListener() {
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
            if (intent.getAction().equals("android.intent.action.ceshiActivity")) {
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
