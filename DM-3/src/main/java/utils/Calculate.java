package utils;

import android.util.Log;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

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

                    //绝对值小于1表示在299-301之前，超过这个范围就不算300N的位移量了
                    if (forceValueAbs < 1) {
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
                        //表示没有找到299-301之内的压力值，本次数据作废
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

}
