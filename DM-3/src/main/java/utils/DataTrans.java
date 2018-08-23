package utils;

import android.os.Environment;
import android.text.format.Time;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.Vector;

public class DataTrans {
	//门夹力
	public final static String FORCESTART = "A1";//开始命令
	public final static String FORCESTOP = "B1";//结束命令
	public final static String FORCECLEAR = "C1";//清空缓存
	public final static String FORCEPATCHDATA = "D1";//批量导入
	public final static String FORCECONTINUETEST = "E1";//持续测试
	public final static String FORCESENDRATEDECLARE = "F1";//发送压力标定系数
	public final static String FORCEREADRATEDECLARE = "G1";//读取压力标定系数
	public final static String DECLAREZERO = "H1";//标定零点

	//测速仪
	public final static String SPEEDSTART = "A2";//开始命令
	public final static String SPEEDSTOP = "B2";//结束命令
	public final static String SPEEDCLEAR = "C2";//清空缓存
	public final static String SPEEDPATCHDATA = "D2";//批量导入
	public final static String SPEEDCONTINUETEST = "E2";//持续测试
	public final static String SPEEDSENDRATEDECLARE = "F2";//发送速度标定系数
	public final static String SPEEDREADRATEDECLARE = "G2";//读取速度标定系数
	//public final static double RATE = 1.0 / 1.6718;

	public final static float RATE = (float)0.89;
	
	//public final static double RATE = 0.061;
	
	//static final float 	N_PER_UNIT = 3.2227f;
	//2015.11.10修改系数及公式
	static final float 	N_PER_UNIT = 0.183f;
	
	public static String tempFileName;
	
	private static double calibrate_real_s;
	
	private static double calibrate_real_x;
	
	private static double calibrate_real_y;
	
	private static double calibrate_real_z;
	
	private static double calibrate_test_s;

	private static double calibrate_test_x;
	
	private static double calibrate_test_y;
	
	private static double calibrate_test_z;
	
	private static boolean calibrate_value_read = false;
	
	private static boolean ken_read = false;
	
	private static String kenString;
	
	//保存读取电量值
	public static int batteryPercentSaved = 0;
	public static boolean batteryPercentGot = false;
	
	
	public static void readKen(byte[] data){
		String kenNum = "";
    	for (int i = 0; i < 8; i++){
    		int num = data[i] & 0xFF;
    		if (num < 10){
    			kenNum += "0" + num; 
    		}else {
				kenNum += num;
			}
    	}
    	int indexNoZero = 0;
    	for (int i = 0; i < 16; i++){
    		String value = kenNum.substring(i, i + 1);
    		
    		if (Integer.parseInt(value) != 0){
    			indexNoZero = i;
    			break;
    		}
    	}
    	ken_read = true;
    	kenString = kenNum.substring(indexNoZero);
	}
	
	public static void setKen(String kenNum){
		kenString = kenNum;
	}
	
	public static String getKen(){
		if (ken_read){
			return kenString;
		}else {
			return null;
		}
	}
	
	public static void readCalibrateValue(byte[] calibrateBytes){
		calibrate_test_z = TwoBytesToInt(calibrateBytes[0], calibrateBytes[1]);
		calibrate_real_z = TwoBytesToInt(calibrateBytes[2], calibrateBytes[3]);
		calibrate_test_y = TwoBytesToInt(calibrateBytes[4], calibrateBytes[5]);
		calibrate_real_y = TwoBytesToInt(calibrateBytes[6], calibrateBytes[7]);
		calibrate_test_x = TwoBytesToInt(calibrateBytes[8], calibrateBytes[9]);
		calibrate_real_x = TwoBytesToInt(calibrateBytes[10], calibrateBytes[11]);
		calibrate_test_s = TwoBytesToInt(calibrateBytes[12], calibrateBytes[13]);
		calibrate_real_s = TwoBytesToInt(calibrateBytes[14], calibrateBytes[15]);
		calibrate_value_read = true;
	}
	
	public static void copyfile(File fromFile, File toFile,Boolean rewrite){
		if(!fromFile.exists()) {
			return;
		}
		if (!fromFile.isFile()) {
			return;
		}
		if (!fromFile.canRead()) {
			return;
		}
		if (!toFile.getParentFile().exists()) {
			toFile.getParentFile().mkdirs();
		}
		if (toFile.exists() && rewrite) {
			toFile.delete();
		}
		try {

			FileInputStream fosfrom = new FileInputStream(fromFile);

			FileOutputStream fosto = new FileOutputStream(toFile);

			byte bt[] = new byte[1024];

			int c;

			while ((c = fosfrom.read(bt)) > 0) {

			fosto.write(bt, 0, c); //将内容写到新文件当中

			}

			fosfrom.close();

			fosto.close();
			
			if (toFile.exists()) {
				fromFile.delete();
			}

			} catch (Exception ex) {

			Log.e("readfile", ex.getMessage());

			}
	}
	
