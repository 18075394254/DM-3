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
	
	static float AdTodB(int ad_value)
	{
		float dB;
		// 2015.11.10 更改计算相关系数及公式
		dB = (float) (14.46 + 21.3 * Math.log10(ad_value * N_PER_UNIT + 1));
		if (dB > 84)
		{
			dB = 3 * dB - 168;
		}else if (dB<30.0)
		{
			dB = 30.0f;
			return dB;
		}else if (dB>100.0) {
			dB = 100.0f;
			return dB;
		}
		return dB;
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
	public static byte[] getKenID(String kenNumber){
		byte[] ken = new byte[4];
		int[] nums = new int[4];
		for (int i = 0; i < 4; i++){
			String num = kenNumber.substring(i * 2, i * 2 + 2);
			nums[i] = Integer.parseInt(num);
			ken[i] = (byte) nums[i];
		}
		return ken;
	}
	
	public static String getKenNumber(byte[] kenNumber){
		StringBuffer sb = new StringBuffer();
		if (kenNumber.length == 4){
			int[] nums = new int[4];
			for (int i = 0; i < 4; i++){
				nums[i] = kenNumber[i] & 0xFF;
				sb.append(String.valueOf(nums[i]));
			}
		}
		String ken = sb.toString();
		return ken;
	}
	
	public static int checkFileEnd(byte[] tail){
		int result = 0;
		int len = tail.length;
		if (len != 72){
			result = -1;//文件尾长度不对
			return result;
		}
		for (int i = 0; i < 8; i++){
			if (tail[i] != 16){
				result = 2;//文件尾的前8位数据不对
				return result;
			}
		}
		for (int i = 8; i < 40; i++){
			if (tail[i] != 0){
				result = 3;//文件尾的9-40位数据不对
				return result;
			}
		}
		for (int i = 48; i < 56; i++) {
			if (tail[i] != 0){
				result = 5;//文件尾的49-56位数据不对
				return result;
			}
		}
		for (int i = 64; i < 72; i++) {
			if (tail[i] != 0){
				result = 7;//文件尾的64-72位数据不对
				return result;
			}
		}
		result = 1;//文件尾解析正确
		return result;
	}
	
	public static void getCalibrateBytes(int[] calibrateInt, byte[] calibrateBytes){
		calibrateBytes[0] = (byte) (calibrateInt[0] >> 8);
		calibrateBytes[1] = (byte) calibrateInt[0];
		calibrateBytes[2] = (byte) (calibrateInt[1] >> 8);
		calibrateBytes[3] = (byte) calibrateInt[1];
		calibrateBytes[4] = (byte) (calibrateInt[2] >> 8);
		calibrateBytes[5] = (byte) calibrateInt[2];
		calibrateBytes[6] = (byte) (calibrateInt[3] >> 8);
		calibrateBytes[7] = (byte) calibrateInt[3];
		calibrateBytes[8] = (byte) (calibrateInt[4] >> 8);
		calibrateBytes[9] = (byte) calibrateInt[4];
		calibrateBytes[10] = (byte) (calibrateInt[5] >> 8);
		calibrateBytes[11] = (byte) calibrateInt[5];
		calibrateBytes[12] = (byte) (calibrateInt[6] >> 8);
		calibrateBytes[13] = (byte) calibrateInt[6];
		calibrateBytes[14] = (byte) (calibrateInt[7] >> 8);
		calibrateBytes[15] = (byte) calibrateInt[7];
	}
	
	public static void generalFilter(int sampleF, int filterF, Vector<Float> vector){
		
	}
	
	public static int checkBatchData(ArrayList<Byte> data, String rootPath, int fileNum){
		int returnValue = 0;
		double rate_x;
		double rate_y;
		double rate_z;
		double rate_s;
		if(calibrate_real_z == 0 || calibrate_test_z == 0){
			rate_z = 1;
		}else{
		  rate_z = (calibrate_real_z * 1.0) / calibrate_test_z;
	    }
		if(calibrate_real_x == 0 || calibrate_test_x == 0){
			rate_x = 1;
		}else{
		  rate_x = (calibrate_real_x * 1.0) / calibrate_test_x;
	    }
		if(calibrate_real_y == 0 || calibrate_test_y == 0){
			rate_y = 1;
		}else{
		  rate_y = (calibrate_real_y * 1.0) / calibrate_test_y;
	    }
		if(calibrate_real_s == 0 || calibrate_test_s == 0){
			rate_s = 1;
		}else{
		  rate_s = (calibrate_real_s * 1.0) / calibrate_test_s;
	    }
		int len = data.size();
		 ArrayList<Integer> receive = new ArrayList<Integer>();
		 receive.clear();
		 for (int i = 0; i < len; i += 2){
			 int lowbyte,highbyte;
			 highbyte = data.get(i) & 0xFF;
			 lowbyte = data.get(i + 1) & 0xFF;
			 int value = lowbyte + highbyte * 256;
			 receive.add(Integer.valueOf(value));
		 }
		 receive.trimToSize();
		 int receiveLen = receive.size();
		 
		 int[] receive_data=new int[receiveLen];
		 for(int i = 0; i < receiveLen; i++){
	        	receive_data[i] = receive.get(i).intValue();
	        }
		 receive.clear();
		 int[] begin_bit = new int[fileNum];
         int[] end_bit = new int[fileNum];
         int j=0;
		  for(int i = 0; i < receiveLen; i++){
			  if(receive_data[i] == 4660 && receive_data[i+1] <= fileNum){
		          begin_bit[j] = i;
		          returnValue = i;
		        }
			  if(receive_data[i] == 4112 && receive_data[i+1]==4112 && 
		    		   receive_data[i+2]==4112 && receive_data[i+3]==4112){
		           end_bit[j] = i;
			       j = j + 1;
			       if(j == fileNum){
			    	   break;
			       }
		        }
		  }
		  
		  int validFileNum = j;
		  ArrayList<Byte> fileData = new ArrayList<Byte>();
		  for (int k = 0; k < j; k++){
			  int start = begin_bit[k] * 2;
			  int end = end_bit[k] * 2;
			  fileData.clear();
			  for (int l = start; l < end + 72; l++){
				  fileData.add(data.get(l));
			  }
			//前24位为文件头
				//解析Lift Id
				String saveliftid = Integer.toHexString(TwoHexToInt(fileData.get(12), fileData.get(13)));
			
				//String saveliftid = Integer.toHexString(TwoBytesToInt(Integer.parseInt(String.valueOf((data.get(12) & 0xFF)), 16), Integer.parseInt(String.valueOf((data.get(13) & 0xFF)), 16)));
				int hl=saveliftid.length();
		        switch(hl)
		        {
		            case 1:
		            	saveliftid="000"+saveliftid;
		                 break;
		            case 2:
		                  saveliftid="00"+saveliftid;
		                  break;
		            case 3:
		                 saveliftid="0"+saveliftid;
		                 break;
		            case 4:
		            	break;
		            default:
		            	//saveliftid = "0000";
		            	break;
		         }
		        String saveliftid1 = Integer.toHexString(TwoHexToInt(fileData.get(14), fileData.get(15)));
		             hl=saveliftid1.length();
		             switch(hl)
		             {
		               case 1:
		                  saveliftid1="000"+saveliftid1;
		                  break;
		               case 2:
		                  saveliftid1="00"+saveliftid1;
		                  break;
		               case 3:
		                  saveliftid1="0"+saveliftid1;
		                  break;
		               case 4:
		                	  break;
		               default:
		            	   //saveliftid1 = "0000";
		            	   break;
		             } 
				//解析TestType
				String testtype = String.valueOf(0);
				//******** testtype始终为 comfort=0
				//解析LiftSpeed
				int liftspeedNum = TwoBytesToInt(fileData.get(16), fileData.get(17));
				String liftspeed = String.format("%-6d", liftspeedNum);
				//解析时间，年月日时分
				int time_of_year,time_of_month,time_of_date,time_of_hour,time_of_min;
				time_of_year = fileData.get(18);
				time_of_month = fileData.get(19);
				time_of_date = fileData.get(20);
				time_of_hour = fileData.get(21);
				time_of_min = fileData.get(22);
				time_of_year=2000+time_of_year;
				String namemonth;
				String namedate;
				String namehour;
				String namemin;
				if (time_of_month < 10){
					namemonth = "0" + String.valueOf(time_of_month);
				}else{
					namemonth = String.valueOf(time_of_month);
				}
				if (time_of_date < 10){
					namedate = "0" + String.valueOf(time_of_date);
				}else{
					namedate = String.valueOf(time_of_date);
				}
				if (time_of_hour < 10){
					namehour = "0" + String.valueOf(time_of_hour);
				}else{
					namehour=String.valueOf(time_of_hour);
				}
				if (time_of_min < 10){
					namemin = "0" + String.valueOf(time_of_min);
				}else{
					namemin = String.valueOf(time_of_min);
				}
				tempFileName = String.valueOf(time_of_year) + namemonth + namedate + saveliftid + saveliftid1 + "-" + String.valueOf(k+1) + ".zks";
				//*************************************
				String we_str;
				StringBuilder sb = new StringBuilder();
				
				we_str = String.valueOf(time_of_year) + "- " + namemonth + "-" + namedate + " ";
					sb.append(we_str);
					we_str = saveliftid + "  " + saveliftid1 + "  ";
					sb.append(we_str);
					we_str = testtype + "     " + liftspeed;
					sb.append(we_str);
					we_str = String.format("%37d", 0);
					sb.append(we_str);
					
					we_str = "     " + "0     " + "0     " + "0     ";
					sb.append(we_str);
					
				//解析数据 Z Y X S
				int zero_z = TwoBytesToInt(fileData.get(6), fileData.get(7));
				int zero_y = TwoBytesToInt(fileData.get(8), fileData.get(9));
				int zero_x = TwoBytesToInt(fileData.get(10), fileData.get(11));
				//测试数据，ZH ZL YH YL XH XL SH SL
				int testTimes = (end - start - 24) / 8;
				int ad_z = 0;
				int ad_y = 0;
				int ad_x = 0;
				int ad_s = 0;
				int value_z = 0;
				int value_y = 0;
				int value_x = 0;
				int value_s = 0;
				start = 24;
				for (int i = 0; i < testTimes; i++ ){
					ad_z = TwoBytesToInt(fileData.get(start + i * 8), fileData.get(start + i * 8 + 1));
					ad_y = TwoBytesToInt(fileData.get(start + i * 8 + 2), fileData.get(start + i * 8 + 3));
					ad_x = TwoBytesToInt(fileData.get(start + i * 8 + 4), fileData.get(start + i * 8 + 5));
					ad_s = TwoBytesToInt(fileData.get(start + i * 8 + 6), fileData.get(start + i * 8 + 7));
					value_z = (int) ((ad_z - zero_z) * RATE * rate_z);
					value_y = (int) ((ad_y - zero_y) * RATE * rate_y);
					value_x = (int) ((ad_x - zero_x) * RATE * rate_x);
					value_s = (int) (AdTodB(ad_s) * 10 * rate_s);
					we_str = String.format("%-6d", value_x);
					sb.append(we_str);
					we_str = String.format("%-6d", value_y);
					sb.append(we_str);
					we_str = String.format("%-6d", value_z);
					sb.append(we_str);
					we_str = String.format("%-6d", value_s);
					sb.append(we_str);
				}
				//最后72位为文件尾
				we_str = "44444 44444 44444 44444 ";
				sb.append(we_str);
				
				we_str = String.format("%-6d", 0);
				sb.append(we_str);

				we_str = String.format("%-6d", 0);
				sb.append(we_str);
					
				we_str = String.format("%-6d", 0);
				sb.append(we_str);
					
				we_str = String.format("%-6d", 0);
				sb.append(we_str);
					
				we_str = "33333 33333 33333 33333 ";
				sb.append(we_str);
				int customSpeed;
				customSpeed = 0;
				we_str = String.format("%-6d", customSpeed);
				sb.append(we_str);
				File file=new File(rootPath, tempFileName);
				String fileName = file.getAbsolutePath().toString();
				FileOutputStream stream;
				try {
					stream = new FileOutputStream(file);
					stream.write(sb.toString().getBytes());
				    stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}	
			  /*if(receive_data[start] == 4660){
				  liftid=receive_data[start+6];
	                //灏嗙數姊疘D鍗佸叚杩涘埗杞崲涓哄崄杩涘埗
	               saveliftid=Integer.toHexString(liftid);
	                int hl=saveliftid.length();
	                switch(hl)
	                {
	                    case 1:
	                    	saveliftid="000"+saveliftid;
	                         break;
	                    case 2:
	                          saveliftid="00"+saveliftid;
	                          break;
	                    case 3:
	                         saveliftid="0"+saveliftid;
	                         break;
	                    case 4:
	                    	break;
	                    default:
	                    	//saveliftid = "0000";
	                    	break;
	                 }
	                     liftid=receive_data[start+7];
	                     saveliftid1=Integer.toHexString(liftid);
	                     hl=saveliftid1.length();
	                     switch(hl)
	                     {
	                       case 1:
	                          saveliftid1="000"+saveliftid1;
	                          break;
	                       case 2:
	                          saveliftid1="00"+saveliftid1;
	                          break;
	                       case 3:
	                          saveliftid1="0"+saveliftid1;
	                          break;
	                       case 4:
	                        	  break;
	                       default:
	                    	   //saveliftid1 = "0000";
	                    	   break;
	                     } 
	                     liftspeed=receive_data[start+8];
	                     savespeed=String.format("%-6d", liftspeed);
	                     time_of_year=receive_data[start+9]/256;
	                     time_of_month=receive_data[start+9]-time_of_year*256;
	                     time_of_year=2000+time_of_year;
	                     if (time_of_month<10)
	                        namemonth="0"+String.valueOf(time_of_month);
	                     else
	                        namemonth=String.valueOf(time_of_month);

	                     time_of_date=receive_data[start+10]/256;
	                     time_of_hour=receive_data[start+10]-time_of_date*256;
	                     if (time_of_date<10)
	                        namedate="0"+String.valueOf(time_of_date);
	                     else
	                        namedate=String.valueOf(time_of_date);
	                     if (time_of_hour<10)
	                        namehour="0"+String.valueOf(time_of_hour);
	                     else
	                        namehour=String.valueOf(time_of_hour);

	                     time_of_min=receive_data[start+11]/256;
	                     if (time_of_min<10)
	                        namemin="0"+String.valueOf(time_of_min);
	                     else
	                        namemin=String.valueOf(time_of_min);
	                    
	                     renamefilename=String.valueOf(time_of_year)+namemonth+namedate+saveliftid+saveliftid1;
	                     
	                     String name;
	                    
	                     testtype=receive_data[start+2];
	                     savetesttype=String.valueOf(testtype);
	                     name=renamefilename+"-"+String.valueOf(k+1)+".zks";
	                     zeroz=receive_data[start+3];
	                     zeroy=receive_data[start+4];
	                     zerox=receive_data[start+5];
	                     name = rootPath+"/"+name;
	                     File file = new File(name);
	                     if(Environment.getExternalStorageState().
		             				equals(Environment.MEDIA_MOUNTED)){
	                    	 int we;
		             			double we_f;
		             			String we_str;
		             			StringBuilder sb = new StringBuilder();
		             			//淇濆瓨鍩烘湰淇℃伅
		             			we_str=String.valueOf(time_of_year)+"- "+namemonth+"-"+namedate+" ";
		             			sb.append(we_str);
		             			we_str=saveliftid+"  "+saveliftid1+"  ";
		             			sb.append(we_str);
		             			we_str=savetesttype+"     "+savespeed;
		             			sb.append(we_str);
		             			we_str = String.format("%37d", 0);
		             			sb.append(we_str);
		             			
		             			we_str="     "+"0     "+"0     "+"0     ";
		             			sb.append(we_str);
		             			double biaoding_x = 1;
		             			double biaoding_y = 1;
		             			double biaoding_z = 1;
		             			double biaoding_s = 1;
		             			for(int n=begin_bit[k]+24;n<end_bit[k];n+=4)
		             			{
		             				
									we_f=(receive_data[n+2]-zerox)/1.86*biaoding_x;
		             				we=(int)(we_f); 
		             				we_str=String.format("%-6d", we);
		             				sb.append(we_str);
		             				we_f=(receive_data[n+1]-zeroy)/1.86*biaoding_y;
		             				we=(int)(we_f); 
		             				we_str=String.format("%-6d", we);
		             				sb.append(we_str);
		             				we_f=(receive_data[n]-zeroz)/0.552*biaoding_z;
		             				we=-1*(int)(we_f); 
		             				we_str=String.format("%-6d", we);
		             				sb.append(we_str);
		             				//鍣０鍊奸渶瑕佽浆db鍊间繚瀛�
		             				we_f=AdTodB(receive_data[n+3])*10*biaoding_s;
		             				we=(int)(we_f);	             				
		             				we_str=String.format("%-6d", we);
		             				sb.append(we_str);
		             			}
		             			we_str = "44444 44444 44444 44444 ";
		             			sb.append(we_str);
		             			int newbiaoz,newbiaoy,newbiaox,newbiaos;
		             			newbiaoz = receive_data[end+8];
		             			newbiaoy = receive_data[end+9];
		             			newbiaox =receive_data[end+10];
		             			newbiaos = receive_data[end+11];
		             			we_str = String.format("%-6d", newbiaoz);
		             			sb.append(we_str);

		             			we_str = String.format("%-6d", newbiaox);
		             			sb.append(we_str);
		             			
		             			we_str = String.format("%-6d", newbiaoy);
		             			sb.append(we_str);
		             			
		             			we_str = String.format("%-6d", newbiaos);
		             			sb.append(we_str);
		             			
		             			we_str = "33333 33333 33333 33333 ";
		             			sb.append(we_str);
		             			int customSpeed;
		             			customSpeed = receive_data[end+16];
		             			we_str = String.format("%-6d", customSpeed);
		             			sb.append(we_str);
		             			try {
		             				FileOutputStream stream=new FileOutputStream(file);	
		             				stream.write(sb.toString().getBytes());
		             			    stream.close();	
								} catch (Exception e) {
									e.printStackTrace();
								}
	                     }
			  }*/
		  }
		return returnValue;
	}
	
	public static void BytesToFile(ArrayList<Byte> data){
		int len = data.size();
		byte[] bytes = new byte[len];
		for (int i = 0; i < len; i++){
			bytes[i] = data.get(i);
		}
		BufferedOutputStream stream = null;
		File file=new File(Environment.getExternalStorageDirectory(),"currentFile.zks");
	    try {
			FileOutputStream fstream = new FileOutputStream(file);
            stream = new BufferedOutputStream(fstream);
            stream.write(bytes);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static int GetBatteryPercent(int adValue){
		float full = 3900;
		float half = 3620;
		int percent = 0;
		//if (adValue >= half){
			percent = (int) ((adValue - half) / (full - half) * 5 + 5) * 10;
		//}else {
			//percent = 0;
		//}
			if (percent > 100){
				percent = 100;
			}
			batteryPercentSaved = percent;
			batteryPercentGot = true;
		return percent;
	}
	
	public static boolean CheckDataSame( Vector<Float> data){
		int sameDataCount = 0;
		int length = data.size();
		for (int i = 1; i < length; i++){
			int previousValue = Math.round(data.get(i - 1));
			int currentValue = Math.round(data.get(i));
			if (previousValue == currentValue){
				sameDataCount++; 
			}else {
				sameDataCount = 0;
			}
			if (sameDataCount >= 3){
				return true;
			}
		}
		return false;
		
	}
}
