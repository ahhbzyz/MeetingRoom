package com.badoo.meetingroom.presentation.view.activity;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.badoo.meetingroom.R;
import com.badoo.meetingroom.presentation.presenter.GetCredentialPresenterImpl;
import com.badoo.meetingroom.presentation.view.GetCredentialView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import java.util.List;

import javax.inject.Inject;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends BaseActivity implements GetCredentialView, EasyPermissions.PermissionCallbacks {

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private final String PREF_ACCOUNT_NAME = "accountName";

    @Inject
    GetCredentialPresenterImpl mGetCredentialPresenter;

    @Inject
    GoogleAccountCredential mCredential;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getApplicationComponent().inject(this);
        mGetCredentialPresenter.setView(this);
        mGetCredentialPresenter.init();
    }

    @Override
    public boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
            GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
            apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    @Override
    public int getGooglePlayServicesStatusCode() {
        GoogleApiAvailability apiAvailability =
            GoogleApiAvailability.getInstance();
        return apiAvailability.isGooglePlayServicesAvailable(this);
    }

    @Override
    public boolean checkGooglePlayServicesAvailability(int connectionStatusCode) {
        GoogleApiAvailability apiAvailability =
            GoogleApiAvailability.getInstance();
        return !apiAvailability.isUserResolvableError(connectionStatusCode);
    }

    @Override
    public void showNoGooglePlayServicesToast() {
        Toast.makeText(getApplicationContext(), "This app requires Google Play Services.", Toast.LENGTH_SHORT).show();
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
    public boolean hasPermissionToAccessContacts() {
        return EasyPermissions.hasPermissions(
            this, Manifest.permission.GET_ACCOUNTS);
    }

    @Override
    public String getAccountNameFromLocal() {
        return getPreferences(Context.MODE_PRIVATE)
            .getString(PREF_ACCOUNT_NAME, null);
    }

    @Override
    public void storeGoogleAccountName(String accountName) {
        SharedPreferences settings =
            getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(PREF_ACCOUNT_NAME, accountName);
        editor.apply();
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
                        mCredential.setSelectedAccountName(accountName);

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
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }
}