	public static int BytesToInt(byte v_0, byte v_1, byte v_2, byte v_3){
		int result = (v_0 & 0xFF) * 16777216 + (v_1 & 0xFF) * 65536 + (v_2 & 0xFF) * 256 + v_3 & 0xFF;
		return result;
	}
	public static int byte2int(byte[] res) {
// 一个byte数据左移24位变成0x??000000，再右移8位变成0x00??0000

		/*int targets = (res[0] & 0xff) | ((res[1] << 8) & 0xff00) // | 表示安位或
				| ((res[2] << 24) >>> 8) | (res[3] << 24);
		return targets;*/

		int value= 0;
		for (int i = 0; i < 4; i++) {
			int shift= (4 - 1 - i) * 8;
			value +=(res[i] & 0x000000FF) << shift;
		}
		return value;
	}
	public static int ThreeBytesToInt( byte v_1, byte v_2, byte v_3){
		int result =(v_1 & 0xFF) * 65536 + (v_2 & 0xFF) * 256 + v_3 & 0xFF;
		return result;
	}

	public static int ByteToInt( byte v_3){
		int result = v_3 & 0xFF;
		return result;
	}
	/**
	 * 从一个byte[]数组中截取一部分
	 * @param src
	 * @param begin
	 * @param count
	 * @return
	 */
	public static byte[] subBytes(byte[] src, int begin, int count) {
		byte[] bs = new byte[count];
		System.arraycopy(src, begin, bs, 0, count);
		return bs;
	}
	
	
	public static byte[] StringToBytes(String string){
		byte[] result = new byte[string.length()];
		for (int i = 0; i < string.length(); i++){
			char tmp = string.charAt(i);
			result[i] = (byte)tmp;
		}
		return result;
	}
	
	public static String BytesToString(byte[] bytes){
		String result = new String(bytes);
		return result;
	}
	
	public static String BufferToString(byte[] buffer, int len){
		byte[] com = new byte[len];
		for (int i = 0; i < len; i++){
			com[i] = buffer[i];
		}
		String command = DataTrans.BytesToString(com);
		return command;
	}
	
	public static byte[] IntToDoubleBytes(int value){
		byte[] result = new byte[2];
		result[0] = (byte) (value >> 8);
		result[1] = (byte) value;
		return result;
	}

	public static byte[] intToBytes(int n){
		byte[] b = new byte[4];

		for(int i = 0;i < 4;i++)
		{
			b[i]=(byte)(n>>(24-i*8));

		}
		return b;
	}

	public static int byteToInt(byte[] b) {

		int mask=0xff;
		int temp=0;
		int n=0;
		for(int i=0;i<b.length;i++){
			n<<=8;
			temp=b[i]&mask;
			n|=temp;
		}
		return n;
	}

	public static int DoubleBytesToInt(byte[] bytes){
		int tmp1 = bytes[0] & 0xFF;
		int tmp2 = bytes[1] & 0xFF;
		int result = tmp1 * 256 + tmp2 ;
		return result;
	}
	
	public static int TwoBytesToInt(byte byte_h, byte byte_l){
		int composite_value;
		if ((byte_h & 0x80) == 0){//判断首位是否为1，为1则为负数
			//符号位不为1，正数
			composite_value = 0;
		}else {
			//符号位为1，负数
			composite_value = -65536;
		}
		int tmp1 = byte_h & 0xFF;
		int tmp2 = byte_l & 0xFF;
		int result = tmp1 * 256 + tmp2;
		return result;
	}
	
	public static int TwoHexToInt(byte byte_h, byte byte_l){
		int tmp1 = byte_h & 0xFF;
		int tmp2 = byte_l & 0xFF;
		tmp1 = Integer.parseInt(String.valueOf(tmp1), 16);
		tmp2 = Integer.parseInt(String.valueOf(tmp2), 16);
		int result = tmp1 * 256 + tmp2 ;
		return result;
	}
	

	
	public static String bytesToHexString(byte[] bytes) {
        String result = "";
        for (int i = 0; i < bytes.length; i++) {
            String hexString = Integer.toHexString(bytes[i] & 0xFF);
            if (hexString.length() == 1) {
                hexString = '0' + hexString;
            }
            result += hexString.toUpperCase();
        }
        return result;
    }

	/**
	 * 函数名称：byteToHex</br> 功能描述：byte转16进制
	 *
	 */
	public static String byteToHex(byte b) {
		String hex = Integer.toHexString(b & 0xFF);
		if (hex.length() == 1) {
			hex = '0' + hex;

		}
		return hex.toUpperCase(Locale.getDefault());

	}



