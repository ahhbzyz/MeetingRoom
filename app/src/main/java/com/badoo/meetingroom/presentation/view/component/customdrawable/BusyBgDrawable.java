package com.badoo.meetingroom.presentation.view.component.customdrawable;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

/**
 * Created by zhangyaozhong on 19/12/2016.
 */

public class BusyBgDrawable extends Drawable {


    private final int pastBgColor = Color.parseColor("#4DD4D4D4");
    private int bgColor;

    private float remainingProgress;

    private Paint mBgPaint;
    private Paint mDividerPaint;
    private final float gap = 20f;

    public BusyBgDrawable(int bgColor) {

        this.bgColor = bgColor;
        mBgPaint = new Paint();

        this.remainingProgress = 1f;

        mDividerPaint = new Paint();
        mDividerPaint.setColor(Color.WHITE);
        mDividerPaint.setStrokeWidth(3);
    }

    public BusyBgDrawable(int bgColor, float leftProgress) {

        this.bgColor = bgColor;

        mBgPaint = new Paint();

        this.remainingProgress = leftProgress;

        mDividerPaint = new Paint();
        mDividerPaint.setColor(Color.WHITE);
        mDividerPaint.setStrokeWidth(3);
    }


    @Override
    public void draw(@NonNull Canvas canvas) {
        Rect bounds = getBounds();

        int width = bounds.right - bounds.left;
        int height = bounds.bottom - bounds.top;

        float bottomRectHeight = remainingProgress * height;
        float topRectHeight = height - bottomRectHeight;

        mBgPaint.setColor(pastBgColor);
        canvas.drawRect(0, 0, width, topRectHeight, mBgPaint);

        mBgPaint.setColor(bgColor);
        canvas.drawRect(0, topRectHeight, width, height, mBgPaint);

        for (float i = -(bottomRectHeight); i <= width; i += gap) {
            canvas.drawLine(i, topRectHeight, i + bottomRectHeight, height, mDividerPaint);
        }
    }


    @Override
    public void setAlpha(int i) {

    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }
}
