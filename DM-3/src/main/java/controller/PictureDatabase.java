package controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.provider.BaseColumns;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.ArrayList;

/**
 * Created by Administrator on 16-10-20.
 */
public class PictureDatabase extends SQLiteOpenHelper {
    public static final String CREATE_FORCE = "create table Force ("

            + "id integer primary key autoincrement, "

            + "liftid text, "

            + "operator text, "

            + "location text, "

            + "name text, "

            + "picture blob, "

            + "fmax real, "

            + "fkin real, "

            + "energy real)";
    public static final String CREATE_SPEED = "create table Speed ("

            + "id integer primary key autoincrement, "

            + "liftid text, "

            + "operator text, "

            + "location text, "

            + "name text, "

            + "picture blob, "

            + "speedAve real, "

            + "speedMax real, "

            + "PspeedAcc real,"

            + "NspeedAcc real,"

            + "closeTime real)";

    public static final String CREATE_BOTH = "create table Both ("

            + "id integer primary key autoincrement, "

            + "liftid text, "

            + "operator text, "

            + "location text, "

            + "name text, "

            + "picture blob, "

            + "fmax real, "

            + "fkin real, "

            + "energy real)";

    //数据库的字段
    public static class PictureColumns implements BaseColumns {
        public static final String PICTURE = "picture";
    }

    private Context mContext;

    //数据库名
    private static final String DATABASE_NAME = "DM--2.db";
    //数据库版本号
    private static final int DATABASE_Version = 1;


