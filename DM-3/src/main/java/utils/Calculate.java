package utils;

import android.util.Log;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/12/16 0016.
 */
public class Calculate {
    private float S=0;
    private float Acc=0;
    private float allS=0;
    private float MaxAcc=0;
    private float MinAcc=0;
    private float fmax=0;
    private float speedMax=0;
    private float fKin=0;
    private float energy=0;
    private float Vave=0;
    private HashMap map=new HashMap();
    public Calculate() {

    }
    //压力数据滤波,滤波频率30hz
    //X=250,Y=30
    public ArrayList<Float> lvbo30(ArrayList<Float> m_cutData, ArrayList<Float> m_filterData, int X, int Y) {
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

//计算平均速度
    public Float CalcVave(ArrayList<Float> distance,int type){
        int FirstIndex=0;
        int MaxIndex=0;
        int LastIndex=0;
        float max=0;
        if (distance != null) {
            //求最大点坐标位置和起始点坐标位置
            for (int i = 0; i < distance.size(); i++) {
                Log.i("hhh", "distance.get(i)  =" + distance.get(i) + " " + i);
                if (distance.get(i) > max) {
                    max = distance.get(i);
                    MaxIndex = i;
                }
                if (type == 0) {
                    if (i < distance.size() - 1) {
                        if (distance.get(i) - 0.025 == 0) {
                            FirstIndex = i;
                        } else if (distance.get(i) - 0.025 < 0 && distance.get(i + 1) - 0.025 > 0) {
                            if (Math.abs(distance.get(i) - 0.025) > Math.abs(distance.get(i + 1) - 0.025)) {
                                FirstIndex = i + 1;
                            } else {
                                FirstIndex = i;
                            }
                        }
                    } else {
                        LastIndex = MaxIndex;
                    }
                }else{
                    if (i < distance.size() - 1) {
                        if (distance.get(i) - 0.05 == 0) {
                            FirstIndex = i;
                        } else if (distance.get(i) - 0.05 < 0 && distance.get(i + 1) - 0.05 > 0) {
                            if (Math.abs(distance.get(i) - 0.05) > Math.abs(distance.get(i + 1) - 0.05)) {
                                FirstIndex = i + 1;
                            } else {
                                FirstIndex = i;
                            }
                        }
                    } else {
                        LastIndex = MaxIndex;
                    }
                }
            }
            //求终点的坐标位置
            for (int i=FirstIndex;i<MaxIndex+1;i++) {
                if(type == 0){
                if (i < MaxIndex) {
                    if (distance.get(MaxIndex) - distance.get(i) == 0.025) {
                        LastIndex = i;
                    } else if (distance.get(MaxIndex) - distance.get(i) > 0.025 && distance.get(MaxIndex) - distance.get(i + 1) < 0.025) {
                        if (Math.abs(distance.get(i) - distance.get(MaxIndex) + 0.025) > Math.abs(distance.get(i + 1) - distance.get(MaxIndex) + 0.025)) {
                            LastIndex = i + 1;
                        } else {
                            LastIndex = i;
                        }
                    }
                }
                }else{
                    if (i < MaxIndex) {
                        if (distance.get(MaxIndex) - distance.get(i) == 0.05) {
                            LastIndex = i;
                        } else if (distance.get(MaxIndex) - distance.get(i) > 0.05 && distance.get(MaxIndex) - distance.get(i + 1) < 0.05) {
                            if (Math.abs(distance.get(i) - distance.get(MaxIndex) + 0.05) > Math.abs(distance.get(i + 1) - distance.get(MaxIndex) + 0.05)) {
                                LastIndex = i + 1;
                            } else {
                                LastIndex = i;
                            }
                        }
                    }
                }
            }


            Vave= (float) ((distance.get(LastIndex) - distance.get(FirstIndex))/((LastIndex - FirstIndex)*0.1));
            Log.i("hhh","distance.get(LastIndex) = "+distance.get(LastIndex) + "  "+LastIndex);
            Log.i("hhh","distance.get(FirstIndex) = "+distance.get(FirstIndex)+ "  "+FirstIndex);
            if (Vave>0) {
                BigDecimal b = new BigDecimal(Vave);
                Vave = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
            }else {
                Vave=0;
            }
        }
        return Vave;
    }
//计算距离
    public ArrayList<Float> CalcDistance(ArrayList<Float> v_speed){
         ArrayList<Float> Distance=new ArrayList<>();
            S=0;
            allS=0;
        if(v_speed != null){
            for(int i=0;i< v_speed.size();i++) {

                if (i == 0) {
                    S = (float) (0.5 * 0.1 * v_speed.get(i));
                } else {
                    if (v_speed.get(i) - v_speed.get(i - 1) == 0) {
                        S = (float) 0.1 * v_speed.get(i);
                    } else {
                        S = (float) (0.1 * Math.abs(v_speed.get(i) * v_speed.get(i) - v_speed.get(i - 1) * v_speed.get(i - 1))) / (2 * Math.abs(v_speed.get(i) - v_speed.get(i - 1)));
                    }
                }
                allS = allS + S;
                Log.i("ggg", "allS11111 =" + allS);
                Distance.add(allS);
            }

        }
        return Distance;
    }
//计算加速度
    public ArrayList<Float> CalcAcclerate(ArrayList<Float> v_speed){
         ArrayList<Float> Acclerate=new ArrayList<>();
        if(v_speed != null){
            for(int i=0;i< v_speed.size();i++) {

                if (i == 0) {
                    Acc=(float)(v_speed.get(i)/0.1);
                } else {
                    //加速度集合
                    Acc=(float)((v_speed.get(i) - v_speed.get(i - 1))/0.1);
                }

                Acclerate.add(Acc);
            }

        }

        return Acclerate;
    }
//计算最大值
    public HashMap CalcForceMax(ArrayList<Float> Speed) {
        ArrayList<Float> Acclerate=new ArrayList<>();
        MaxAcc=0;
        MinAcc=0;
        speedMax=0;
        map.clear();
        Acclerate.clear();

            if (Speed != null){
                for (int i=0;i<Speed.size();i++){
                    float value = Speed.get(i).floatValue();
                    if (value > speedMax){
                        speedMax=value;
                        BigDecimal b = new BigDecimal(speedMax);
                        speedMax = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
                    }
                }
                map.put("speedMax",speedMax);
                speedMax=0;

                Acclerate=CalcAcclerate(Speed);

            }
            if (Acclerate != null) {
                for (int i = 0; i < Acclerate.size(); i++) {
                    float value = Acclerate.get(i).floatValue();

                    if (value > MaxAcc) {
                        MaxAcc = value;
                        BigDecimal b = new BigDecimal(MaxAcc);
                        MaxAcc = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
                    }

                }
            }
            map.put("MaxAcc",MaxAcc);

            MaxAcc=0;

            for (int i=0;i<Acclerate.size();i++){
                float value = Acclerate.get(i).floatValue();

                if (value < MinAcc){
                    MinAcc=value;
                    BigDecimal b = new BigDecimal(MinAcc);
                    MinAcc = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
                }

            }
            map.put("MinAcc",MinAcc);
            MinAcc=0;



            return map;
    }
//计算测试时间
    public Float testTime(ArrayList<Float> distance){
        float time=0;
        int FirstIndex=0;
        int LastIndex=0;
        float max=0;
        if(distance != null) {
            for (int i = 0; i < distance.size(); i++) {
                if (distance.get(i) > max){
                    max=distance.get(i);
                }
            }
            for (int i = 0; i < distance.size(); i++) {
                if (distance.get(i) > 0){
                    FirstIndex=i;
                    break;
                }
            }
            for (int i=0;i<distance.size();i++){
                if(distance.get(i) == max){
                    LastIndex=i;
                    break;
                }
            }
            time= (float) ((LastIndex - FirstIndex)*0.1);
        }
        return time;
    }


}
