
package com.droidwidgets.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.droidwidgets.R;
import com.droidwidgets.listeners.OnPinSuccessListener;

/**
 * This is a custom EdiText class that functions as PinEntry View. Better replacement for 4 EditText and their
 * configuration overhead
 *
 * @author Keshava11
 */
public class PinView extends EditText {
    private final Bitmap mBoxDrawableBmp;
    private String mPinData = "";
    private final Rect mPinBoxRect = new Rect();
    private int mCharLength = 0;
    private final String[] mVisibleCharList = new String[]{".", "*"};
    private int mCharType = -1;
    private final Paint[] mTextPaintList = new Paint[2];
    private boolean mKeyBoardCheck = false;
    private OnPinSuccessListener mOnPinSuccessListener = null;

    public PinView(Context context, OnPinSuccessListener iPinListener) {
        super(context);

        mOnPinSuccessListener = iPinListener;

        // Initializing paint list
        mTextPaintList[0] = getPaint(Color.RED, 4.0f);
        mTextPaintList[1] = getPaint(Color.LTGRAY, 3.0f);

        // Fetching bitmap from shape drawable to drawn on EditText
        mBoxDrawableBmp = drawableToBitmap(getResources().getDrawable(R.drawable.square_shape));

		/*
         * Set input type, max length filter and text gravity
		 */
        setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
        setGravity(Gravity.CENTER_VERTICAL);

        // Register listeners
        addTextChangedListener(mTextWatcher);
        setOnTouchListener(mTouchListener);
        getViewTreeObserver().addOnGlobalLayoutListener(mGlobalLayoutListener);
    }

    /**
     * Method creating Paint object
     *
     * @param iColor     paint color
     * @param iStrokeWid width of the stroke
     * @return Paint object
     */
    private Paint getPaint(int iColor, float iStrokeWid) {
        Paint mEditTextPaint = new Paint();
        mEditTextPaint.setColor(iColor);
        mEditTextPaint.setStyle(Style.STROKE);
        mEditTextPaint.setStrokeWidth(iStrokeWid);
        mEditTextPaint.setTextSize(50.0f);

        return mEditTextPaint;
    }

    /**
     * Listener added to check changes in the view hierarchy. Mainly used to check if soft keyboard is visible or not
     */
    private OnGlobalLayoutListener mGlobalLayoutListener = new OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            Rect rect = new Rect();

            getWindowVisibleDisplayFrame(rect);

            int heightDiff = getRootView().getHeight() - (rect.bottom - rect.top);
            int keyboardHeight = (int) getResources().getDimension(R.dimen.keyboard_height);

            if (heightDiff <= (keyboardHeight + 30)) {
                mKeyBoardCheck = true;
            }
        }
    };

    /**
     * Listener for listening touch events on EditText and
     */
    private OnTouchListener mTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View iView, MotionEvent iEvent) {
            // Setting the current touched View focusable
            iView.setFocusable(true);
            iView.setFocusableInTouchMode(true);
            iView.requestFocus();

            String currentContent = getText().toString();
            if (currentContent.length() > 0) {
                // Check for Character selection
                setSelection(currentContent.length());
                if (mKeyBoardCheck) {
                    // Set keyboard visible
                    InputMethodManager inputMethodMgr = (InputMethodManager) getContext().getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    inputMethodMgr.showSoftInput(PinView.this, 0);
                    mKeyBoardCheck = false;
                }
                return true;
            }
            return false;
        }
    };

    /**
     * Drawing box bitmap and hints and red dots on Text entry
     */
    @Override
    protected void onDraw(Canvas iCanvas) {
        // Looping to create four Pin boxes
        for (int i = 0; i < 4; i++) {
            int left = (mBoxDrawableBmp.getWidth() + 20) * i;
            int top = 0;

            // Drawing Pin Boxes
            mPinBoxRect.set(left, top, left + mBoxDrawableBmp.getWidth(), top + mBoxDrawableBmp.getHeight());
            iCanvas.drawBitmap(mBoxDrawableBmp, null, mPinBoxRect, null);

            // Tweaking a little bit and
            int heightIncrement = 0;
            if (i >= 0 && i < mCharLength) {
                // This is for dots
                mCharType = 0;
                heightIncrement = 0;
            } else {
                // This is for star
                mCharType = 1;
                heightIncrement = 15;
            }

            iCanvas.drawText(mVisibleCharList[mCharType], mPinBoxRect.exactCenterX() - 12, mPinBoxRect.exactCenterY()
                    + heightIncrement, mTextPaintList[mCharType]);
        }
    }

    /**
     * Listener for text changes in the EditText
     */
    private TextWatcher mTextWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence iSequence, int iStart, int iBefore, int iCount) {

        }

        @Override
        public void beforeTextChanged(CharSequence iSequence, int iStart, int iCount, int iAfter) {

        }

        @Override
        public void afterTextChanged(Editable iEditable) {
            // Fetching PinData
            mPinData = iEditable.toString();
            mCharLength = mPinData.length();

            // Setting cursor to also remain on end, so that next character always comes to the last irrespective of
            // focus
            setSelection(mCharLength);

            // Checking for pin length to be 4 and as soon as it is 4 send callback to the listeners
            if (mCharLength == 4) {
                mOnPinSuccessListener.pinSuccess(mPinData);
            }
        }
    };

    /**
     * Method for fetching bitmap from the shapeDrawable created for each Pin box to draw it on canvas
     *
     * @param drawable shape drawable
     * @return corresponding bitmap
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    /**
     * This method is for clearing text from the edittext
     */
    public void removeText() {
        // Make CharLength = -1 and settext() with empty string
        mCharLength = -1;
        setText("");
    }
}
