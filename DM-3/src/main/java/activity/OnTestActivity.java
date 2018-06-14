package activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
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
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
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
import model.Point;
import utils.BluetoothState;
import utils.Calculate;
import utils.MyService;
import view.MySeverityView;


/**
 * Created by Administrator on 16-10-17.
 */
public class OnTestActivity extends BaseActivity {
    private Spinner spinner;
    private TextView tv_testWay;
    private TextView tv_testStatus;
    private Button startTest,stopTest,backMain,pictureShow;
    private Button dataResult;
    final String[] arrayWay = new String[] { "测试电梯门刚度" };
    private String state=null;
    PictureDatabase pictureDB;
    SQLiteDatabase db;
    private int indexF=1;
    private int indexS=1;
    private int indexB=1;
    Bitmap bitmap;


    //测试状态
    String Force_State="Force";
    String Speed_State="Speed";
    String Both_State="Both";

    String testWay=Force_State;
    ArrayList<Point> pointsF=new ArrayList<>();
    ArrayList<Point> pointsS=new ArrayList<>();
    private ArrayList<Float> m_filterData = new ArrayList<Float>();
    private ArrayList<Float> m_cutData = new ArrayList<Float>();
    private ArrayList<Float> Distance = new ArrayList<Float>();
    private ArrayList<Float> Speed = new ArrayList<Float>();
    private ArrayList<Float> Acclerate = new ArrayList<Float>();
    private float fmax=0;
    private float energy=0;
    private float fKin=0;
    private float speedAve=0;
    private float speedMax=0;
    private float speedAcc=0;
    private float openTime=0;
    private float closeTime=0;
    private boolean isConnect=true;
    private boolean isStart=false;
    StringBuilder sb=new StringBuilder();
    String totalData = "";
    private float value=0;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                m_cutData.clear();
                String message = (String) msg.obj;
                Log.i("wpcyy628254", "message = "+message );
                if (message.equals("A1") ) {
                    state="force";
                    tv_testStatus.setText("正在测试中...");
                }else if (message.equals("A2")) {
                    String msgdata = MyApplication.getString();
                    totalData = totalData + msgdata;
                    Log.i("wpcyy628254", "msgdata = "+msgdata );
                    String[] s = msgdata.split(",");
                   // if (s.length == 6){
                    float a = Float.parseFloat(s[0]) / 100;
                    float b = Float.parseFloat(s[2]) / 100;
                    float c = Float.parseFloat(s[4]) / 100;
                       float forceValue =  (a+b+c)/3;
                    Log.i("wpcyy628254", "a = "+a +" b = "+b+" c= "+c+" forceValue"+forceValue);
                        textForce.setText(forceValue +"");
                        float disValue =  (Float.parseFloat(s[1]) / 100+ Float.parseFloat(s[3]) / 100+ Float.parseFloat(s[5]) / 100)/3;
                        textDis.setText(disValue + "");
                   // }
                }else if(message.equals("B1")) {

                    final String[] s = totalData.split(",");
                         for (int i = 0; i < s.length -1; i++) {


                                 value = ((Float.parseFloat(s[i]) / 100));
                                 sb.append(s[i] + " , ");
                                 Log.i("ggg", "value =" + value);
                                 m_cutData.add(value);

                         }
                    if(m_cutData.size() != 0){

                        map=calculate.CalcForceMax(m_cutData);

                        fmax=(float)map.get("speedMax");
                        fKin=(float)map.get("MaxAcc");
                        energy=(float)map.get("MinAcc");
                        onSave();
                        fmax = 0;
                        fKin=0;
                        energy = 0;
                        map.clear();
                        tv_testStatus.setText("测试完成");
                        state = null;
                        dataResult.setVisibility(View.VISIBLE);
                        dataResult.setClickable(true);
                        stopTest.setTextColor(Color.BLACK);
                    }
                    startTest.setEnabled(true);
                    //Log.i("wyy123", "all value = " + sb.toString());

                }
            }else if(msg.what==1){
                isConnect=true;
            }else if(msg.what==2){
                OnTestActivity.this.finish();
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
    String name=null;
    private boolean cantest=true;
    Calculate calculate=new Calculate();
    HashMap map=new HashMap();
    private float S=0;
    private float Acc=0;
    private float allS=0;
    private float MaxAcc=0;
    private float MinAcc=0;
    private int type;
    private ImageView backimage;
    private TextView textForce;
    private TextView textDis;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//强制为竖屏
        setContentView(R.layout.activity_ontest);

        pictureDB=new PictureDatabase(this);
        db=pictureDB.getWritableDatabase();

        textForce = getView(R.id.textForce);
        textDis = getView(R.id.textDis);
        backimage=getView(R.id.back);
        backimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnTestActivity.this.finish();
            }
        });


        tv_testWay=getView(R.id.textView3);
        tv_testStatus=getView(R.id.textView4);
        startTest=getView(R.id.startTest);
        stopTest=getView(R.id.stopTest);
        dataResult=getView(R.id.dataResult);
        backMain=getView(R.id.backMain);
        pictureShow=getView(R.id.picture);
        tv_testWay.setText("测试门刚度");

        bindIntent = new Intent(this, MyService.class);
        bindService(bindIntent, connection, BIND_AUTO_CREATE);

        Intent intent=getIntent();
        name=intent.getStringExtra("name");


        if (name.equals("DM-2-1")){
            tv_testWay.setText(arrayWay[0]);
            dataResult.setVisibility(View.INVISIBLE);
            dataResult.setClickable(false);
            cantest = true;
        }else if(name.equals("DM-2-2")){
            tv_testWay.setText(arrayWay[1]);
            final String[] arrayClear = new String[] { "测试中分式电梯门", "测试旁开式电梯门"};
            Dialog alertDialog = new AlertDialog.Builder(this).
                    setTitle("请选择测试的电梯门类型？").
                    setIcon(R.mipmap.launcher)
                    .setSingleChoiceItems(arrayClear, 0, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(which == 0) {
                                type = 0;
                            }else if(which ==1){
                                type = 1;
                            }
                        }
                    }).
                            setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).
                            setNegativeButton("取消", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub
                                }
                            }).
                            create();
            alertDialog.show();
            cantest = true;
        }else{
            tv_testWay.setText(arrayWay[0]);
            cantest = false;
        }

        receiver = new MyReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction("android.intent.action.ontestActivity");
        filter.addAction("android.intent.action.connect");
        filter.addAction("android.intent.action.disconnect");
        filter.addAction("android.intent.action.connectfailed");
        OnTestActivity.this.registerReceiver(receiver, filter);

        IntentFilter filter1=new IntentFilter();
        filter1.addAction("android.bluetooth.device.action.ACL_CONNECTED");
        filter1.addAction("android.bluetooth.device.action.ACL_DISCONNECTED");
        OnTestActivity.this.registerReceiver(mReceiver, filter1);

        startTest.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isConnect) {
                    if (cantest) {
                        if (name.equals("DM-2-1")) {
                            mBinder.sendMessage("A1", BluetoothState.ONTESTACTIVITY);
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                            String dateStr = formatter.format(curDate);
                            Log.i("wp628254", "发送信号给下位机时间 = " + dateStr);
                            startTest.setTextColor(Color.RED);
                            dataResult.setVisibility(View.INVISIBLE);
                            dataResult.setClickable(false);
                            isStart=true;
                            textForce.setText("null");
                            textDis.setText("null");

                        } else if (name.equals("DM-2-2")) {
                            mBinder.sendMessage("A2", BluetoothState.ONTESTACTIVITY);
                            startTest.setTextColor(Color.RED);
                            dataResult.setVisibility(View.INVISIBLE);
                            dataResult.setClickable(false);
                            isStart=true;
                        }
                        startTest.setEnabled(false);
                    } else{
                        Toast.makeText(OnTestActivity.this, "连接的不是所测试的蓝牙设备，请重新连接设备", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(OnTestActivity.this, "未连接蓝牙设备", Toast.LENGTH_SHORT).show();
                }

            }
        });

        stopTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isStart) {
                    if (name.equals("DM-2-1")) {
                        mBinder.sendMessage("B1",BluetoothState.ONTESTACTIVITY);
                        startTest.setTextColor(Color.BLACK);
                        stopTest.setTextColor(Color.RED);
                        tv_testStatus.setText("数据上传中...");
                        isStart=false;
                    }else if(name.equals("DM-2-2")){
                        mBinder.sendMessage("B2",BluetoothState.ONTESTACTIVITY);
                        startTest.setTextColor(Color.BLACK);
                        stopTest.setTextColor(Color.RED);
                        tv_testStatus.setText("数据上传中...");
                        isStart=false;
                    }
                }else{
                    Toast.makeText(OnTestActivity.this, "还未开始测试", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dataResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  Intent intent = new Intent(OnTestActivity.this, ResultActivity.class);
                if (tv_testWay.getText().toString().equals(arrayWay[0])) {
                    String data = "Force";
                    intent.putExtra("extra_data", data);
                    startActivity(intent);
                } else if (tv_testWay.getText().toString().equals(arrayWay[1])) {
                    String data = "Speed";
                    intent.putExtra("extra_data", data);
                    startActivity(intent);
                }*/
            }
        });

        backMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        pictureShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // startActivity(new Intent(OnTestActivity.this, SendReceiveActivity.class));


            }
        });

    }


    //将得到的数据存储
    private void onSave() {
        //获取sd卡目录
        String sdpath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String appName = getString(R.string.app_name);
        if (tv_testWay.getText().toString().equals(arrayWay[0])) {
            String fileDir = sdpath + "/" + appName + "/data";
            File newfileDir = new File(fileDir);
            if (!newfileDir.exists()) {
                boolean isSuccess = newfileDir.mkdirs();
                System.out.println("isSuccess:" + isSuccess);
            }
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date curDate = new Date(System.currentTimeMillis());//获取当前时间
            String dateStr = formatter.format(curDate);
            String name = dateStr + "-" + String.valueOf(indexF) + ".ds";

            //保存图片到数据库
            MySeverityView severityView = new MySeverityView(OnTestActivity.this, m_filterData);
            bitmap = createViewBitmap(severityView);
            //bitmap = getViewBitmap(forceView);
            Log.i("cyy628254","bitmap = "+bitmap);
            pictureDB.initDataBaseF(db, bitmap, MyApplication.FORCE, name, MainActivity.s_mLiftId, MainActivity.s_mOperator, MainActivity.s_mLocation, fmax, fKin, energy);
            // pointsF.clear();

            indexF++;
            String path = fileDir + "/" + name;
            File newfile = new File(path);
            try {
                newfile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    public Bitmap createViewBitmap(View v) {
        Resources resources = this.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        int width = dm.widthPixels;
        int height = dm.heightPixels *10/16;
        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        String dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
        Log.i("cyy628254", "制作图片前 = " + dateStr);
        v.draw(canvas);
        String dateStr2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
        Log.i("cyy628254", "制作图片后 = " + dateStr2);
        return bitmap;
    }

    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        unregisterReceiver(mReceiver);
        unbindService(connection);
    }

    public void onStart() {
        super.onStart();
       /* dataResult.setVisibility(View.INVISIBLE);
        dataResult.setClickable(false);*/
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
                OnTestActivity.this.finish();
            }
        }

    };

    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if (intent.getAction().equals("android.intent.action.ontestActivity")) {
                Bundle bundle = intent.getExtras();
                String message = bundle.getString("msg");
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                String dateStr = formatter.format(curDate);
                Log.i("wp628254", "handler发送数据时间 = " + dateStr);
                handler.obtainMessage(0, 1, -1, message).sendToTarget();
            }else if(intent.getAction().equals("android.intent.action.connect")){
                Bundle bundle = intent.getExtras();
                String message=bundle.getString("msg");
                handler.obtainMessage(1, 1, -1, message).sendToTarget();
            }else if(intent.getAction().equals("android.intent.action.disconnect")){
                /*Bundle bundle = intent.getExtras();
                String message=bundle.getString("msg");
                handler.obtainMessage(2, 1, -1, message).sendToTarget();*/
            }else if(intent.getAction().equals("android.intent.action.connectfailed")){
                Bundle bundle = intent.getExtras();
                String message=bundle.getString("msg");
                handler.obtainMessage(3, 1, -1, message).sendToTarget();
            }
        }
    }


}