	/**
	 * 将字节数组转换成16进制字符串
	 *
	 * @param array
	 *            需要转换的字符串
	 * @param toPrint
	 *            是否为了打印输出，如果为true则会每4自己添加一个空格
	 * @return 转换完成的字符串
	 */
	public static String byteArrayToHexString(byte[] array, boolean toPrint) {
		if (array == null) {
			return "null";
		}
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < array.length; i++) {
			sb.append(byteToHex(array[i]));
			if (toPrint && (i + 1) % 4 == 0) {
				sb.append(" ");
			}
		}
		return sb.toString();
	}

	/**
	 * 将字节数组转换成16进制字符串
	 *
	 * @param array
	 *            需要转换的字符串(字节间没有分隔符)
	 * @return 转换完成的字符串
	 */
	public static String byteArrayToHexString(byte[] array) {
		return byteArrayToHexString(array, false);
	}

	public static byte[] getTime(){
		byte[] byteTime = new byte[8];
		Time t=new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料。
		t.setToNow(); // 取得系统时间。
		int year = t.year - 2000;
		//Time.MONTH及Calendar.MONTH 默认的月份为  0-11  
		//所以使用的时候要自己加1. 
		int month = t.month + 1;
		int date = t.monthDay;
		int hour = t.hour; // 0-23
		int minute = t.minute;
		int second = t.second;
		byteTime[0] = (byte) year;
		byteTime[1] = (byte) month;
		byteTime[2] = (byte) date;
		byteTime[3] = (byte) hour;
		byteTime[4] = (byte) minute;
		byteTime[5] = (byte) second;
		byteTime[6] = (byte) 0;
		byteTime[7] = (byte) 0;
		return byteTime;
	}

	public static byte[] sendRate(int value,String string){
		//F1+#+’ST’(2Byte)+压力标定系数(2Byte)+’END’(3Byte)+*,
		byte[] byteRate=new byte[11];
		if (string.equals("force")) {
			String s = "F1";
			byte[] bt01 = StringToBytes(s);
			for (int i = 0; i < bt01.length; i++) {
				byteRate[0] = bt01[0];
				byteRate[1] = bt01[1];
			}
		}else if(string.equals("speed")){
			String s = "F2";
			byte[] bt01 = StringToBytes(s);
			for (int i = 0; i < bt01.length; i++) {
				byteRate[0] = bt01[0];
				byteRate[1] = bt01[1];
			}
		}
		byteRate[2]='#';
	 	String s="ST";
		byte[] bt34=StringToBytes(s);
		for (int i=0;i<bt34.length;i++) {
			byteRate[3] = bt34[0];
			byteRate[4]=bt34[1];
		}
		byte[] bt56=IntToDoubleBytes(value);
			byteRate[5] = bt56[0];
			byteRate[6] = bt56[1];


		byte[] bt789=StringToBytes("END");
		for(int i=0;i<bt789.length;i++){
			byteRate[7]=bt789[0];
			byteRate[8]=bt789[1];
			byteRate[9]=bt789[2];
		}
		byteRate[10]='*';
		return byteRate;
	}

	public static byte[] sendbytes(){
		//#+数据长度(4Byte)+’ST’(2Byte)+文档编号(2Byte)+数据内容.....+’END’(3Byte)+*
		byte[] bytes=new byte[53];
		bytes[0]='#';
		byte[] bt2345=DataTrans.intToBytes(40);
		bytes[1]=bt2345[0];
		bytes[2]=bt2345[1];
		bytes[3]=bt2345[2];
		bytes[4]=bt2345[3];
		bytes[5]='S';
		bytes[6]='T';
		byte[] bt78=DataTrans.IntToDoubleBytes(23);
		bytes[7]=bt78[0];
		bytes[8]=bt78[1];
		for(int i=0;i<40;i+=2){
			byte[] bti=DataTrans.IntToDoubleBytes(new Random().nextInt(999));
			for (int j=0;j<2;j++){
				bytes[i+9]=bti[0];
				bytes[i+10]=bti[1];
			}
		}
		bytes[49]='E';
		bytes[50]='N';
		bytes[51]='D';
		bytes[52]='*';

		return bytes;
	}



	public static ArrayList<Byte> copyBytes(byte[] buffer, int length, ArrayList<Byte> data){
		for (int i = 0; i < length; i++){
			data.add(buffer[i]);
		}
		return data;
	}

	public static byte[] ArrayToBytes(ArrayList<Byte> arrayList, byte[] data){
		int len = data.length;
		for (int i = 0; i < len; i++) {
			data[i] = arrayList.get(i);
		}
		return data;
	}
}
