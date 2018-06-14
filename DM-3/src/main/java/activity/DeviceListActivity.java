/*
 * Copyright 2014 Akexorcist
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.dm_3.R;

import java.util.Set;

import utils.BluetoothState;

@SuppressLint("NewApi")
public class DeviceListActivity extends Activity {
    // Debugging
    private static final String TAG = "BluetoothSPP";
    private static final boolean D = true;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 23;

    // Member fields
    private BluetoothAdapter mBtAdapter;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;
    private Set<BluetoothDevice> pairedDevices;
    private Button scanButton;
    private TextView tv_title;
    private ImageView backimage;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//强制为竖屏
        // Setup the window
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        int listId = getIntent().getIntExtra("layout_list", R.layout.activity_device_list);
        setContentView(listId);
        backimage= (ImageView) findViewById(R.id.back);
        backimage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
               DeviceListActivity.this.finish();
            }
        });

        tv_title= (TextView) findViewById(R.id.tv_title);
        String strBluetoothDevices = getIntent().getStringExtra("bluetooth_devices");
        if(strBluetoothDevices == null) 
        	strBluetoothDevices = "Bluetooth Devices";
        tv_title.setText(strBluetoothDevices);
        
        // Set result CANCELED in case the user backs out
        setResult(Activity.RESULT_CANCELED);
        

        scanButton = (Button) findViewById(R.id.button_scan);
        String strScanDevice = getIntent().getStringExtra("scan_for_devices");
        if(strScanDevice == null) 
        	strScanDevice = "搜索蓝牙设备";
        scanButton.setText(strScanDevice);
        scanButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                requestBluetoothPermission();

            }
        });


        int layout_text = getIntent().getIntExtra("layout_text", R.layout.device_name);
        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this, layout_text);

        // Find and set up the ListView for paired devices
        ListView pairedListView = (ListView) findViewById(R.id.list_devices);
        pairedListView.setAdapter(mPairedDevicesArrayAdapter);
        pairedListView.setOnItemClickListener(mDeviceClickListener);
        
        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);

        // Get the local Bluetooth adapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        // Get a set of currently paired devices
        pairedDevices = mBtAdapter.getBondedDevices();

        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            String noDevices = "No devices found";
            mPairedDevicesArrayAdapter.add(noDevices);
        }

    }


    //安卓开发6.0蓝牙权限授权
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION:
                Log.i("wp123","grantResults[0]="+grantResults[0]);
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //授权成功
                    Toast.makeText(DeviceListActivity.this, "正在搜索蓝牙设备...", Toast.LENGTH_SHORT).show();
                    doDiscovery();
                } else {
                    //授权拒绝
                }
                break;

        }
    }
    private void requestBluetoothPermission(){
        //判断系统版本
        Log.i("wp123","系统版本为"+Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
            //判断这个权限是否已经授权过
            Log.i("wp123","checkCallPhonePermission = "+checkCallPhonePermission);
            if(checkCallPhonePermission != PackageManager.PERMISSION_GRANTED){

                //判断是否需要 向用户解释，为什么要申请该权限,该方法只有在用户在上一次已经拒绝过你的这个权限申请才会调用。
                if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION))
                    Toast.makeText(this,"Need bluetooth permission.",Toast.LENGTH_SHORT).show();

                  /*  参数1 Context
                * 参数2 需要申请权限的字符串数组，支持一次性申请多个权限，对话框逐一询问
                * 参数3 requestCode 主要用于回调的时候检测*/

                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
                return;
            }else{
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);

            }
        } else {
            Toast.makeText(DeviceListActivity.this, "正在搜索蓝牙设备...", Toast.LENGTH_SHORT).show();
            doDiscovery();
        }
    }
    protected void onDestroy() {
        super.onDestroy();
        // Make sure we're not doing discovery anymore
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }

        // Unregister broadcast listeners
        this.unregisterReceiver(mReceiver);
        this.finish();
    }

    // Start device discover with the BluetoothAdapter
	private void doDiscovery() {
        if (D) Log.d(TAG, "doDiscovery()");
        
        // Remove all element from the list
        mPairedDevicesArrayAdapter.clear();
        
        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            String strNoFound = getIntent().getStringExtra("no_devices_found");
            if(strNoFound == null) 
            	strNoFound = "No devices found";
            mPairedDevicesArrayAdapter.add(strNoFound);
        }
        
        // Indicate scanning in the title
        String strScanning = getIntent().getStringExtra("scanning");
        if(strScanning == null) 
        	strScanning = "Scanning for devices...";
        setProgressBarIndeterminateVisibility(true);
        tv_title.setText(strScanning);

        // Turn on sub-title for new devices
        // findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);
        // If we're already discovering, stop it
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }

        // Request discover from BluetoothAdapter
        mBtAdapter.startDiscovery();
    }

    // The on-click listener for all devices in the ListViews
    private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // Cancel discovery because it's costly and we're about to connect
            if(mBtAdapter.isDiscovering())
            	mBtAdapter.cancelDiscovery();

            String strNoFound = getIntent().getStringExtra("no_devices_found");
            if(strNoFound == null) 
            	strNoFound = "No devices found";
	        if(!((TextView) v).getText().toString().equals(strNoFound)) {
	            // Get the device MAC address, which is the last 17 chars in the View
	            String info = ((TextView) v).getText().toString();
	            String address = info.substring(info.length() - 17);
	            String name = info.substring(0, info.length() - 17);
                Log.i("5.22","name = "+name);
	            // Create the result Intent and include the MAC address
	            Intent intent = new Intent();
	            intent.putExtra(BluetoothState.EXTRA_DEVICE_ADDRESS, address);
                intent.putExtra(BluetoothState.EXTRA_DEVICE_NAME, name);
	            // Set result and finish this Activity
	            setResult(Activity.RESULT_OK, intent);
	            finish();
            }
        }
    };

    // The BroadcastReceiver that listens for discovered devices and
    // changes the title when discovery is finished
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    String strNoFound = getIntent().getStringExtra("no_devices_found");
                    if(strNoFound == null) 
                    	strNoFound = "No devices found";                    
                    
                	if(mPairedDevicesArrayAdapter.getItem(0).equals(strNoFound)) {
                		mPairedDevicesArrayAdapter.remove(strNoFound);
                	}
                	mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                }
                
            // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setProgressBarIndeterminateVisibility(false);
                String strSelectDevice = getIntent().getStringExtra("select_device");
                if(strSelectDevice == null)
                    Toast.makeText(DeviceListActivity.this, "搜索完成，点击连接蓝牙", Toast.LENGTH_SHORT).show();
                strSelectDevice = "Select a device to connect";
                tv_title.setText(strSelectDevice);
            }
        }
    };



}
