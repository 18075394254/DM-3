package view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.jar.Attributes;

import controller.MyApplication;
import model.Point;

public class MySurfaceView extends SurfaceView implements Runnable, Callback {


    private SurfaceHolder mHolder; // 用于控制SurfaceView

    private Thread t; // 声明一条线程

    private boolean flag; // 线程运行的标识，用于控制线程

    private Canvas mCanvas; // 声明一张画布

    private Paint p; // 声明一支画笔

    private int ScreenWidth,ScreenHeight;//屏幕宽高

    private int nowX,nowY;

    private int chartWidth;//图形坐标的宽度

    private int hennowX,zongnowY;

    private Bitmap bitmap;
    // 控件宽高
    private int viewWidth, viewHeigth;
    Resources resources = this.getResources();
    DisplayMetrics dm = resources.getDisplayMetrics();

    private ArrayList<Point> allpoint;


    public MySurfaceView(Context context) {
        super(context);

        this.setZOrderOnTop(true);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);

        //屏幕宽高
        ScreenWidth = dm.widthPixels;
        ScreenHeight = dm.heightPixels;

        chartWidth = ScreenWidth * 7/8+50-ScreenWidth/8;

        //当前X,Y的坐标
        nowX = ScreenWidth/2;
        nowY = ScreenHeight/2;
        mHolder = getHolder(); // 获得SurfaceHolder对象
        mHolder.addCallback(this); // 为SurfaceView添加状态监听
        p = new Paint(); // 创建一个画笔对象
        p.setColor(Color.GREEN); // 设置画笔的颜色为绿色
        setFocusable(true); // 设置焦点匹配
        p.setAntiAlias(true);
        p.setStyle(Paint.Style.FILL);
        p.setStrokeWidth(4);
        p.setTextSize(40);
    }
    public MySurfaceView(Context context,AttributeSet attrs) {
        super(context,attrs);

        this.setZOrderOnTop(true);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);

        //屏幕宽高
        ScreenWidth = dm.widthPixels;
        ScreenHeight = dm.heightPixels;

        chartWidth = ScreenWidth * 7/8+50-ScreenWidth/8;

        //当前X,Y的坐标
        nowX = ScreenWidth/2;
        nowY = ScreenHeight/2;
        mHolder = getHolder(); // 获得SurfaceHolder对象
        mHolder.addCallback(this); // 为SurfaceView添加状态监听
        p = new Paint(); // 创建一个画笔对象
        p.setColor(Color.GREEN); // 设置画笔的颜色为白色
        setFocusable(true); // 设置焦点匹配
        p.setAntiAlias(true);
        p.setStyle(Paint.Style.FILL);
        p.setStrokeWidth(4);
        p.setTextSize(40);
    }

    /**
     * 自定义一个方法
     */
    public void Draw() {
        if (mHolder != null) {
            mCanvas = mHolder.lockCanvas(); // 获得画布对象，开始对画布画画
            // mCanvas.drawRGB(255, 255,255); // 把画布填充为白色

            mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//绘制透明色
            p.setColor(Color.GREEN);
            mCanvas.drawLine(nowX, 0, nowX, ScreenHeight * 17 / 32, p);
            mCanvas.drawLine(nowX - 150, ScreenHeight * 5 / 32, nowX + 150, ScreenHeight * 5 / 32, p);

            allpoint = MyApplication.getPointString();
            if (allpoint.size() != 0) {

                //计算时间轴的长度
                int timeLength = 2;
                if (allpoint.size() < 800 ){
                    timeLength=20;
                }else if(allpoint.size() > 799  && allpoint.size() < 1200){
                    timeLength=30;
                }else if (allpoint.size() > 1199 && allpoint.size() < 1600){
                    timeLength=40;
                }else if (allpoint.size() > 1599 && allpoint.size() < 2000){
                    timeLength=50;
                }else if (allpoint.size() > 1999  && allpoint.size() < 2400){
                    timeLength=60;
                }else{
                    timeLength=70;
                }

               // int proportion = (int)(0.025*allpoint.size()/timeLength);//0.025秒一个点，计算所有点所需时间与时间轴比例

                int index = (int)((hennowX * allpoint.size()) / (chartWidth*(0.025*allpoint.size()/timeLength)));

                if (index > allpoint.size()-1){
                    p.setColor(Color.BLACK);
                    mCanvas.drawText("( null ,null)", nowX + 10, ScreenHeight * 5 / 32 - 30, p);

                }else {
                    p.setColor(Color.BLACK);
                    mCanvas.drawText("(" + allpoint.get(index).getX() + "," + allpoint.get(index).getY() + ")", nowX + 10, ScreenHeight * 5 / 32 - 30, p);
                }
            }else{
                p.setColor(Color.BLACK);
                mCanvas.drawText("( null ,null)", nowX + 10, ScreenHeight * 5 / 32 - 30, p);

            }
            try {
                if (mHolder != null)
                    mHolder.unlockCanvasAndPost(mCanvas); // 完成画画，把画布显示在屏幕上
            } catch (Exception e) {
                Log.i("mtag", "Exception！");
                flag = false;
            }
        }else{
            flag = false;
        }
    }

    /**
     * 当SurfaceView创建的时候，调用此函数
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        t = new Thread(this); // 创建一个线程对象
        flag = true; // 把线程运行的标识设置成true
        t.start(); // 启动线程
        Log.i("mtag", "SurfaceView创建了！");
    }

    /**
     * 当SurfaceView的视图发生改变的时候，调用此函数
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        Log.i("mtag", "SurfaceView视图改变了！");
    }

    /**
     * 当SurfaceView销毁的时候，调用此函数
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        flag = false; // 把线程运行的标识设置成false
        Log.i("mtag","SurfaceView被销毁了！");
    }

    /**
     * 当屏幕被触摸时调用
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        nowX = (int) event.getX()  ; // 获得屏幕被触摸时对应的X轴坐标
        hennowX = nowX - (ScreenWidth/8-15);
        if (nowX > (ScreenWidth * 7 / 8 + 50-15)){
            nowX = ScreenWidth * 7 / 8 + 50-15;
            hennowX = ScreenWidth * 7 / 8 + 50-15;
        }else if (nowX < (ScreenWidth/8-15)){
            hennowX = 0;
            nowX = (ScreenWidth/8-15);
        }

        // y = (int) event.getY()-(ScreenHeight - bitHeight); // 获得屏幕被触摸时对应的Y轴坐标
        return true;
    }

    /**
     * 当用户按键时调用
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){    //当用户点击↑键时
            nowX--;    //设置Y轴坐标减1
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void run() {
        while (flag) {
            Draw(); // 调用自定义画画方法
            try {
                Thread.sleep(50); // 让线程休息50毫秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}