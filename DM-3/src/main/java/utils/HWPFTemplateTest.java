package utils;

import android.content.res.AssetManager;
import android.os.Environment;

import com.example.user.dm_3.R;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import controller.MyApplication;


/**
 * Created by Administrator on 2017/1/9 0009.
 */

    public class HWPFTemplateTest {
        /**
         * 用一个doc文档作为模板，然后替换其中的内容，再写入目标文档中。
         * @throws Exception
         */


        public void testTemplateWrite(String docPath,String people,String time,String location,String number,String picPath) throws Exception {
           // String templatePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "model.doc";
            InputStream is = null;
            AssetManager manager = MyApplication.getContext().getAssets();
            try {
                is = manager.open("model.doc");
            } catch (Exception e) {
                e.printStackTrace();
            }
           // InputStream is = new FileInputStream(templatePath);
            HWPFDocument doc = new HWPFDocument(is);
            Range range = doc.getRange();
            //把range范围内的${reportDate}替换为需要的参数
            range.replaceText("${paramPeople}", people);
            range.replaceText("${paramTime}", time);
            range.replaceText("${paramLocation}",location);
            range.replaceText("${paramNumber}", number);

            OutputStream os = new FileOutputStream(docPath);
            //把doc输出到输出流中
            doc.write(os);
            this.closeStream(os);
            this.closeStream(is);
        }

        /**
         * 关闭输入流
         * @param is
         */
        private void closeStream(InputStream is) {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * 关闭输出流
         * @param os
         */
        private void closeStream(OutputStream os) {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
}