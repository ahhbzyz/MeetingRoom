package com.badoo.meetingroom.presentation.view.component.customdrawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

/**
 * Created by zhangyaozhong on 19/12/2016.
 */

public class TimelineBarDrawable extends Drawable {

    private final Paint backgroundPaint;
    private int[]colors;
    private float leftProgress;

    public TimelineBarDrawable(int[]colors, float leftProgress) {
        this.colors = colors;
        this.leftProgress = leftProgress;
        backgroundPaint = new Paint();
    }

    @Override
    public void draw(Canvas canvas) {

        if (colors.length != 2) {
            return;
        }

        // get drawable dimensions
        Rect bounds = getBounds();

        int width = bounds.right - bounds.left;
        int height = bounds.bottom - bounds.top;

        // draw background gradient


        float leftBarHeight= leftProgress * height;
        float firstBarHeight = height - leftBarHeight;

        backgroundPaint.setColor(colors[0]);
        canvas.drawRect(0, 0, width, firstBarHeight, backgroundPaint);

        backgroundPaint.setColor(colors[1]);
        canvas.drawRect(0, firstBarHeight, width, height, backgroundPaint);


    }

    @Override
    public void setAlpha(int i) {

    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    protected boolean onLevelChange(int level) {
        invalidateSelf();
        return true;
    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }
}
