package com.badoo.meetingroom.presentation.view.activity;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ProgressBar;

import com.badoo.meetingroom.R;
import com.badoo.meetingroom.presentation.presenter.impl.GetCredentialPresenterImpl;
import com.badoo.meetingroom.presentation.view.GetCredentialView;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends BaseActivity implements GetCredentialView, EasyPermissions.PermissionCallbacks {

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    @Inject
    GetCredentialPresenterImpl mGetCredentialPresenter;

    @Inject
    GoogleAccountCredential mCredential;

    @BindView(R.id.layout_google_services_info) CoordinatorLayout mGoogleServicesInfoLayout;
    @BindView(R.id.pb_loading_data) ProgressBar mLoadingDataBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        this.getApplicationComponent().inject(this);
        mGetCredentialPresenter.setView(this);
        mGetCredentialPresenter.init();

        findViewById(R.id.btn_status).setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, RoomEventsActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.btn_cal).setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, RoomEventsActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
            MainActivity.this,
            connectionStatusCode,
            REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    @Override
    public void showAccountNameOnSnackBar(String accountName) {
        mCredential.setSelectedAccountName(accountName);
        Snackbar.make(mGoogleServicesInfoLayout,
            "You logged as " + mCredential.getSelectedAccountName(),
            Snackbar.LENGTH_LONG)
            .show();
    }

    @Override
    public void showNoGooglePlayServicesOnSnackBar() {
        Snackbar.make(mGoogleServicesInfoLayout,
            "No Google Play Services on your device",
            Snackbar.LENGTH_SHORT).show();
    }


    @Override
    public void showChooseAccountDialog() {
        startActivityForResult(
            mCredential.newChooseAccountIntent(),
            REQUEST_ACCOUNT_PICKER);
    }

    @Override
    public void showRequestPermissionsDialog() {
        EasyPermissions.requestPermissions(
            this,
            "This app needs to access your Google account (via Contacts).",
            REQUEST_PERMISSION_GET_ACCOUNTS,
            Manifest.permission.GET_ACCOUNTS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    mGetCredentialPresenter.onNoGooglePlayServicesError();
                } else {
                    mGetCredentialPresenter.init();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        mGetCredentialPresenter.storeGoogleAccountName(accountName);
                        mGetCredentialPresenter.init();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    mGetCredentialPresenter.init();
                }
                break;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
            requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        mGetCredentialPresenter.init();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        //mGetCredentialPresenter.init();
    }

    @Override
    public void showLoadingData(boolean visibility) {
        if (visibility) {
            this.mLoadingDataBar.setVisibility(View.VISIBLE);
        } else {
            this.mLoadingDataBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void showRetryLoading(boolean visibility) {

    }

    @Override
    public void showError(String message) {

    }

    @Override
    public Context context() {
        return this.getApplicationContext();
    }
}
