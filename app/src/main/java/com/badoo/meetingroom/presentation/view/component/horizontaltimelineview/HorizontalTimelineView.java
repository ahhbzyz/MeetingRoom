package com.badoo.meetingroom.presentation.view.component.horizontaltimelineview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.OverScroller;

import com.badoo.meetingroom.R;
import com.badoo.meetingroom.presentation.model.RoomEventModel;
import com.badoo.meetingroom.presentation.view.timeutils.TimeHelper;

import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Created by Yaozhong on 09/12/2016.
 */

public class HorizontalTimelineView extends View {

    private static final float DEFAULT_TIMELINE_STROKE_WIDTH = 25f;
    private static final int DEFAULT_TIMELINE_HEIGHT = 200;

    private static final int DEFAULT_TIMELINE_MARK_COLOR = Color.parseColor("#333844");
    private static final int DEFAULT_TIMELINE_MARK_WIDTH = 2;
    private static final float DEFAULT_TIMELINE_MARK_CX = 200f;

    private static final int DEFAULT_MOVED_TIMELINE_MARK_COLOR = Color.parseColor("#D4D4D4");


    private static final int DEFAULT_PAST_TIME_SLOT_COLOR = Color.parseColor("#D4D4D4");

    private static final int DEFAULT_GAP_TIME_SLOT_COLOR = Color.parseColor("#FFFFFF");
    private static final float DEFAULT_GAP_TIME_SLOT_WIDTH = 5f;

    // Default current time text
    private static final float DEFAULT_CURR_TIME_TEXT_SIZE = 35f;


    // Default slot time text
    private static final float DEFAULT_SLOT_TIME_TEXT_SIZE = 25f;
    private static final int DEFAULT_SLOT_TIME_TEXT_COLOR = Color.parseColor("#C2C2C2");

    private static final int MIN_SLOT_TIME = 5;


    /**
     * Current time text
     */
    private float mCurrTimeTextSize;


    private String mCurrTimeText = "00:00";
    private Paint mCurrTimeTextPaint;
    private Rect mCurrTimeTextBounds;
    private float mCurrTimeTextWidth = 0;

    private Paint mMovedCurrTimeTextPaint;


    /**
     * Slot time text
     */
    private float mSlotTimeTextSize;
    private int mSlotTimeTextColor;

    private Paint mSlotTimeTextPaint;

    private final float mSlotTimeOffset = 20f;

    /**
     * Timeline height
     */
    private int mTimelineHeight;
    private float mTimelineStrokeWidth;

    private final float mTimelineOffset = 20f;

    /**
     * Timeline default start x-axis and default y-axis
     */
    private float mTimelineStartCx = 0;
    private float mTimelineCy = 0;

    private Paint mTimelinePathPaint;

    /**
     * Time line mark
     */
    private Paint mTimelineMarkPaint;
    private float mTimelineMarkWidth;
    private int mTimelineMarkColor;
    private float mTimelineMarkCx;

    /**
     * Dynamic timeline mark
     */
    private Paint mMovedTimelineMarkPaint;
    private int mMovedTimelineMarkColor;
    private float mMovedTimelineMarkCx;

    /**
     * Time slot list
     */
    private LinkedList<RoomEventModel> mEvents;
    private ListIterator<RoomEventModel> mListIterator;

    /**
     * Interval slot
     */
    private int mGapTimeSlotColor;
    private Paint mGapTimeSlotPaint;
    private float mGapTimeSlotWidth;


    /**
     * Past time slot
     */
    private int mPastTimeSlotColor;


    private float mAccumTimeSlotWidth = 0f;

    private HtvTouchHandler mHtvTouchHandler;
    private OverScroller mScroller;

    private float mHourHeight = 240f;
    private float mWidthPerMillis;


    public HorizontalTimelineView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray ta = context.getTheme().obtainStyledAttributes(
            attrs,
            R.styleable.HorizontalTimelineView,
            0, 0
        );

