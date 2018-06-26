package activity;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.dm_3.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import controller.BaseActivity;
import controller.MyApplication;
import controller.PictureDatabase;
import utils.BluetoothState;
import utils.Calculate;
import utils.MyService;
import view.MySeverityView;

/**
 * Created by Administrator on 16-12-7.
 */
public class ImportAllActivity extends BaseActivity{
    String savepath;
    ArrayList<Integer> list=new ArrayList<>();
    //测试压力数据的集合
    private ArrayList<Float> m_ForceData = new ArrayList<Float>();
    //测试位移数据的集合
    private ArrayList<Float> m_DisData = new ArrayList<Float>();
    private StringBuilder sb=new StringBuilder();
   private TextView progressValue;
   private ProgressBar progressBar;
    private TextView biaoti;
    Button daoru;
    int filsNum=0;
    PictureDatabase pictureDB;
    SQLiteDatabase db;
    Bitmap bitmap;
    private float fmax=0;
    private float energy=0;
    private float fKin=0;
    Calculate calculate=new Calculate();
    private float speedAve=0;
    private float speedMax=0;
    private float MaxAcc=0;
    private float MinAcc=0;
    private float speedAcc=0;
    private float openTime=0;
    private float closeTime=0;

    private int progress=0;
    private String message;
    private float value = 0;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
             message = (String) msg.obj;
            if (message.equals("D")){
                message=MyApplication.getString();
                new Thread(
                        new Runnable() {
                            @Override
                            public void run() {

                                if (message.getBytes().length > 23) {
                                    m_ForceData.clear();
                                    m_DisData.clear();
                                    String[] s = message.split(",");
                                    Log.i("cyy123", "s.length=" + s.length);
                                    // Toast.makeText(MyApplication.getContext(), "解析的字符串的长度为" + s.length, Toast.LENGTH_SHORT).show();
                                   //21332是ST转为的数字，以这个数字为分隔符分隔数据
                                    for (int i = 0; i < s.length; i++) {
                                        if (s[i].contains("21332")) {
                                            list.add(i);
                                            filsNum++;
                                            Log.i("Tag", "i = " + i);
                                        }
                                    }
                                    if (list.size() != 0) {
                                        if (list.size() == 1) {
                                            //list.get(0) + 2 去掉“ST”和文件编号
                                            for (int j = list.get(0) + 2; j < s.length; j++) {
                                                value = ((Float.parseFloat(s[j]) / 100));

                                                if (i%2 == 0) {
                                                    m_ForceData.add(value);
                                                }else{
                                                    m_DisData.add(value);
                                                }
                                            }
                                            onSave(s[1]);
                                            // bnp.incrementProgressBy(100);
                                            handler1.obtainMessage(0, 100, -1, message).sendToTarget();

                                        } else {
                                            for (int k = 0; k < list.size(); k++) {
                                                if (k < list.size() - 1) {
                                                    for (int j = list.get(k) + 2; j < list.get(k + 1); j++) {
                                                        value = ((Float.parseFloat(s[j]) / 100));

                                                        if (i%2 == 0) {
                                                            m_ForceData.add(value);
                                                        }else{
                                                            m_DisData.add(value);
                                                        }
                                                    }
                                                    onSave(s[list.get(k) + 1]);
                                                    progress=(k+1) * 100 / filsNum;
                                                    // bnp.incrementProgressBy(progress);
                                                    handler1.obtainMessage(0, progress, -1, message).sendToTarget();
                                                    // Log.i("mtag", "progressValue = " + progressValue.getText().toString());
                                                    Log.i("mtag", "解析数据并绘图时间" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()))+"progress"+k * 100 / filsNum);
                                                } else {
                                                    //去掉后面的End*造成的不正常数据
                                                    for (int j = list.get(list.size() - 1) + 2; j < s.length-2; j++) {
                                                        value = ((Float.parseFloat(s[j]) / 100));

                                                        if (i%2 == 0) {
                                                            m_ForceData.add(value);
                                                        }else{
                                                            m_DisData.add(value);
                                                        }
                                                    }
                                                    onSave(s[list.get(k)+1]);
                                                    handler1.obtainMessage(0, 100, -1, message).sendToTarget();
                                                }
                                            }
                                        }

                                    }

                                } else {
                                    Toast.makeText(ImportAllActivity.this, "仪器没有文件可以导入", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                        }
                ).start();
            }else{
                Toast.makeText(ImportAllActivity.this, "仪器没有文件可以导入", Toast.LENGTH_SHORT).show();
                finish();
            }

        }

    };

    private Handler handler1=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final int progress=msg.arg1;
            Log.i("mtag","handler1 "+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis())));
            switch(msg.what){
                case 0:
                    if (progress == 100) {
                        progressBar.setProgress(progress);
                        progressValue.setText(progress + " % ");
                        Toast.makeText(ImportAllActivity.this, "数据导入完成", Toast.LENGTH_SHORT).show();
                        MyApplication.setString("");
                        Log.i("mtag", "数据导入完成时间" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis())));
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //批量导入完成后，打开文件列表
                                Intent intent = new Intent(ImportAllActivity.this, OpenAllActivity.class);
                                intent.putExtra("path", savepath);
                                startActivity(intent);
                                finish();
                            }
                        }, 500);
                    }else{
                        progressBar.setProgress(progress);
                        progressValue.setText(progress + " % ");
                    }

                    break;
            }


        }
    };

    private MyService.DiscoveryBinder mBinder;
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
    private String name=null;
    private int i=0;
    private HashMap map=new HashMap();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//强制为竖屏
        setContentView(R.layout.activity_importall);

        progressValue = (TextView) findViewById(R.id.pValue);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        biaoti=getView(R.id.biaoti);


        IntentFilter filter1=new IntentFilter();
        filter1.addAction("android.bluetooth.device.action.ACL_CONNECTED");
        filter1.addAction("android.bluetooth.device.action.ACL_DISCONNECTED");
        ImportAllActivity.this.registerReceiver(mReceiver, filter1);

       // progressValue = (TextView) findViewById(R.id.progressValue);
        progressBar.setMax(100);
        progressBar.setProgress(0);
        daoru= (Button) findViewById(R.id.daoru);

        pictureDB=new PictureDatabase(this);
        db=pictureDB.getWritableDatabase();


        bindIntent = new Intent(this, MyService.class);
        bindService(bindIntent, connection, BIND_AUTO_CREATE);
        Intent intent=getIntent();
        savepath=intent.getStringExtra("path");
        name=intent.getStringExtra("name");

        receiver = new MyReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction("android.intent.action.ceshiActivity");
        ImportAllActivity.this.registerReceiver(receiver, filter);

        daoru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // if (name.equals("DM-2-1")) {
                    daoru.setTextColor(Color.RED);
                    mBinder.sendMessage("D1", BluetoothState.IMPORTALLACTIVITY);
                    Log.i("mtag", "发送D1的时间" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis())));
                    biaoti.setText("正在导入，请稍等...");
                    progressValue.setText("正在接收数据...");
               // }

            }
        });
    }


    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        unregisterReceiver(mReceiver);
        unbindService(connection);
    }
    public void onStart() {
        super.onStart();
    }
    public Bitmap createViewBitmap(View v) {
        Resources resources = this.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        int width = dm.widthPixels;
        int height = dm.heightPixels *10/16;
        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        return bitmap;
    }
    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if (intent.getAction().equals("android.intent.action.ceshiActivity")) {
                Bundle bundle = intent.getExtras();
                String message1 = bundle.getString("msg");
                Log.i("mtag","广播接受到数据时间"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis())));
                handler.obtainMessage(0, 1, -1, message1).sendToTarget();
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
                ImportAllActivity.this.finish();
            }
        }

    };
    private void onSave(String i) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String dateStr = formatter.format(curDate);
        File newfile=null;
       // if (name.equals("DM-2-1")) {
            String name = dateStr + "-" + i + ".ds";
            String path = savepath + "/" + name;
            newfile = new File(path);
            /*calculate.lvbo30(m_cutData, m_filterData, 250, 30);
            map=calculate.CalcForceMax(m_filterData, m_cutData, 0);
            fmax=(float)map.get("fmax");
            fKin=(float)map.get("fKin");
            energy=(float)map.get("energy");*/
            MySeverityView forceView = new MySeverityView(ImportAllActivity.this, m_ForceData,m_DisData);
            bitmap = createViewBitmap(forceView);
            pictureDB.initDataBaseF(db, bitmap, MyApplication.FORCE, name, MainActivity.s_mLiftId, MainActivity.s_mOperator, MainActivity.s_mLocation, fmax, fKin, energy);
            map.clear();
       // }
        fmax = 0;
        fKin=0;
        energy = 0;
        speedMax=0;
        MaxAcc=0;
        MinAcc=0;
        m_DisData.clear();
        m_ForceData.clear();
        try {
            if (newfile != null){
                newfile.createNewFile();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
