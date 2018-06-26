package fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.user.dm_3.R;

import java.util.ArrayList;

import activity.MoreMessageActivity;
import controller.MyApplication;
import controller.PictureDatabase;

/**
 * Created by Administrator on 2017/1/20 0020.
 */
public class DataFragment extends Fragment{
    private String name;
    private ImageView imageView,backImage,deleteImage;
    private TextView textFmax,textFkin,textEnergy,titleText,textMinAcc;
    private Button moveToPrevious,moveToNext,moreMessage;
    private PictureDatabase pictureDB;
    private SQLiteDatabase db;
    ArrayList<Float> datalist=new ArrayList<>();
    ArrayList<String>  infolist=new ArrayList<>();
    private View view;
    private onChangeListener mCallback;
    private int position;

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
        textFmax=(TextView)view.findViewById(R.id.open_forceMax);
        textFkin=(TextView)view.findViewById(R.id.open_forceKin);
        textEnergy=(TextView)view.findViewById(R.id.open_energy);
        titleText=(TextView)view.findViewById(R.id.titleText);
        textMinAcc= (TextView) view.findViewById(R.id.open_MinAcc);
        backImage=(ImageView)view.findViewById(R.id.backBtn);
        //deleteImage=(ImageView)view.findViewById(R.id.deleteBtn);
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
            Bitmap bitmap = pictureDB.getBitmap(db, MyApplication.FORCEDIS, name);
            if (bitmap != null){
                imageView.setImageBitmap(bitmap);
            }else{
                imageView.setImageResource(R.mipmap.ic_launcher);
            }

            datalist=pictureDB.getDatas(db, MyApplication.FORCEDIS, name);
            infolist = pictureDB.getInfos(db, MyApplication.FORCEDIS, name);
            if (datalist != null && datalist.size() > 0 ) {
                textFmax.setText("Fmax = " + datalist.get(0) + " N");
                textFkin.setText("Fkin = " + datalist.get(1) + " N");
                textEnergy.setText("Energy = " + datalist.get(2) + " J");
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

        /*deleteImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //删除当前的文体，首先需要确认
                AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                        .setTitle("删除")
                        .setMessage("确定要删除文件吗")
                        .setPositiveButton("确定", dialoglistener)
                        .setNegativeButton("取消", dialoglistener)
                        .create(); // 创建对话框
                alertDialog.show(); // 显示对话框

            }
        });*/

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
                Log.i("ppp","NextPosition  "+ position);
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

    public interface onChangeListener
    {
        public void onChange(int position, int what);

    }

    //将bitmap放大8倍
    public Bitmap getBitmap(Bitmap bitmap){
        Matrix matrix = new Matrix();
        matrix.postScale(4.0f,4.0f);

        Bitmap resizeBmp = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);

        return resizeBmp;
    }

}
