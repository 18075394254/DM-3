package activity;

import android.content.Intent;
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
 * Created by Administrator on 2016/12/15 0015.
 */
public class MoreMessageActivity extends BaseActivity {
    private ListView listView;
    private ImageView imageBack;
    private ArrayList<String> list1=new ArrayList<>();
    private ArrayList<String> list=new ArrayList<>();
    private String strExt=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//强制为竖屏
        setContentView(R.layout.data_more);
        listView=getView(R.id.listmessage);
        imageBack=getView(R.id.back);

        Intent intent=getIntent();
        list1=intent.getStringArrayListExtra("list");
        strExt=intent.getStringExtra("strExt");
        if(list1 != null && list1.size()>0) {
            if (strExt.equals("ds")) {
                list.add("压力值： " + list1.get(0) +" N");
                list.add("位移值： " + list1.get(1)+" mm");
                list.add("设备编号：  " + list1.get(3));
                list.add(" 操作员：   " + list1.get(4));
                list.add(" 地点：     " + list1.get(5));


            }
        }

        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoreMessageActivity.this.finish();
            }
        });

        ArrayAdapter<String> adapter=new ArrayAdapter<String>(MoreMessageActivity.this,android.R.layout.simple_list_item_1,list);
        listView.setAdapter(adapter);

    }
}
