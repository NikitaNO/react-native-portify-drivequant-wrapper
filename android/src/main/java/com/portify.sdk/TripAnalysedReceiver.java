package com.portify.sdk;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.drivequant.sdk.model.TripNotification;
import com.drivequant.sdk.model.postgeneric.Beacon;
import com.drivequant.sdk.model.postgeneric.PostGeneric;
import com.drivequant.sdk.model.postgeneric.PostGenericResponse;
import com.facebook.react.bridge.WritableNativeMap;

public class TripAnalysedReceiver extends com.drivequant.sdk.tripanalysis.receivers.TripAnalysedReceiver {
    @Override
    public void onTripReceived(Context context, PostGeneric postGeneric, PostGenericResponse postGenericResponse) {
        Log.i(Constants.LOG_CAT, "TripAnalysisReceiver.onTripReceived");
        Log.i(Constants.LOG_CAT, "Distance: " + postGenericResponse.getItineraryStatistics().getDistance());

        Beacon beacon = postGeneric.getSmartphoneData().getBeacon();
        if(beacon != null) {
            Log.i(Constants.LOG_CAT, "BeaconUuid " + beacon.getProximityUuid());
        } else {
            Log.i(Constants.LOG_CAT, "No beacon registered on trip");
        }
    }
}