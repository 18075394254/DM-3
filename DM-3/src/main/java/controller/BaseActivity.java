package controller;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

/**
 * Created by Administrator on 16-8-23.
 */
public class BaseActivity extends Activity {

    public static String STATE_SPEED="speed";
    public static String STATE_FORCE="force";
    public static String STATE_ALL="all";
    public static String STATE_TESTING="testing";
    public static String STATE_DONE="testingDone";
    public static String STATE_NOSELETEWAY="no selete way";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("BaseActivity", getClass().getSimpleName());
        ActivityCollector.addActivity(this);

}

    // 减少findViewById的次数

    public final <E extends View> E getView(int id) {
        try {
            return (E) findViewById(id);
        } catch (ClassCastException ex) {
            Log.e("Tag", "Could not cast View to concrete class.", ex);
            throw ex;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
        
    }
}
