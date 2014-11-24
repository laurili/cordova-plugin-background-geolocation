package com.tenforwardconsulting.cordova.bgloc;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;

public class BackgroundGpsPlugin extends CordovaPlugin {
    private static final String TAG = "BackgroundGpsPlugin";

    public static final String ACTION_START = "start";
    public static final String ACTION_STOP = "stop";
    public static final String ACTION_CONFIGURE = "configure";
    public static final String ACTION_SET_CONFIG = "setConfig";

    private Intent updateServiceIntent;

    private Boolean isEnabled = false;

    private String dbname = "cordova_bg_locations";
    private String routeid = "1";
    private String stationaryRadius = "30";
    private String desiredAccuracy = "100";
    private String distanceFilter = "30";
    private String locationTimeout = "60";
    private String isDebugging = "false";
    private String notificationTitle = "Background tracking";
    private String notificationText = "ENABLED";
    private String activityType = "OTHER";
    private String stopOnTerminate = "false";

    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) {
        Activity activity = this.cordova.getActivity();
        Boolean result = false;
        updateServiceIntent = new Intent(activity, LocationUpdateService.class);

        if (ACTION_START.equalsIgnoreCase(action) && !isEnabled) {
            result = true;
            if (routeid == null || dbname == null) {
                callbackContext.error("Call configure before calling start");
            } else {
                callbackContext.success();
                updateServiceIntent.putExtra("dbname", dbname);
                updateServiceIntent.putExtra("routeid", routeid);
                updateServiceIntent.putExtra("stationaryRadius", stationaryRadius);
                updateServiceIntent.putExtra("desiredAccuracy", desiredAccuracy);
                updateServiceIntent.putExtra("distanceFilter", distanceFilter);
                updateServiceIntent.putExtra("locationTimeout", locationTimeout);
                updateServiceIntent.putExtra("desiredAccuracy", desiredAccuracy);
                updateServiceIntent.putExtra("isDebugging", isDebugging);
                updateServiceIntent.putExtra("notificationTitle", notificationTitle);
                updateServiceIntent.putExtra("notificationText", notificationText);
                updateServiceIntent.putExtra("activityType", activityType);
                updateServiceIntent.putExtra("stopOnTerminate", stopOnTerminate);

                activity.startService(updateServiceIntent);
                isEnabled = true;
            }
        } else if (ACTION_STOP.equalsIgnoreCase(action)) {
            isEnabled = false;
            result = true;
            activity.stopService(updateServiceIntent);
            callbackContext.success();
        } else if (ACTION_CONFIGURE.equalsIgnoreCase(action)) {
            result = true;
            try {
                // Params.
                //    0       1       2           		3               4                5               6            7           8                9               10             
                //[dbname, routeid, stationaryRadius, distanceFilter, locationTimeout, desiredAccuracy, debug, notificationTitle, notificationText, activityType, stopOnTerminate]
                this.dbname = data.getString(0);
                this.routeid = data.getString(1);
                this.stationaryRadius = data.getString(2);
                this.distanceFilter = data.getString(3);
                this.locationTimeout = data.getString(4);
                this.desiredAccuracy = data.getString(5);
                this.isDebugging = data.getString(6);
                this.notificationTitle = data.getString(7);
                this.notificationText = data.getString(8);
                this.activityType = data.getString(9);
                this.stopOnTerminate = data.getString(10);
            } catch (JSONException e) {
                callbackContext.error("dbname and routeid required as parameters: " + e.getMessage());
            }
        } else if (ACTION_SET_CONFIG.equalsIgnoreCase(action)) {
            result = true;
            // TODO reconfigure Service
            callbackContext.success();
        }

        return result;
    }

    /**
     * Override method in CordovaPlugin.
     * Checks to see if it should turn off
     */
    public void onDestroy() {
        Activity activity = this.cordova.getActivity();

        if(isEnabled && stopOnTerminate.equalsIgnoreCase("true")) {
            activity.stopService(updateServiceIntent);
        }
    }
}
