package fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.pdf.PrintedPdfDocument;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.user.dm_3.R;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import activity.MoreMessageActivity;

import controller.MyApplication;
import controller.PictureDatabase;
import utils.Calculate;
import utils.HWPFTemplateTest;

/**
 * Created by Administrator on 2017/1/20 0020.
 */
public class DataFragment extends Fragment{
    private String name;
    private ImageView imageView,backImage,shareImage;
    private TextView textForce,textDis,titleText;
    private Button moveToPrevious,moveToNext,moreMessage;
    private PictureDatabase pictureDB;
    private SQLiteDatabase db;
    ArrayList<String> datalist=new ArrayList<>();
    ArrayList<String>  infolist=new ArrayList<>();
    private View view;
    private onChangeListener mCallback;
    private int position;
    private Bitmap bitmap;
    private Activity activity;
    private String dataList= "";
    private String s_mLiftId ="";
    private String s_mOperator = "";
    private String s_mLocation = "";
    Calculate calculate =new Calculate();

    @Override

    public void onAttach(Activity activity)

    {

        super.onAttach(activity);

        try {

            mCallback = (onChangeListener) activity;

        } catch (ClassCastException e)

        {
            throw new ClassCastException(activity.toString() +" must implement OnHeadlineSelectedListener");

        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i("Fragment", "Fragment被关闭了！");
        imageView.setImageBitmap(null);
        imageView = null;
        view = null;
        System.gc();

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
       /* outState.putString("data", mData + "_save");*/

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args=getArguments();
        name=args.getString("name");
        position=args.getInt("position");
        Log.i("Fragment", "name  "+ name);
        Log.i("Fragment","ItemPosition  "+ position);

        pictureDB = new PictureDatabase(getContext());
        db = pictureDB.getWritableDatabase();

        view=inflater.inflate(R.layout.dataitem, container, false);
        imageView=(ImageView)view.findViewById(R.id.pinchImageView);
        textForce=(TextView)view.findViewById(R.id.open_force);
        textDis=(TextView)view.findViewById(R.id.open_dis);
        titleText=(TextView)view.findViewById(R.id.titleText);
        backImage=(ImageView)view.findViewById(R.id.backBtn);
        shareImage=(ImageView)view.findViewById(R.id.shareBtn);
        moveToNext=(Button)view.findViewById(R.id.next);
        moveToPrevious=(Button)view.findViewById(R.id.previous);
        moreMessage=(Button)view.findViewById(R.id.allmessage);

        click();
        titleText.setText(name);
        String strExt;
        //获取文件名的后两位
        if (name.length() > 2) {
            strExt = name.substring(name.length() - 2);
        }else{
            strExt=name+"1";
        }
        if(strExt.equals("ds")) {
            // bitmap = getBitmap(pictureDB.getBitmap(db, MyApplication.FORCE,name));
            bitmap = pictureDB.getBitmap(db, MyApplication.FORCEDIS, name);
            Log.i("mtag","bitmap.width = "+bitmap.getWidth());
            Log.i("mtag","bitmap.height = "+bitmap.getHeight());
            if (bitmap != null){
                imageView.setImageBitmap(bitmap);
            }else{
                imageView.setImageResource(R.mipmap.ic_launcher);
            }

            datalist=pictureDB.getDatas(db, MyApplication.FORCEDIS, name);
            infolist = pictureDB.getInfos(db, MyApplication.FORCEDIS, name);
            if (datalist != null && datalist.size() > 0 ) {
                if (Integer.parseInt(datalist.get(2)) == 0) {

                    textForce.setText("压力值 = "+datalist.get(0)+" N");
                    textDis.setText("位移量 = " + datalist.get(1)+" mm");
                }else {
                    textForce.setText("数据不达标,压力未达到300N");
                    textDis.setText("");
                }

            }

        }
        return view;

    }
    public void click(){
        backImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getActivity().finish();

            }
        });

        shareImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //获取sd卡目录
                String sdpath = Environment.getExternalStorageDirectory().getAbsolutePath();
                String appName = getString(R.string.app_name);

                String wordPath = sdpath + "/" + appName+ "/测试报告.pdf";
                String picPath = sdpath + "/" + appName+ "/data.png";
                FileOutputStream  out =null;
                try {
                   // calculate.CreatePng(picPath,bitmap);
                   // new HWPFTemplateTest().testTemplateWrite(wordPath,name.substring(0,16),"吴鹏","中科智能","00011",picPath);
                    calculate.GenPDF(getActivity(),wordPath, "DM-3", name.substring(0, 16), infolist.get(4), infolist.get(5), infolist.get(3), infolist.get(0), infolist.get(1), bitmap);
                    shareWordFile(wordPath);

                } catch (Exception e) {
                    Toast.makeText(getContext(),"写入文件出错",Toast.LENGTH_SHORT).show();
                }finally {
                    if(out != null){
                        try {
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
              /*  File file = null;
                try{
                     file =new File(SavePath);
                    if (file.exists()){
                        file.delete();
                    }
                    GetandSaveCurrentImage(activity,file);
                    shareSingleImage(SavePath);
                }catch (Exception e){
                    Toast.makeText(getContext(),"图片文件出错",Toast.LENGTH_SHORT).show();
                }*/



            }
        });

        moreMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> list=new ArrayList<String>();
                String strExt = name.substring(name.length() - 2);
                if(strExt.equals("ds")) {
                    list = pictureDB.getInfos(db, MyApplication.FORCEDIS,titleText.getText().toString());

                }
                Intent intent=new  Intent(getActivity(), MoreMessageActivity.class);
                intent.putStringArrayListExtra("list", list);
                intent.putExtra("strExt",strExt);
                startActivity(intent);
            }
        });

        moveToPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("ppp","previousPosition  "+ position);
                mCallback.onChange(position - 1, 0);
            }
        });

        moveToNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("ppp", "NextPosition  " + position);
                mCallback.onChange(position + 1, 0);
            }
        });
    }

    DialogInterface.OnClickListener dialoglistener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    mCallback.onChange(position,1);
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    break;
                default:
                    break;
            }

        }
    };
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
                 super.onActivityCreated(savedInstanceState);
                  activity = getActivity();

             }

    public interface onChangeListener
    {
        public void onChange(int position, int what);

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



}
