package com.byteshaft.kidmonitor.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.location.LocationManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import com.byteshaft.kidmonitor.AppGlobals;
import com.byteshaft.kidmonitor.constants.AppConstants;
import com.byteshaft.kidmonitor.services.UploadService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Helpers {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private static Context context = AppGlobals.getContext();

    public static TelephonyManager getTelephonyManager() {
        return (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    public static boolean isAnyLocationServiceAvailable() {
        LocationManager locationManager = getLocationManager();
        return isGpsEnabled(locationManager) || isNetworkBasedGpsEnabled(locationManager);
    }

    private static LocationManager getLocationManager() {
        return (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    static boolean isGpsEnabled(LocationManager locationManager) {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private static boolean isNetworkBasedGpsEnabled(LocationManager locationManager) {
        return locationManager.isProviderEnabled((LocationManager.NETWORK_PROVIDER));
    }

    public static String getTimeStamp() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        return simpleDateFormat.format(calendar.getTime());
    }

    public static String getFileExtensionForType(String contentType) {
        switch (contentType) {
            case AppConstants.TYPE_SOUND_RECORDINGS:
            case AppConstants.TYPE_CALL_RECORDINGS:
                return ".aac";
            case AppConstants.TYPE_VIDEO_RECORDINGS:
                return ".mp4";
            default:
                return ".unknown";
        }
    }

    public static String getCurrentDateandTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyy h:mm a zz");
        return sdf.format(new Date());
    }

    public static void checkInternetAndUploadPendingData() {
        Context context = AppGlobals.getContext();
        context.startService(new Intent(context, UploadService.class));
    }

    public static void setOrientation(Camera.Parameters parameters) {
        Display display = ((WindowManager) AppGlobals.getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        switch (display.getRotation()) {
            case Surface.ROTATION_0:
                Log.i("SPY", "0");
                parameters.setRotation(90);
                break;
            case Surface.ROTATION_90:
                Log.i("SPY", "90");
                break;
            case Surface.ROTATION_180:
                Log.i("SPY", "180");
                break;
            case Surface.ROTATION_270:
                Log.i("SPY", "270");
                parameters.setRotation(180);
        }
    }

    public static int getBitRateForResolution(int width, int height) {
        // Not perfect but gets use there.
        return (width * height) * 6;
    }

    public static String getDeviceIdentifier() {

        return getDeviceMakeModel() + "-" + getIMEI();
    }

    public static boolean isTokenSent() {
        return AppGlobals.getPreferenceManager().getBoolean("token_sent", false);
    }

    public static String getIMEI() {
        TelephonyManager telephonyManager = (TelephonyManager) AppGlobals.getContext().getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }

    public static String getDeviceMakeModel() {
        return Build.MANUFACTURER + "-" + Build.MODEL;
    }

    public static boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(AppGlobals.getContext());
        return resultCode == ConnectionResult.SUCCESS;
    }
}