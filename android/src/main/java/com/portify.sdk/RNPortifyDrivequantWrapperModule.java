
package com.portify.sdk;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.drivequant.sdk.model.TripNotification;
import com.drivequant.sdk.model.postgeneric.Vehicle;
import com.drivequant.sdk.tripanalysis.DriveQuantTripAnalysis;
import com.drivequant.sdk.tripanalysis.exception.SDKNotInitializedException;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.soloader.SoLoader;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import java.util.ArrayList;

import static com.drivequant.sdk.tripanalysis.DriveQuantTripAnalysis.startTrip;


public class RNPortifyDrivequantWrapperModule extends ReactContextBaseJavaModule implements ActivityEventListener, LifecycleEventListener {

    private final ReactApplicationContext reactContext;
    public static ReactApplicationContext staticReactContext;
    private final Context context;

    private String username;
    private ArrayList<String> beaconStrings = new ArrayList<>();

    private static final int PERMISSION_ACCESS_FINE_LOCATION=1;

    public RNPortifyDrivequantWrapperModule(ReactApplicationContext reactContext) {
        super(reactContext);
        reactContext.addActivityEventListener(this);
        reactContext.addLifecycleEventListener(this);

        this.reactContext = reactContext;
        staticReactContext = reactContext;
        context = reactContext.getApplicationContext();

        Log.i(Constants.LOG_CAT, "ctor RNPortifyDrivequantWrapperModule");
        SoLoader.init(reactContext.getApplicationContext(), false);
    }

    @Override
    public String getName() {
        return "RNPortifyDrivequantWrapper";
    }

    // JavaScript bridge
    public static void sendEvent(String event, WritableNativeMap params) {
        Log.i(Constants.LOG_CAT, "Sending event over JS bridge");

        if(staticReactContext == null) {
            Log.i(Constants.LOG_CAT, "staticReactContext was null while trying to use JS bridge.");
            return;
        }

        staticReactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(event, params);
    }

