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


/**
 * Created by Administrator on 16-10-17.
 */
public class MySeverityView extends View {

    static final int UNKNOW_STATE = 0;
    static final int UPDATE = 1;

    //ArrayList<Point> points=new ArrayList<>();
    public int test_state = UNKNOW_STATE;
    // 控件宽高
    private int viewWidth, viewHeigth;
    Path path=new Path();
    ArrayList<Point> points = new ArrayList<>();
    ArrayList<Point> points2 = new ArrayList<>();
    ArrayList<Point> Accpoints = new ArrayList<>();
    private ArrayList<Float> points_x = null;
    private ArrayList<Float> points_y = null;
    private ArrayList<Float> v_speed=new ArrayList<>();
    private ArrayList<Float> mData=new ArrayList<>();
    private ArrayList<Point> RateValueY=new ArrayList<>();
    Paint paint;
    Paint paint2;
    Paint paint3;
    Paint paint4;
    private Paint paint6;
    private int STEPS=12;
    private float S=0;
    private float allS=0;
    private int width=0;
    private int height=0;
    private Path path2=new Path();
    private Path path3=new Path();
    StringBuilder sb=new StringBuilder();
    float max1=0;
    float max2=0;
    private float Acc=0;
    private Paint paint5;
    private Float MaxAcc2;
    private Float MaxAcc1;
    private Float min1;
    private Float min2;
    int timeLength=1;


