package activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.user.dm_3.R;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import adapter.Adapter;
import controller.MyApplication;
import controller.PictureDatabase;
import fragment.DataFragment;
import model.ItemBean;


public class OpenAllActivity extends Activity implements Adapter.OnShowItemClickListener {

    private TextView pathText;
    private ListView fileslist;
    private Button backView, batchOpen;
    private List<ItemBean> dataList;
    private List<ItemBean> selectList;
    private static boolean isShow=false; // 是否显示CheckBox标识

    private LinearLayout lay;

    String m_strpath = null;
    //记录当前父文件夹；
    File curdir;
    //记录当前路径下所有文件夹的文件数组；
    File[] currentFiles;
    FileFilter filter;

    private static String m_lastPath = "";
    private String m_openPath = null;

    ArrayList<String> listDs = new ArrayList<>();
    private RelativeLayout layout2;
    private RelativeLayout layout;
    private Adapter adapter;
    private  File dirfile;
    private File[] dirfiles;
    private int count1=1;
    private int count2=1;
    private String name=null;
    PictureDatabase pictureDB;
    SQLiteDatabase db;

    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//强制为竖屏
        setContentView(R.layout.activity_openall);
        fileslist = (ListView) this.findViewById(R.id.files);
        pathText = (TextView) this.findViewById(R.id.tvpath);

        layout2 = (RelativeLayout)findViewById(R.id.iconGroup);
        layout = (RelativeLayout)findViewById(R.id.relative);
        lay= (LinearLayout) findViewById(R.id.lay);
        dataList = new ArrayList<ItemBean>();
        selectList = new ArrayList<ItemBean>();

        backView = (Button) findViewById(R.id.backView);
        batchOpen = (Button) findViewById(R.id.batchOpen);

        pictureDB=new PictureDatabase(this);
        db=pictureDB.getWritableDatabase();



        Intent data = getIntent();
        m_strpath = data.getStringExtra("path");
        // 获取系统的SDCard目录；
        if (m_strpath == null) {
            if (!m_lastPath.equals("")) {
                m_strpath = m_lastPath;
            } else {
                m_strpath = Environment.getExternalStorageDirectory().getAbsolutePath();
                Log.i("===", "m_strpath" + m_strpath);
            }

        }


