package activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.TextView;

import com.example.user.dm_3.R;
import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnPageChangeListener;

import controller.BaseActivity;

import static java.lang.String.format;

/**
 * Created by user on 2018/6/22.
 */
public class UserExplainActivity extends BaseActivity implements OnPageChangeListener {
    public static final String SAMPLE_FILE = "DM-2.pdf";



    PDFView pdfView;

    private TextView currentPage;
    String pdfName = SAMPLE_FILE;


    Integer pageNumber = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        pdfView = (PDFView) findViewById(R.id.pdfView);
        currentPage = (TextView) findViewById(R.id.currentPage);
        display(pdfName, false);
        currentPage.setText("");
    }

    private void display(String assetFileName, boolean jumpToFirstPage) {
        if (jumpToFirstPage) pageNumber = 1;
        setTitle(pdfName = assetFileName);

        pdfView.fromAsset(assetFileName)
                .defaultPage(pageNumber)
                .onPageChange(this)
                .load();
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
        currentPage.setText(format("%s %s / %s", pdfName, page, pageCount));
    }

    private boolean displaying(String fileName) {
        return fileName.equals(pdfName);
    }
}
