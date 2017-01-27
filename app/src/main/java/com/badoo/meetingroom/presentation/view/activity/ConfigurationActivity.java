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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.badoo.meetingroom.R;
import com.badoo.meetingroom.presentation.Badoo;
import com.badoo.meetingroom.presentation.model.intf.RoomModel;
import com.badoo.meetingroom.presentation.presenter.intf.ConfigurationPresenter;
import com.badoo.meetingroom.presentation.view.view.ConfigurationView;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.EasyPermissions;

public class ConfigurationActivity extends BaseActivity implements ConfigurationView, EasyPermissions.PermissionCallbacks {

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    @Inject
    ConfigurationPresenter mPresenter;

    @Inject
    GoogleAccountCredential mCredential;

    @BindView(R.id.layout_google_services_info) CoordinatorLayout mGoogleServicesInfoLayout;
    @BindView(R.id.pb_loading_data) ProgressBar mLoadingDataBar;
    @BindView(R.id.spinner_room_list) Spinner mRoomListSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);
        ButterKnife.bind(this);

        getComponent().inject(this);
        mPresenter.setView(this);
        mPresenter.init();
        findViewById(R.id.btn_status).setEnabled(false);
        findViewById(R.id.btn_status).setOnClickListener(view -> {
            if (Badoo.getCurrentRoom() == null) {
                // Toast
                return;
            }
            mPresenter.finishConfigurations();

        });
    }



    @Override
    public void setUpRoomListSpinner(List<RoomModel> roomModelList) {
        String [] roomNameArray = new String[roomModelList.size()];
        for (int i = 0; i < roomModelList.size(); i++) {
            roomNameArray[i] = roomModelList.get(i).getName();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roomNameArray);
        mRoomListSpinner.setAdapter(adapter);
        mRoomListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Badoo.setCurrentRoom(roomModelList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Badoo.setCurrentRoom(roomModelList.get(0));
            }
        });
        findViewById(R.id.btn_status).setEnabled(true);
    }

    @Override
    public void showRoomStatusView() {
        Intent intent = new Intent(this, RoomStatusActivity.class);
        startActivity(intent);
    }

    @Override
    public void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
            ConfigurationActivity.this,
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
    public void showRecoverableAuth(UserRecoverableAuthIOException e) {
        this.startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
    }

    @Override
    public void showConnectGoogleCalendarSuccessful() {
        Toast.makeText(this, "Connected with Google Calendar", Toast.LENGTH_SHORT).show();
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
                    mPresenter.onNoGooglePlayServicesError();
                } else {
                    mPresenter.init();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        mPresenter.storeGoogleAccountName(accountName);
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    mPresenter.init();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        mPresenter.init();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
    }


    @Override
    public void showLoadingData(String message) {
        this.mLoadingDataBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void dismissLoadingData() {
        this.mLoadingDataBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public Context context() {
        return this.getApplicationContext();
    }

    @Override
    protected void onSystemTimeRefresh() {

    }

    @Override
    protected void onCalendarUpdate() {
        super.onCalendarUpdate();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }
}
