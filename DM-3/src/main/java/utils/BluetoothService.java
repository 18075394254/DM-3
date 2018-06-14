/*
 * Copyright (C) 2009 The Android Open Source Project
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
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import controller.MyApplication;

@SuppressLint("NewApi")
public class BluetoothService {
    // Debugging
    private static final String TAG = "Bluetooth Service";
    
    // Name for the SDP record when creating server socket
    private static final String NAME_SECURE = "Bluetooth Secure";

    //接收下位机数据分类
    private static final int RECEIVE_COMMAND = 1;
    private static final int RECEIVE_FORCE_SPEED_VALUE = 2;
    private static final int RECEIVE_CESHI = 3;
    private static final int RECEIVE_ALLSPEEDDATA = 4;
    private static final int RECEIVE_DEC_FORCE_SPEED_RATE = 5;
    private static final int RECEIVE_FORCE_SPEED_TESTVALUE = 6;
    //接收三组数据的状态
    private static final int RECEIVE_THREEDATA = 7;
    private static final int RECEIVE_ITIME = 9;
    //初始值设为接收命令状态
    private int receiveState = RECEIVE_COMMAND;

    // Unique UUID for this application
    private static final UUID UUID_ANDROID_DEVICE =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final UUID UUID_OTHER_DEVICE =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    
    // Member fields
    private final BluetoothAdapter mAdapter;
    private  Handler mHandler;
    private AcceptThread mSecureAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private int mState;
    private boolean isAndroid = BluetoothState.DEVICE_ANDROID;

    ArrayList<String> forceList=new ArrayList<>();
    private StringBuilder sb=new StringBuilder();
    private int count=0;
    private int index=0;
    byte[] all=new byte[1024*1024];
    private int abc=0;
    private int flag=0;


    // Constructor. Prepares a new BluetoothChat session
    // context : The UI Activity Context
    // handler : A Handler to send messages back to the UI Activity
    public BluetoothService(Context context, Handler handler) {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = BluetoothState.STATE_NONE;
        mHandler = handler;
    }

    
    // Set the current state of the chat connection
    // state : An integer defining the current connection state
    private synchronized void setState(int state) {
        Log.d("TAG", "setState() " + mState + " -> " + state);
       // Toast.makeText(MyApplication.getContext(), "setState() = " + state, Toast.LENGTH_SHORT).show();
        mState = state;
        
        // Give the new state to the Handler so the UI Activity can update
        mHandler.obtainMessage(BluetoothState.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    // Return the current connection state. 
    public synchronized int getState() {
        return mState;
    }

    // Start the chat service. Specifically start AcceptThread to begin a
    // session in listening (server) mode. Called by the Activity onResume() 
    public synchronized void start(boolean isAndroid) {
        // Cancel any thread attempting to make a connection
        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}
        
        setState(BluetoothState.STATE_LISTEN);
        
        // Start the thread to listen on a BluetoothServerSocket
        if (mSecureAcceptThread == null) {
            mSecureAcceptThread = new AcceptThread(isAndroid);
            mSecureAcceptThread.start();
            BluetoothService.this.isAndroid = isAndroid;
        }
    }

    // Start the ConnectThread to initiate a connection to a remote device
    // device : The BluetoothDevice to connect
    // secure : Socket Security type - Secure (true) , Insecure (false)
    public synchronized void connect(BluetoothDevice device) {
        // Cancel any thread attempting to make a connection
        if (mState == BluetoothState.STATE_CONNECTING) {
            if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
        }
        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        setState(BluetoothState.STATE_CONNECTING);
    }

    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection
     * @param socket  The BluetoothSocket on which the connection was made
     * @param device  The BluetoothDevice that has been connected
     */
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice
            device, final String socketType) {
        // Cancel the thread that completed the connection
        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        // Cancel the accept thread becau se we only want to connect to one device
        if (mSecureAcceptThread != null) {
            mSecureAcceptThread.cancel();
            mSecureAcceptThread = null;
        }

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket, socketType);
        mConnectedThread.start();

        // Send the name of the connected device back to the UI Activity
        Message msg = mHandler.obtainMessage(BluetoothState.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(BluetoothState.DEVICE_NAME, device.getName());
        bundle.putString(BluetoothState.DEVICE_ADDRESS, device.getAddress());
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        setState(BluetoothState.STATE_CONNECTED);
    }

    // Stop all threads
    public synchronized void stop() {
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        if (mSecureAcceptThread != null) {
            mSecureAcceptThread.cancel();
            mSecureAcceptThread.kill();
            mSecureAcceptThread = null;
        }
        setState(BluetoothState.STATE_NONE);
    }

    // Write to the ConnectedThread in an unsynchronized manner
    // out : The bytes to write
    public void  write(byte[] out) {
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != BluetoothState.STATE_CONNECTED) return;
            r = mConnectedThread;
        }
        // Perform the write unsynchronized
        r.write(out);
    }

    // Indicate that the connection attempt failed and notify the UI Activity
    private void connectionFailed() {
        // Start the service over to restart listening mode
        BluetoothService.this.start(BluetoothService.this.isAndroid);
    }

    // Indicate that the connection was lost and notify the UI Activity
    private void connectionLost() {
        // Start the service over to restart listening mode
        BluetoothService.this.start(BluetoothService.this.isAndroid);
    }

    // This thread runs while listening for incoming connections. It behaves
    // like a server-side client. It runs until a connection is accepted
    // (or until cancelled)
    private class AcceptThread extends Thread {
        // The local server socket
        private BluetoothServerSocket mmServerSocket;
        private String mSocketType;
        boolean isRunning = true;

        public AcceptThread(boolean isAndroid) {
            BluetoothServerSocket tmp = null;

            // Create a new listening server socket
            try {
                if(isAndroid)
                    tmp = mAdapter.listenUsingRfcommWithServiceRecord(NAME_SECURE, UUID_ANDROID_DEVICE);
                else
                    tmp = mAdapter.listenUsingRfcommWithServiceRecord(NAME_SECURE, UUID_OTHER_DEVICE);
            } catch (IOException e) { }
            mmServerSocket = tmp;
        }

        public void run() {
            setName("AcceptThread" + mSocketType);
            BluetoothSocket socket = null;

            // Listen to the server socket if we're not connected
            while (mState != BluetoothState.STATE_CONNECTED && isRunning) {
                try {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    break;
                }

                // If a connection was accepted
                if (socket != null) {
                    synchronized (BluetoothService.this) {
                        switch (mState) {
                        case BluetoothState.STATE_LISTEN:
                        case BluetoothState.STATE_CONNECTING:
                            // Situation normal. Start the connected thread.
                            connected(socket, socket.getRemoteDevice(),
                                    mSocketType);
                            break;
                        case BluetoothState.STATE_NONE:
                        case BluetoothState.STATE_CONNECTED:
                            // Either not ready or already connected. Terminate new socket.
                            try {
                                socket.close();
                            } catch (IOException e) { }
                            break;
                        }
                    }
                }
            }
        }

        public void cancel() {
            try {
                mmServerSocket.close();
                mmServerSocket = null;
            } catch (IOException e) { }
        }

        public void kill() {
            isRunning = false;
        }
    }


    // This thread runs while attempting to make an outgoing connection
    // with a device. It runs straight through
    // the connection either succeeds or fails
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private String mSocketType;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                if(BluetoothService.this.isAndroid)
                    tmp = device.createRfcommSocketToServiceRecord(UUID_ANDROID_DEVICE);
                else
                    tmp = device.createRfcommSocketToServiceRecord(UUID_OTHER_DEVICE);
            } catch (IOException e) { }
            mmSocket = tmp;
        }

        public void run() {
            // Always cancel discovery because it will slow down a connection
            mAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                Log.i("wp123","正在连接蓝牙");
                mmSocket.connect();
                Log.i("wp123","蓝牙连接上了");
            } catch (IOException e) {
                // Close the socket
                try {
                    mmSocket.close();
                } catch (IOException e2) { }
                connectionFailed();
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (BluetoothService.this) {
                mConnectThread = null;
            }

            // Start the connected thread
            connected(mmSocket, mmDevice, mSocketType);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    // This thread runs during a connection with a remote device.
    // It handles all incoming and outgoing transmissions.
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket, String socketType) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer;
            int bytes;
            String msg ;
            int availableBytes;
            byte[] bt;
            // Keep listening to the InputStream while connected
            while (true) {
                    try {
                        //获得的有效字节数
                        availableBytes = mmInStream.available();

                        if(availableBytes > 0) {
                            abc+=availableBytes;

                           buffer = new byte[availableBytes];

                            // buffer store for the stream
                            // Read from the InputStream
                            bytes = mmInStream.read(buffer);
                            Log.i("wp628254", "bytes = " + bytes);
                            if(bytes>0) {
                                bt = new byte[bytes];
                                for(int i=0;i<bytes;i++){
                                    bt[i]=buffer[i];
                                }
                                switch(receiveState){

                                    case RECEIVE_COMMAND:
                                        count++;
                                        for(int i=0;i<availableBytes;i++){
                                            all[index]=bt[i];
                                            index++;
                                        }
                                        if(index>1) {
                                            msg = new String(all, 0, index);
                                            mHandler.obtainMessage(BluetoothState.MESSAGE_READ, bytes, -1, msg).sendToTarget();
                                            Log.i("wpcyy628254", "msg = " + msg);
                                            if (msg.equals("A1")){
                                                receiveState =  RECEIVE_THREEDATA;
                                            }
                                            index = 0;
                                            count = 0;
                                            abc = 0;
                                            sb.delete(0, sb.length());
                                        }

                                        break;

                                    //每次测试时接收的三组数据
                                    case RECEIVE_THREEDATA:
                                        Log.i("wpcyy628254", "接收到数据" );
                                        count++;
                                        for(int i=0;i<availableBytes;i++){
                                            all[index]=bt[i];
                                            index++;
                                        }
                                        Log.i("wpcyy628254", "index = " + index);
                                        if (index == 12){
                                            for (int i = 0;i < index;i+=2){
                                             sb.append(DataTrans.TwoBytesToInt(all[i], all[i + 1]) + " , ");
                                            }
                                            //将字符串赋值给全局变量
                                            MyApplication.setString(sb.toString());
                                            //发送A2给测试界面，提示收到三组小数据，可以解析显示了
                                            mHandler.obtainMessage(BluetoothState.MESSAGE_READ, bytes, -1, "A2").sendToTarget();
                                            all=new byte[1024*1024];
                                            index=0;
                                            count=0;
                                            abc=0;
                                            sb.delete(0,sb.length());
                                        }

                                        break;


                                    //测试压力和速度
                                    case RECEIVE_FORCE_SPEED_VALUE:

                                        count++;
                                        if (count == 1){
                                            String dateStr2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
                                            Log.i("wp628254", "刚接收数据的时间 = " + dateStr2);
                                        }
                                        for(int i=0;i < availableBytes;i++){
                                            all[index]=bt[i];
                                            index++;
                                        }
                                        Log.i("wp628254", "index = " + index);
                                        //#+数据长度(4Byte)+’ST’(2Byte)+文档编号(2Byte)+数据内容.....+’END’(3Byte)+*
                                        if(index>15) {
                                            byte[] last=new byte[6];
                                            for(int i=0;i<6;i++){
                                                last[i]=all[index-6+i];
                                            }
                                            Log.i("wp123","? END*B2 ? ="+DataTrans.BytesToString(last));
                                            if (DataTrans.BytesToString(last).equals("END*B1") || DataTrans.BytesToString(last).equals("END*B2")) {
                                                byte[] length=new byte[4];
                                                for(int i=0;i<4;i++){
                                                    length[i]=all[i+1];
                                                }
                                                int datalength=DataTrans.byte2int(length);
                                                int datanumber=DataTrans.TwoBytesToInt(all[7],all[8]);
                                                byte[] bt0=new byte[1];
                                                bt0[0]=all[0];
                                               // sb.append(DataTrans.BytesToString(bt0)+" "+datalength+" ");
                                                Log.i("wp123", "datalength =" + datalength + " " + DataTrans.BytesToString(bt0));
                                                byte[] bt56=new byte[2];
                                                bt56[0]=all[5];
                                                bt56[1]=all[6];
                                               // sb.append(DataTrans.BytesToString(bt56) + " " + datanumber + " ");
                                                Log.i("wp123", "datanumber =" + DataTrans.BytesToString(bt56) + " " + datanumber + " ");
                                                for (int i=9;i<index-6;i+=2){
                                                    if(i<index-6-2) {
                                                        sb.append(DataTrans.TwoBytesToInt(all[i], all[i + 1]) + " , ");
                                                    }else{
                                                        sb.append(DataTrans.TwoBytesToInt(all[i], all[i + 1]));
                                                    }
                                                }
                                               // sb.append(DataTrans.BytesToString(last));
                                                mHandler.obtainMessage(BluetoothState.MESSAGE_READ, bytes, -1, sb.toString()).sendToTarget();
                                                all=new byte[1024*1024];
                                                index=0;
                                                count=0;
                                                abc=0;
                                                sb.delete(0,sb.length());

                                            }else{
                                               // mHandler.obtainMessage(BluetoothState.MESSAGE_READ, bytes, -1, "数据接收错误").sendToTarget();
                                            }
                                        }

                                        break;
                                   //发送E1 让测力仪设备持续测试
                                    case RECEIVE_FORCE_SPEED_TESTVALUE:
                                        count++;
                                           // msg = DataTrans.TwoBytesToInt(bt[0], bt[1]) + "";
                                       for(int i=0;i<availableBytes;i++){
                                           all[index]=bt[i];
                                           index++;
                                       }
                                        if(index>4) {
                                        byte[] last=new byte[2];
                                        for(int i=0;i<2;i++){
                                            last[i]=all[index-2+i];
                                        }
                                        Log.i("wp123","? E1 E2 ? ="+DataTrans.BytesToString(last));
                                        if (DataTrans.BytesToString(last).equals("E2") || DataTrans.BytesToString(last).equals("E1")) {
                                            for (int i=0;i<index-2;i+=2){
                                                if(i<index-2-2) {
                                                    sb.append(DataTrans.TwoBytesToInt(all[i], all[i + 1]) + " , ");
                                                }else{
                                                    sb.append(DataTrans.TwoBytesToInt(all[i], all[i + 1]));
                                                }
                                            }

                                            //sb.append(DataTrans.BytesToString(last));
                                            mHandler.obtainMessage(BluetoothState.MESSAGE_READ, bytes, -1, sb.toString()).sendToTarget();
                                            all=new byte[1024*1024];
                                            index=0;
                                            count=0;
                                            sb.delete(0,sb.length());
                                            abc=0;

                                        }
                                    }

                                        break;

                                    case RECEIVE_CESHI:

                                        msg=DataTrans.bytesToHexString(buffer);

                                        mHandler.obtainMessage(BluetoothState.MESSAGE_READ,bytes,-1,msg).sendToTarget();
                                        abc=0;
                                        break;

                                    //全部导入数据
                                    case RECEIVE_ALLSPEEDDATA:
                                        count++;
                                        for(int i=0;i<availableBytes;i++){
                                            all[index]=bt[i];
                                            index++;
                                        }
                                        //#+数据长度(4Byte)+’ST’(2Byte)+文档编号(2Byte)+数据内容.....+’END’(3Byte)+*

                                        if(index>15) {

                                            byte[] last2=new byte[2];
                                            for(int i=0;i<2;i++){
                                                last2[i]=all[index-2+i];
                                            }
                                            Log.i("RECEIVE_ALLSPEEDDATA","? END*D2 ? ="+DataTrans.BytesToString(last2));
                                           if(DataTrans.BytesToString(last2).equals("D2") || DataTrans.BytesToString(last2).equals("D1")){
                                               byte[] length=new byte[4];
                                               for(int i=0;i<4;i++){
                                                   length[i]=all[i+1];
                                               }
                                               int datalength=DataTrans.byte2int(length);
                                                int datanumber=DataTrans.TwoBytesToInt(all[7], all[8]);
                                               byte[] bt0=new byte[1];
                                               bt0[0]=all[0];

                                               byte[] bt56=new byte[2];
                                               bt56[0]=all[5];
                                               bt56[1]=all[6];
                                              // sb.append(DataTrans.BytesToString(bt56) + " " + datanumber + " ");
                                               /* for (int i=9;i<index-2;i+=2){
                                                    sb.append(DataTrans.TwoBytesToInt(all[i], all[i + 1]) + " , ");
                                                }*/
                                               Log.i("RECEIVE_ALLSPEEDDATA", "接收的字符串数据为 = " + DataTrans.BytesToString(all));
                                               for (int i=5;i<index-2;i+=2){
                                                   if(i<index-2-2) {
                                                       sb.append(DataTrans.TwoBytesToInt(all[i], all[i + 1]) + " , ");
                                                   }else{
                                                       sb.append(DataTrans.TwoBytesToInt(all[i], all[i + 1]));
                                                   }
                                               }
                                               Log.i("RECEIVE_ALLSPEEDDATA","sb.toString() = "+sb.toString());
                                               // sb.append(DataTrans.BytesToString(last2));
                                               MyApplication.setString(sb.toString());
                                                mHandler.obtainMessage(BluetoothState.MESSAGE_READ,bytes,-1,"D").sendToTarget();
                                                all=new byte[1024*1024];
                                                index=0;
                                                count=0;
                                                abc=0;
                                                sb.delete(0,sb.length());
                                               Log.i("RECEIVE_ALLSPEEDDATA", "发送完成");
                                            }


                                        }
                                        break;

                                    case RECEIVE_DEC_FORCE_SPEED_RATE:
                                        //G1+#+’ST’(2Byte)+压力标定系数(2Byte)+ ’END’(3Byte)+*
                                        count++;
                                        for(int i=0;i<availableBytes;i++){
                                            all[index]=bt[i];
                                            index++;
                                        }
                                        if(index>10) {
                                            byte[] last = new byte[4];
                                            for (int i = 0; i < 4; i++) {
                                                last[i] = all[index - 4 + i];
                                            }
                                            if(DataTrans.BytesToString(last).equals("END*")){
                                                byte[] rate=new byte[5];
                                                for(int i=0;i<5;i++){
                                                    rate[i]=all[i];
                                                }
                                                int forcerate=DataTrans.TwoBytesToInt(all[5],all[6]);
                                                Log.i("wyy123","decforce = "+DataTrans.BytesToString(rate)+forcerate+DataTrans.BytesToString(last));
                                               // sb.append(DataTrans.BytesToString(rate) + forcerate + DataTrans.BytesToString(last));
                                                mHandler.obtainMessage(BluetoothState.MESSAGE_READ,bytes,-1,forcerate+"").sendToTarget();
                                                all=new byte[1024*1024];
                                                index=0;
                                                count=0;
                                                abc=0;
                                                sb.delete(0, sb.length());
                                            }
                                        }

                                        break;


                                }
                            }
                        }

                    } catch (IOException e) {
                        connectionLost();
                        // Start the service over to restart listening mode
                        BluetoothService.this.start(BluetoothService.this.isAndroid);
                        break;
                    }
            }
        }
            // Write to the connected OutStream.
        // @param buffer  The bytes to write
        public void write(byte[] buffer) {
            try {
                int bufferlen=buffer.length;
                Log.i("wp123","writeLength = "+bufferlen);
                if(bufferlen == 2){
                    String command=DataTrans.BytesToString(buffer);
                    if(command.equals(DataTrans.FORCESTART)){
                        Log.i("wp123","发送A1到设备，让测力仪设备开始测试");
                        receiveState=RECEIVE_COMMAND;

                    }else if(command.equals(DataTrans.FORCESTOP)){
                        Log.i("wp123","发送B1到设备，让测力仪设备停止测试");
                        receiveState = RECEIVE_COMMAND;

                    }else if(command.equals(DataTrans.FORCECLEAR)){
                        Log.i("wp123","发送C1到设备，让测力仪设备清空缓存");
                        receiveState=RECEIVE_COMMAND;

                    }else if(command.equals(DataTrans.FORCEPATCHDATA)){
                        Log.i("wp123","发送D1到设备，让测力仪设备批量导入数据");
                        //receiveState=RECEIVE_ALLFORCEDATA;
                        receiveState=RECEIVE_ALLSPEEDDATA;

                    }else if(command.equals(DataTrans.FORCECONTINUETEST)){
                        Log.i("wp123","发送E1到设备，让测力仪设备持续测试");
                        receiveState=RECEIVE_FORCE_SPEED_TESTVALUE;

                    }else if(command.equals(DataTrans.DECLAREZERO)){
                        Log.i("wp123","发送H1到设备，让测力仪标定零点");
                        receiveState=RECEIVE_COMMAND;

                    }else if(command.equals(DataTrans.FORCEREADRATEDECLARE)){
                        Log.i("wp123","发送G1到设备，读取压力标定系数");
                        receiveState=RECEIVE_DEC_FORCE_SPEED_RATE;

                    }
                    //F1+#+’ST’(2Byte)+压力标定系数(2Byte)+’END’(3Byte)+*
                }else if(bufferlen==11){
                    byte[] bt=new byte[2];
                    bt[0]=buffer[0];
                    bt[1]=buffer[1];
                    String command=DataTrans.BytesToString(bt);
                    if(command.equals(DataTrans.FORCESENDRATEDECLARE)) {
                        Log.i("wp123", "发送F1+ST+#+压力标定系数+END+*");
                        receiveState=RECEIVE_COMMAND;

                    }else if(command.equals(DataTrans.SPEEDSENDRATEDECLARE)){
                        Log.i("wp123","发送F2+ST+#+速度标定系数+END+*");
                        receiveState=RECEIVE_COMMAND;
                    }

                }else if(bufferlen == 8){
                    Log.i("wp123","发送时间到设备上");
                    receiveState=RECEIVE_ITIME;
                }else{
                    receiveState=RECEIVE_CESHI;
                }


                mmOutStream.write(buffer);


            } catch (Exception e) { }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }
}