package controller;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import model.User;

public class UserDatabase extends SQLiteOpenHelper {

	SQLiteDatabase db;
	public UserDatabase(Context context) {
		/*
		 * 参数二：指定数据库文件的名字
		 * 参数四：数据库的版本号
		 * */
		super(context,"text.db",null, 1);
		// TODO Auto-generated constructor stub
		db=getReadableDatabase();
	}
	//数据库初始化时运行的方法，只会在数据库文件不存在时运行一次
	//一旦数据库文件存在，不再运行
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		Log.i("===", "===onCreate  onCreate");
		/*
		 * 
		 * 通常数据库中不允许出现同名的表
		 * 因此添加if not exists 关键字，用于实现数据库中不存在指定的wp表时创建表
		 * 如果已经存在，不再继续创建
		 * */
		db.execSQL("create table if not exists user (_id Integer primary key autoincrement,name text,pwd text)");
	}
	
	//当数据库版本号发生变化时运行的方法，针对于版本变化，必须是newVersion>oldVersion
	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		db.execSQL("drop table if exists user");
		onCreate(db);
	}
	/*
	 * 通常情况下，建议将所有的数据库操作封装为该类的自定义方法
	 * 方便后期统一修改管理
	 * */
	//1.自定义方法，用于添加数据
	public void add(String name, String pwd) {
		// TODO Auto-generated method stub
		/*
		 * 添加数据的sql语句
		 * insert into 表名(列名1，列名2...) values (值1，值2...)
		 * values中可以通过指定 ？ 先代表占位，稍后再给这个位置赋值
		 * 稍后通过execSQL方法的参数二的Object数组给每一个问号赋值
		 * 即前面的sql语句中有多少个 ？ ，Object数组中就应该有多少个元素
		 * */
		db.execSQL("insert into user (name,pwd) values (?,?) ",new Object[]{name,pwd});
	}
	
	//2.自定义方法，用于删除数据
		public void delete(){
			/*
			 * 删除数据的sql语句
			 * 
			 * delete from 表名       删除表中所有的数据
			 * delete from 表名   where 条件表达式  删除符合条件的数据
			 * 
			 * */
			db.execSQL("delete from user ");
		}
	
	//4.自定义方法，用于查询全表的数据
	public ArrayList<User> getAllUsers(){
		
	/*
	 * 查询全表的sql语句
	 * select * from 表名
	 * */
	//能够获取查询的结果，选择使用rawQuery方法得到查询结果
	/*
	 * 参数一：要执行的查询sql语句
	 * 参数二：参数一中如果存在？，通过此参数给？赋值，如果参数一中没有？，填null即可
	 * */
	Cursor cursor=db.rawQuery("select * from user", null);
	ArrayList<User> list=new ArrayList<User>();
	/*
	 * 通过Cursor对象获取查询数据，并将查询的数据添加到集合中，用于返回
	 * Cursor：游标
	 * 特点：默认是指向查询结果表的第一行的上方，必须每次向下移动一行，
	 * 		每次移动后Cursor对象中可获得当前行的所有数据
	 *
	 * 数据库中获取查询结果的原则：通过Cursor确定行数，再通过列名确定列数，即可将指定行指定列中的数据取出
	 * */
	/*
	 * moveToNext方法用于让Cursor向下挪动一行，并且获取该行数据
	 * 一旦返回值是false，代表已经移动到末尾，没有更多的数据了
	 * */
	while(cursor.moveToNext()){
		/*
		 * 通过cursor调用get方法获取本行指定类中的数据
		 * 方法的参数要求填写该列对应的列索引
		 * 可以通过getColumnIndex方法获取指定列名对应的列索引
		 * */
		String name=cursor.getString(cursor.getColumnIndex("name"));
		String pwd=cursor.getString(cursor.getColumnIndex("pwd"));
	
		
		list.add(new User(name,pwd));
	}
	return list;
}
	
}