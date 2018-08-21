package activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.example.user.dm_3.R;

import java.util.ArrayList;

import controller.BaseActivity;
import controller.MyApplication;
import controller.PictureDatabase;
import utils.Calculate;
import view.MySurfaceView;

/**
 * Created by Administrator on 16-10-13.
 */
public class ResultActivity extends BaseActivity {
    private Button btn_look,btn_back;
    private TextView forceValue,disValue;
    ImageView imageView;
    private float scaleWidth=1,scaleHeight=1;
    ArrayList<String> list=new ArrayList<>();
    PictureDatabase pd;
    SQLiteDatabase sd;
    String strExt=null;
    Calculate calculate = new Calculate();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//强制为竖屏
        setContentView(R.layout.activity_result);

        //获取打开的文件所在的路径
        Intent data = this.getIntent();
        //获得路径
       String filename = data.getStringExtra("filename");
        calculate.setAllData(filename);

       /* MySurfaceView surfaceView = new MySurfaceView(this);
        LinearLayout l = new LinearLayout(this);   //l就是当前的页面的布局

        l.addView(surfaceView);   //加入新的view
        if (MyApplication.getWindowWidth() == 720){
            l.setPadding(0, 360, 0, 0);  //设置位置
        }else if(MyApplication.getWindowWidth() == 1080){
            l.setPadding(0, 550, 0, 0);  //设置位置
        }else{
            l.setPadding(0, MyApplication.getWindowWidth()/2, 0, 0);  //设置位置
        }

        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        l.setLayoutParams(p);  //新的view的参数
        this.addContentView(l, p);  //加入新的view*/

        forceValue=getView(R.id.forcevalue);
        disValue=getView(R.id.disvalue);


        btn_back=getView(R.id.btn_back);
        btn_look=getView(R.id.btn_look);
        imageView=getView(R.id.pinchImageView);

        pd=new PictureDatabase(this);
        sd = pd.getWritableDatabase();

                strExt="ds";
            if (pd.getLastBitmap(sd, MyApplication.FORCEDIS) != null) {
                imageView.setImageBitmap(pd.getLastBitmap(sd, MyApplication.FORCEDIS));
            }
            if (pd.getLastDatas(sd,MyApplication.FORCEDIS) != null){
                list=pd.getLastDatas(sd,MyApplication.FORCEDIS);
                Log.i("2018-06-26 ", "isQualified list.get(2) = " + list.get(2));
                if (Integer.parseInt(list.get(2))== 1){
                    forceValue.setText("数据不达标,压力未达到300N");
                    disValue.setText("");
                }else if(Integer.parseInt(list.get(2))== 2){
                    forceValue.setText("数据不达标,未找到接近300N的压力");
                    disValue.setText("");
                }else{
                    forceValue.setText("压力 = "+list.get(0)+" N");
                    disValue.setText("位移 = " + list.get(1)+" mm");
                }


            }



        clickListener();


    }

    private void clickListener() {
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_look.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new  Intent(ResultActivity.this,MoreMessageActivity.class);
                intent.putStringArrayListExtra("list", list);
                intent.putExtra("strExt",strExt);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.setPointString(null);
    }
}
