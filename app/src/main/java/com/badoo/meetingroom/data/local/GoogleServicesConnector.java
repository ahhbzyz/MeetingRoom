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

public class GoogleServicesConnector {

    private final Context mContext;

    @Inject
    GoogleServicesConnector(Context context) {
        this.mContext = context;
    }

    int getGooglePlayServicesResult() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        return apiAvailability.isGooglePlayServicesAvailable(mContext);
    }

    boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(mContext);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    boolean isUserResolvableError() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(mContext);
        return apiAvailability.isUserResolvableError(connectionStatusCode);
    }

    boolean hasPermissionToAccessContacts() {
        return EasyPermissions.hasPermissions(
            mContext, Manifest.permission.GET_ACCOUNTS);
    }
}
