package view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import model.Cubic;
import model.Point;
import utils.Calculate;


/**
 * Created by Administrator on 16-10-17.
 */
public class MySeverityView extends View {

    // 控件宽高
    private int viewWidth, viewHeigth;
    Path path=new Path();
    ArrayList<Point> points = new ArrayList<>();
    ArrayList<Point> points2 = new ArrayList<>();

    private ArrayList<Float> m_ForceData=new ArrayList<>();
    private ArrayList<Float> m_DisData=new ArrayList<>();
    private ArrayList<Point> RateValueY=new ArrayList<>();
    //画坐标轴
    Paint paint;
    //画位移曲线
    Paint paint2;
    //画标识线
    Paint paint4;
    //画压力曲线
    private Paint paint3;

    private int width=0;
    private int height=0;
    private Path path2=new Path();
    int timeLength=1;


    //建议添加，当在java文件中new此控件对象时，运行此构造方法
    //将压力数据集合和位移数据集合传递进来
    public MySeverityView(Context context, ArrayList<Float> forceData,ArrayList<Float> disData) {
        super(context);

        /*m_ForceData = new Calculate().lvbo40(forceData,m_ForceData,40,40);
        m_DisData = new Calculate().lvbo40(disData,m_DisData,40,40);*/
        m_ForceData = forceData;
        m_DisData = disData;

        //将时间轴与压力轴的数据绑定在一起
        if (m_ForceData != null) {
            //根据压力数据的个数调整时间轴的长度
                if (m_ForceData.size() < 800 ){
                    timeLength=2;
                }else if(m_ForceData.size() > 799  && m_ForceData.size() < 1200){
                    timeLength=3;
                }else if (m_ForceData.size() > 1199 && m_ForceData.size() < 1600){
                    timeLength=4;
                }else if (m_ForceData.size() > 1599 && m_ForceData.size() < 2000){
                    timeLength=5;
                }else if (m_ForceData.size() > 1999  && m_ForceData.size() < 2400){
                    timeLength=6;
                }else{
                timeLength=7;
            }

            for (int i = 0; i < m_ForceData.size(); i++) {
                points.add(new Point((float) (0.025 + 0.025 * (i - 1)), m_ForceData.get(i)));
              //  forceData.add(m_ForceData.get(i));
            }
            //max1 = CalcMax(forceData);
    }
        //将时间轴与位移轴的数据绑定在一起
        if (m_DisData != null) {
            for (int i = 0; i < m_DisData.size(); i++) {
                points2.add(new Point((float) (0.025 + 0.025 * (i - 1)), m_DisData.get(i)));

               // disData.add(m_DisData.get(i));
            }
           // max1 = CalcMax(disData);
        }


    }
    //建议添加，当控件要被添加到布局文件中进行显示时运行此构造方法
    /*
    * 当要把该控件放置到布局文件中显示，又没有添加该两参的构造方法的时候
    * 会抛出异常：java.lang.NoSuchMethodException: <init> [class android.content.Context, interface android.util.AttributeSet]
    * */
    public MySeverityView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }
    //可选添加，不会被系统自动调用。除非在其余的构造方法中通过this（）方式调用此构造方法，才会运行
    public MySeverityView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /*
    * 手动添加此方法
    * 作用：通过此方法的canvas参数决定该控件上的显示内容
    *
    * Canvas 画布，特点：可通过本对象调用一系列绘制图形的方法
    * 可以将图形绘制到此对象中
    * 该对象上绘制了哪些图形，当前的自定义控件上就显示对应的内容
    * */


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //初始化画笔对象 画坐标及文字
        paint = new Paint();
        //设置画笔的颜色
        paint.setColor(Color.BLACK);
        //设置抗锯齿效果
        paint.setAntiAlias(true);
        //设置画笔效果，如实心，空心等
        paint.setStyle(Paint.Style.STROKE);
        //设置边框宽度
        paint.setStrokeWidth(1);


        //画位移曲线
        paint2=new Paint();
        paint2.setColor(Color.BLUE);
        paint2.setAntiAlias(true);
        paint2.setStyle(Paint.Style.STROKE);
        paint2.setStrokeWidth(4);

        //画压力曲线
        paint3=new Paint();
        paint3.setColor(Color.RED);
        paint3.setAntiAlias(true);
        paint3.setStyle(Paint.Style.STROKE);
        paint3.setStrokeWidth(4);


        //画标识线
        paint4 = new Paint();
        paint4.setColor(Color.GREEN);
        paint4.setAntiAlias(true);
        paint4.setStyle(Paint.Style.STROKE);
        paint4.setStrokeWidth(4);


        Resources resources = this.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();

        width = dm.widthPixels;
        height = dm.heightPixels;
        viewHeigth=height*9/16-height/32;
        viewWidth=width * 7/8+50-width/8;
        //绘制坐标轴
        for(int i=0;i< 9;i++){
            canvas.drawLine(width/8-15, height/32 +viewHeigth/10 + viewHeigth/10 * i, width * 7/8+50 -15,height/32 +viewHeigth/10 + viewHeigth/10 * i,paint);
            canvas.drawLine(width/8-15 +viewWidth/10 + viewWidth/10 * i,height/32,width/8-15 +viewWidth/10 + viewWidth/10 * i,height*9/16,paint);
        if(i==7){
               /* //绘制标准线
                int Y=(height/32  + viewHeigth/10 * i);
                canvas.drawLine(width /8 - 15, Y, width * 7 / 8 + 50-15, Y, paint4);
                paint4.setTextSize(40);
                canvas.drawText("300/N",width/8+10,Y-5,paint4);

            float Y2= (float) (height/32  + viewHeigth/10 * 8.5);
            canvas.drawLine(width /8 - 15, Y2, width * 7 / 8 + 50 - 15, Y2, paint4);
            paint4.setTextSize(40);
            canvas.drawText("15/mm",width * 6 / 8 ,Y2-5,paint4);*/
            }
        }

        //绘制矩形
        paint.setColor(Color.BLACK);
        canvas.drawRect(width / 8-15, height / 32, width * 7 / 8 + 50-15, height * 9 / 16, paint);

        //绘制文字
        paint.setColor(Color.BLACK);
        paint.setTextSize(30);
        /*
        * 1.要绘制的文字内容
        * 2,3控制文字绘制的起始点
        * 起始点的位置控制的是文字的左下角位置
        * 4.画笔
        * */
        paint.setStyle(Paint.Style.FILL);

        String forceValue;
        String disValue;
        String time;

        //画坐标轴上的刻度
        for(int i=0;i<=10;i++) {
            if (i == 0) {
                canvas.drawText("0", width / 8 - 45 - 15, height * 9 / 16 + 30, paint);
            } else if(i > 0 && i < 10){
                //设置画笔的颜色
                paint.setColor(Color.BLACK);
                //时间轴坐标值
                time = timeLength * i + "";
                canvas.drawText(time, width / 8 - 20 - 15 + (viewWidth) / 10 * i, height * 9 / 16 + 30, paint);
                //设置画笔的颜色
                paint.setColor(Color.RED);
                //压力轴坐标值
                forceValue = i * 100 + "";
                canvas.drawText(forceValue, width / 8 - 50 - 15, height / 32 + (viewHeigth) / 10 * (10 - i) + 10, paint);
                //设置画笔的颜色
                paint.setColor(Color.BLUE);
                //位移轴坐标值
                disValue = i * 10 + "";
                canvas.drawText(disValue, width * 7 / 8 + 50 + 10 - 15, height / 32 + 10 + (viewHeigth) / 10 * (10 - i), paint);

            }else{
                //设置画笔的颜色
                paint.setColor(Color.RED);
                canvas.drawText("Force/N", 0, height / 32 + 10 + (viewHeigth) / 10 * (10 - i), paint);
                paint.setColor(Color.BLUE);
                canvas.drawText("S/mm", width * 7 / 8 - 30, height / 32 + 10, paint);
                paint.setColor(Color.BLACK);
                canvas.drawText("Time/S", width * 7 / 8 - 15, height * 9 / 16 + 30, paint);
            }
        }



        canvas.drawLine(width / 8 + 10, height / 32 + (height * 9 / 16 - height / 32) / 10 + (height * 9 / 16 - height / 32) / 10 * 10 - 10, (float) width / 8 + 10 + viewWidth / 5, height / 32 + (height * 9 / 16 - height / 32) / 10 + (height * 9 / 16 - height / 32) / 10 * 10 - 10, paint3);
        canvas.drawText("压力曲线", (float) width / 8 + 10 + viewWidth / 5 + 20, height / 32 + (height * 9 / 16 - height / 32) / 10 + (height * 9 / 16 - height / 32) / 10 * 10 + 5, paint);

        canvas.drawLine((float) width / 8 + 10 + 5 * viewWidth / 10, height / 32 + (height * 9 / 16 - height / 32) / 10 + (height * 9 / 16 - height / 32) / 10 * 10 - 10, (float) width / 8 + 10 + 7 * viewWidth / 10, height / 32 + (height * 9 / 16 - height / 32) / 10 + (height * 9 / 16 - height / 32) / 10 * 10 - 10, paint2);
        canvas.drawText("位移曲线", (float) width / 8 + 10 + 7 * viewWidth / 10 + 20, height / 32 + (height * 9 / 16 - height / 32) / 10 + (height * 9 / 16 - height / 32) / 10 * 10 + 5, paint);


        if(points != null) {
            for (int i = 0; i < points.size(); i++) {
                RateValueY.add(points.get(i));
            }

        }
        Log.i("RateValueY.size ", "RateValueY.size = " + RateValueY.size());
        for(int i=0;i<RateValueY.size();i++){
            float x = (width/8-15 +(viewWidth)/10*RateValueY.get(i).getX()/timeLength);
            float y = (float)(height*9/16-(viewHeigth)/10*(RateValueY.get(i).getY())/100);

            if (i==0){
                path.moveTo(x,y);
            }else {
                path.lineTo(x, y);
            }
        }
        canvas.drawPath(path, paint3);

        RateValueY.clear();



        if(points2.size() != 0) {
            for (int i = 0; i < points2.size(); i++) {
                RateValueY.add(points2.get(i));
            }
        }

        if (RateValueY != null) {
            for (int i = 0; i < RateValueY.size(); i++) {
                float x = (width / 8 -15 + (viewWidth) / 10 * RateValueY.get(i).getX()/timeLength);
                float y = (float) (height * 9 / 16 - (viewHeigth) / 10 * RateValueY.get(i).getY()/10);

                if (i==0){
                    Log.i("points.size ", "RateValueY2222.get(i).getX() = " + RateValueY.get(i).getX());
                    path2.moveTo(x,y);
                }else {
                    path2.lineTo(x, y);
                }

            }
            canvas.drawPath(path2, paint2);
        }

        RateValueY.clear();

        points.clear();

    }
}
