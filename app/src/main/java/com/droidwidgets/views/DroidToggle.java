
package com.droidwidgets.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ToggleButton;

/**
 * This class represents a Custom Toggle Button which also acts as Switch and works for versions prior (4.0).
 * TODO Since it is a very simple toggle button implementation , a lot of genaralization and customization is still possible
 *
 * @author ravikumar
 */
public class DroidToggle extends ToggleButton {
    private int mWidth = 0;
    private int mHeight = 0;

    private RectF mBackGroundRect = null;
    private RectF mLeftForeGroundRect = null;
    private RectF mRightForeGroundRect = null;

    private Paint mToggleRectPaint = null;
    private Paint mWritingPaint = null;

    private float mRadiusX = 3.0f;
    private float mRadiusY = 3.0f;

    private CharSequence mTextOff = null;
    private CharSequence mTextOn = null;

    /**
     * Enum representing various paint types
     *
     * @author ravikumar
     */
    enum PAINT_TYPE {
        BG_BLACK, LFG_GREEN, RFG_RED
    }

    public DroidToggle(Context context, AttributeSet attrs) {
        super(context, attrs);

        initializeRects();
        initializePaint();

        mTextOff = getTextOff();
        mTextOn = getTextOn();

        if (mTextOff == null || mTextOff.equals("") || mTextOn == null || mTextOn.equals("")) {
            throw new RuntimeException(
                    "The value for attribute android:textOff and android:textOn can never be null or empty");
        }
    }


    /**
     * This method initializes various rects required while drawing
     */
    private void initializeRects() {
        mBackGroundRect = new RectF();
        mLeftForeGroundRect = new RectF();
        mRightForeGroundRect = new RectF();
    }

    /**
     * This method initializes various paint objects used for drawing
     */
    private void initializePaint() {
        mToggleRectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mToggleRectPaint.setStyle(Style.FILL);

        mWritingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mWritingPaint.setStrokeWidth(1.0f);
        mWritingPaint.setStyle(Style.FILL_AND_STROKE);
        mWritingPaint.setTextAlign(Align.CENTER);
        mWritingPaint.setTextSize(18.0f);

        updatePaintColor(PAINT_TYPE.BG_BLACK);
        updateTextPaintColor(isChecked());
    }


    /**
     * This method updates the color of Paint object used for drawing rectangles
     *
     * @param iPaintType Constant representing particular type of Paint
     */
    private void updatePaintColor(PAINT_TYPE iPaintType) {
        switch (iPaintType) {
            case BG_BLACK:
                mToggleRectPaint.setColor(Color.BLACK);
                break;
            case LFG_GREEN:
                mToggleRectPaint.setColor(Color.GREEN);
                break;
            case RFG_RED:
                mToggleRectPaint.setColor(Color.RED);
                break;
            default:
                mToggleRectPaint.setColor(Color.BLACK);
                break;
        }
    }


    /**
     * This method updates the color of Paint object used for writing
     *
     * @param isChecked boolean reprenting current checked state of widget
     */
    private void updateTextPaintColor(boolean isChecked) {
        mWritingPaint.setColor((isChecked) ? (Color.WHITE) : (Color.GRAY));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Get the calculated dimensions for drawing
        mWidth = getWidth();
        mHeight = getHeight();

        // update positions for Background rect
        mBackGroundRect.set(0, 0, mWidth, mHeight);

        // Draw outer rectangle
        updatePaintColor(PAINT_TYPE.BG_BLACK);
        canvas.drawRoundRect(mBackGroundRect, mRadiusX, mRadiusY, mToggleRectPaint);

        mLeftForeGroundRect.set(5, 5, mWidth / 2, mHeight - 5);
        mRightForeGroundRect.set(mWidth / 2, 5, mWidth - 5, mHeight - 5);

        // Drawing inner rectangle(Left Green or Right Red) and text with in it based upon Toggle button's checked state
        doDrawing(isChecked(), canvas);

    }


    /**
     * This method does the actual drawing based upon Toggle button's checked state
     *
     * @param checked boolean defining checked state of view
     * @param canvas  Canvas of this view on which drawing has to be done
     */
    private void doDrawing(boolean checked, Canvas canvas) {
        RectF rectToDraw = (checked) ? mLeftForeGroundRect : mRightForeGroundRect;
        PAINT_TYPE rectPaintType = (checked) ? (PAINT_TYPE.LFG_GREEN) : (PAINT_TYPE.RFG_RED);

        // Draw left inner rectangle representing CHECKED/ON state
        updatePaintColor(rectPaintType);
        canvas.drawRoundRect(rectToDraw, mRadiusX, mRadiusY, mToggleRectPaint);

        // Update Paint and draw text for ON state
        updateTextPaintColor(checked);
        canvas.drawText(mTextOn.toString(), mLeftForeGroundRect.width() * 0.5f, mBackGroundRect.height() * 0.5f + mWritingPaint.descent(),
                mWritingPaint);

        // Update Paint and draw text for OFF state
        updateTextPaintColor(!checked);
        canvas.drawText(mTextOff.toString(), mBackGroundRect.width() * 0.75f,
                mBackGroundRect.height() * 0.5f + mWritingPaint.descent(), mWritingPaint);
    }


}
