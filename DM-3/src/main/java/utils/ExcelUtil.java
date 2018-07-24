package utils;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

import jxl.Workbook;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import model.DataBean;

public class ExcelUtil {
	//内存地址
	public static String root = Environment.getExternalStorageDirectory()
			.getPath();

	public static void writeExcel(Context context, List<DataBean> exportOrder,
			File file) throws Exception {
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) && getAvailableStorage() > 1000000) {
			Toast.makeText(context, "SD卡不可用", Toast.LENGTH_LONG).show();
			return;
		}
		String[] title = {"文件名", "压力值", "位移值", "是否达标", "操作员", "地点", "设备"};

		// 创建Excel工作表
		WritableWorkbook wwb;
		OutputStream os = new FileOutputStream(file);
		wwb = Workbook.createWorkbook(os);

		// 添加第一个工作表并设置第一个Sheet的名字
		WritableSheet sheet = wwb.createSheet("Data", 0);
		try {
			sheet.setColumnView(0, 30);
			sheet.setColumnView(1, 15);
			sheet.setColumnView(2, 15);
			sheet.setColumnView(3, 15);
			sheet.setColumnView(4, 15);
			sheet.setColumnView(5, 15);
			sheet.setColumnView(6, 15);



		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Label label;
		for (int i = 0; i < title.length; i++) {
			// Label(x,y,z) 代表单元格的第x+1列，第y+1行, 内容z
			// 在Label对象的子对象中指明单元格的位置和内容
			label = new Label(i, 0, title[i], getHeader());
			// 将定义好的单元格添加到工作表中
			sheet.addCell(label);
		}
		if (exportOrder.size() > 0) {
			for (int i = 0; i < exportOrder.size(); i++) {
				DataBean order = exportOrder.get(i);

				Label fileName1 = new Label(0, i + 1, order.getFileName());
				Label forceValue = new Label(1, i + 1, order.getForceValue()+"");
				Label disValue = new Label(2, i + 1, order.getDisValue()+"");
				Label isTrue = new Label(3, i + 1, order.getIsTrue()+"");
				Label operator = new Label(4, i + 1, order.getOperator());
				Label location = new Label(5, i + 1, order.getLocation());
				Label liftid = new Label(6, i + 1, order.getLiftid());

				sheet.addCell(fileName1);
				sheet.addCell(forceValue);
				sheet.addCell(disValue);
				sheet.addCell(isTrue);
				sheet.addCell(operator);
				sheet.addCell(location);
				sheet.addCell(liftid);


			}
			Toast.makeText(context, "写入Excel成功", Toast.LENGTH_LONG).show();
			// 写入数据
			wwb.write();
			// 关闭文件
			wwb.close();
		}
	}

	public static WritableCellFormat getHeader() {
		WritableFont font = new WritableFont(WritableFont.TIMES, 10,
				WritableFont.BOLD);// 定义字体
		try {
			font.setColour(Colour.BLUE);// 蓝色字体
		} catch (WriteException e1) {
			e1.printStackTrace();
		}
		WritableCellFormat format = new WritableCellFormat(font);
		try {
			format.setAlignment(jxl.format.Alignment.CENTRE);// 左右居中
			format.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);// 上下居中
			// format.setBorder(Border.ALL, BorderLineStyle.THIN,
			// Colour.BLACK);// 黑色边框
			// format.setBackground(Colour.YELLOW);// 黄色背景
		} catch (WriteException e) {
			e.printStackTrace();
		}
		return format;
	}
	
	/** 获取SD可用容量 */
	private static long getAvailableStorage() {

		StatFs statFs = new StatFs(root);
		long blockSize = statFs.getBlockSize();
		long availableBlocks = statFs.getAvailableBlocks();
		long availableSize = blockSize * availableBlocks;
		// Formatter.formatFileSize(context, availableSize);
		return availableSize;
	}
}
