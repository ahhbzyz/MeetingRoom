package com.badoo.meetingroom.presentation.view.component.drawable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import com.badoo.meetingroom.R;

/**
 * Created by zhangyaozhong on 23/01/2017.
 */

public class TimeSlotWithDashesDrawable extends Drawable {

    private final Paint backgroundPaint;
    private final Paint dashPaint;
    private final float dashWidth;
    private float CORNER_RADIUS = 10;
    private int numOfDashes = 3;

    public TimeSlotWithDashesDrawable (Context context, int color, float dashWidth) {
        this.backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.dashPaint = new Paint();
        this.dashPaint.setColor(Color.WHITE);
        this.backgroundPaint.setColor(color);
        this.dashWidth = dashWidth;

        CORNER_RADIUS = context.getResources().getDimension(R.dimen.time_slot_corner_radius);
    }

    @Override
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();

        float width = bounds.right - bounds.left;
        float height = bounds.bottom - bounds.top;

        int even  = numOfDashes % 2 == 0 ? 1 : 0;
        float dashStartPos = width / 2f - dashWidth / 2f - dashWidth * 2 * (numOfDashes / 2 - even);

        canvas.drawRoundRect(new RectF(0, 0, width, height), CORNER_RADIUS, CORNER_RADIUS, backgroundPaint);

        for (float i = 0; i < numOfDashes; i++) {
            canvas.drawRect(dashStartPos, 0,  dashStartPos + dashWidth, height, dashPaint);
            dashStartPos += 2 * dashWidth;
        }
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