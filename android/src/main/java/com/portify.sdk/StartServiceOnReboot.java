package com.portify.sdk;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.util.Log;

public class StartServiceOnReboot extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(Constants.LOG_CAT, "Received intent to start service after reboot");
        String user = StorageHelper.LoadUserFromStorage(context);
        Log.i(Constants.LOG_CAT, "User was loaded " + user);
        if(!user.equals("")) {
            Intent startServiceIntent = new Intent(context, PortifyBackgroundService.class);
            startServiceIntent.setAction(Constants.START_FOREGROUND_ACTION);
            startServiceIntent.putExtra("user", user);
            startServiceIntent.putStringArrayListExtra("beacons", StorageHelper.LoadBeaconsFromStorage(context));
            ContextCompat.startForegroundService(context, startServiceIntent);
            Log.i(Constants.LOG_CAT, "Service was started!");
        }
    }


}
