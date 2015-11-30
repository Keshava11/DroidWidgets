package com.droidwidgets;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.droidwidgets.listeners.OnPinSuccessListener;
import com.droidwidgets.views.PinView;

/**
 * Activity showing simple use of various custom widgets
 */
public class DroidWidgetsActivity extends ActionBarActivity implements OnPinSuccessListener {

    private LinearLayout mPinViewParent = null;
    private PinView mPinView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_droid_widgets);

        addPinView();
    }

    /**
     * Adds PinView to the activity's content layout
     */
    private void addPinView() {
        mPinViewParent = (LinearLayout) findViewById(R.id.droid_widgets_root_layout);

        // Instantiate PinView and add it to the activity's content layout
        mPinView = new PinView(this, this);
        mPinView.setBackgroundColor(Color.TRANSPARENT);
        LinearLayout.LayoutParams pinViewParams = new LinearLayout.LayoutParams((int) getResources().getDimension(
                R.dimen.pin_entry_width), (int) getResources().getDimension(R.dimen.pin_entry_height));
//        pinViewParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        pinViewParams.gravity = Gravity.CENTER_HORIZONTAL;
        mPinView.setLayoutParams(pinViewParams);
        mPinViewParent.addView(mPinView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.droid_widgets, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void pinSuccess(String iPinContent) {
        // TODO Action once Pin Entry is completed . Like comparison against a String
    }
}
