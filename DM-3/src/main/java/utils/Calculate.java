package utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.pdf.PrintedPdfDocument;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import controller.MyApplication;
import model.Point;

/**
 * Created by Administrator on 2016/12/16 0016.
 */
public class Calculate {

    private float forceMax=0;
    private float standerdValue = (float) 300.0;
    //数据是否合格 0是1否
    private int isQualified = 0;
    private HashMap map=new HashMap();
    public Calculate() {

    }
    //压力数据滤波,滤波频率30hz
    //X=250,Y=30
    public ArrayList<Float> lvbo40(ArrayList<Float> m_cutData, ArrayList<Float> m_filterData, int X, int Y) {
        m_filterData.clear();
        double[] y = new double[3];
        Double t, a, b, k;
        t = X / (3.1415926 * Y);
        k = t * t + 1.414 * t + 1;
        k = 1 / k;
        a = -2 * (1 - t * t) * k;
        b = (t * t - 1.414 * t + 1) * k;
        y[0] = m_cutData.get(0).floatValue();
        y[1] = a * y[0] + m_cutData.get(1).floatValue() + 2 * m_cutData.get(0).floatValue();
        float temp;
        temp = (float) (y[0] * k);
        m_filterData.add(Float.valueOf(temp));
        temp = (float) (y[1] * k);
        m_filterData.add(Float.valueOf(temp));
        for (int i = 2; i < m_cutData.size(); i++) {
            y[2] = a * y[1] - b * y[0] + m_cutData.get(i).floatValue() + 2 * m_cutData.get(i - 1).floatValue()
                    + m_cutData.get(i - 2).floatValue();
            temp = (float) (y[2] * k);
            m_filterData.add(Float.valueOf(temp));

            y[0] = y[1];
            y[1] = y[2];

        }
        return m_filterData;
    }




//计算最大值
    public float getMax(ArrayList<Float> array) {


        float Max=0;

            //求出最大值，以及测试数据的合格情况
            if (array != null){
                for (int i=0;i<array.size();i++){
                    float value = array.get(i).floatValue();
                    if (value > Max){
                        Max=value;
                    }
                }

            }

            return Max;
    }

    //计算平均值
    public float getAverage(ArrayList<Float> array) {


        float average =0;
        float sum = 0;
        //求出最大值，以及测试数据的合格情况
        if (array != null){
            for (int i=0;i<array.size();i++){
                sum += array.get(i).floatValue();

            }
            average = sum/array.size();

        }

        return average;
    }
    //计算最小值
    public float getMin(ArrayList<Float> array) {

        float Min = 0;


        //求出最小值，以及测试数据的合格情况
        if (array != null){
            Min = array.get(0).floatValue();
            for (int i=0;i<array.size();i++){
                float value = array.get(i).floatValue();
                if (value < Min){
                    Min = value;
                }
            }

        }

        return Min;
    }
    /**

     * //计算出最接近300N的那个数对应的位移值

     * @param m_DisData 表示位移点的数组集合

     * @param forceValues 压力的数据集合

     * @param targetNum 标准线 300

     * @return

     */

