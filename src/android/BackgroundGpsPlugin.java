package com.tenforwardconsulting.cordova.bgloc;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;
import android.content.ServiceConnection;
import android.content.ComponentName;
import android.os.IBinder;

import com.tenforwardconsulting.cordova.bgloc.LocationUpdateServiceApi;

public class BackgroundGpsPlugin extends CordovaPlugin {
    private static final String TAG = "BackgroundGpsPlugin";

    public static final String ACTION_START = "start";							// start service
    public static final String ACTION_STOP = "stop";							// stop service
    public static final String ACTION_ISRUNNING = "isRunning";					// check if service is running
    public static final String ACTION_CONFIGURE = "configure";					// configure service
    public static final String ACTION_SET_CONFIG = "setConfig";					// reconfigure (not implemented)
	public static final String ACTION_BIND = "bind";							// bind to actual service (binding done in execute)
	public static final String ACTION_GET_SERVICE_STATUS = "getServiceStatus";	// get service status: -2 = gps not enabled, -1 service not running, >=0 service running with returned groupid

    private Intent updateServiceIntent;

    private Boolean isEnabled = false;

    private String dbname = "cordova_bg_locations";
    private Integer groupid = 0;
    private String stationaryRadius = "10";
    private String desiredAccuracy = "5";
    private String distanceFilter = "5";
    private String locationTimeout = "10";
    private String isDebugging = "false";
    private String notificationTitle = "Background tracking";
    private String notificationText = "ENABLED";
    private String activityType = "OTHER";
    private String stopOnTerminate = "false";
	
	private Boolean mBound = false;
	
	LocationUpdateServiceApi mLocationUpdateServiceApi;

    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) {
        Activity activity = this.cordova.getActivity();
        updateServiceIntent = new Intent(activity, LocationUpdateService.class);
		activity.bindService(updateServiceIntent, mConnection, 1);

        if (ACTION_START.equalsIgnoreCase(action) && !isEnabled) {
            if (groupid == null || dbname == null) {
                callbackContext.error("Call configure before calling start");
            } else {
                callbackContext.success();
                updateServiceIntent.putExtra("dbname", dbname);
                updateServiceIntent.putExtra("groupid", groupid);
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
            return true;
        } else if (ACTION_STOP.equalsIgnoreCase(action)) {
            isEnabled = false;
			activity.unbindService(mConnection);
            activity.stopService(updateServiceIntent);
            callbackContext.success();
            return true;
        } else if (ACTION_CONFIGURE.equalsIgnoreCase(action)) {
            try {
                // Params.
                //    0       1       2           		3               4                5               6            7           8                9               10             
                //[dbname, groupid, stationaryRadius, distanceFilter, locationTimeout, desiredAccuracy, debug, notificationTitle, notificationText, activityType, stopOnTerminate]
                this.dbname = data.getString(0);
                this.groupid = data.getInt(1);
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
                callbackContext.error("dbname and groupid required as parameters: " + e.getMessage());
            }
            return true;
        } else if (ACTION_SET_CONFIG.equalsIgnoreCase(action)) {
            // TODO reconfigure Service
            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, "Config set");
       		pluginResult.setKeepCallback(true);
        	callbackContext.sendPluginResult(pluginResult);
        	return true;
        } else if (ACTION_BIND.equalsIgnoreCase(action)) { 
			try {
				PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, "Service bound");
				pluginResult.setKeepCallback(true);
				while(mBound == false) {}
				callbackContext.sendPluginResult(pluginResult);
				return true;
			} catch(Exception ex) {
				Log.e(TAG, "Error binding service: " + ex.getMessage());
			}
        } else if (ACTION_GET_SERVICE_STATUS.equalsIgnoreCase(action)) { 
			try {
				Integer status = mLocationUpdateServiceApi.getServiceStatus();
				PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, status);
				pluginResult.setKeepCallback(true);
				callbackContext.sendPluginResult(pluginResult);
			} catch(Exception ex) {
				Log.e(TAG, "Error getting service status: " + ex.getMessage());
			}
        	return true;
        }

        return false;
    }

    /**
     * Override method in CordovaPlugin.
     * Checks to see if it should turn off
     */
    public void onDestroy() {
        Activity activity = this.cordova.getActivity();
		activity.unbindService(mConnection);

        if(isEnabled && stopOnTerminate.equalsIgnoreCase("true")) {
			activity.stopService(updateServiceIntent);			
        }
    }
	
	private ServiceConnection mConnection = new ServiceConnection() {
		// Called when the connection with the service is established
		public void onServiceConnected(ComponentName className, IBinder service) {
			mLocationUpdateServiceApi = LocationUpdateServiceApi.Stub.asInterface(service);
			mBound = true;
		}

		// Called when the connection with the service disconnects unexpectedly
		public void onServiceDisconnected(ComponentName className) {
			Log.e(TAG, "Service has unexpectedly disconnected");
			mLocationUpdateServiceApi = null;
			mBound = false;
		}
	};
}
