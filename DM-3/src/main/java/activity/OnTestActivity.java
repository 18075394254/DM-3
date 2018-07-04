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
import java.math.BigDecimal;
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
    private TextView tv_testWay;
    private TextView tv_testStatus;
    private Button startTest,stopTest,backMain,pictureShow;
    private Button dataResult;
    PictureDatabase pictureDB;
    SQLiteDatabase db;
    private int indexF=1;
    Bitmap bitmap;
    //测试压力数据的集合
    private ArrayList<Float> m_ForceData = new ArrayList<Float>();
    //测试位移数据的集合
    private ArrayList<Float> m_DisData = new ArrayList<Float>();
    //300N压力值
    private float force=0;
    //300N压力值下的位移值
    private float dis=0;
    private boolean isConnect=true;
    private boolean isStart=false;
    //将所有的测试数据拼接成一个字符串
    String totalData = "";
    //用来表示接收到的每个数据的值
    private float value=0;
    //数据是否合格的标志
    private int isQualified = 0;

    private Intent bindIntent;
    private MyReceiver receiver;
    String name=null;
    private boolean cantest=true;

    Calculate calculate=new Calculate();
    //将数据计算后得到的map数组
    HashMap map=new HashMap();
    private ImageView backimage;
    private TextView textForce;
    private TextView textDis;

    //每条数据的内容，用于写入文件中
    private String dataString ="";
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                m_DisData.clear();
                m_ForceData.clear();
                String message = (String) msg.obj;
                Log.i("wpcyy628254", "message = "+message );
                //下位机回复A1，表示仪器接收到测试信息，开始测试
                if (message.equals("A1") ) {
                    tv_testStatus.setText("正在测试中...");

                    //表示下位机将测试的三组数据，自己设置回复A2，并通过MyApplication.getString方法获取存入的数据
                }else if (message.equals("A2")) {
                    String msgdata = MyApplication.getString();
                    //将数据拼接起来，当测试完成后，解析数据绘制图形
                    totalData = totalData + msgdata;
                    Log.i("mtag", "msgdata" + msgdata);

                    //分隔字符串
                    String[] s = msgdata.split(",");
                    //三组数据(压力，位移，压力，位移，压力，位移)
                    float a = Float.parseFloat(s[0]) / 100;
                    float b = Float.parseFloat(s[2]) / 100;
                    float c = Float.parseFloat(s[4]) / 100;
                    float d= Float.parseFloat(s[1]) / 100;
                    float e = Float.parseFloat(s[3]) / 100;
                    float f = Float.parseFloat(s[5]) / 100;

                    float forceValue =  (a+b+c)/3;
                    BigDecimal forceValue2 = new BigDecimal(forceValue);
                    forceValue = forceValue2.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
                        //将压力平均值显示到文本中
                        textForce.setText(forceValue +"");


                    float disValue =  (d+e+f)/3;
                    BigDecimal disValue2 = new BigDecimal(disValue);
                    disValue = disValue2.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
                    //将位移平均值显示到文本中
                         textDis.setText(disValue + "");
                   // }
                    
                    //接收到仪器发送的B1,表示测试完成，开始解析数据
                }else if(message.equals("B1")) {
                    Log.i("mtag", "接收到B1时间" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis())));
                    tv_testStatus.setText("数据上传中...");
                    String[] s = totalData.split(",");
                    Log.i("points.size ", "s.length = " + s.length);
                    //s.length - 1是为了防止最后一个""信息影响数据解析
                         for (int i = 0; i < s.length -1; i++) {

                                 value = ((Float.parseFloat(s[i]) / 100));
                             dataString = dataString+value+",";
                             if (i%2 == 0) {
                                 m_ForceData.add(value);
                             }else{
                                 m_DisData.add(value);
                             }

                         }
                    if(m_DisData.size() != 0){
                       map =  calculate.getDisValue(m_DisData, m_ForceData, 300);
                        force = (float) map.get("forceValue");
                        dis = (float) map.get("disValue");
                        isQualified = (int) map.get("isQualified");

                        Log.i("2018-06-26 ", "isQualified = " + isQualified);
                        onSave();
                        totalData = "";
                        dataString ="";
                        map.clear();
                        tv_testStatus.setText("测试完成");
                        dataResult.setVisibility(View.VISIBLE);
                        dataResult.setClickable(true);
                        stopTest.setTextColor(Color.BLACK);
                    }
                    startTest.setEnabled(true);
                    Log.i("mtag", "数据处理完成时间 " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis())));


                }

            }else if(msg.what==1){
                isConnect=true;

                //表示蓝牙突然断开，就关闭测试界面
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



            tv_testWay.setText("测试电梯门刚度:");
            dataResult.setVisibility(View.INVISIBLE);
            dataResult.setClickable(false);
            cantest = true;


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
                            mBinder.sendMessage("A1", BluetoothState.ONTESTACTIVITY);
                            startTest.setTextColor(Color.RED);
                            dataResult.setVisibility(View.INVISIBLE);
                            dataResult.setClickable(false);
                            isStart=true;
                            textForce.setText("null");
                            textDis.setText("null");

                        }
                        startTest.setEnabled(false);


                } else {
                    Toast.makeText(OnTestActivity.this, "未连接蓝牙设备", Toast.LENGTH_SHORT).show();
                }

            }
        });

        stopTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isStart) {
                        mBinder.sendMessage("B1", BluetoothState.ONTESTACTIVITY);
                        startTest.setTextColor(Color.BLACK);
                        stopTest.setTextColor(Color.RED);
                    Log.i("mtag", "发送B1的时间 " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis())));
                        isStart=false;
                }else{
                    Toast.makeText(OnTestActivity.this, "还未开始测试", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dataResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OnTestActivity.this, ResultActivity.class);

                    startActivity(intent);

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

                startActivity(new Intent(OnTestActivity.this, SendReceiveActivity.class));


            }
        });

    }


    //将得到的数据存储
    private void onSave() {
        //获取sd卡目录
        String sdpath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String appName = getString(R.string.app_name);

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
            MySeverityView severityView = new MySeverityView(OnTestActivity.this, m_ForceData,m_DisData);
            bitmap = createViewBitmap(severityView);

            Log.i("cyy628254","bitmap = "+bitmap);
            pictureDB.initDataBase(db, bitmap, MyApplication.FORCEDIS, name, MainActivity.s_mLiftId, MainActivity.s_mOperator, MainActivity.s_mLocation, force, dis,isQualified);
            // pointsF.clear();

            indexF++;
            String path = fileDir + "/" + name;
            File newfile = new File(path);
            try {
                newfile.createNewFile();
                calculate.writeSetingsToFile(newfile, totalData);
            } catch (IOException e) {
                e.printStackTrace();
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
        v.draw(canvas);
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
                handler.obtainMessage(0, 1, -1, message).sendToTarget();
            }else if(intent.getAction().equals("android.intent.action.connect")){
                Bundle bundle = intent.getExtras();
                String message=bundle.getString("msg");
                handler.obtainMessage(1, 1, -1, message).sendToTarget();
            }else if(intent.getAction().equals("android.intent.action.disconnect")){

            }else if(intent.getAction().equals("android.intent.action.connectfailed")){
                Bundle bundle = intent.getExtras();
                String message=bundle.getString("msg");
                handler.obtainMessage(3, 1, -1, message).sendToTarget();
            }
        }
    }


}