    public HashMap getDisValue(ArrayList<Float> m_DisData,ArrayList<Float> forceValues, int targetNum) {
            float disValue =0;
            float forceValue =0;
            forceMax=0;
            map.clear();
        //根据数组长度判断forceValue中等于300的数有几个
        ArrayList<Float> disValueArr =new ArrayList<>();

        //当没有压力值等于300N但是最大值大于300N时，将压力值与300的绝对值添加到数组中，再算出最小值，就是最接近300的值
        ArrayList<Float> forceValueArr =new ArrayList<>();

        //当没有压力值等于300N但是最大值大于300N时，且压力值与300N的差的绝对值有多个相同时，计算位移平均值，就是最接近300的值
        ArrayList<Float> disValueArray =new ArrayList<>();

        if (forceValues != null) {
            //计算出最大值
           forceMax = getMax(forceValues);
            Log.i("2018-06-26 ", "forceMax = " + forceMax);
            //最大值大于299时表示测试的数据合格，然后求位移距离
            if (forceMax > 299){
                for (int i = 0; i < forceValues.size(); i++) {
                    if (forceValues.get(i) == targetNum) {

                        //将压力值等于300N时对应的位移值添加到数组
                        disValueArr.add(m_DisData.get(i));
                        Log.i("2018-06-26 ", "m_DisData.get(i) = " + m_DisData.get(i));
                    }
                }

                Log.i("2018-06-26 ", "disValueArr.size = " + disValueArr.size());
                //只有一个等于300N,找到对应的值
                if (disValueArr.size() == 1){
                    //本次数据合格
                    isQualified = 0;
                    map.put("isQualified",isQualified);

                    disValue = disValueArr.get(0);
                    map.put("disValue",disValue);

                    //有多个压力值等于300N时，取对应的最大的位移值
                }else if (disValueArr.size() > 1){
                    //本次数据合格
                    isQualified = 0;
                    map.put("isQualified",isQualified);

                    disValue = getMax(disValueArr);
                    map.put("disValue",disValue);

                    //压力数组中没有等于300N的值，取最接近300N的压力值对应的位移值
                }else if (disValueArr.size() == 0){
                    //将压力值与300差的绝对值添加到数组中
                    for (int i = 0; i < forceValues.size(); i++) {
                        float value = forceValues.get(i)-300;
                        forceValueArr.add(Math.abs(value));
                    }

                    //计算出绝对值中最小的值
                    float forceValueAbs = getMin(forceValueArr);
                    Log.i("2018-06-26 ", "forceValueAbs = " + forceValueAbs);

                    //绝对值小于1表示在295-305之前，超过这个范围就不算300N的位移量了
                    if (forceValueAbs < 5) {
                        //如果有多个最小绝对值，就将这几个值的位移值保存到数组中
                        for (int i = 0; i < forceValueArr.size(); i++) {
                            if (forceValueAbs == forceValueArr.get(i)) {
                                disValueArray.add(m_DisData.get(i));
                                Log.i("2018-06-26 ", "m_DisData.get(i) = " + m_DisData.get(i));
                            }
                        }

                        //本次数据合格
                        isQualified = 0;
                        map.put("isQualified",isQualified);

                        disValue = getAverage(disValueArray);
                        map.put("disValue", disValue);
                    }else{
                        //表示没有找到295-305之内的压力值，本次数据作废
                        isQualified = 2;
                        map.put("isQualified",isQualified);

                        map.put("disValue",disValue);
                    }
                }
                float force = (float) 300.0;
                map.put("forceValue",force);
            }else{
                isQualified = 1;
                //小于299表示测试的所有数据都不到300N，本次数据不合格
                map.put("isQualified",isQualified);
                map.put("disValue",disValue);
                map.put("forceValue",forceValue);
            }



        }
            return map;

    }
    /**
     * 获取目标文件的里面的String
     *
     * @param file
     *            目标文件
     * @param content
     *            写入的内容
     * @return
     */
    public void writeSetingsToFile(File file, String content) {


        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(file);
            fos.write(content.getBytes("utf-8"));
            fos.flush();
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();

        }
    }
    //读文件
    public String readSetingsToFile(File file) {

        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            char[] input = new char[fis.available()];
            isr.read(input);
            isr.close();
            fis.close();
            String in = new String(input);
            return in;
        } catch (FileNotFoundException e) {

            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    //设置数据
    public void setAllData(String filename){
        String content = null;
        //测试数据的集合
        ArrayList<Point> m_AllData = new ArrayList<Point>();
        content =  new Calculate().readSetingsToFile(new File(filename));
        if (content != null){
            String[] s = content.split(",");
            Log.i("points.size ", "s.length = " + s.length);
            //s.length - 1是为了防止最后一个""信息影响数据解析
            for (int i = 0; i < s.length -1; i+=2) {
                if (i % 2 == 0) {
                    m_AllData.add(new Point(Float.parseFloat(s[i]),Float.parseFloat(s[i+1])));
                }
            }
            MyApplication.setPointString(m_AllData);
            Log.i("mtag", "数据读取完成！");
        }
    }

    public void GenPDF(Activity activity,String path,String mType,String date,String people,String location,String number,String force,String dis,Bitmap bitmap) {
        //获取屏幕宽高
        int w = MyApplication.getWindowWidth();
        int h = MyApplication.getWindowHeight();
        PrintAttributes attributes = new PrintAttributes.Builder()
                .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                .setResolution(new PrintAttributes.Resolution("1", "print", w, h))
                .setMinMargins(new PrintAttributes.Margins(0, 0, 0, 0))
                .setColorMode(PrintAttributes.COLOR_MODE_COLOR)
                        //.setDuplexMode(PrintAttributes.DUPLEX_MODE_NONE)
                .build();
        PrintedPdfDocument pdfDocument = new PrintedPdfDocument(activity, attributes);

        // 绘制PDF
        PdfDocument.Page page = pdfDocument.startPage(0);   // 创建页，页号从0开始
        //PdfDocument.PageInfo pageInfo = page.getInfo(); // 获取页信息，可以根据长宽来排版

        Canvas canvas = page.getCanvas();


        int titleBaseLine = 72;
        int leftMargin = 54;
        int center = w/4;
        // Toast.makeText(activity,"宽度为 = "+center,Toast.LENGTH_SHORT).show();
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(25);
        canvas.drawText("测试报告", center, titleBaseLine, paint);
        paint.setTextSize(10);
        canvas.drawText("测试仪器："+mType, leftMargin, titleBaseLine + 20, paint);
      //  canvas.drawText("测试单位：", leftMargin, titleBaseLine + 40, paint);
        canvas.drawText("测试时间："+date, leftMargin, titleBaseLine + 40, paint);
        canvas.drawText("测试人员："+people, leftMargin, titleBaseLine + 60, paint);
        canvas.drawText("测试地点："+location, center, titleBaseLine + 20, paint);
        canvas.drawText("电梯编号："+number, center, titleBaseLine + 40, paint);
        canvas.drawText("补充信息：", center, titleBaseLine + 60, paint);
        canvas.drawText("压力值："+force, leftMargin, titleBaseLine + 80, paint);
        canvas.drawText("位移值："+dis, center, titleBaseLine + 80, paint);
        canvas.drawBitmap(scale(bitmap, 0.4f,0.4f),leftMargin,titleBaseLine+100,paint);
        pdfDocument.finishPage(page);  // 结束页

        // 输出到文件
        try {
            File file = new File(path);
            FileOutputStream outputStream = new FileOutputStream(file);
            pdfDocument.writeTo(outputStream);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 图片的缩放方法
     *
     * @param src    ：源图片资源
     * @param scaleX ：横向缩放比例
     * @param scaleY ：纵向缩放比例
     */
    public Bitmap scale(Bitmap src, float scaleX, float scaleY) {
        Matrix matrix = new Matrix();
        matrix.postScale(scaleX, scaleY);
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(),
                matrix, true);
    }


    /**
     * 获取和保存当前屏幕的截图
     */
    private void GetandSaveCurrentImage(Activity activty,File file)
    {
        //1.构建Bitmap
        //获取屏幕宽高
        int w = MyApplication.getWindowHeight();
        int h = MyApplication.getWindowHeight();

        Bitmap bitmap = Bitmap.createBitmap( w, h, Bitmap.Config.ARGB_8888 );

        //2.获取屏幕
        View decorView = activty.getWindow().getDecorView();
        decorView.setDrawingCacheEnabled(true);
        bitmap = decorView.getDrawingCache();

        //3.保存Bitmap
        try {

            if (!file.exists()) {
                file.createNewFile();
            }

            FileOutputStream fos = null;
            fos = new FileOutputStream(file);
            if (null != fos) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.flush();
                fos.close();

                // Toast.makeText(this, "截屏文件已保存至SDCard/AndyDemo/ScreenImage/下", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取SDCard的文件夹路径功能
     * @return
     */
    private String getSDCardPath(){
        File sdcardDir = null;
        //推断SDCard是否存在
        boolean sdcardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        if(sdcardExist){
            sdcardDir = Environment.getExternalStorageDirectory();
        }
        return sdcardDir.toString();
    }





    public void DeletePng(String picPath) {
        File file = new File(picPath);
        if (file.exists()) {
            file.delete();
        }
    }

    public void CreatePng(String picPath,Bitmap bitmap) {
        //String picPath = "/mnt/sdcard/tmp/debug01.png";

        File bitmapFile = new File(picPath);
        FileOutputStream bitmapWtriter = null;
        try {
            bitmapWtriter = new FileOutputStream(bitmapFile);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.PNG, 30, bitmapWtriter);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(picPath);
            bitmap  = BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {

        }

    }

}
