package com.badoo.meetingroom.presentation.view.fragment;
import android.support.v4.app.Fragment;

import com.badoo.meetingroom.di.AndroidApplication;
import com.badoo.meetingroom.di.components.ApplicationComponent;

/**
 * Created by zhangyaozhong on 29/12/2016.
 */

public class BaseFragment extends Fragment {
    protected ApplicationComponent getApplicationComponent() {
        return ((AndroidApplication) getActivity().getApplication()).getApplicationComponent();
    }



}
