package com.badoo.meetingroom.presentation.view.Dialog;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.DialogFragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.badoo.meetingroom.R;



/**
 * Created by zhangyaozhong on 14/01/2017.
 */

public class ImmersiveDialogFragment extends DialogFragment {

    public static ImmersiveDialogFragment newInstance() {
        return new ImmersiveDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_event_organizer, container, false);
        Typeface stolzlRegularTypeface = Typeface.createFromAsset(getActivity().getAssets(),"fonts/stolzl_regular.otf");
        ((TextView) view.findViewById(R.id.tv_event_period)).setTypeface(stolzlRegularTypeface);

        if (getDialog().getWindow() != null) {
            getDialog().getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            getDialog().getWindow().setBackgroundDrawableResource(R.drawable.dialog_rounded_organizer);
        }
        return view;
    }

    @SuppressLint("CommitTransaction")
    public void show(Activity activity) {

        // Show the dialog.
        FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
        Fragment prev = activity.getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        show(activity.getFragmentManager(), null);

        // It is necessary to call executePendingTransactions() on the FragmentManager
        // before hiding the navigation bar, because otherwise getWindow() would raise a
        // NullPointerException since the window was not yet created.
        getFragmentManager().executePendingTransactions();

        // Hide the navigation bar. It is important to do this after show() was called.
        // If we would do this in onCreateDialog(), we would get a requestFeature()
        // error.
        if (getDialog().getWindow() != null) {

            getDialog().getWindow().getDecorView().setSystemUiVisibility(
                getActivity().getWindow().getDecorView().getSystemUiVisibility()
            );

            // Make the dialogs window focusable again.
            getDialog().getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            );
        }
    }
}
