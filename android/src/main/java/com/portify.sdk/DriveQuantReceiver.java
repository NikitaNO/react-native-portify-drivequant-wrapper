package com.portify.sdk;

import android.content.Context;
        import android.content.Intent;
import android.util.Log;

import com.facebook.react.bridge.WritableNativeMap;

public class DriveQuantReceiver extends com.drivequant.sdk.tripanalysis.receivers.DriveQuantReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String intentClassName = "";
        try{
            intentClassName = intent.getComponent().getClassName();
        } catch(NullPointerException npe) {
            Log.e(Constants.LOG_CAT, "NullPointerException getting classname of intent: " + npe.getMessage());
        }

        Log.i(Constants.LOG_CAT, "DriveQuantReceiver.onReceive(): " + intentClassName);
        super.onReceive(context, intent);
    }
}