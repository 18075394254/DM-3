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
    private TextView forceMax,forceKin,forceEnergy,MinAcc;
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

        forceMax=getView(R.id.forceMax);
        forceKin=getView(R.id.forceKin);
        forceEnergy=getView(R.id.energy);
        MinAcc=getView(R.id.MinAcc);

        btn_back=getView(R.id.btn_back);
        btn_look=getView(R.id.btn_look);
        imageView=getView(R.id.pinchImageView);

        pd=new PictureDatabase(this);
        sd = pd.getWritableDatabase();
        Intent intent=getIntent();
        String data=intent.getStringExtra("extra_data");

        Log.i("wp123","data = " + data);

        if(data.equals(MyApplication.FORCE)) {
                strExt="df";
            if (pd.getLastBitmap(sd, MyApplication.FORCE) != null) {
                imageView.setImageBitmap(pd.getLastBitmap(sd, MyApplication.FORCE));
            }
            if (pd.getLastDatas(sd,MyApplication.FORCE) != null){
                list=pd.getLastDatas(sd,MyApplication.FORCE);
                forceMax.setText("Fmax = "+list.get(0)+" N");
                forceKin.setText("Fkin = "+list.get(1)+" N");
                forceEnergy.setText("Energy = "+list.get(2)+" J");

            }
        }else if(data.equals(MyApplication.SPEED)){
             strExt="dv";
            if (pd.getLastBitmap(sd, MyApplication.SPEED) != null) {
                imageView.setImageBitmap(pd.getLastBitmap(sd, MyApplication.SPEED));
            }
            if (pd.getLastBitmap(sd, MyApplication.SPEED) != null) {
                list=pd.getLastDatas(sd,MyApplication.SPEED);
                forceMax.setText("Vave = "+list.get(0)+" m/s");
                forceKin.setText("Vmax = "+list.get(1)+" m/s");
                forceEnergy.setText("+Vacc = "+list.get(2)+" m²/s");
                MinAcc.setText("-Vacc = "+list.get(3)+" m²/s");
            }
        }

        clickListener();
       // pd.delete(sd);

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
