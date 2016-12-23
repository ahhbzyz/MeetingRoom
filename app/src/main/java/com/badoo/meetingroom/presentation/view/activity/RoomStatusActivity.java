//package com.badoo.meetingroom.presentation.view.activity;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.os.Bundle;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.app.AppCompatActivity;
//import android.view.View;
//import android.widget.TextView;
//
//
//import com.badoo.meetingroom.MainActivity;
//import com.badoo.meetingroom.data.repository.datasource.RoomEventDataStoreFactory;
//import com.badoo.meetingroom.domain.mapper.RoomEventDataMapper;
//import com.badoo.meetingroom.data.repository.RoomEventDataRepository;
//import com.badoo.meetingroom.domain.RoomEvent;
//import com.badoo.meetingroom.R;
//import com.badoo.meetingroom.domain.interactor.DefaultSubscriber;
//import com.badoo.meetingroom.domain.repository.RoomEventRepository;
//import com.badoo.meetingroom.presentation.view.component.circletimerview.CircleTimerView;
//import com.badoo.meetingroom.presentation.view.component.circletimerview.OnCircleBtnClickListener;
//import com.badoo.meetingroom.presentation.view.component.circletimerview.OnCountDownListener;
//import com.badoo.meetingroom.presentation.view.component.horizontaltimelineview.HorizontalTimelineView;
//import com.badoo.meetingroom.presentation.view.component.custombutton.LongPressButton;
//import com.badoo.meetingroom.utils.Utils;
//import com.google.api.client.util.DateTime;
//import com.google.api.services.calendar.model.Event;
//
//import java.util.ArrayList;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Queue;
//
//public class RoomStatusActivity extends AppCompatActivity {
//
//
//    private CircleTimerView ctv;
//    private HorizontalTimelineView htv;
//
//    private List<Event> events;
//    private Queue<RoomEvent> queue;
//    private List<RoomEvent> mEvents;
//    TextView tv;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_room_events);
//        mEvents = MainActivity.roomEventList;
//        ctv = (CircleTimerView)findViewById(R.id.ctv_status);
//        ctv.setTailIconDrawable(R.drawable.ic_arrow_left);
//        ctv.setCircleBtnIconDrawable(R.drawable.ic_add_black);
//        ctv.setAlertIconDrawable(R.drawable.ic_alert_white);
//        ctv.setTimerInfoTextPadding(30);
//
//        htv = (HorizontalTimelineView)findViewById(R.id.htv_room_events);
//
//        tv  = (TextView)findViewById(R.id.tv_curr_date_time);
//        tv.setText(Utils.getCurrentDateAndWeek(getApplicationContext()));
//        queue = new LinkedList<>();
//        mEvents = new ArrayList<>();
//
//        RoomEventDataStoreFactory us = new RoomEventDataStoreFactory(this);
//        RoomEventDataMapper roomEventDataMapper = new RoomEventDataMapper();
//        RoomEventRepository roomEventRepository = new RoomEventDataRepository(us, roomEventDataMapper);
//       // GetRoomEventList getUserEvents = new GetRoomEventList(roomEventRepository);
//
//        DateTime now = new DateTime(Utils.getNextDaysMidNight(0));
//        DateTime end = new DateTime(Utils.getNextDaysMidNight(1));
//
//        //getUserEvents.excute(new UserEventListSubscriber(), MainActivity.mCredential, now, end);
//
//
//
//
//
//
//        ctv.setOnCircleBtnClickListener(new OnCircleBtnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
//
//        ctv.setOnCountDownListener(new OnCountDownListener() {
//            @Override
//            public void onCountDownTicking(long millisUntilFinished) {
//                ctv.setTimerTimeText(millisUntilFinished);
//                htv.invalidate();
//            }
//
//            @Override
//            public void onCountDownFinished() {
//                if(queue!= null && !queue.isEmpty())  {
//                    RoomEvent e = queue.remove();
//                    ctv.startCountDownTimer(e);
//                } else {
//
//                }
//
//                htv.removeFirstEventFromList();
//            }
//        });
//
//
//
//        LongPressButton button = (LongPressButton)findViewById(R.id.ttb10);
//        button.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_clear_white));
//        button.setOnCountDownListener(() -> {
//
//        });
//        registerReceiver(mTimeRefreshReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
//
//    }
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
//                    queue.offer(roomEvent);
//                }
//                if (!ctv.isTimerRunning() && !queue.isEmpty()) {
//                    htv.setEventList(mEvents);
//                    RoomEvent e = queue.remove();
//                    ctv.startCountDownTimer(e);
//                }
//            }
//        }
//    }
//
//    private BroadcastReceiver mTimeRefreshReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (Intent.ACTION_TIME_TICK.equals(intent.getAction())) {
//                tv.setText(Utils.getCurrentDateAndWeek(getApplicationContext()));
//                htv.setCurrTimeText();
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
