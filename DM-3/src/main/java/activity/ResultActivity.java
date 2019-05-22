package activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.user.dm_3.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
    ImageView imageView,shareImage;
    private float scaleWidth=1,scaleHeight=1;
    ArrayList<String> list=new ArrayList<>();
    PictureDatabase pd;
    SQLiteDatabase sd;
    String strExt=null;
    Calculate calculate = new Calculate();
    Bitmap bitmap = null;
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

        forceValue=getView(R.id.forcevalue);
        disValue=getView(R.id.disvalue);


        btn_back=getView(R.id.btn_back);
        btn_look=getView(R.id.btn_look);
        imageView=getView(R.id.pinchImageView);
        shareImage = getView(R.id.shareBtn);

        pd=new PictureDatabase(this);
        sd = pd.getWritableDatabase();

                strExt="ds";
            if (pd.getLastBitmap(sd, MyApplication.FORCEDIS) != null) {
                bitmap = pd.getLastBitmap(sd, MyApplication.FORCEDIS);
                imageView.setImageBitmap(bitmap);
            }
            list=pd.getLastDatas(sd,MyApplication.FORCEDIS);
            if (list != null){

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
        shareImage
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //获取sd卡目录
                        String sdpath = Environment.getExternalStorageDirectory().getAbsolutePath();
                        String appName = getString(R.string.app_name);

                        String wordPath = sdpath + "/" + appName+ "/测试报告.pdf";
                        String picPath = sdpath + "/" + appName+ "/data.png";
                        FileOutputStream out =null;
                        try {

                            calculate.GenPDF(ResultActivity.this,wordPath, "DM-3", list.get(6).substring(0, 16), list.get(4), list.get(5), list.get(3), list.get(0), list.get(1), bitmap);
                            shareWordFile(wordPath);

                        } catch (Exception e) {
                            Toast.makeText(ResultActivity.this, "写入文件出错", Toast.LENGTH_SHORT).show();
                        }finally {
                            if(out != null){
                                try {
                                    out.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                    }
                });
    }
    //分享单张图片
    public void shareWordFile(String filepath) {
        // String imagePath2 = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+ "Pictures"+"/"+"taobao"+"/"+"191983953.jpg";
        //由文件得到uri
        Uri imageUri = Uri.fromFile(new File(filepath));
        Log.d("share", "uri:" + imageUri);  //输出：file:///storage/emulated/0/test.jpg

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setType("application/msword");
        startActivity(Intent.createChooser(shareIntent, "分享到"));
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.setPointString(null);
    }
}
