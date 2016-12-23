package com.badoo.meetingroom.data.local;

import android.Manifest;
import android.content.Context;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import javax.inject.Inject;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by zhangyaozhong on 23/12/2016.
 */

public class GoogleServiceConnector {

    private final Context mContext;

    @Inject
    public GoogleServiceConnector(Context context) {
        this.mContext = context;
    }

    public int getGooglePlayServicesResult() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        return apiAvailability.isGooglePlayServicesAvailable(mContext);
    }

    public boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(mContext);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    public boolean acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(mContext);
        return !apiAvailability.isUserResolvableError(connectionStatusCode);
    }

    public boolean hasPermissionToAccessContacts() {
        return EasyPermissions.hasPermissions(
            mContext, Manifest.permission.GET_ACCOUNTS);
    }
}
