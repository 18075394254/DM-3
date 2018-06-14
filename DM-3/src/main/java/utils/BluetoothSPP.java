/*
 * Copyright (C) 2014 Akexorcist
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package utils;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

import model.Point;


@SuppressLint("NewApi")
public class BluetoothSPP {
    // Listener for Bluetooth Status & Connection
    private BluetoothStateListener mBluetoothStateListener = null;
    private OnDataReceivedListener mDataReceivedListener = null;
    private OnPointReceivedListener mPointReceivedListener = null;
    private BluetoothConnectionListener mBluetoothConnectionListener = null;
    private AutoConnectionListener mAutoConnectionListener = null;

    private String mesg;
    // Context from activity which call this class
    private Context mContext;
    
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;

    // Member object for the chat services
    private BluetoothService mChatService = null;
    
    // Name and Address of the connected device
    private String mDeviceName = null;
    private String mDeviceAddress = null;

    private boolean isAutoConnecting = false;
    private boolean isAutoConnectionEnabled = false;
    private boolean isConnected = false;
    private boolean isConnecting = false;
    private boolean isServiceRunning = false;
    
    private String keyword = "";
    private boolean isAndroid = BluetoothState.DEVICE_ANDROID;
    
    private BluetoothConnectionListener bcl;
    private int c = 0;

    public BluetoothSPP(Context context) {
        mContext = context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }
    
    public interface BluetoothStateListener {
        void onServiceStateChanged(int state);
    }
    
    public interface OnDataReceivedListener {
        void onDataReceived(byte[] data, String message);
    }
    public interface OnPointReceivedListener {
        void onPointReceived(byte[] data, ArrayList<Point> points);
    }
    public interface BluetoothConnectionListener {
        void onDeviceConnected(String name, String address);
        void onDeviceDisconnected();
        void onDeviceConnectionFailed();
    }
    
    public interface AutoConnectionListener {
        void onAutoConnectionStarted();
        void onNewConnection(String name, String address);
    }



    public boolean isBluetoothAvailable() {
        try {
            if (mBluetoothAdapter == null || mBluetoothAdapter.getAddress().equals(null))
                return false;
        } catch (NullPointerException e) {
             return false;
        }
        return true;
    }
    
    public boolean isBluetoothEnabled() {
        return mBluetoothAdapter.isEnabled();
    }


    public boolean isServiceAvailable() {
        return mChatService != null;
    }
    
    public boolean isAutoConnecting() {
        return isAutoConnecting;
    }
    
    public boolean startDiscovery() {
        return mBluetoothAdapter.startDiscovery();
    }
    
    public boolean isDiscovery() {
        return mBluetoothAdapter.isDiscovering();
    }
    
    public boolean cancelDiscovery() {
        return mBluetoothAdapter.cancelDiscovery();
    }
    
    public void setupService() {
        mChatService = new BluetoothService(mContext, mHandler);
    }
    
    public BluetoothAdapter getBluetoothAdapter() {
        return mBluetoothAdapter;
    }
    
    public int getServiceState() {
        if(mChatService != null) 
            return mChatService.getState();
        else 
            return -1;
    }


    public void startService(boolean isAndroid) {
        if (mChatService != null) {
            if (mChatService.getState() == BluetoothState.STATE_NONE) {
                isServiceRunning = true;
                mChatService.start(isAndroid);
                BluetoothSPP.this.isAndroid = isAndroid;
            }
        }
    }
    
    public void stopService() {
        if (mChatService != null) {
            isServiceRunning = false;
            mChatService.stop();
        }
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (mChatService != null) {
                    isServiceRunning = false;
                    mChatService.stop();
                }
            }
        }, 500);
    }
    
    public void setDeviceTarget(boolean isAndroid) {
        stopService();
        startService(isAndroid);
        BluetoothSPP.this.isAndroid = isAndroid;
    }


    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothState.MESSAGE_WRITE:


                    break;
                case BluetoothState.MESSAGE_READ:
                    StringBuilder sb=new StringBuilder();
                    byte[] bytes=new byte[1024];
                    String message= (String) msg.obj;
                    if (message != null) {
                        if (mDataReceivedListener != null) {
                            mDataReceivedListener.onDataReceived(bytes, message);
                            Log.i("wp123", "messageData 接收到数据了");
                        }
                    }

                   break;


            case BluetoothState.MESSAGE_DEVICE_NAME:
                mDeviceName = msg.getData().getString(BluetoothState.DEVICE_NAME);
                mDeviceAddress = msg.getData().getString(BluetoothState.DEVICE_ADDRESS);
                if(mBluetoothConnectionListener != null)
                    mBluetoothConnectionListener.onDeviceConnected(mDeviceName, mDeviceAddress);
                isConnected = true;
                break;
            case BluetoothState.MESSAGE_TOAST:
                Toast.makeText(mContext, msg.getData().getString(BluetoothState.TOAST)
                        , Toast.LENGTH_SHORT).show();
                break;
            case BluetoothState.MESSAGE_STATE_CHANGE:
                if(mBluetoothStateListener != null)
                    mBluetoothStateListener.onServiceStateChanged(msg.arg1);
                if(isConnected && msg.arg1 != BluetoothState.STATE_CONNECTED) {
                    if(mBluetoothConnectionListener != null)
                        mBluetoothConnectionListener.onDeviceDisconnected();
                    if(isAutoConnectionEnabled) {
                        isAutoConnectionEnabled = false;
                        autoConnect(keyword);
                    }
                    isConnected = false;
                    mDeviceName = null;
                    mDeviceAddress = null;
                }
                
                if(!isConnecting && msg.arg1 == BluetoothState.STATE_CONNECTING) {
                    isConnecting = true;
                } else if(isConnecting) {
                    if(msg.arg1 != BluetoothState.STATE_CONNECTED) {
                        if(mBluetoothConnectionListener != null)
                            mBluetoothConnectionListener.onDeviceConnectionFailed();
                    }
                    isConnecting = false;
                }
                break;
            }
        }
    };
    
    public void stopAutoConnect() {
        isAutoConnectionEnabled = false;
    }
    
    public void connect(Intent data) {
        String address = data.getExtras().getString(BluetoothState.EXTRA_DEVICE_ADDRESS);
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        mChatService.connect(device);

    }


    public void connect(String address,String deviceName) {
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (deviceName.contains("DM-2-1")) {

            mChatService.connect(device);


            Log.i("wppp","device.getBondState() "+device.getBondState() );

        } else if (deviceName.contains("DM-2-2")) {

            mChatService.connect(device);
        }
    }


    
    public void disconnect() {
        if(mChatService != null) {
            isServiceRunning = false;
            mChatService.stop();
            if(mChatService.getState() == BluetoothState.STATE_NONE) {
                isServiceRunning = true;
                mChatService.start(BluetoothSPP.this.isAndroid);
            }
        }
    }
    
    public void setBluetoothStateListener (BluetoothStateListener listener) {
        mBluetoothStateListener = listener;
    }

    public void setOnDataReceivedListener (OnDataReceivedListener listener) {
        mDataReceivedListener = listener;
    }

    public void setOnPointReceivedListener (OnPointReceivedListener listener) {
        mPointReceivedListener = listener;
    }

    public void setBluetoothConnectionListener (BluetoothConnectionListener listener) {
        mBluetoothConnectionListener = listener;
    }
    
    public void setAutoConnectionListener(AutoConnectionListener listener) {
        mAutoConnectionListener = listener;
    }
    
    public void enable() {
        mBluetoothAdapter.enable();
    }
    
    public void send(byte[] data, boolean CRLF) {
        if(mChatService.getState() == BluetoothState.STATE_CONNECTED) {
            if(CRLF) {
                byte[] data2 = new byte[data.length + 2];
                for(int i = 0 ; i < data.length ; i++) 
                    data2[i] = data[i];
                data2[data2.length - 2] = 0x0A;
                data2[data2.length - 1] = 0x0D;
                Log.i("wp123","byte[] data2"+DataTrans.BytesToString(data));
                mChatService.write(data2);
            } else {
                mChatService.write(data);
                Log.i("wd123", "byte[] data=" + DataTrans.BytesToString(data));
            }
        }
    }
    
    public void send(String data, boolean CRLF) {
        if(mChatService.getState() == BluetoothState.STATE_CONNECTED) {
            if(CRLF)
              //  data += "\r\n";
            Log.i("wp123","data  "+data);
            byte[] bt=data.getBytes();
            Log.i("wp123","btlength  "+bt.length);
            mChatService.write(data.getBytes());
        }
    }
    
    public String getConnectedDeviceName() {
        return mDeviceName;
    }
    
    public String getConnectedDeviceAddress() {
        return mDeviceAddress;
    }
    
    public String[] getPairedDeviceName() {
        int c = 0;
        Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();  
        String[] name_list = new String[devices.size()];
        for(BluetoothDevice device : devices) {  
            name_list[c] = device.getName();
            c++;
        }  
        return name_list;
    }
    
    public String[] getPairedDeviceAddress() {
        int c = 0;
        Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();  
        String[] address_list = new String[devices.size()];
        for(BluetoothDevice device : devices) {  
            address_list[c] = device.getAddress();
            c++;
        }  
        return address_list;
    }

    public void autoConnect(String keywordName) {

    }

    public void pair(String strAddr, String strPsw,BluetoothAdapter bluetoothAdapter,BluetoothDevice device) {
       // boolean result = false;

        bluetoothAdapter.cancelDiscovery();

        if (!bluetoothAdapter.isEnabled())
        {
            bluetoothAdapter.enable();
        }

        if (!BluetoothAdapter.checkBluetoothAddress(strAddr))
        { // 检查蓝牙地址是否有效

            Log.d("mylog", "devAdd un effient!");
        }

        if (device.getBondState() == BluetoothDevice.BOND_NONE) {

            try {
                ClsUtils.setPin(device.getClass(),device,"1234");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                ClsUtils.cancelPairingUserInput(device.getClass(), device);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Log.d("BlueToothTestActivity", "未配对......");
    }else if(device.getBondState() == BluetoothDevice.BOND_BONDING){
            Log.d("BlueToothTestActivity", "正在配对......");

    }else if(device.getBondState() == BluetoothDevice.BOND_BONDED){
            Log.d("BlueToothTestActivity", "完成配对");
            // connect(device);//连接设备
           // mChatService.connect(device);
        }

      /*  if (device.getBondState() != BluetoothDevice.BOND_BONDED)
        {
            try
            {
                Log.d("mylog", "NOT BOND_BONDED");
                ClsUtils.setPin(device.getClass(), device, strPsw); // 手机和蓝牙采集器配对
                ClsUtils.createBond(device.getClass(), device);
                // remoteDevice = device; // 配对完毕就把这个设备对象传给全局的remoteDevice
                result = true;
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block

                Log.d("mylog", "setPiN failed!");
                e.printStackTrace();
            } //

        }
        else
        {
            Log.d("mylog", "HAS BOND_BONDED");
            try {
                ClsUtils.createBond(device.getClass(), device);
                ClsUtils.setPin(device.getClass(), device, strPsw); // 手机和蓝牙采集器配对
                ClsUtils.createBond(device.getClass(), device);
                //  remoteDevice = device; // 如果绑定成功，就直接把这个设备对象传给全局的remoteDevice
                result = true;
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                Log.d("mylog", "setPiN failed!");
                e.printStackTrace();
            }
        }*/
       // return result;
    }
}
