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
import android.widget.TextView;


import com.example.user.dm_3.R;

import java.util.ArrayList;

import controller.BaseActivity;
import controller.MyApplication;
import controller.PictureDatabase;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//强制为竖屏
        setContentView(R.layout.activity_result);

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
                }else{
                    forceValue.setText("压力值 = "+list.get(0)+" N");
                    disValue.setText("位移量 = " + list.get(1)+" mm");
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

       /* btn_look.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new  Intent(ResultActivity.this,MoreMessageActivity.class);
                intent.putStringArrayListExtra("list", list);
                intent.putExtra("strExt",strExt);
                startActivity(intent);
            }
        });*/
    }


}
