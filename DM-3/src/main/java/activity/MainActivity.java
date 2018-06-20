package activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.dm_3.R;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.util.ArrayList;

import controller.BaseActivity;
import controller.MyApplication;
import utils.BluetoothState;
import utils.ClsUtils;
import utils.MyService;


public class MainActivity extends BaseActivity {
    //配置信息
    static String s_mLiftId="";
    static String s_mOperator="";
    static String s_mLocation="";
    //连接蓝牙按钮
    private Button connectButton;
    //设置信息按钮
    private Button setInfoButton;
    //开始测试按钮
    private Button onTestButton;
    //数据导入按钮
    private Button importButton;
    //打开文件按钮
    private Button openFileButton;
    //清空数据按钮
    private Button clearButton;
    //显示蓝牙状态信息的文本
    private TextView blueToothMsg;
    //蓝牙地址
    private String deviceAddress;
    //蓝牙名称
    private String deviceName;
    //Service与Activity通信的介质
    private MyService.DiscoveryBinder mBinder;
    //绑定Service的intent
    private Intent bindIntent;
    //蓝牙是否已经连接的标志
    private boolean isConnect = false;
    //蓝牙名称
    private String name = null;
    //侧滑菜单
    private SlidingMenu menu;

    private MyReceiver receiver;
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