        try {
            mTimelineStrokeWidth = ta.getFloat(R.styleable.HorizontalTimelineView_timelineStrokeWidth, DEFAULT_TIMELINE_STROKE_WIDTH);
            mTimelineHeight = ta.getInt(R.styleable.HorizontalTimelineView_timelineHeight, DEFAULT_TIMELINE_HEIGHT);

            mCurrTimeTextSize = ta.getFloat(R.styleable.HorizontalTimelineView_currTimeTextSize, DEFAULT_CURR_TIME_TEXT_SIZE);

            mSlotTimeTextColor = ta.getColor(R.styleable.HorizontalTimelineView_slotTimeTextColor, DEFAULT_SLOT_TIME_TEXT_COLOR);
            mSlotTimeTextSize = ta.getFloat(R.styleable.HorizontalTimelineView_slotTimeTextSize, DEFAULT_SLOT_TIME_TEXT_SIZE);

            mTimelineMarkCx = ta.getFloat(R.styleable.HorizontalTimelineView_timelineMarkCx, DEFAULT_TIMELINE_MARK_CX);
            mMovedTimelineMarkCx = mTimelineMarkCx;
            mTimelineMarkWidth = ta.getFloat(R.styleable.HorizontalTimelineView_timelineMarkWidth, DEFAULT_TIMELINE_MARK_WIDTH);
            mTimelineMarkColor = ta.getColor(R.styleable.HorizontalTimelineView_timelineMarkColor, DEFAULT_TIMELINE_MARK_COLOR);

            mMovedTimelineMarkColor = ta.getColor(R.styleable.HorizontalTimelineView_movedTimelineMarkColor, DEFAULT_MOVED_TIMELINE_MARK_COLOR);

            mPastTimeSlotColor = ta.getColor(R.styleable.HorizontalTimelineView_pastTimeSlotColor, DEFAULT_PAST_TIME_SLOT_COLOR);

            mGapTimeSlotColor = ta.getColor(R.styleable.HorizontalTimelineView_gapTimeSlotColor, DEFAULT_GAP_TIME_SLOT_COLOR);
            mGapTimeSlotWidth = ta.getFloat(R.styleable.HorizontalTimelineView_gapTimeSlotWidth, DEFAULT_GAP_TIME_SLOT_WIDTH);
        }
        finally {
            ta.recycle();
        }

