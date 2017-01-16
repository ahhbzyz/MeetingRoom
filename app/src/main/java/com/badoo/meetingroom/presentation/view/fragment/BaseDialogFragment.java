package com.badoo.meetingroom.presentation.view.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.badoo.meetingroom.di.AndroidApplication;
import com.badoo.meetingroom.di.components.ApplicationComponent;
import com.badoo.meetingroom.di.components.DaggerMeetingRoomBookingComponent;
import com.badoo.meetingroom.di.components.MeetingRoomBookingComponent;
import com.badoo.meetingroom.di.modules.ActivityModule;

/**
 * Created by zhangyaozhong on 16/01/2017.
 */

public class BaseDialogFragment extends DialogFragment {

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