    @ReactMethod
    public void VerifyGooglePlayService(final Promise promise) {
        Log.i(Constants.LOG_CAT, "VerifyGooglePlayService");
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            promise.resolve(false);
        } else {
            promise.resolve(true);
        }
    }

    @ReactMethod
    public void VerifyAccessFineLocation(final Promise promise){
        Log.i(Constants.LOG_CAT, "VerifyAccessFineLocation");

        Activity currentActivity = getCurrentActivity();
        if(currentActivity == null){
            Log.i(Constants.LOG_CAT, "currentActivity is null");
            promise.resolve(false);
            return;
        }

        ActivityCompat.requestPermissions(getCurrentActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ACCESS_FINE_LOCATION);

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            promise.resolve(false);
        } else {
            promise.resolve(true);
        }
    }

    // Should be done before InitializeBackgroundService
    @ReactMethod
    public void WithUser(String username, final Promise promise) {
        Log.i(Constants.LOG_CAT, "WithUser");
        this.username = username;
        promise.resolve(username);
        Log.i(Constants.LOG_CAT, "Setting user to " + username);
    }

    // Should be done before InitializeBackgroundService
    @ReactMethod
    public void AddBeacon(String uuid, int major, int minor, final Promise promise) {
        Log.i(Constants.LOG_CAT, "AddBeacon major/minor: " + uuid + ":" + major + ":" + minor);
        this.beaconStrings.add(uuid + ":" + major + ":" + minor);
        promise.resolve(true);
    }

    @ReactMethod
    public void startTrip(String userId, ReadableMap userVehicle, ReadableMap userNotification, boolean startForeground) {
        try{
            Vehicle vehicle = createVehicle(userVehicle);
            TripNotification notification = createForegroundNotification(userNotification);

            DriveQuantTripAnalysis.startTrip(userId, vehicle, context, startForeground, notification);
            Log.i(Constants.LOG_CAT, "Trip was started.");
        } catch(SDKNotInitializedException sde) {
            Log.e(Constants.LOG_CAT, "startTrip could not be unbound. SDKNotInitialized. " + sde.getMessage());
        }
    }

    private TripNotification createForegroundNotification(ReadableMap userNotification) {
        String content = userNotification.getString("content");
        String cancel = userNotification.getString("cancel");
        int iconId = userNotification.getInt("iconId");
        int cancelIconId = userNotification.getInt("cancelIconId");
        int noGpsIconId = userNotification.getInt("noGpsIconId");
        boolean enableCancel = userNotification.getBoolean("enableCancel");
        String gpsAccuracyButtonContent = userNotification.getString("gpsAccuracyButtonContent");
        String gpsAccuracyContent = userNotification.getString("gpsAccuracyContent");

        TripNotification tripNotification = new TripNotification(context.getString(R.string.app_name), content, cancel, iconId, cancelIconId, enableCancel);
        tripNotification.setNoGpsIconId(noGpsIconId);
        tripNotification.setGpsAccuracyContent(gpsAccuracyContent);
        tripNotification.setGpsAccuracyButtonContent(gpsAccuracyButtonContent);
        tripNotification.setEnableCancel(enableCancel);

        return tripNotification;
    }

    private Vehicle createVehicle(ReadableMap userVehicle) {
        Vehicle vehicle = new Vehicle();
        vehicle.setCarConsumption(userVehicle.getInt("carConsumption"));
        vehicle.setCarEngineIndex(userVehicle.getInt("carEngineIndex"));
        vehicle.setCarGearboxIndex(userVehicle.getInt("carGearboxIndex"));
        vehicle.setCarMass(userVehicle.getInt("carMass"));
        vehicle.setCarPower(userVehicle.getInt("carPower"));
        vehicle.setCarTypeIndex(userVehicle.getInt("carTypeIndex"));
        vehicle.setCarPassengers(userVehicle.getInt("carPassengers"));

        return vehicle;
    }


    @ReactMethod
    public void stopTrip() {
        try{
            DriveQuantTripAnalysis.stopTrip(context);
            Log.i(Constants.LOG_CAT, "Trip was started.");
        } catch(SDKNotInitializedException sde) {
            Log.e(Constants.LOG_CAT, "startTrip could not be unbound. SDKNotInitialized. " + sde.getMessage());
        }
    }


    // This should be done on login
    @ReactMethod
    public void InitializeBackgroundService(final Promise promise) {
        Log.i(Constants.LOG_CAT, "Entering InitializeBackgroundService");

        if(IsServiceInitialized()) {
            Log.i(Constants.LOG_CAT, "Service was already initialized, resolving promise to false");
            promise.resolve(true);
            return;
        }

        if(this.username.equals("") || this.beaconStrings.size() == 0) {
            Log.i(Constants.LOG_CAT, "User or beacon not set, resolving promise to false");
            promise.resolve(false);
            return;
        }

        Log.i(Constants.LOG_CAT, "Service will be initialized");

        // Replace storage information with the user currently logging in
        StorageHelper.ReplaceServiceStartInformationIfNeeded(context, this.username, this.beaconStrings);

        Intent startServiceIntent = new Intent(context, PortifyBackgroundService.class);
        startServiceIntent.setAction(Constants.START_FOREGROUND_ACTION);

        // Add our "extra data"
        startServiceIntent.putExtra("user", this.username);
        startServiceIntent.putStringArrayListExtra("beacons", this.beaconStrings);

        ContextCompat.startForegroundService(context, startServiceIntent);
        Log.i(Constants.LOG_CAT, "Service was started, resolving promise to true");
        promise.resolve(true);
    }

    @ReactMethod
    public void StopBackgroundService(final Promise promise){
        StorageHelper.ClearRestartInformation(context);
        Intent stopServiceIntent = new Intent(context, PortifyBackgroundService.class);
        stopServiceIntent.setAction(Constants.STOP_FOREGROUND_ACTION);
        ContextCompat.startForegroundService(context, stopServiceIntent);
        promise.resolve(true);
    }

    private boolean IsServiceInitialized() {
        Log.i(Constants.LOG_CAT, "Entering IsServiceInitialized");
        ActivityManager manager = (ActivityManager)getReactApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        try{
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (PortifyBackgroundService.class.getName().equals(service.service.getClassName())) {
                    Log.i(Constants.LOG_CAT,"Portify foregroundservice is running");
                    Log.i(Constants.LOG_CAT,"Returning value of service.foreground AND service.started: " + (service.foreground && service.started));
                    return service.foreground && service.started;
                }
            }
        }catch(Exception e) {
            Log.e(Constants.LOG_CAT,"Something went wrong: " + e.getMessage());
        }

        Log.i(Constants.LOG_CAT,"Service was not running");
        return false;
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        Log.i(Constants.LOG_CAT, "onActivityResult, reactContext null? " + (reactContext == null));
    }

    @Override
    public void onNewIntent(Intent intent) {
        Log.i(Constants.LOG_CAT, "onNewIntent, reactContext null? " + (reactContext == null));
    }

    @Override
    public void onHostResume() {
        Log.i(Constants.LOG_CAT, "onHostResume, reactContext null? " + (reactContext == null));
    }

    @Override
    public void onHostPause() {
        Log.i(Constants.LOG_CAT, "onHostPause, reactContext null? " + (reactContext == null));
    }

    @Override
    public void onHostDestroy() {
        Log.i(Constants.LOG_CAT, "onHostDestroy, reactContext null? " + (reactContext == null));
    }
}
