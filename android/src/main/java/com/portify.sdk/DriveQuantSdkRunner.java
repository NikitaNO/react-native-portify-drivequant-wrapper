package com.portify.sdk;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.util.Log;

import com.drivequant.sdk.model.EfficiencyData;
import com.drivequant.sdk.model.RoutePoint;
import com.drivequant.sdk.model.SafetyData;
import com.drivequant.sdk.model.TripNotification;
import com.drivequant.sdk.model.postgeneric.Beacon;
import com.drivequant.sdk.model.postgeneric.Vehicle;
import com.drivequant.sdk.tripanalysis.DriveQuantTripAnalysis;

import com.drivequant.sdk.tripanalysis.exception.SDKNotInitializedException;
import com.drivequant.sdk.tripanalysis.listener.TripAnalysisListener;
import com.drivequant.sdk.tripanalysis.service.BeaconScanService;

import java.util.ArrayList;
import java.util.List;

public class DriveQuantSdkRunner {
    private String username;
    private List<Beacon> beacons = new ArrayList<>();
    private Application application;
    private TripAnalysisListener tripAnalysisListener = new TripAnalysisListener() {
        @Override
        public void onNewPoint(RoutePoint routePoint, SafetyData safetyData, EfficiencyData efficiencyData) {
            Log.i(Constants.LOG_CAT, "Data received");
        }

        @Override
        public void onServiceBounded(int i) {
            Log.i(Constants.LOG_CAT, "TripAnalysisListener.onServiceBounded() - Listening for trips.");
        }
    };

    public DriveQuantSdkRunner(Application application) {
        this.application = application;
    }

    public void AddBeacon(ArrayList<Beacon> beacons) {
        Log.i(Constants.LOG_CAT, "Added " + beacons.size() + " beacons");
        this.beacons = beacons;
    }

    public void SetUser(String username) {
        Log.i(Constants.LOG_CAT, "Setting username in SDK Runner to " + username);
        this.username = username;
    }

    private void bindTripAnalysisListener() {
        try{
            DriveQuantTripAnalysis.bindService(tripAnalysisListener, application);
            Log.i(Constants.LOG_CAT, "TripAnalysisListener was bound.");
        } catch(SDKNotInitializedException sde) {
            Log.e(Constants.LOG_CAT, "TripAnalysisListener could not be bound. SDKNotInitialized. " + sde.getMessage());
        }
    }

    public void destroySdkRunner() {
        unbindTripAnalysisListener();
        destroyDriveQuantTripAnalysis();
    }

    private void destroyDriveQuantTripAnalysis() {
        try {
            DriveQuantTripAnalysis.stopBLEScan();
            Log.i(Constants.LOG_CAT, "Stopped BLE Scan");
            DriveQuantTripAnalysis.stopScan();
            Log.i(Constants.LOG_CAT, "Stopped Scan");
            DriveQuantTripAnalysis.setBeaconRequired(false);
            DriveQuantTripAnalysis.deactivateAutoStart();
        }catch(SDKNotInitializedException sde) {
            Log.e(Constants.LOG_CAT, "Could not deactivate autostart. " + sde.getMessage());
        }
    }

    private void unbindTripAnalysisListener()  {
        try{
            DriveQuantTripAnalysis.unbindService(application);
            Log.i(Constants.LOG_CAT, "TripAnalysisListener was unbound.");
        } catch(SDKNotInitializedException sde) {
            Log.e(Constants.LOG_CAT, "TripAnalysisListener could not be unbound. SDKNotInitialized. " + sde.getMessage());
        }
    }

    public boolean InitiateSdk() {
        // Maybe send a "command" back that it has been initialized
        DriveQuantTripAnalysis.init(application, beacons);
        try {
            DriveQuantTripAnalysis.setUserId(username);

            // Take service "out" and unbind it on destroy
            bindTripAnalysisListener();
            DriveQuantTripAnalysis.setBeaconRequired(true);
            DriveQuantTripAnalysis.setDefaultVehicle(Vehicle.getDefaultVehicule());
            DriveQuantTripAnalysis.setStopTimeout(240); // Maybe test with timeout on up to 480 (min)
            DriveQuantTripAnalysis.activateAutoStart(true, createForegroundTripNotification(), beacons);
            return true;
        } catch (SDKNotInitializedException e) {
            Log.e(Constants.LOG_CAT, "SDKNotInitializedException " + e.getMessage());
            return false;
        }
    }

    private TripNotification createForegroundTripNotification(){
        int iconId = application.getResources().getIdentifier("ic_launcher", "mipmap", application.getApplicationContext().getPackageName());
        String appName = "FIP Drive";
        String notificationText = "..analyserar pågående resa";

        TripNotification tripNotification = new TripNotification(appName, notificationText, iconId, false);
        return tripNotification;
    }
}
