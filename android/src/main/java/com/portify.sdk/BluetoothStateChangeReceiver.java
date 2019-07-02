package com.portify.sdk;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;

public class BluetoothStateChangeReceiver extends BroadcastReceiver {
    public boolean isRegistered;

    public Intent register(Context context) {
        try {
            return !isRegistered ? context.registerReceiver(this, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)) : null;
        } finally {
            isRegistered = true;
        }
    }

    public boolean unregister(Context context) {
        return isRegistered && unregisterInternal(context);
    }

    private boolean unregisterInternal(Context context) {
        try{
            context.unregisterReceiver(this);
        }catch(IllegalArgumentException iae) {
            Log.e(Constants.LOG_CAT, "Exception unregistrering service " + iae.getMessage());
        }

        isRegistered = false;
        return true;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Application application = ((Application)context.getApplicationContext());
        Context baseContext = context.getApplicationContext();

        if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())) {
            if(intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_TURNING_OFF) {
                Intent stopServiceIntent = new Intent(baseContext, PortifyBackgroundService.class);
                stopServiceIntent.setAction(Constants.STOP_FOREGROUND_ACTION);
                ContextCompat.startForegroundService(baseContext, stopServiceIntent);
            }

            if(intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_TURNING_ON) {

            }

            if(intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_OFF) {
                Intent stopServiceIntent = new Intent(baseContext, PortifyBackgroundService.class);
                stopServiceIntent.setAction(Constants.STOP_FOREGROUND_ACTION);
                ContextCompat.startForegroundService(baseContext, stopServiceIntent);

                NotificationManager notificationManager = (NotificationManager) application.getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationHelper.createNotificationChannelIfNeeded(notificationManager);
                Notification bluetoothNotification = NotificationHelper.getBluetoothNotification(application, baseContext);
                notificationManager.notify(123456, bluetoothNotification);
            }

            if(intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_ON) {
                Log.i(Constants.LOG_CAT, "Received intent to start service after bluetooth toggled on");
                String user = StorageHelper.LoadUserFromStorage(baseContext);
                Log.i(Constants.LOG_CAT, "User was loaded " + user);
                if(!user.equals("") || user.toLowerCase().equals("false")) {
                    Intent startServiceIntent = new Intent(baseContext, PortifyBackgroundService.class);
                    startServiceIntent.setAction(Constants.START_FOREGROUND_ACTION);
                    startServiceIntent.putExtra("user", user);
                    startServiceIntent.putStringArrayListExtra("beacons", StorageHelper.LoadBeaconsFromStorage(baseContext));
                    ContextCompat.startForegroundService(baseContext, startServiceIntent);
                    Log.i(Constants.LOG_CAT, "Service was started!");
                }
            }
        }
    }
}