        init();
    }

    private void init() {

        mEvents = new LinkedList<>();

        mHtvTouchHandler = new HtvTouchHandler(this);
        mScroller = mHtvTouchHandler.getScroller();


        // Time line paint
        mTimelinePathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTimelinePathPaint.setStyle(Paint.Style.STROKE);
        mTimelinePathPaint.setStrokeWidth(mTimelineStrokeWidth);

        // Current time mark
        mTimelineMarkPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTimelineMarkPaint.setStyle(Paint.Style.STROKE);
        mTimelineMarkPaint.setColor(mTimelineMarkColor);
        mTimelineMarkPaint.setStrokeWidth(mTimelineMarkWidth);

        // Static timeline mark
        mMovedTimelineMarkPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMovedTimelineMarkPaint.setStyle(Paint.Style.STROKE);
        mMovedTimelineMarkPaint.setColor(mMovedTimelineMarkColor);
        mMovedTimelineMarkPaint.setStrokeWidth(mTimelineMarkWidth);


        // Current time text
        mCurrTimeTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCurrTimeTextPaint.setTextSize(mCurrTimeTextSize);
        mCurrTimeTextPaint.setColor(mTimelineMarkColor);
        mCurrTimeTextBounds = new Rect();

        // Current time text
        mMovedCurrTimeTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMovedCurrTimeTextPaint.setTextSize(mCurrTimeTextSize);
        mMovedCurrTimeTextPaint.setColor(mMovedTimelineMarkColor);


        mSlotTimeTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSlotTimeTextPaint.setColor(mSlotTimeTextColor);
        mSlotTimeTextPaint.setTextSize(mSlotTimeTextSize);
        Rect rect = new Rect();
        mSlotTimeTextPaint.getTextBounds("00:00", 0, "00:00".length(), rect);

        mWidthPerMillis = rect.width() * 1.5f / TimeHelper.min2Millis(MIN_SLOT_TIME);

        mGapTimeSlotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mGapTimeSlotPaint.setColor(mGapTimeSlotColor);
        mGapTimeSlotPaint.setStyle(Paint.Style.STROKE);
        mGapTimeSlotPaint.setStrokeWidth(mTimelineStrokeWidth);



    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Set accumulated width to the position of Mark
        mAccumTimeSlotWidth = mMovedTimelineMarkCx;

        // Get height and width of text to positioning the position of timeline
        mCurrTimeTextPaint.getTextBounds(mCurrTimeText, 0, mCurrTimeText.length(), mCurrTimeTextBounds);
        float timeTextHeight = mCurrTimeTextBounds.height();
        mCurrTimeTextWidth = mCurrTimeTextBounds.width();

        // Draw rest slots

        mListIterator = mEvents.listIterator();
        while (mListIterator.hasNext()) {
            drawSlot(canvas, mListIterator.next());
            if (mAccumTimeSlotWidth > canvas.getWidth()) {
                //break;
            }
        }

        canvas.drawText("24:00", mAccumTimeSlotWidth - 80f, mTimelineCy + getTimelineOffset() - mTimelineStrokeWidth / 2f - mSlotTimeOffset, mSlotTimeTextPaint);

        // Draw moved time text
        canvas.drawText(mCurrTimeText, mMovedTimelineMarkCx - mCurrTimeTextWidth / 2f, timeTextHeight, mMovedCurrTimeTextPaint);

        // Draw moved time mark
        canvas.drawLine(mMovedTimelineMarkCx, timeTextHeight + mTimelineOffset, mMovedTimelineMarkCx, canvas.getHeight(), mMovedTimelineMarkPaint);

        // Draw static time text
        canvas.drawText(mCurrTimeText, mTimelineMarkCx - mCurrTimeTextWidth / 2f, timeTextHeight, mCurrTimeTextPaint);

        // Draw static time mark
        canvas.drawLine(mTimelineMarkCx, timeTextHeight + mTimelineOffset, mTimelineMarkCx, canvas.getHeight(), mTimelineMarkPaint);
    }

    private void drawSlot(Canvas canvas, RoomEventModel event) {



        float currSlotWidth = event.getRemainingTime() * mWidthPerMillis;
        float initSlotWidth = event.getDuration() * mWidthPerMillis;

        // Draw slot time text
        // TODO Hide text when no space
        String startTimeText = event.getStartTimeInText();
        canvas.drawText(startTimeText, mAccumTimeSlotWidth - (initSlotWidth - currSlotWidth), mTimelineCy + getTimelineOffset() - mTimelineStrokeWidth / 2f - mSlotTimeOffset, mSlotTimeTextPaint);


        // Fill up past time slot when one time slot expired
        if (mAccumTimeSlotWidth == mMovedTimelineMarkCx) {
            mTimelinePathPaint.setColor(mPastTimeSlotColor);
            canvas.drawLine(mTimelineStartCx, mTimelineCy + getTimelineOffset(), mMovedTimelineMarkCx - (initSlotWidth - currSlotWidth) - mGapTimeSlotWidth, mTimelineCy + getTimelineOffset(), mTimelinePathPaint);
        }


        if (event.isProcessing()) {
            // Draw gap before past time slot
            mTimelinePathPaint.setColor(Color.WHITE);
            canvas.drawLine(mMovedTimelineMarkCx - (initSlotWidth - currSlotWidth) - mGapTimeSlotWidth, mTimelineCy + getTimelineOffset(), mMovedTimelineMarkCx - (initSlotWidth - currSlotWidth), mTimelineCy + getTimelineOffset(), mTimelinePathPaint);

            // Draw past time slot before Mark
            mTimelinePathPaint.setColor(mPastTimeSlotColor);
            canvas.drawLine(mMovedTimelineMarkCx - (initSlotWidth - currSlotWidth), mTimelineCy + getTimelineOffset(), mMovedTimelineMarkCx, mTimelineCy + getTimelineOffset(), mTimelinePathPaint);

            // Draw past time slot after Mark
            mTimelinePathPaint.setColor(event.getEventColor());
            canvas.drawLine(mMovedTimelineMarkCx, mTimelineCy + getTimelineOffset(), mAccumTimeSlotWidth += currSlotWidth, mTimelineCy + getTimelineOffset(), mTimelinePathPaint);
        } else {
            // Draw gap between each time slot
            mTimelinePathPaint.setColor(Color.WHITE);
            canvas.drawLine(mAccumTimeSlotWidth - mGapTimeSlotWidth, mTimelineCy + getTimelineOffset(), mAccumTimeSlotWidth, mTimelineCy + getTimelineOffset(), mTimelinePathPaint);
            // Draw time slot hasn't expired
            mTimelinePathPaint.setColor(event.getEventColor());
            canvas.drawLine(mAccumTimeSlotWidth, mTimelineCy + getTimelineOffset(), mAccumTimeSlotWidth += currSlotWidth, mTimelineCy + getTimelineOffset(), mTimelinePathPaint);
        }



    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int desiredHeight = mTimelineHeight;

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int height;

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        }
        else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(desiredHeight, heightSize);
        }
        else {
            height = desiredHeight;
        }

        mTimelineCy = height / 2f;
        setMeasuredDimension(widthSize, height);
    }

    public float getWidthTimeRatio() {
        return mWidthPerMillis;
    }

    public float getTimelineCy() {
        return mTimelineCy + getTimelineOffset();
    }

    public void setCurrTimeText(String time) {
        mCurrTimeText = time;
        invalidate();
    }

    public float getCurrTimeTextWidth() {
        return mCurrTimeTextWidth;
    }

    public void setEventList(LinkedList<RoomEventModel> mEvents) {
        this.mEvents = mEvents;
        invalidate();
    }

    public LinkedList<RoomEventModel> getEventList() {
        return this.mEvents;
    }


    private float getTimelineOffset() {
        return mCurrTimeTextBounds == null ? 0 : (mCurrTimeTextBounds.height() + mTimelineOffset) / 2f;
    }


    public float getTimelineMarkCx() {
        return mTimelineMarkCx;
    }

    public float getMovedTimelineMarkCx() {
        return mMovedTimelineMarkCx;
    }

    private float getTodayLeftTimeWidth() {
        float nextDayMidNight = TimeHelper.getMidNightTimeOfDay(1);
        return (nextDayMidNight - TimeHelper.getCurrentTimeInMillis()) * mWidthPerMillis;
    }

    public void updateTimelineMarkCx(float i) {

        if (getTodayLeftTimeWidth() <= (getWidth() - DEFAULT_TIMELINE_MARK_CX)) {
            return;
        }
        //mMovedTimelineMarkCx -= i;
        if (mMovedTimelineMarkCx - i >= -(getTodayLeftTimeWidth() - getWidth()) && mMovedTimelineMarkCx - i <= DEFAULT_TIMELINE_MARK_CX) {
            mMovedTimelineMarkCx -= i;
        }
        else if (mMovedTimelineMarkCx - i > DEFAULT_TIMELINE_MARK_CX) {
            mMovedTimelineMarkCx = DEFAULT_TIMELINE_MARK_CX;
        }
        else if (mMovedTimelineMarkCx - i < -(getTodayLeftTimeWidth() - getWidth())) {
            mMovedTimelineMarkCx = -(getTodayLeftTimeWidth() - getWidth());


        }

        invalidate();
    }

    public void resetMovedTimelineMarkCx() {
        this.mMovedTimelineMarkCx = DEFAULT_TIMELINE_MARK_CX;
        invalidate();
    }

    public float getTimeLineStrokeWidth() {
        return mTimelineStrokeWidth;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (getTodayLeftTimeWidth() <= (getWidth() - DEFAULT_TIMELINE_MARK_CX)){
            return;
        }
        boolean needsInvalidate = false;
        if (mScroller.computeScrollOffset()) {
            int currX = mScroller.getCurrX();
            boolean canScroll = currX <= DEFAULT_TIMELINE_MARK_CX && currX >= -(getTodayLeftTimeWidth() - getWidth());
            if (canScroll) {
                needsInvalidate = true;
                mMovedTimelineMarkCx = currX;
            } else if (currX > DEFAULT_TIMELINE_MARK_CX){
                mMovedTimelineMarkCx = DEFAULT_TIMELINE_MARK_CX;
            } else if (currX < -(getTodayLeftTimeWidth() - getWidth())) {
                mMovedTimelineMarkCx = -(getTodayLeftTimeWidth() - getWidth()) - 1;

            }
        }

        if(needsInvalidate) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        return mHtvTouchHandler.onTouchEvent(event);
    }

    public void updateCurrentStatus() {
        invalidate();
    }
}