        filter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.canRead() && file.canWrite();
            }
        };
        File root = new File(m_strpath);
        //如果SD卡存在；
        if (root.exists()) {

            curdir = root;
            currentFiles = root.listFiles(filter);

            // 使用当前目录下的全部文件、文件夹来填充ListView
            inflateListView(currentFiles);

        }

        fileslist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                if (isShow) {
                    return false;
                } else {
                    isShow = true;
                    for (ItemBean bean : dataList) {
                        bean.setShow(true);
                    }
                    adapter.notifyDataSetChanged();
                    showOpervate();
                    fileslist.setLongClickable(false);
                }
                return true;
            }
        });
        fileslist.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (isShow) {
                    ItemBean bean = dataList.get(position);
                    boolean isChecked = bean.isChecked();
                    if (isChecked) {
                        bean.setChecked(false);
                    } else {
                        bean.setChecked(true);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    //如果点击的是第一行
                    if (position == 0) {
                        try {
                            if (!curdir.getCanonicalPath().equals("/mnt")) {

                                // 获取上一级目录
                                curdir = curdir.getParentFile();
                                // 列出当前目录下的所有文件
                                currentFiles = curdir.listFiles(filter);
                                // 再次更新ListView
                                inflateListView(currentFiles);

                            } else {
                                finish();
                            }

                            if (curdir.getCanonicalPath().equals("/")) {
                                finish();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return;
                    }

                    if (position > 1 || position == 1) {
                        // 如果用户单击了文件
                        if (currentFiles[position - 1].isFile()) {
                            //取得文件名
                            String fileName = currentFiles[position - 1].getName();
                            String strExt =null;
                            if (fileName.length() > 2) {
                                strExt = fileName.substring(fileName.length() - 2);
                            } else {
                                strExt = fileName+" 1 ";
                            }
                            if (!strExt.equals("ds")) {
                                Toast.makeText(OpenAllActivity.this, "不是电梯门刚度测试文件！", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            //传递需要打开的文件名给MainActivity;
                           // Intent intent = new Intent(OpenAllActivity.this, OpenPictureActivity.class);
                            Intent intent = new Intent(OpenAllActivity.this, DataLookActivity.class);
                            String mPath = curdir.getAbsolutePath();
                            m_lastPath = mPath;

                            //mPath += "/" + fileName;
                            //只传递文件的路径
                            intent.putExtra("path", mPath);
                            intent.putExtra("filename", fileName);
                            intent.putExtra("position", position - 1);
                            //startActivity(intent);
                            startActivityForResult(intent, 0x01);
                        } else {
                            // 获取用户点击的文件夹 下的所有文件
                            File[] tem = currentFiles[position - 1].listFiles();
                            //获取用户单击的列表项对应的文件夹，设为当前的文件夹
                            curdir = currentFiles[position - 1];
                            //保存当前的父文件夹内的全部文件和文件夹
                            currentFiles = tem;
                            // 再次更新ListView
                            inflateListView(currentFiles);
                        }
                    }
                }
            }

    });

        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        batchOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listDs.clear();
                batchOpen.setTextColor(Color.RED);
                m_openPath = pathText.getText().toString();
                File dirfile = new File(m_openPath);
                currentFiles = dirfile.listFiles();
                if (currentFiles.length == 0) {
                    batchOpen.setTextColor(Color.BLACK);
                    Toast.makeText(OpenAllActivity.this, "该文件夹下没有文件", Toast.LENGTH_SHORT).show();
                } else {
                    for (int i = 0; i < currentFiles.length; i++) {
                        File file = currentFiles[i];
                        if(file.isFile()) {
                            String filename = file.getName();
                            String str;
                            if (filename.length()> 2) {
                                str = filename.substring(filename.length() - 2);
                            }else{
                                str=filename+"1";
                            }
                            Log.i("cyy123", "filename=" + filename);
                            Log.i("cyy123", "str=" + str);
                            if (str.equals("ds")) {
                                listDs.add(filename);
                            } 
                        }
                    }
                    if (listDs.size() == 0) {
                        batchOpen.setTextColor(Color.BLACK);
                        Toast.makeText(OpenAllActivity.this, "该文件夹下没有电梯门刚度检测系统文件", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(OpenAllActivity.this, PacthOpenActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putStringArrayList("listDs", listDs);
                        bundle.putInt("listDs.size()", listDs.size());
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                }

            }
        });


    }

    public void onShowItemClick(ItemBean bean) {
        if (bean.isChecked() && !selectList.contains(bean)) {
            selectList.add(bean);
        } else if (!bean.isChecked() && selectList.contains(bean)) {
            selectList.remove(bean);
        }
    }

    /**
     * 显示操作界面
     */
    private void showOpervate() {
        layout.setVisibility(View.VISIBLE);
        layout2.setVisibility(View.GONE);
        lay.setVisibility(View.VISIBLE);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.operate_in);
        lay.setAnimation(anim);
        // 返回、删除、全选和反选按钮初始化及点击监听
        Button tvBack =(Button) findViewById(R.id.operate_back);
        Button tvDelete = (Button) findViewById(R.id.operate_delete);
        Button tvSelect = (Button) findViewById(R.id.operate_select);
        Button tvInvertSelect = (Button) findViewById(R.id.invert_select);

        tvBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isShow) {
                    selectList.clear();
                    for (ItemBean bean : dataList) {
                        bean.setChecked(false);
                        bean.setShow(false);
                    }
                    adapter.notifyDataSetChanged();
                    isShow = false;
                    fileslist.setLongClickable(true);
                    dismissOperate();

                }
            }
        });
        tvSelect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                for (ItemBean bean : dataList) {
                    if (!bean.isChecked()) {
                        bean.setChecked(true);
                        if (!selectList.contains(bean)) {
                            selectList.add(bean);
                        }
                    }
                }
                Log.i("cyy123", "selectList.size()1 = " + selectList.size());
                adapter.notifyDataSetChanged();
            }
        });
        tvInvertSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (ItemBean bean : dataList){
                    if (!bean.isChecked()){
                        bean.setChecked(true);
                        if (!selectList.contains(bean)) {
                            selectList.add(bean);
                        }
                    }else {
                        bean.setChecked(false);
                        if (selectList.contains(bean)) {
                            selectList.remove(bean);
                        }
                    }
                }
                Log.i("cyy123", "selectList.size()2 = " + selectList.size());
                adapter.notifyDataSetChanged();
            }
        });
        tvDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (selectList != null && selectList.size() > 0) {
                    for(int i=0;i<selectList.size();i++) {
                        for (int j = 0; j < dataList.size(); j++) {
                            if (selectList.get(i).getMsg().equals(dataList.get(j).getMsg())) {
                                m_openPath = pathText.getText().toString();
                                dirfile = new File(m_openPath + "/" + selectList.get(i).getMsg());
                                String filename = dirfile.getName();
                                if (filename != null) {
                                    String str = null;
                                    if (filename.length() > 2) {
                                        str = filename.substring(filename.length() - 2);
                                    } else {
                                        str = filename + 1;
                                    }
                                    if (dirfile == null || !dirfile.exists()) {
                                        return;
                                    } else if (dirfile.isDirectory() || str.equals("..")) {
                                        if (count1 == 1) {
                                            Toast.makeText(OpenAllActivity.this, "不能删除文件夹", Toast.LENGTH_SHORT).show();
                                            count1++;
                                        }

                                    } else if (!str.equals("ds")) {
                                        if (count2 == 1) {
                                            Toast.makeText(OpenAllActivity.this, "不能删除DM-3以外的文件", Toast.LENGTH_SHORT).show();
                                            count2++;
                                        }

                                    } else {
                                        dirfile.delete();
                                        dataList.remove(j);
                                        if (str.equals("ds")) {
                                            pictureDB.delete(db, MyApplication.FORCEDIS, filename);
                                        }

                                    }

                                }
                            }
                        }
                    }
                    dataList.removeAll(selectList);
                    if (isShow) {
                        selectList.clear();
                        for (ItemBean bean : dataList) {
                            bean.setChecked(false);
                            bean.setShow(false);
                        }
                        adapter.notifyDataSetChanged();
                        isShow = false;
                        fileslist.setLongClickable(true);
                        dismissOperate();
                    }
                    currentFiles = curdir.listFiles(filter);

                    // 使用当前目录下的全部文件、文件夹来填充ListView
                    inflateListView(currentFiles);

                    selectList.clear();
                    fileslist.setAdapter(adapter);
                } else {
                    Toast.makeText(OpenAllActivity.this, "请选择条目", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 隐藏操作界面
     */
    private void dismissOperate() {
        layout2.setVisibility(View.VISIBLE);
        layout.setVisibility(View.GONE);
        Animation anim = AnimationUtils.loadAnimation(OpenAllActivity.this, R.anim.operate_out);
        lay.setVisibility(View.GONE);
        lay.setAnimation(anim);
    }

    @Override
    protected void onResume() {
        super.onResume();
        batchOpen.setTextColor(Color.BLACK);
    }

    @Override
    public void onBackPressed() {
        if (isShow) {
            selectList.clear();
            for (ItemBean bean : dataList) {
                bean.setChecked(false);
                bean.setShow(false);
            }
            adapter.notifyDataSetChanged();
            isShow = false;
            fileslist.setLongClickable(true);
            dismissOperate();
        } else {
            try {
                Log.i("mtag", "curdir.getCanonicalPath() = " + curdir.getCanonicalPath());
                if (!curdir.getCanonicalPath().equals(Environment.getExternalStorageDirectory().getAbsolutePath())) {

                    // 获取上一级目录
                    curdir = curdir.getParentFile();
                    Log.i("mtag","curdir = "+curdir);
                    // 列出当前目录下的所有文件
                    currentFiles = curdir.listFiles(filter);
                    // 再次更新ListView
                    inflateListView(currentFiles);

                } else {
                    finish();
                }

                if (curdir.getCanonicalPath().equals("/")) {
                    finish();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 根据文件夹填充ListView
     *
     * @param files
     */
    public void inflateListView(File[] files) {
        if (files == null) {
            return;
        }
        dataList.clear();
        dataList.add(new ItemBean("..",R.mipmap.folder,false,false));
        for (int i = 0; i < files.length; i++) {

            if (currentFiles[i].isDirectory()) {
                //如果是文件夹就显示的图片为文件夹的图片
                dataList.add(new ItemBean( currentFiles[i].getName(),R.mipmap.folder,false,false));
            } else {
                dataList.add(new ItemBean(currentFiles[i].getName(),R.mipmap.file,false,false));
            }

        }
        adapter = new Adapter(dataList, this);
        fileslist.setAdapter(adapter);
        adapter.setOnShowItemClickListener(this);
        //填充数据集
       // fileslist.setAdapter(adapter);

        try {
            pathText.setText(curdir.getCanonicalPath());
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0x01)
        {
            // 列出当前目录下的所有文件
            currentFiles = curdir.listFiles(filter);
            // 再次更新ListView
            inflateListView(currentFiles);
        }
    }
}