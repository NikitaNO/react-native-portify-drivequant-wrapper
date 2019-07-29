package com.portify.sdk;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.drivequant.sdk.model.postgeneric.Beacon;

import java.util.ArrayList;

public class PortifyBackgroundService extends Service {
    private DriveQuantSdkRunner driveQuantSdkRunner;

    private void InitializeReceivers() {
        Log.i(Constants.LOG_CAT, "InitializeReceiver");
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this.getApplication());
        TripAnalysedReceiver tripAnalysedReceiver = new TripAnalysedReceiver();
        localBroadcastManager.registerReceiver(tripAnalysedReceiver, new IntentFilter("com.drivequant.sdk.TRIP_ANALYSED"));

        BluetoothStateChangeReceiver bluetoothStateChangeReceiver = new BluetoothStateChangeReceiver();
        bluetoothStateChangeReceiver.register(this.getApplicationContext());
    }

    private void UnregisterReceivers() {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this.getApplication());
        TripAnalysedReceiver tripAnalysedReceiver = new TripAnalysedReceiver();
        localBroadcastManager.unregisterReceiver(tripAnalysedReceiver);

        BluetoothStateChangeReceiver bluetoothStateChangeReceiver = new BluetoothStateChangeReceiver();
        bluetoothStateChangeReceiver.unregister(this.getApplicationContext());
    }

    @Override
    public void onCreate() {
        Log.i(Constants.LOG_CAT, "onCreate()");
        NotificationHelper.createNotificationChannelIfNeeded((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));

        // If we are running "Oreo" (or later..) we start the service here because otherwise we will get a crash..
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int NOTIFICATION_ID = 12345689;
            startForeground(NOTIFICATION_ID, NotificationHelper.getCompatNotification(getApplication(), getApplicationContext()));
        }

        InitializeReceivers();
        driveQuantSdkRunner = new DriveQuantSdkRunner(this.getApplication());
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        // check if initialize and remove listener?
        Log.i(Constants.LOG_CAT, "destroy");
        UnregisterReceivers();
        driveQuantSdkRunner = null;
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            NotificationHelper.createNotificationChannelIfNeeded((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));

            if(intent.getAction().equals(Constants.START_FOREGROUND_ACTION)) {
                // If we are running "pre-Oreo" we start the service here..
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) {
                    Log.i(Constants.LOG_CAT, "Pre-Oreo, starting service early..");
                    int NOTIFICATION_ID = 12345689;
                    startForeground(NOTIFICATION_ID, NotificationHelper.getCompatNotification(getApplication(), getApplicationContext()));
                }

                Log.i(Constants.LOG_CAT, "PortifyBackgroundService onStartCommand with intent START_FOREGROUND_ACTION");
                String user = intent.getStringExtra("user");
                ArrayList<String> beaconStrings = intent.getStringArrayListExtra("beacons");

                ArrayList<Beacon> beacons = new ArrayList<>();
                for(String beaconString : beaconStrings) {
                    String[] parts = beaconString.split(":");
                    Beacon beacon = new Beacon();
                    beacon.setProximityUuid(parts[0]);
                    beacon.setMajor(Integer.parseInt(parts[1]));
                    beacon.setMinor(Integer.parseInt(parts[2]));
                    beacons.add(beacon);
                }

                driveQuantSdkRunner.SetUser(user);
                driveQuantSdkRunner.AddBeacon(beacons);
                driveQuantSdkRunner.InitiateSdk();
            }

            if(intent.getAction().equals(Constants.STOP_FOREGROUND_ACTION)) {
                Log.i(Constants.LOG_CAT, "PortifyBackgroundService onStartCommand with intent STOP_FOREGROUND_ACTION");
                if (driveQuantSdkRunner != null) {
                    driveQuantSdkRunner.destroySdkRunner();
                    Log.i(Constants.LOG_CAT, "SDK runner destroyed.");
                    driveQuantSdkRunner = null;
                }
                stopForeground(true);
                stopSelf();
            }
        } catch (Exception e) {
            Log.e(Constants.LOG_CAT, "BACKGROUND SERVICE onStartCommand EXCEPTION " + e.getMessage());
            stopForeground(true);
            stopSelf();
        }

        return START_REDELIVER_INTENT;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
