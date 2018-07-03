package activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;


import com.example.user.dm_3.R;

import java.util.ArrayList;

import controller.BaseActivity;


/**
 * Created by Administrator on 2017/1/3 0003.
 */
public class DeviceDetailsActivity extends BaseActivity {
    private ListView listView;
    private ImageView backimage;
    ArrayAdapter<String> adapter;
    ArrayList<String> list=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//强制为竖屏
        setContentView(R.layout.activity_device_details);

        listView=getView(R.id.id_listview);
        backimage=getView(R.id.back);
        list.add("设备名称 ：电梯门综合检测系统（DM-3）");
        list.add("用途 ：用于电梯门刚度测试");
        list.add("版本号 ：1.0");
        list.add("生产公司 ：安徽中科智能高技术有限责任公司");
        list.add("地址 ：合肥市高新区科学大道100号");
        adapter=new ArrayAdapter<String>(DeviceDetailsActivity.this,android.R.layout.simple_list_item_1,list);

        listView.setAdapter(adapter);

        backimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceDetailsActivity.this.finish();
            }
        });


    }
}
