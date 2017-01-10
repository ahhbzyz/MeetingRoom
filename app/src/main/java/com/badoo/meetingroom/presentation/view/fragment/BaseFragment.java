package com.badoo.meetingroom.presentation.view.fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.badoo.meetingroom.di.AndroidApplication;
import com.badoo.meetingroom.di.components.ApplicationComponent;
import com.badoo.meetingroom.di.components.DaggerMeetingRoomBookingComponent;
import com.badoo.meetingroom.di.components.MeetingRoomBookingComponent;
import com.badoo.meetingroom.di.modules.ActivityModule;

/**
 * Created by zhangyaozhong on 29/12/2016.
 */

public class BaseFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected ApplicationComponent getApplicationComponent() {
        return ((AndroidApplication) getActivity().getApplication()).getApplicationComponent();
    }

    protected MeetingRoomBookingComponent getComponent() {
        return DaggerMeetingRoomBookingComponent
            .builder()
            .applicationComponent(getApplicationComponent())
            .activityModule(new ActivityModule(getActivity()))
            .build();
    }

}
