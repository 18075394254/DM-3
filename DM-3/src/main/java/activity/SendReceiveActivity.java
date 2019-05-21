package activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.example.user.dm_3.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import controller.BaseActivity;
import controller.MyApplication;
import utils.BluetoothState;
import utils.MyService;

/**
 * Created by Administrator on 16-10-21.
 */
public class SendReceiveActivity extends BaseActivity {
    private TextView receiveText,msgText;
    private EditText sendEdit;
    private Button sendButton,sendByte;
    private StringBuilder sb=new StringBuilder();
    private String string;
    ArrayList<Integer> list=new ArrayList<>();
    private ArrayList<Float> m_filterData = new ArrayList<Float>();
    private ArrayList<Float> m_cutData = new ArrayList<Float>();
    private String dataString ="";
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String message = (String) msg.obj;
            if (message.equals("D")){
               /* sb.append(MyApplication.getString());
                receiveText.setText(sb.toString());*/
                final String[] s = MyApplication.getString().replace(" ", "").split(",");
                Log.i("cyy123", "s.length=" + s.length);
                // Toast.makeText(MyApplication.getContext(), "解析的字符串的长度为" + s.length, Toast.LENGTH_SHORT).show();
                for (int i = 0; i < s.length; i++) {
                    //Log.i("Tag", "s[i] = " + s[i]);
                    sb.append(s[i]+" , ");
                    if (s[i].contains("21332")) {
                         list.add(i);
                        // filsNum++;
                        Log.i("Tag", "i = " + i);
                    }
                }
                if (list.size() != 0) {
                    if (list.size() == 1) {
                        for (int j = list.get(0) + 2; j < s.length; j++) {
                            m_cutData.add(Float.parseFloat(s[j]));
                        }
                        //  onSave(s[1]);
                        for(int i=0;i<m_cutData.size();i++){
                            Log.i("mtag","m_cytData = "+m_cutData.get(i));
                            sb.append(m_cutData.get(i) +" ");
                        }

                    } else {
                        for (int k = 0; k < list.size(); k++) {
                            if (k < list.size() - 1) {
                                for (int j = list.get(k); j < list.get(k + 1); j++) {
                                    //value = ((Float.parseFloat(s[j]) / 100));
                                    dataString = dataString+Float.parseFloat(s[j])+",";

                                }
                              dataString = dataString+"      一二三四五六七八九         ";
                                // onSave(s[list.get(k) + 1]);

                            } else {
                                for (int j = list.get(list.size() - 1); j < s.length; j++) {
                                    dataString = dataString+Float.parseFloat(s[j])+",";

                                }
                                receiveText.setText(dataString+"               ");
                                dataString="";
                                // onSave(s[list.get(k) + 1]);
                            }
                        }
                    }
                }

            }else if (message.equals("A2")){

                sb.append(MyApplication.getString()+" 下一组 ");
                receiveText.setText(sb.toString());
            }



           /* MyApplication.setString("");
            receiveText.setText(sb.toString());*/

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//强制为竖屏
        setContentView(R.layout.pictureresult);
        receiveText=getView(R.id.receiveText);
        msgText=getView(R.id.msg);
        sendEdit=getView(R.id.sendEdit);
        sendButton=getView(R.id.sendButton);
        sendByte=getView(R.id.ceshi);

        bindIntent = new Intent(this, MyService.class);
        bindService(bindIntent, connection, BIND_AUTO_CREATE);

        receiver = new MyReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction("android.intent.action.ceshiActivity");
        SendReceiveActivity.this.registerReceiver(receiver, filter);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBinder.sendMessage(sendEdit.getText().toString(), BluetoothState.CESHIACTIVITY);
                sendEdit.setText("");
            }
        });
        sendByte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sdpath = Environment.getExternalStorageDirectory().getAbsolutePath();
                File file = new File(sdpath + "/" + "DM-1" + "/data");
                String fileDir = sdpath + "/" +  "DM-1" +  "/data";
                if (!file.exists()) {
                    boolean isSuccess = file.mkdirs();
                    System.out.println("isSuccess:" + isSuccess);
                }
                /*if (file.exists()) {
                    DeleteFile(file);
                    //Toast.makeText(mContext, "删除DM-2成功", Toast.LENGTH_SHORT).show();
                }*/
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        unbindService(connection);
}
    public void onStart() {
        super.onStart();
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
    public void DeleteFile(File file) {
        if (file.exists() == false) {
            return;
        } else {
            if (file.isFile()) {
                file.delete();
                return;
            }
            if (file.isDirectory()) {
                File[] childFile = file.listFiles();
                if (childFile == null || childFile.length == 0) {
                    file.delete();
                    return;
                }
                for (File f : childFile) {
                    DeleteFile(f);
                }
                file.delete();
            }
        }
    }

}
