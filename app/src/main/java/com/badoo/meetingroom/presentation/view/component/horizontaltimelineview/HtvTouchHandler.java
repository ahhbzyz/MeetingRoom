package com.badoo.meetingroom.presentation.view.component.horizontaltimelineview;

import android.graphics.RectF;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.OverScroller;

import com.badoo.meetingroom.presentation.model.EventModel;

import java.util.LinkedList;

/**
 * Created by zhangyaozhong on 12/12/2016.
 */

public class HtvTouchHandler implements GestureDetector.OnGestureListener {

    private HorizontalTimelineView view;
    private GestureDetectorCompat mDetector;
    private OverScroller mScroller;
    private boolean scrolled;
    private float touchX;
    private float touchY;
    private EventModel mSelectedEvent;

    public HtvTouchHandler(HorizontalTimelineView view){
        this.view = view;
        mDetector = new GestureDetectorCompat(view.getContext(), this);
        mScroller = new OverScroller(view.getContext());
    }

    boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mScroller.forceFinished(true);
                touchX = event.getX();
                touchY = event.getY();

                LinkedList<EventModel> mEvents = view.getEventList();
                float startX = view.getMovedTimelineMarkCx();
                for (EventModel e : mEvents) {
                    RectF slotBounds = new RectF();
                    slotBounds.set(startX, view.getTimelineCy() - view.getTimeLineStrokeWidth(), startX += e.getRemainingTime() * view.getWidthTimeRatio(), view.getTimelineCy() + view.getTimeLineStrokeWidth());
                    if (slotBounds.contains(touchX, touchY)) {
                        mSelectedEvent = e;
                        break;
                        //Toast.makeText(view.getContext(), TimeHelper.formatMillisInHrsAndMins(mSelectedEvent.getStartTime()), Toast.LENGTH_LONG).show();
                    }
                }

                RectF markBounds = new RectF();
                markBounds.set(view.getTimelineMarkCx() - view.getCurrTimeTextWidth() / 2f, 0, view.getTimelineMarkCx() + view.getCurrTimeTextWidth() / 2f, view.getHeight());
                if (markBounds.contains(touchX, touchY)) {
                    view.resetMovedTimelineMarkCx();
                }


                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
        }
        return this.mDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        scrolled = false;
        return true;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        mScroller.forceFinished(true);
        if (scrolled) {
            view.updateTimelineMarkCx(distanceX);
        }
        scrolled = true;
        return true;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        mScroller.forceFinished(true);
        mScroller.fling((int)view.getMovedTimelineMarkCx(), 0, (int)(velocityX), 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 0);
        ViewCompat.postInvalidateOnAnimation(view);
        return true;
    }

    OverScroller getScroller() {
        return mScroller;
    }
}
