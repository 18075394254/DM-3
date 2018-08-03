package activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;


import com.example.user.dm_3.R;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

import controller.PictureDatabase;
import fragment.DataFragment;

/**
 * Created by Administrator on 2017/1/20 0020.
 */
public class DataLookActivity extends FragmentActivity implements DataFragment.onChangeListener {
    private ViewPager viewPager;
    private ArrayList<Fragment> fragmentList;
    private DataFragment itemFragment;
    private FragmentManager manager;
    MyViewPagerAdapter adapter;
    //路径字符串
    private String strFilePath;
    //文件名
    private String name;
    //当前目录文件夹
    private File curDir;
    //当前目录下的文件集合
    private File[] curfiles;
    //文件的个数
    private int fileCount;
    //文件夹的个数
    private int dirCount;
    //保存图片的数据库
    private PictureDatabase pictureDB;
    private SQLiteDatabase db;
    private LayoutInflater inflater;
    //点击文件的位置
    private int position;
    //每个文件的文件名
    private String filename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//强制为竖屏
        setContentView(R.layout.datashow);
        viewPager= (ViewPager) findViewById(R.id.viewPager);

        pictureDB = new PictureDatabase(this);
        db = pictureDB.getWritableDatabase();

        inflater=getLayoutInflater();
        //获取打开的文件所在的路径
        Intent data = this.getIntent();
        //获得路径
        strFilePath = data.getStringExtra("path");
        //获得文件名
        name = data.getStringExtra("filename");
        //获得点击的位置
        position=data.getIntExtra("position", 0);


        curDir = new File(strFilePath);
        curfiles = curDir.listFiles(filter);
        fileCount = curfiles.length;
        dirCount=curDir.listFiles().length;
        fragmentList = new ArrayList<Fragment>();
        Log.i("lll", "fileCount = " + fileCount);
        //根据合适的文件数量添加相应的Fragment
        for (int i=0;i<fileCount;i++){
            name=curfiles[i].getName();
                    itemFragment=new DataFragment();
                    Bundle args=new Bundle();
                    args.putString("name", name);
                    args.putInt("position", i);
                    itemFragment.setArguments(args);
                    fragmentList.add(itemFragment);

        }
        manager = getSupportFragmentManager();
        adapter = new MyViewPagerAdapter(manager);
        viewPager.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        //将当前点击的位置去掉不是需要文件的数量得到所需文件的位置
        int curposition = position - (dirCount - fileCount);
        Log.i("lll", "curposition = " + curposition);
        viewPager.setCurrentItem(curposition );
    }
    //查看是不是符合门刚度测试的的条件
    FileFilter filter = new FileFilter()
    {
        @Override
        public boolean accept(File file)
        {
            String name = file.getName();
            int dot = name.indexOf(".");
            String extraName = name.substring(dot+1);
            if (extraName.equalsIgnoreCase("ds"))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
    };

    @Override
    public void onChange(int position,int what) {

        switch(what) {
            case 0:
                if (position > -1 && position < fileCount) {
                    viewPager.setCurrentItem(position);
                } else if (position < 0) {
                    Toast.makeText(this, "前面没有数据了", Toast.LENGTH_SHORT).show();
                } else if (position > fileCount - 1) {
                    Toast.makeText(this, "后面没有数据了", Toast.LENGTH_SHORT).show();
                }
                break;
            case 1:

                if (position > -1 && position < fragmentList.size()) {
                    File curFile = curfiles[position];
                    filename = curFile.getName();
                    String strExt = filename.substring(filename.length() - 2);
                    if (curFile.exists()) {
                        curFile.delete();
                        if (strExt.equals("ds")) {
                            pictureDB.delete(db, "ForceDisDB", filename);
                        }
                    }
                    if (fragmentList.size() == 1){
                        finish();
                    }

                    fragmentList.remove(position).onDestroy();
                    viewPager.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    viewPager.setCurrentItem(position);
                    if(position == fragmentList.size()-1 && position != 0){
                        fragmentList.remove(position).onDestroy();
                        viewPager.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        viewPager.setCurrentItem(position-1);
                    }

                    break;
                }

        }
    }

    public class MyViewPagerAdapter extends FragmentStatePagerAdapter{

        public MyViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            return super.instantiateItem(container, position);
        }
    }
   /* public class MyViewPagerAdapter extends FragmentPagerAdapter {
        public MyViewPagerAdapter(FragmentManager fm) {
            super(fm);
            // TODO Auto-generated constructor stub
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            //回收图片，释放内存  没什么效果
            ViewPager vpContainer = (ViewPager)container;
            View view = vpContainer.getChildAt(position);
            if(view!=null){
               ImageView imageView = (ImageView)view.findViewById(R.id.pinchImageView);
                releaseImageViewResouce(imageView);
                Log.i("Ceshi2Activity", "图片释放完成 = " + position);
            }
        }
        @Override
        public Fragment getItem(int arg0) {
            return fragmentList.get(arg0);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
         public int getItemPosition(Object object) {
            // TODO Auto-generated method stub
            return PagerAdapter.POSITION_NONE;
        }

        *//**
         * 释放图片资源的方法
         * @param imageView
         *//*
        public void releaseImageViewResouce(ImageView imageView) {
            if (imageView == null) return;
            Drawable drawable = imageView.getDrawable();
            if (drawable != null && drawable instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                Bitmap bitmap = bitmapDrawable.getBitmap();
                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                    bitmap=null;
                }
            }
            System.gc();
        }
    }*/

}