    //建议添加，当在java文件中new此控件对象时，运行此构造方法
    public MySeverityView(Context context, ArrayList<Float> pointY) {
        super(context);
        points_x = new ArrayList<Float>();
        points_y = new ArrayList<Float>();

        if (pointY != null) {

            if (pointY.size() < 400 || pointY.size() == 400){
                    timeLength=1;
                }else if(pointY.size() > 800 || pointY.size() == 800 && pointY.size() < 1200){
                    timeLength=2;
                }else{
                    timeLength=3;
                }
            for (int i = 0; i < pointY.size(); i++) {
                points.add(new Point((float) (0.025+ 0.025 * (i-1)), pointY.get(i)));
                v_speed.add(pointY.get(i));
            }
        max1 = CalcMax(v_speed);
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

        //初始化画笔对象
        paint = new Paint();

        paint2=new Paint();

        paint3=new Paint();

        paint4=new Paint();

        paint5=new Paint();

        paint6=new Paint();
        //设置画笔的颜色
        paint.setColor(Color.BLACK);
        //设置抗锯齿效果
        paint.setAntiAlias(true);
        //设置画笔效果，如实心，空心等
        paint.setStyle(Paint.Style.STROKE);
        //设置边框宽度
        paint.setStrokeWidth(1);

        paint6.setColor(Color.RED);
        paint6.setAntiAlias(true);
        paint6.setStyle(Paint.Style.STROKE);
        paint6.setStrokeWidth(4);

        paint2.setColor(Color.BLUE);
        paint2.setAntiAlias(true);
        paint2.setStyle(Paint.Style.STROKE);
        paint2.setStrokeWidth(4);

        paint3.setColor(Color.BLACK);
        paint3.setAntiAlias(true);
        // paint3.setStyle(Paint.Style.STROKE);
        paint3.setStrokeWidth(2);
        paint3.setTextSize(40);
        //设置抗锯齿效果
        paint3.setAntiAlias(true);
        //设置画笔效果，如实心，空心等
        paint3.setStyle(Paint.Style.STROKE);
        //设置边框宽度
        paint3.setStrokeWidth(3);

        paint4.setColor(Color.BLACK);
        paint4.setAntiAlias(true);
        // paint4.setStyle(Paint.Style.STROKE);
        paint4.setStrokeWidth(2);
        paint4.setTextSize(40);

       // paint5.setColor(this.getResources().getColor(R.color.fuchsia));
        paint5.setColor(Color.GREEN);
        paint5.setAntiAlias(true);
        paint5.setStyle(Paint.Style.STROKE);
        paint5.setStrokeWidth(3);
        paint5.setTextSize(40);

        Resources resources = this.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        float density1 = dm.density;
        width = dm.widthPixels;
        height = dm.heightPixels;
        viewHeigth=height*9/16-height/32;
        viewWidth=width * 7/8+50-width/8;
        //绘制坐标轴
        for(int i=0;i< 9;i++){
            canvas.drawLine(width/8-15, height/32 +viewHeigth/10 + viewHeigth/10 * i, width * 7/8+50 -15,height/32 +viewHeigth/10 + viewHeigth/10 * i,paint);
            canvas.drawLine(width/8-15 +viewWidth/10 + viewWidth/10 * i,height/32,width/8-15 +viewWidth/10 + viewWidth/10 * i,height*9/16,paint);
            /*if(i==8){
                //绘制标准线
                paint.setColor(Color.GREEN);
                paint.setStrokeWidth(3);
                int Y=(height/32 +viewHeigth/10 + viewHeigth/10 * i);
                canvas.drawLine(width / 8, Y, width * 7 / 8 + 50, Y, paint);
                int Y2=height/32 +viewHeigth/10 + viewHeigth/10 * 5;
                canvas.drawLine(width / 8, Y2, width * 7 / 8 + 50, Y2, paint);
                paint.setTextSize(40);
                canvas.drawText("0.2m/s",width/8+10,Y-5,paint);
                //canvas.drawText("0.8m/s",width/8+10,Y2-5,paint);

            }*/
        }

        //绘制矩形
        paint.setColor(Color.BLACK);
        canvas.drawRect(width / 8-15, height / 32, width * 7 / 8 + 50-15, height * 9 / 16, paint3);

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

        String speed;
        String time;
        String weiyi;
        //画坐标轴上的刻度
        for(int i=0;i<=10;i++) {
            if(i==0){
                canvas.drawText("0",width/8-45-15,height*9/16+30,paint);
            }else if (i > 0 &&  i < 4) {
                time = timeLength * i + "";
                canvas.drawText(time, width/8-20 -15 + (viewWidth)/10 * i, height*9/16+30, paint);
                speed = (float)i/10 + "";
                canvas.drawText(speed, width / 8 - 50 -15, height / 32 + (viewHeigth) / 10 * (10 - i) + 10, paint);
                canvas.drawText(speed, width*7/8+50+10 -15, height/32 +10+(viewHeigth) / 10 * (10 - i), paint);
            }else if(i==4){
                weiyi=(float)4/10+"";
                time = timeLength * i + "";
                canvas.drawText(time, width/8-20 -15 + (viewWidth)/10 * i, height*9/16+30, paint);
                canvas.drawText("Speed/V",0, height/32+10+(viewHeigth) / 10 * (10 - i), paint);
                canvas.drawText("S/m",width*7/8+50+10 -25, height/32+10, paint);
                canvas.drawText("Time/S",width*7/8-15,height*9/16+30,paint);
                canvas.drawText(weiyi, width*7/8+50+10 -15, height/32 +(viewHeigth)/10*(10-i)+10, paint);
            }else if(i>4 && i<10){
                time = timeLength * i + "";
                speed = (float)(i-7)/2 + "";
                weiyi=(float)i/10 + "";
                Log.i("lol", "speed = " + speed);
                canvas.drawText(time, width / 8 - 20 -15 + (viewWidth) / 10 * i, height*9/16+30, paint);
                canvas.drawText(speed, width / 8 - 50 -15, height / 32 + (viewHeigth) / 10 * (10 - i) + 10, paint);
                canvas.drawText(weiyi, width*7/8+50+10 -15, height/32 +(viewHeigth)/10*(10-i)+10, paint);
                canvas.drawText("Acc/a",30 -15, height/32+10 , paint);
            }
        }



        if(points != null) {
            for (int i = 0; i < points.size(); i++) {
                points_x.add(points.get(i).getX());
                points_y.add(points.get(i).getY());
            }

            pathLine(points_x, points_y, path,1);
            max2 = CalcMax(mData);
            mData.clear();
        }
        for(int i=0;i<RateValueY.size();i++){
            float x = (width/8-15 +(viewWidth)/10*RateValueY.get(i).getX()/timeLength);
            float y = (float)(height*9/16-(viewHeigth)/10*(RateValueY.get(i).getY()*max1/max2)/0.1);
            Log.i("mtag","y = "+y + "max1 = "+max1 +"max2 = "+max2);
            if (i==0){
                path.moveTo(x,y);
            }else {
                path.lineTo(x, y);
            }
        }
        canvas.drawPath(path, paint6);
        points_x.clear();
        points_y.clear();
        RateValueY.clear();


        if(v_speed != null){
            for(int i=0;i< v_speed.size();i++) {

                if (i == 0) {
                    S = 0;
                    Acc=0;
                } else {
                    if (v_speed.get(i) - v_speed.get(i - 1) == 0) {
                        S = (float) 0.1 * v_speed.get(i);
                    } else {
                        S = (float) (0.1 * Math.abs(v_speed.get(i) * v_speed.get(i) - v_speed.get(i - 1) * v_speed.get(i - 1))) / (2 * Math.abs(v_speed.get(i) - v_speed.get(i - 1)));
                    }
                    //加速度集合
                    Acc=(float)((v_speed.get(i) - v_speed.get(i - 1))/0.1);
                }
                allS = allS + S;

                points2.add(new Point((float) (0.1 + 0.1 * (i-1)), allS));

                Accpoints.add(new Point((float) (0.1 + 0.1 * (i-1)), Acc));
            }

        }
        if(points2 != null) {
            for (int i = 0; i < points2.size(); i++) {
                points_x.add(points2.get(i).getX());
                points_y.add(points2.get(i).getY());
            }
            pathLine(points_x, points_y, path2,1);
            mData.clear();
        }
        path2.moveTo(width / 8-15, height * 9 / 16);
        if (RateValueY != null) {
            for (int i = 0; i < RateValueY.size(); i++) {
                float x = (width / 8 + (viewWidth) / 10 * RateValueY.get(i).getX()/timeLength);
                float y = (float) (height * 9 / 16 - (viewHeigth) / 10 * RateValueY.get(i).getY() / 0.1);
                /*if (i==0){
                    path2.moveTo(x,y);
                }else {*/
                    path2.lineTo(x, y);
               // }
            }
            canvas.drawPath(path2, paint2);
        }
        points_x.clear();
        points_y.clear();
        RateValueY.clear();

        if(Accpoints != null) {
            for (int i = 0; i < Accpoints.size(); i++) {
                points_x.add(Accpoints.get(i).getX());
                points_y.add(Accpoints.get(i).getY());
            }
            MaxAcc1=CalcMax(points_y);
            min1=CalcMin(points_y);
            Log.i("mmm", "MaxAcc1 = " + MaxAcc1 );
            pathLine(points_x, points_y, path3, 0);
            MaxAcc2=CalcMax(mData);
            min2=CalcMin(mData);
            Log.i("mmm", "MaxAcc2 = " + MaxAcc2 );
            mData.clear();

        }
        if (RateValueY != null) {
            for (int i = 0; i < RateValueY.size(); i++) {
                float x = (width / 8-15 + (viewWidth) / 10 * RateValueY.get(i).getX()/timeLength);
                float y = (float)(height * 9 / 16 -(viewHeigth)/10*7 - (viewHeigth) / 10 * RateValueY.get(i).getY()/0.5*((MaxAcc1-min1)/(MaxAcc2-min2)));
                if (i==0){
                    path3.moveTo(x,y);
                }else {
                    path3.lineTo(x, y);
                }
            }
            canvas.drawPath(path3, paint5);
        }
        points.clear();
        points2.clear();
        Accpoints.clear();
        points_x.clear();
        points_y.clear();
        RateValueY.clear();

        canvas.drawLine(width / 8 + 10, height / 32 + (height * 9 / 16 - height / 32) / 10 + (height * 9 / 16 - height / 32) / 10 * 10 - 10, (float) width / 8 + 10 + viewWidth / 5, height / 32 + (height * 9 / 16 - height / 32) / 10 + (height * 9 / 16 - height / 32) / 10 * 10 - 10, paint6);
        canvas.drawText("V", (float) width / 8 + 10 + viewWidth / 5 + 20, height / 32 + (height * 9 / 16 - height / 32) / 10 + (height * 9 / 16 - height / 32) / 10 * 10 + 5, paint4);

        canvas.drawLine((float) width / 8 + 10 + 3*viewWidth / 10 , height/32 +(height * 9 / 16 - height / 32) / 10 + (height * 9 / 16 - height/32)/10 * 10-10,(float) width / 8 + 10 + 5*viewWidth / 10,height/32 +(height*9/16-height/32)/10 + (height*9/16-height/32)/10 * 10-10,paint2);
        canvas.drawText("S",(float) width / 8 + 10 + 5*viewWidth / 10+20,height/32 +(height * 9 / 16 - height / 32) / 10 + (height * 9 / 16 - height/32)/10 * 10+5,paint4);

        canvas.drawLine((float) width / 8 + 10 + 6*viewWidth / 10 , height/32 +(height * 9 / 16 - height / 32) / 10 + (height * 9 / 16 - height/32)/10 * 10-10,(float) width / 8 + 10 + 8*viewWidth / 10,height/32 +(height*9/16-height/32)/10 + (height*9/16-height/32)/10 * 10-10,paint5);
        canvas.drawText("A",(float) width / 8 + 10 + 8*viewWidth / 10+20,height/32 +(height * 9 / 16 - height / 32) / 10 + (height * 9 / 16 - height/32)/10 * 10+5,paint4);

    }

    private List<Cubic> calculate(List<Float> x) {
        int n = x.size() - 1;
        float[] gamma = new float[n + 1];
        float[] delta = new float[n + 1];
        float[] D = new float[n + 1];
        int i;

        gamma[0] = 1.0f / 2.0f;
        for (i = 1; i < n; i++) {
            gamma[i] = 1 / (4 - gamma[i - 1]);
        }
        gamma[n] = 1 / (2 - gamma[n - 1]);

        delta[0] = 3 * (x.get(1) - x.get(0)) * gamma[0];
        for (i = 1; i < n; i++) {
            delta[i] = (3 * (x.get(i + 1) - x.get(i - 1)) - delta[i - 1])
                    * gamma[i];
        }
        delta[n] = (3 * (x.get(n) - x.get(n - 1)) - delta[n - 1]) * gamma[n];

        D[n] = delta[n];
        for (i = n - 1; i >= 0; i--) {
            D[i] = delta[i] - gamma[i] * D[i + 1];
        }

		/* now compute the coefficients of the cubics */
        List<Cubic> cubics = new LinkedList<Cubic>();
        for (i = 0; i < n; i++) {
            Cubic c = new Cubic(x.get(i), D[i], 3 * (x.get(i + 1) - x.get(i))
                    - 2 * D[i] - D[i + 1], 2 * (x.get(i) - x.get(i + 1)) + D[i]
                    + D[i + 1]);
            cubics.add(c);
        }
        return cubics;
    }

    public void pathLine(ArrayList<Float> points_x,ArrayList<Float> points_y,Path path,int flag){
        List<Cubic> calculate_x = calculate(points_x);
        List<Cubic> calculate_y = calculate(points_y);
       /* if (flag == 1) {
            path.moveTo(width / 8, height * 9 / 16);
        }else{
            path.moveTo(width / 8, height * 9 / 16 -(viewHeigth)/10*7);
        }*/

        for (int i = 0; i < calculate_x.size(); i++) {
            for (int j = 1; j <= STEPS; j++) {
                float u =  j / (float) STEPS;
                float pointx=calculate_x.get(i).eval(u);
                float pointy=calculate_y.get(i).eval(u);
                if (flag == 1) {
                    if (pointy < 0){
                        pointy=0;
                    }
                }else{

                }
                mData.add(pointy);
                RateValueY.add(new Point(pointx,pointy));
            }
        }
       // path.moveTo(RateValueY.get(0).getX(), RateValueY.get(0).getY());
        Log.i("123","allPointY = "+sb.toString());
    }

    public Float CalcMax(ArrayList<Float> m_filterData) {
        float fmax = 0;
        int size = m_filterData.size();
        for (int i = 0; i < size; i++) {
            float value = m_filterData.get(i).floatValue();
            if (value > fmax) {
                fmax = value;
                BigDecimal b = new BigDecimal(fmax);
                fmax = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
            }
        }
        return fmax;
    }

    public Float CalcMin(ArrayList<Float> m_filterData) {
        float min = 0;
        int size = m_filterData.size();
        for (int i = 0; i < size; i++) {
            float value = m_filterData.get(i).floatValue();
            if (value < min) {
                min = value;
                BigDecimal b = new BigDecimal(min);
                min = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
            }
        }
        return min;
    }

}
