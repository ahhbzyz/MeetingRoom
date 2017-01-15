package com.badoo.meetingroom.presentation.view.Dialog;


import android.app.Activity;
import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.DialogFragment;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
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


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_event_organizer, container, false);
        Typeface stolzlRegularTypeface = Typeface.createFromAsset(getActivity().getAssets(),"fonts/stolzl_regular.otf");
        ((TextView) v.findViewById(R.id.tv_event_period)).setTypeface(stolzlRegularTypeface);

        if (getDialog().getWindow() != null) {
            getDialog().getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            getDialog().getWindow().setBackgroundDrawableResource(R.drawable.dialog_rounded_organizer);
        }

        return v;
    }

    public void show(Activity activity) {

        // Show the dialog.
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
        }

        // Make the dialogs window focusable again.
        getDialog().getWindow().clearFlags(
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        );

    }
}
