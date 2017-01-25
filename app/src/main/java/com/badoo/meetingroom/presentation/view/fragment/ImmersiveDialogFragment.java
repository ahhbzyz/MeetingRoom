package com.badoo.meetingroom.presentation.view.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.view.WindowManager;

/**
 * Created by zhangyaozhong on 16/01/2017.
 */

public abstract class ImmersiveDialogFragment extends BaseDialogFragment{

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
        if (!this.isAdded()) {
            getFragmentManager().executePendingTransactions();
        }

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
