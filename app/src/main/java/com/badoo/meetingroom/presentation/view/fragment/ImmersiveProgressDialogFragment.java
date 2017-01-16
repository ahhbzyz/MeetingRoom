package com.badoo.meetingroom.presentation.view.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.os.Bundle;

import android.view.WindowManager;


/**
 * Created by zhangyaozhong on 14/01/2017.
 */

public class ImmersiveProgressDialogFragment extends DialogFragment {

    private String message;

    public static ImmersiveProgressDialogFragment newInstance() {
        return new ImmersiveProgressDialogFragment();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        if (message != null) {
            progressDialog.setMessage(message);
        }

        if (progressDialog.getWindow() != null) {
            progressDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        }

        return progressDialog;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @SuppressLint("CommitTransaction")
    public void show(Activity activity) {

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
        if (getDialog() != null && getDialog().getWindow() != null) {
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
