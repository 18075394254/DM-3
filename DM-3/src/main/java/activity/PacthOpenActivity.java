package activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.user.dm_3.R;

import java.util.ArrayList;
import java.util.HashMap;

import controller.BaseActivity;
import controller.MyApplication;
import controller.PictureDatabase;

/**
 * Created by Administrator on 16-10-27.
 */
public class PacthOpenActivity extends BaseActivity {

    private GridView gridview;
    protected ArrayList<HashMap<String, String>> srcTable;
    protected SimpleAdapter saTable;// 适配器
    PictureDatabase pd ;
    SQLiteDatabase sd;

    ArrayList<String> listInfo=new ArrayList<>();
    ArrayList<Float> listData=new ArrayList<>();
    ArrayList<String> listDs=new ArrayList<>();
    private Handler handler;
    private int sizeDs;
    String[] items;

    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE); //无标题
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patchopen);

        listDs.clear();
        gridview = (GridView) findViewById(R.id.gridview);
        pd=new PictureDatabase(this);
        sd=pd.getWritableDatabase();

        Intent intent=this.getIntent();
        Bundle bundle=intent.getExtras();
        listDs=bundle.getStringArrayList("listDs");
        sizeDs=bundle.getInt("listDs.size()");


        srcTable = new ArrayList<HashMap<String, String>>();
        saTable = new SimpleAdapter(this,
                srcTable,// 数据来源
                R.layout.griditem,//XML实现
                new String[] { "ItemText" },  // 动态数组与ImageItem对应的子项
                new int[] { R.id.ItemText });


        // 添加并且显示
        gridview.setAdapter(saTable);
        // 添加消息处理
        gridview.setOnItemClickListener(new ItemClickListener());
       // RemoveAll();

        //添加数据测试
        //addData();
        if(listDs != null && listDs.size()== sizeDs ){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    addData();
                }
            }).start();
        }
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                saTable.notifyDataSetChanged();
            }
        };

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
//land
        }
        else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        {
//port
        }
    }

    public void addHeader(String name){

            String items[] = {name, "Force", "Displacement", "是否达标", "操作员", "地点", "设备"};
            for (String strText:items) {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("ItemText",strText);
                srcTable.add(map);
            }


    }

    public void addData(){
        if (listDs.size() > 0){

            //添加表头
            addHeader("FileName");
    if (listDs != null && listDs.size() ==sizeDs && listDs.size() > 0 ) {
        for (int i = 0; i < listDs.size(); i++) {
            String name = listDs.get(i);
            Log.i("ggg","name = "+name);
            //listData = pd.getDatas(sd, "Force", name);
            listInfo = pd.getInfos(sd, MyApplication.FORCEDIS, name);
            items = new String[]{name, listInfo.get(0) + "", listInfo.get(1) + "", listInfo.get(2) + "", listInfo.get(3) + "", listInfo.get(4) + "", listInfo.get(5) + ""};

           // Log.i("ggg", " " + listData.get(0) + " " + listData.get(1) + " " + listData.get(2));
            if (items.length>0){
            for (String strText : items) {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("ItemText", strText);
                srcTable.add(map);
            }

        }
    }
        handler.sendEmptyMessage(0);
}
        }
    }

    //清空列表
    public void RemoveAll()
    {
        listDs.clear();
        listInfo.clear();
        listData.clear();
        srcTable.clear();
        saTable.notifyDataSetChanged();
    }

    // 表格单击处理
    class ItemClickListener implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> arg0,View arg1,int arg2,long arg3){
            // 在本例中arg2=arg3
            @SuppressWarnings("unchecked")
            HashMap<String, Object> item = (HashMap<String, Object>) arg0.getItemAtPosition(arg2);
            // 显示所选Item的ItemText
            Toast.makeText(getApplicationContext(), (String) item.get("ItemText"), Toast.LENGTH_SHORT).show();


        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RemoveAll();
    }
}