    //用来处理数据以及显示蓝牙状态的handler
    private Handler mhandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //蓝牙连接完成
            if (msg.what == 1){
                blueToothMsg.setText((String)msg.obj);
                isConnect=true;
                //蓝牙连接失败
            }else if(msg.what==2){
                blueToothMsg.setText((String)msg.obj);
                mBinder.closeConnect();
                isConnect=false;
                //蓝牙断开连接
            }else if(msg.what ==3){
                blueToothMsg.setText((String)msg.obj);
                isConnect=false;
            }else{

            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connectButton = getView(R.id.connect);
        setInfoButton = getView(R.id.setinfo);
        onTestButton = getView(R.id.ontest);
        importButton = getView(R.id.importdata);
        openFileButton = getView(R.id.openfile);
        clearButton = getView(R.id.cleardata);
        blueToothMsg = getView(R.id.msg);
        //绑定Service
        bindIntent = new Intent(this, MyService.class);
        bindService(bindIntent, connection, BIND_AUTO_CREATE);


        //蓝牙状态以及接收数据的广播
        receiver = new MyReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction("android.intent.action.mainActivity");
        filter.addAction("android.intent.action.connect");
        filter.addAction("android.intent.action.disconnect");
        filter.addAction("android.intent.action.connectfailed");
        MainActivity.this.registerReceiver(receiver, filter);

        //监听系统蓝牙状态的广播
        IntentFilter filter1=new IntentFilter();
        filter1.addAction("android.bluetooth.device.action.ACL_CONNECTED");
        filter1.addAction("android.bluetooth.device.action.ACL_DISCONNECTED");
        MainActivity.this.registerReceiver(mReceiver, filter1);


        // 注册Receiver来获取蓝牙设备相关的结果
      /*  IntentFilter intent = new IntentFilter();
        intent.addAction(BluetoothDevice.ACTION_FOUND);// 用BroadcastReceiver来取得搜索结果
        intent.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intent.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        intent.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(searchDevices, intent);*/

        //按钮的监听
        onClick();
        //获得设置的参数信息的方法
        loadConfig();
        //侧滑菜单的设置
        onSlidingMenu();
    }
    //按钮的监听事件
    private void onClick(){
        //蓝牙连接按钮监听
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到搜索蓝牙界面，并有返回值
                startActivityForResult(new Intent(MainActivity.this, DeviceListActivity.class), BluetoothState.REQUEST_CONNECT_DEVICE);
            }
        });
        //设置信息按钮监听
        setInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            startActivity(new Intent(MainActivity.this,ParaSettingActivity.class));
            }
        });
        //开始测试按钮监听
        onTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnect) {
                    Intent intent = new Intent(MainActivity.this, OnTestActivity.class);
                    intent.putExtra("name", name);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "未连接设备蓝牙", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //导入数据按钮监听
        importButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnect) {
                    Intent intent = new Intent(MainActivity.this, ChooseDirActivity.class);
                    intent.putExtra("name", name);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "未连接设备蓝牙", Toast.LENGTH_SHORT).show();
                }

            }
        });   //打开文件按钮监听
        openFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,OpenAllActivity.class));
            }
        });
        //清空数据按钮监听
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnect) {
                    mBinder.sendMessage("C1", BluetoothState.MAINACTIVITY);
                } else {
                    Toast.makeText(MainActivity.this, "未连接设备蓝牙", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    //侧滑菜单
    private void onSlidingMenu() {
        // configure the SlidingMenu
        menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        // 设置触摸屏幕的模式
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setShadowDrawable(R.drawable.shadow);

        // 设置滑动菜单视图的宽度
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        // 设置渐入渐出效果的值
        menu.setFadeDegree(0.35f);
        /**
         * SLIDING_WINDOW will include the Title/ActionBar in the content
         * section of the SlidingMenu, while SLIDING_CONTENT does not.
         */
        //把滑动菜单添加进所有的Activity中，可选值SLIDING_CONTENT ， SLIDING_WINDOW
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        //为侧滑菜单设置布局
        menu.setMenu(R.layout.leftmenu);

        ListView listView;
        listView= (ListView) findViewById(R.id.listView);
        Button declare_menu= (Button) findViewById(R.id.declare_menu);

        declare_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(MainActivity.this,DeclareActivity.class));
            }
        });
        ArrayList<String> list=new ArrayList<>();
        list.add("连接蓝牙");
        list.add("断开蓝牙");
        list.add("使用说明");
        list.add("设备详情");
        list.add("清空数据");
        list.add("退出程序");
        // ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list);

        listView.setAdapter(new ArrayAdapter(this,R.layout.slidelist,list));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
                    case 0:
                        startActivityForResult(new Intent(MainActivity.this, DeviceListActivity.class), BluetoothState.REQUEST_CONNECT_DEVICE);
                        break;
                    case 1:
                        if (isConnect) {
                            mBinder.closeConnect();
                            Toast.makeText(MainActivity.this, "正在断开蓝牙连接", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(MainActivity.this, "蓝牙未连接", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 2:
                       // startActivity(new Intent(MainActivity.this,UseExplainActivity.class));
                        break;
                    case 3:
                        //startActivity(new Intent(MainActivity.this,DeviceDetailsActivity.class));
                        break;
                    case 4:
                        if (isConnect) {

                                    mBinder.sendMessage("C1",BluetoothState.MAINACTIVITY);


                        } else {
                            Toast.makeText(MainActivity.this, "未连接设备蓝牙", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 5:
                        Dialog alertDialog = new AlertDialog.Builder(MainActivity.this).
                                setTitle("确定要退出程序吗？").
                                setIcon(R.mipmap.launcher).
                                setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        System.exit(0);
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
                        break;
                }
            }
        });

    }
    public void onStart() {
        super.onStart();
        //mBinder.setupService();
    }

    //页面摧毁时调用的方法
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        unregisterReceiver(mReceiver);
        unbindService(connection);
        unregisterReceiver(searchDevices);
    }


    //当搜索蓝牙界面关闭时，接收蓝牙搜索界面的返回信息
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if(resultCode == Activity.RESULT_OK) {
                //得到蓝牙的地址
                deviceAddress = data.getExtras().getString(BluetoothState.EXTRA_DEVICE_ADDRESS);
                //得到蓝牙的名称
                deviceName = data.getExtras().getString(BluetoothState.EXTRA_DEVICE_NAME);
                //判断是不是目标蓝牙，是的话就配对连接，不是就提示
               // if (deviceName.contains("DM-3") || deviceName.contains("DM-3")) {
                    // mBinder.connectd(deviceAddress, deviceName);
                    BluetoothAdapter btAdapt = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice btDev = btAdapt.getRemoteDevice(deviceAddress);
                    try {
                        Boolean returnValue = false;
                        mBinder.connectd(deviceAddress, deviceName);
                     /*   if (btDev.getBondState() == BluetoothDevice.BOND_NONE) {
                            // Toast.makeText(this, "远程设备发送蓝牙配对请求", Toast.LENGTH_SHORT).show();
                            //这里只需要createBond就行了
                            ClsUtils.createBond(btDev.getClass(), btDev);

                        }else if(btDev.getBondState() == BluetoothDevice.BOND_BONDED){
                            mBinder.connectd(deviceAddress, deviceName);
                            //Toast.makeText(this," ....正在连接..", Toast.LENGTH_SHORT).show();
                        }*/
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
               /* } else {
                    Toast.makeText(this, "连接的不是测试仪器的蓝牙，请重新选择！", Toast.LENGTH_SHORT).show();
                }*/
            }


        } else if(requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if(resultCode == Activity.RESULT_OK) {

            } else {
                Toast.makeText(getApplicationContext()
                        , "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
    //获得设置的参数信息
    public void loadConfig()
    {
        SharedPreferences sp = getSharedPreferences("info", Context.MODE_PRIVATE);
        s_mLiftId = sp.getString("liftid", "");
        s_mOperator = sp.getString("operator", "");
        s_mLocation = sp.getString("location", "");
    }
    //重写系统返回键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Dialog alertDialog = new AlertDialog.Builder(this).
                    setTitle("确定要退出程序吗？").
                    setIcon(R.mipmap.launcher).
                    setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
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
        }
        return super.onKeyDown(keyCode, event);
    }

    //表示蓝牙连接状态以及接收蓝牙发送数据的广播
    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            //接收数据信息
            if (intent.getAction().equals("android.intent.action.mainActivity")) {
                Bundle bundle = intent.getExtras();
                String message = bundle.getString("msg");
                mhandler.obtainMessage(0, 1, -1, message).sendToTarget();
                //蓝牙连接信息
            }else if(intent.getAction().equals("android.intent.action.connect")){
                Bundle bundle = intent.getExtras();
                String message=bundle.getString("msg");
                name=message;
                Log.i("Tag", "name = " + name);
                String message1="连接到蓝牙 " + message + "上";
                mhandler.obtainMessage(1, 1, -1, message1).sendToTarget();
                //蓝牙断开连接的状态监听不到，在下方用监听系统的蓝牙状态判断了
            }else if(intent.getAction().equals("android.intent.action.disconnect")){

                    //蓝牙连接失败的信息
            }else if(intent.getAction().equals("android.intent.action.connectfailed")){
                Bundle bundle = intent.getExtras();
                String message=bundle.getString("msg");
                mhandler.obtainMessage(3, 1, -1, message).sendToTarget();
            }
        }
    }

    //监听系统的蓝牙断开信息
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive (Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d("aaa", device.getName() + " ACTION_ACL_CONNECTED");
            } else if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
                Log.d("aaa", " ACTION_ACL_DISCONNECTED");
                Toast.makeText(MyApplication.getContext(), "蓝牙断开连接", Toast.LENGTH_SHORT).show();
                String message1="蓝牙断开连接";

                mhandler.obtainMessage(2, 1, -1, message1).sendToTarget();
            }
        }

    };

    private final BroadcastReceiver searchDevices = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            BluetoothDevice device = null;
            // 搜索设备时，取得设备的MAC地址
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() == BluetoothDevice.BOND_NONE) {

                }
            }else if(BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)){
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                switch (device.getBondState()) {
                    case BluetoothDevice.BOND_BONDING:
                        Log.d("BlueToothTestActivity", "正在配对......");
                        break;
                    case BluetoothDevice.BOND_BONDED:
                        Log.d("BlueToothTestActivity", "完成配对");
                        mBinder.connectd(deviceAddress, deviceName);
                        break;
                    case BluetoothDevice.BOND_NONE:
                        Log.d("BlueToothTestActivity", "取消配对");
                    default:
                        break;
                }
            }

        }
    };
}
