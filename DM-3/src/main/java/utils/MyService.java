package utils;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 16-11-18.
 */
public class MyService extends Service {
    BluetoothSPP bt;
    private DiscoveryBinder mBinder=new DiscoveryBinder();
    private int state=0;
    private Intent intent;
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent1, int flags, int startId) {


        bt=new BluetoothSPP(MyService.this);

        if (!bt.isBluetoothEnabled()) {
            bt.enable();
        } else {
            if(!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_ANDROID);
            }
        }

        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            @Override
            public void onDataReceived(byte[] data, String message) {
                switch (state){
                    case BluetoothState.MAINACTIVITY:
                        intent = new Intent();
                        intent.putExtra("msg", message);
                        intent.setAction("android.intent.action.mainActivity");
                        sendBroadcast(intent);
                        break;

                    case BluetoothState.ONTESTACTIVITY:
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                        String dateStr = formatter.format(curDate);
                        Log.i("wp628254", "广播发送数据时间 = " + dateStr);
                        intent = new Intent();
                        intent.putExtra("msg", message);
                        intent.setAction("android.intent.action.ontestActivity");
                        sendBroadcast(intent);

                        break;

                    case BluetoothState.CESHIACTIVITY:
                        intent = new Intent();
                        intent.putExtra("msg", message);
                        intent.setAction("android.intent.action.ceshiActivity");
                        sendBroadcast(intent);
                        break;

                    case BluetoothState.DECFORCEACTIVITY:
                        intent = new Intent();
                        intent.putExtra("msg", message);
                        intent.setAction("android.intent.action.decForceActivity");
                        sendBroadcast(intent);
                        break;

                    case BluetoothState.DECSPEEDACTIVITY:
                        intent = new Intent();
                        intent.putExtra("msg", message);
                        intent.setAction("android.intent.action.decSpeedActivity");
                        sendBroadcast(intent);
                        Log.i("mtag", "发送广播的时间" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis())));
                        break;

                    case BluetoothState.IMPORTALLACTIVITY:
                        intent = new Intent();
                        intent.putExtra("msg", message);
                        intent.setAction("android.intent.action.ceshiActivity");
                        sendBroadcast(intent);
                        Log.i("mtag", "发送广播的时间" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis())));
                        break;
                }

            }
        });


        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            public void onDeviceConnected(String name, String address) {
                Toast.makeText(getApplicationContext(), "连接到蓝牙 " + name + "上", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra("msg", name);
                intent.setAction("android.intent.action.connect");
                sendBroadcast(intent);
            }

            public void onDeviceDisconnected() {

            }

            public void onDeviceConnectionFailed() {
                Toast.makeText(getApplicationContext(), "蓝牙连接失败", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra("msg", "蓝牙连接失败");
                intent.setAction("android.intent.action.connectfailed");
                sendBroadcast(intent);

            }
        });

        return super.onStartCommand(intent, flags, startId);
    }



    @Override
    public IBinder onBind(Intent intent) {
        Log.i("TAG", "onBind()");
        return mBinder;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("TAG", "onDestroy();");
        bt.stopService();
    }

    public class DiscoveryBinder extends Binder {

        //连接设备
        public void connectd(String deviceAddress,String deviceName){
          bt.connect(deviceAddress,deviceName);
        }

        //发送字符串
        public void sendMessage(String message, int activity){
           bt.send(message, true);
            state=activity;
        }
        //发送字符数组
        public void sendbytes(byte[] bytes){
            bt.send(bytes,false);
        }

        public void closeConnect(){
            bt.stopService();
        }

        public int  getDeviceName(){
            return bt.getServiceState();
        }
        public void setupService(){
            if(!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_ANDROID);
            }
        }
    }


}
