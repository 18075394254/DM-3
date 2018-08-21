package receiver;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import utils.ClsUtils;

/**
 * Created by Administrator on 16-11-10.
 */

public class PairingRequest extends BroadcastReceiver {
    String strPsw = "1234";
    final String ACTION_PAIRING_REQUEST = "android.bluetooth.device.action.PAIRING_REQUEST";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION_PAIRING_REQUEST)) {

            BluetoothDevice device = intent
                    .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if ( device.getName().contains("DM-3")){
                strPsw = "1234";
            }else{
                strPsw = "1236";
            }
            if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                try {
                    abortBroadcast();//如果没有将广播终止，则会出现一个一闪而过的配对框。
                    //1.确认配对
                  //  ClsUtils.setPairingConfirmation(device.getClass(), device, true);

                    ClsUtils.setPin(device.getClass(), device, strPsw); // 手机和蓝牙采集器配对

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    Toast.makeText(context, "请求连接错误...", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }
}