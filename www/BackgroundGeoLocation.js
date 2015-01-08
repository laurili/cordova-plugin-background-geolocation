var exec = require("cordova/exec");
module.exports = {
    /**
    * @property {Object} stationaryRegion
    */
    stationaryRegion: null,
    /**
    * @property {Object} config
    */
    config: {},

    configure: function(success, failure, config) {
        this.config = config;
        var dbname		        = config.dbname || "cordova_bg_locations", // SQLite database name
            groupid             = config.groupid || -2, 				   // groupid, integer to group locations, must be <> -1
            stationaryRadius    = (config.stationaryRadius >= 0) ? config.stationaryRadius : 10, // meters
            distanceFilter      = (config.distanceFilter >= 0) ? config.distanceFilter : 5,    // meters
            locationTimeout     = (config.locationTimeout >= 0) ? config.locationTimeout : 10,      // seconds
            desiredAccuracy     = (config.desiredAccuracy >= 0) ? config.desiredAccuracy : 5,    // meters
            debug               = config.debug || false,
            notificationTitle   = config.notificationTitle || "Background tracking",
            notificationText    = config.notificationText || "ENABLED";
            activityType        = config.activityType || "OTHER";
            stopOnTerminate     = config.stopOnTerminate || false;

        exec(success || function() {},
             failure || function() {},
             'BackgroundGeoLocation',
             'configure',
             [dbname, groupid, stationaryRadius, distanceFilter, locationTimeout, desiredAccuracy, debug, notificationTitle, notificationText, activityType, stopOnTerminate]
        );
    },
    bind: function(success) {
    	if (device.platform == "Android") {
    		exec(success || function() {},
    	    	function() {},
    	    	'BackgroundGeoLocation',
    	    	'bind',
    	    	[]);
    	} else {
   			if (success) {
   				success();
   			}
    	}
    	
    },
	start: function(success, failure, config) {
        exec(success || function() {},
             failure || function() {},
             'BackgroundGeoLocation',
             'start',
             []);
    },
    stop: function(success, failure, config) {
        exec(success || function() {},
            failure || function() {},
            'BackgroundGeoLocation',
            'stop',
            []);
    },
	getServiceStatus: function(success,failure) {
    	exec(success || function() {},
    		failure || function() {},
    		'BackgroundGeoLocation',
    		'getServiceStatus',
    		[]);
    },
    finish: function(success, failure) {
        exec(success || function() {},
            failure || function() {},
            'BackgroundGeoLocation',
            'finish',
            []);
    },
    changePace: function(isMoving, success, failure) {
        exec(success || function() {},
            failure || function() {},
            'BackgroundGeoLocation',
            'onPaceChange',
            [isMoving]);
    },
    /**
    * @param {Integer} stationaryRadius
    * @param {Integer} desiredAccuracy
    * @param {Integer} distanceFilter
    * @param {Integer} timeout
    */
    setConfig: function(success, failure, config) {
        this.apply(this.config, config);
        exec(success || function() {},
            failure || function() {},
            'BackgroundGeoLocation',
            'setConfig',
            [config]);
    },
    /**
    * Returns current stationaryLocation if available.  null if not
    */
    getStationaryLocation: function(success, failure) {
        exec(success || function() {},
            failure || function() {},
            'BackgroundGeoLocation',
            'getStationaryLocation',
            []);
    },
    /**
    * Add a stationary-region listener.  Whenever the devices enters "stationary-mode", your #success callback will be executed with #location param containing #radius of region
    * @param {Function} success
    * @param {Function} failure [optional] NOT IMPLEMENTED
    */
    onStationary: function(success, failure) {
        var me = this;
        success = success || function() {};
        var callback = function(region) {
            me.stationaryRegion = region;
            success.apply(me, arguments);
        };
        exec(callback,
            failure || function() {},
            'BackgroundGeoLocation',
            'addStationaryRegionListener',
            []);
    },
    apply: function(destination, source) {
        source = source || {};
        for (var property in source) {
            if (source.hasOwnProperty(property)) {
                destination[property] = source[property];
            }
        }
        return destination;
    }
};
