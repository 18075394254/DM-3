package activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;


import com.example.user.dm_3.R;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import controller.BaseActivity;

public class ChooseDirActivity extends BaseActivity {
	
	//ListView变量
	private ListView list;
	private TextView pathText;
	private ImageView cancelView,startView;
	//记录当前父文件夹；
	File curdir;
	//记录当前路径下所有文件夹的文件数组；
	File[] currentFiles;
	FileFilter filter;
	static String m_savaPath=null;
	String name=null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//强制为竖屏
		setContentView(R.layout.activity_choosedir);
		list = (ListView)findViewById(R.id.dirlist);
		pathText = (TextView)findViewById(R.id.curdir);
		cancelView= (ImageView) findViewById(R.id.canceldlView);
		startView= (ImageView) findViewById(R.id.startView);

		Intent intent=getIntent();
		name=intent.getStringExtra("name");

		filter = new FileFilter() {
			@Override
			public boolean accept(File file) {
				return file.isDirectory() && file.canRead() && file.canWrite();
			}
		};

		if (m_savaPath == null) {
			m_savaPath = Environment.getExternalStorageDirectory().getAbsolutePath();
		}
		File root = new File(m_savaPath);
		//如果SD卡存在；
		if (root.exists()) 
		{
			curdir = root;
			currentFiles = root.listFiles(filter);
			for (int i=0;i<currentFiles.length;i++){
				Log.i("===y123", " currentFiles[i] " + currentFiles[i]);

			}
			// 使用当前目录下的全部文件、文件夹来填充ListView 
			inflateListView(currentFiles);
			pathText.setText(curdir.getAbsolutePath());
		}

		cancelView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		startView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				m_savaPath = pathText.getText().toString();
				Intent intent = new Intent(ChooseDirActivity.this,ImportAllActivity.class);
				//Bundle newbundle = new Bundle();
				intent.putExtra("path", m_savaPath);
				intent.putExtra("name", name);
				startActivity(intent);
				m_savaPath=null;

			}
		});

		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				if (position == 0) {
					try {
						if (!curdir.getCanonicalPath().equals("/mnt")) {

							// 获取上一级目录 
							curdir = curdir.getParentFile();
							// 列出当前目录下的所有文件
							currentFiles = curdir.listFiles(filter);
							// 再次更新ListView 
							inflateListView(currentFiles);
							pathText.setText(curdir.getAbsolutePath());
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

				// 获取用户点击的文件夹 下的所有文件
				File tempfile = currentFiles[position - 1];
				File[] tem = tempfile.listFiles(filter);

				//获取用户单击的列表项对应的文件夹，设为当前的文件夹
				curdir = tempfile;
				//保存当前的父文件夹内的全部文件和文件夹 
				currentFiles = tem;
				// 再次更新ListView 
				inflateListView(currentFiles);
				pathText.setText(curdir.getAbsolutePath());
			}
		});

	}



	/**
	 *根据文件夹填充ListView
	 * 
	 * @param files
	 */
	private void inflateListView(File[] files) {

		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		
		Map<String, Object> uplevel = new HashMap<String, Object>();
		uplevel.put("icon", R.mipmap.folder);
		uplevel.put("filename", "..");
		data.add(uplevel);
		
		String[] mfrom = new String[]{"filename", "icon" };
		int[] mTo = new int[]{R.id.file_name, R.id.icon };
		for (int i = 0; i < files.length; i++) 
		{
			//只显示目录
			Map<String, Object> itemMap = new HashMap<String, Object>();
			//如果是文件夹就显示的图片为文件夹的图片 
			itemMap.put("icon", R.mipmap.folder);
			itemMap.put("filename", files[i].getName());
			data.add(itemMap);
			
		}
		// 定义一个SimpleAdapter
		SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.listitem, mfrom, mTo);
		//填充数据集
		list.setAdapter(adapter);

	}


}