    //创建数据库
    public PictureDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_Version);
        this.mContext = context;
    }

    //创建表并初始化表,用于初次使用软件时生成数据库表。
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_FORCE);
        db.execSQL(CREATE_SPEED);
        db.execSQL(CREATE_BOTH);
         Toast.makeText(mContext, "表创建成功", Toast.LENGTH_SHORT).show();
        String sdpath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File file = new File(sdpath + "/" + "DM-2");
        if (file.exists()) {
            DeleteFile(file);
            //Toast.makeText(mContext, "删除DM-2成功", Toast.LENGTH_SHORT).show();
        }

    }

    public void DeleteFile(File file) {
        if (file.exists() == false) {
            return;
        } else {
            if (file.isFile()) {
                file.delete();
                return;
            }
            if (file.isDirectory()) {
                File[] childFile = file.listFiles();
                if (childFile == null || childFile.length == 0) {
                    file.delete();
                    return;
                }
                for (File f : childFile) {
                    DeleteFile(f);
                }
                file.delete();
            }
        }
    }

    //将转换后的图片存入到数据库中
    public void initDataBaseF(SQLiteDatabase db, Bitmap bitmap, String tableName, String name, String liftid, String operator, String location, float fmax, float fkin, float energy) {
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("picture", getPicture(bitmap));
        cv.put("liftid", liftid);
        cv.put("operator", operator);
        cv.put("location", location);
        cv.put("fmax", fmax);
        cv.put("fkin", fkin);
        cv.put("energy", energy);
        db.insert(tableName, null, cv);
        cv.clear();
    }

    //将转换后的图片存入到数据库中
    public void initDataBaseS(SQLiteDatabase db, Bitmap bitmap, String tableName, String name, String liftid, String operator, String location, float speedAve, float speedMax, float PspeedAcc, float NspeedAcc, float closeTime) {
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("picture", getPicture(bitmap));
        cv.put("liftid", liftid);
        cv.put("operator", operator);
        cv.put("location", location);
        cv.put("speedAve", speedAve);
        cv.put("speedMax", speedMax);
        cv.put("PspeedAcc", PspeedAcc);
        cv.put("NspeedAcc", NspeedAcc);
        cv.put("closeTime", closeTime);
        db.insert(tableName, null, cv);
       // Toast.makeText(mContext, "插入数据成功", Toast.LENGTH_SHORT).show();
        cv.clear();
    }

    //删除表
    public void dropTable(SQLiteDatabase db) {
        String sql1 = "drop table Force";
        String sql2 = "drop table Speed";
        String sql3 = "drop table Both";
        db.execSQL(sql1);
        db.execSQL(sql2);
        db.execSQL(sql3);
    }

    //将drawable转换成可以用来存储的byte[]类型
    private byte[] getPicture(Bitmap bitmap) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
        return os.toByteArray();
    }

    //更新数据库
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS " + MyApplication.FORCE);
        db.execSQL(" DROP TABLE IF EXISTS " + MyApplication.SPEED);
        db.execSQL(" DROP TABLE IF EXISTS " + MyApplication.BOTH);
        onCreate(db);
    }

    //2.自定义方法，用于删除数据
    public void delete(SQLiteDatabase db, String tableName) {
			/*
			 * 删除数据的sql语句
			 *
			 * delete from 表名       删除表中所有的数据
			 * delete from 表名   where 条件表达式  删除符合条件的数据
			 *
			 * */
        db.delete(tableName, null, null);
    }

    //2.自定义方法，用于删除数据
    public void delete(SQLiteDatabase db, String tableName, String name) {
			/*
			 * 删除数据的sql语句
			 *
			 * delete from 表名       删除表中所有的数据
			 * delete from 表名   where 条件表达式  删除符合条件的数据
			 *
			 * */
        db.delete(tableName, "name = ? ", new String[]{name});
        //Toast.makeText(mContext, "删除数据成功", Toast.LENGTH_SHORT).show();
    }

    //查询信息
    public ArrayList<String> getInfos(SQLiteDatabase sd, String tableName, String name) {

        ArrayList<String> infos = new ArrayList<String>();

        //查询数据库
        Cursor c = sd.query(tableName, null, null, null, null, null, null);
        if (c != null && c.getCount() != 0) {
            if (c.moveToFirst()) {
                do {
                        if (tableName.equals(MyApplication.FORCE)) {
                            String name2 = c.getString(c.getColumnIndex("name"));
                            if (name2.equals(name)) {
                                float fmax = c.getFloat(c.getColumnIndex("fmax"));
                                infos.add(fmax+"");
                                float fkin = c.getFloat(c.getColumnIndex("fkin"));
                                infos.add(fkin+"");
                                float energy = c.getFloat(c.getColumnIndex("energy"));
                                infos.add(energy+"");
                                String liftid = c.getString(c.getColumnIndex("liftid"));
                                infos.add(liftid);
                                String operator = c.getString(c.getColumnIndex("operator"));
                                infos.add(operator);
                                String location = c.getString(c.getColumnIndex("location"));
                                infos.add(location);

                            }
                        } else if (tableName.equals(MyApplication.SPEED)) {
                            String name2 = c.getString(c.getColumnIndex("name"));
                            if (name2.equals(name)) {
                                float speedAve = c.getFloat(c.getColumnIndex("speedAve"));
                                infos.add(speedAve+"");
                                float speedMax = c.getFloat(c.getColumnIndex("speedMax"));
                                infos.add(speedMax+"");
                                float PspeedAcc = c.getFloat(c.getColumnIndex("PspeedAcc"));
                                infos.add(PspeedAcc+"");
                                float NspeedAcc = c.getFloat(c.getColumnIndex("NspeedAcc"));
                                infos.add(NspeedAcc+"");
                                float closeTime = c.getFloat(c.getColumnIndex("closeTime"));
                                infos.add(closeTime+"");
                                String liftid = c.getString(c.getColumnIndex("liftid"));
                                infos.add(liftid);
                                String operator = c.getString(c.getColumnIndex("operator"));
                                infos.add(operator);
                                String location = c.getString(c.getColumnIndex("location"));
                                infos.add(location);
                            }
                    }
                } while (c.moveToNext());
            }
        }
        if (c != null){
            c.close();
        }
        return infos;
    }

    //查询名字对应的图片
    public Bitmap getBitmap(SQLiteDatabase sd, String tableName, String name) {
        Bitmap bitmap = null;
        //查询数据库
        Cursor c = sd.query(tableName, null, null, null, null, null, null);
        if (c != null && c.getCount() != 0) {
            if (c.moveToFirst()) {
                do {
                    String name2 = c.getString(c.getColumnIndex("name"));
                    if (name2.equals(name)) {
                        //获取数据
                        byte[] b = c.getBlob(c.getColumnIndexOrThrow(PictureColumns.PICTURE));
                        //将获取的数据转换成bitmap
                        //bitmap = BitmapFactory.decodeByteArray(b, 0, b.length, null);
                        bitmap=byteToBitmap(b);
                    }
                } while (c.moveToNext());
            }
        }
        if (c != null){
            c.close();
        }

        return bitmap;
    }

    //查询最后一张图片
    public Bitmap getLastBitmap(SQLiteDatabase sd, String tableName) {
        Bitmap bitmap = null;
        //查询数据库
        Cursor c = sd.query(tableName, null, null, null, null, null, null);
        if (c != null && c.getCount() != 0) {
            c.moveToLast();
            //获取数据
            byte[] b = c.getBlob(c.getColumnIndexOrThrow(PictureColumns.PICTURE));
            //将获取的数据转换成bitmap
           // bitmap = BitmapFactory.decodeByteArray(b, 0, b.length, null);
            bitmap=byteToBitmap(b);
        }
        if (c != null){
            c.close();
        }
        return bitmap;
    }

    //查询表中所有图片
    public ArrayList<Bitmap> getBitmaps(SQLiteDatabase sd, String tableName) {

        ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();

        //查询数据库
        Cursor c = sd.query(tableName, null, null, null, null, null, null);
        if (c != null && c.getCount() != 0) {
            if (c.moveToFirst()) {
                do {
                    //获取数据
                    byte[] b = c.getBlob(c.getColumnIndexOrThrow(PictureColumns.PICTURE));
                    //将获取的数据转换成bitmap
                   // Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length, null);
                    Bitmap bitmap=byteToBitmap(b);
                    bitmaps.add(bitmap);

                } while (c.moveToNext());

            }
            if (c != null){
                c.close();
            }
            return bitmaps;
        }
        return null;
    }

    //查询名字对应的数据
    public ArrayList<Float> getDatas(SQLiteDatabase sd, String tableName, String name) {

        ArrayList<Float> infos = new ArrayList<Float>();

        //查询数据库
        Cursor c = sd.query(tableName, null, null, null, null, null, null);
        if (c != null && c.getCount() != 0) {
            if (c.moveToFirst()) {
                do {
                    if (tableName.equals(MyApplication.FORCE)) {
                        String name2 = c.getString(c.getColumnIndex("name"));
                        if (name2.equals(name)) {
                            float fmax = c.getFloat(c.getColumnIndex("fmax"));
                            infos.add(fmax);
                            float fkin = c.getFloat(c.getColumnIndex("fkin"));
                            infos.add(fkin);
                            float energy = c.getFloat(c.getColumnIndex("energy"));
                            infos.add(energy);

                        }
                    } else if (tableName.equals(MyApplication.SPEED)) {
                        String name2 = c.getString(c.getColumnIndex("name"));
                        if (name2.equals(name)) {
                            float speedAve = c.getFloat(c.getColumnIndex("speedAve"));
                            infos.add(speedAve);
                            float speedMax = c.getFloat(c.getColumnIndex("speedMax"));
                            infos.add(speedMax);
                            float PspeedAcc = c.getFloat(c.getColumnIndex("PspeedAcc"));
                            infos.add(PspeedAcc);
                            float NspeedAcc = c.getFloat(c.getColumnIndex("NspeedAcc"));
                            infos.add(NspeedAcc);
                            float closeTime = c.getFloat(c.getColumnIndex("closeTime"));
                            infos.add(closeTime);
                        }
                    }
                } while (c.moveToNext());
            }
        }
        if (c != null){
            c.close();
        }
        return infos;
    }

    //查询名字对应的数据
    public ArrayList<String> getLastDatas(SQLiteDatabase sd, String tableName) {

        ArrayList<String> infos = new ArrayList<String>();

        //查询数据库
        Cursor c = sd.query(tableName, null, null, null, null, null, null);
        if (c != null && c.getCount() != 0) {
            c.moveToLast();
            if (tableName.equals(MyApplication.FORCE)) {
                float fmax = c.getFloat(c.getColumnIndex("fmax"));
                infos.add(fmax+"");
                float fkin = c.getFloat(c.getColumnIndex("fkin"));
                infos.add(fkin+"");
                float energy = c.getFloat(c.getColumnIndex("energy"));
                infos.add(energy+"");
                String liftid = c.getString(c.getColumnIndex("liftid"));
                infos.add(liftid);
                String operator = c.getString(c.getColumnIndex("operator"));
                infos.add(operator);
                String location = c.getString(c.getColumnIndex("location"));
                infos.add(location);

            } else if (tableName.equals(MyApplication.SPEED)) {
                float speedAve = c.getFloat(c.getColumnIndex("speedAve"));
                infos.add(speedAve+"");
                float speedMax = c.getFloat(c.getColumnIndex("speedMax"));
                infos.add(speedMax+"");
                float PspeedAcc = c.getFloat(c.getColumnIndex("PspeedAcc"));
                infos.add(PspeedAcc+"");
                float NspeedAcc = c.getFloat(c.getColumnIndex("NspeedAcc"));
                infos.add(NspeedAcc+"");
                float closeTime = c.getFloat(c.getColumnIndex("closeTime"));
                infos.add(closeTime+"");
                String liftid = c.getString(c.getColumnIndex("liftid"));
                infos.add(liftid);
                String operator = c.getString(c.getColumnIndex("operator"));
                infos.add(operator);
                String location = c.getString(c.getColumnIndex("location"));
                infos.add(location);

            }
            if (c != null){
                c.close();
            }
            return infos;
        }
        return null;
    }

    public static Bitmap byteToBitmap(byte[] imgByte) {
        InputStream input = null;
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        input = new ByteArrayInputStream(imgByte);
        SoftReference softRef = new SoftReference(BitmapFactory.decodeStream(
                input, null, options));
        bitmap = (Bitmap) softRef.get();
        if (imgByte != null) {
            imgByte = null;
        }

        try {
            if (input != null) {
                input.close();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bitmap;
    }
}