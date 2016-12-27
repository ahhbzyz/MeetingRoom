//package com.badoo.meetingroom.presentation.view.activity;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.badoo.meetingroom.data.repository.RoomEventDataRepository;
//import com.badoo.meetingroom.data.repository.datasource.RoomEventDataStoreFactory;
//import com.badoo.meetingroom.domain.mapper.RoomEventDataMapper;
//import com.badoo.meetingroom.domain.interactor.DefaultSubscriber;
//import com.badoo.meetingroom.domain.repository.RoomEventRepository;
//import com.badoo.meetingroom.presentation.view.adapter.DailyEventsAdapter;
//import com.badoo.meetingroom.domain.RoomEvent;
//import com.badoo.meetingroom.R;
//import com.badoo.meetingroom.presentation.view.timeutils.TimeHelper;
//import com.google.api.client.util.DateTime;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class DailyEventsActivity extends AppCompatActivity {
//
//
//    private List<RoomEvent> mEvents;
//
//    private RecyclerView mRecyclerView;
//    private LinearLayoutManager mLayoutManager;
//    private DailyEventsAdapter mAdapter;
//    private LinearLayout mTimelineMarkLayout;
//    RelativeLayout.LayoutParams params;
//    private TextView tv;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_daily_events);
//
//
//        mEvents = new ArrayList<>();
//
//        RoomEventDataStoreFactory us = new RoomEventDataStoreFactory(this);
//        RoomEventDataMapper roomEventDataMapper = new RoomEventDataMapper();
//        RoomEventRepository roomEventRepository = new RoomEventDataRepository(us, roomEventDataMapper);
//        //GetRoomEventList getUserEvents = new GetRoomEventList(roomEventRepository);
//
//        DateTime now = new DateTime(TimeHelper.getMidNightTimeOfDay(0));
//        DateTime end = new DateTime(TimeHelper.getMidNightTimeOfDay(1));
//
//        //getUserEvents.excute(new UserEventListSubscriber(), MainActivity.mCredential, now, end);
//
//        tv = (TextView)findViewById(R.id.tv_curr_time);
//        tv.setText(TimeHelper.formatTime(TimeHelper.getCurrentTimeInMillis()));
//
//        mRecyclerView = (RecyclerView) findViewById(R.id.rv_daily_events);
//
//        // use this setting to improve performance if you know that changes
//        // in content do not change the layout size of the RecyclerView
//        mRecyclerView.setHasFixedSize(true);
//
//
//        // use a linear layout manager
//        mLayoutManager = new LinearLayoutManager(this);
//        mRecyclerView.setLayoutManager(mLayoutManager);
//        mAdapter = new DailyEventsAdapter(getApplicationContext(), mEvents);
//        mRecyclerView.setAdapter(mAdapter);
//
//
//        mTimelineMarkLayout = (LinearLayout)findViewById(R.id.layout_timeline_mark);
//
//
//        float currTimeHeight = (TimeHelper.getCurrentTimeSinceMidNight()) *  300f / (60 * 60 * 1000);
//        mTimelineMarkLayout.measure(0, 0);
//        mTimelineMarkLayout.setY(currTimeHeight-mTimelineMarkLayout.getMeasuredHeight() / 2f);
//
//
//        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//
//                float startY = mTimelineMarkLayout.getY();
//                mTimelineMarkLayout.setY(startY - dy);
//
//            }
//        });
//
//        registerReceiver(mTimeRefreshReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
//    }
//
//
//    private final class UserEventListSubscriber extends DefaultSubscriber<List<RoomEvent>> {
//
//        @Override
//        public void onNext(List<RoomEvent> roomEvents) {
//            super.onNext(roomEvents);
//            if (roomEvents == null || roomEvents.size() == 0) {
//
//            } else {
//                for (RoomEvent roomEvent : roomEvents) {
//                    mEvents.add(roomEvent);
//                }
//                mAdapter.notifyDataSetChanged();
//
//            }
//        }
//    }
//
//    private BroadcastReceiver mTimeRefreshReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (Intent.ACTION_TIME_TICK.equals(intent.getAction())) {
//                float startY = mTimelineMarkLayout.getY();
//                float incremental = 60f * 1000 * (300f / (60 * 60 * 1000));
//                mTimelineMarkLayout.setY(startY + incremental);
//                mAdapter.notifyDataSetChanged();
//                tv.setText(TimeHelper.formatTime(TimeHelper.getCurrentTimeInMillis()));
//            }
//        }
//    };
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        unregisterReceiver(mTimeRefreshReceiver);
//    }
//}
