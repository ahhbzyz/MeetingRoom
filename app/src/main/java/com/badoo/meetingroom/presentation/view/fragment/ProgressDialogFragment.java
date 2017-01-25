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

public class ProgressDialogFragment extends ImmersiveDialogFragment {

    private String message;

    public static ProgressDialogFragment newInstance() {
        return new ProgressDialogFragment();
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
}
