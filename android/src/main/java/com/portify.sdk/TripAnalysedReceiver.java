package com.portify.sdk;

import android.content.Context;
import android.util.Log;

import com.drivequant.sdk.model.postgeneric.Beacon;
import com.drivequant.sdk.model.postgeneric.PostGeneric;
import com.drivequant.sdk.model.postgeneric.PostGenericResponse;
import com.facebook.react.bridge.WritableNativeMap;

import static com.portify.sdk.RNPortifyDrivequantWrapperModule.sendEvent;

public class TripAnalysedReceiver extends com.drivequant.sdk.tripanalysis.receivers.TripAnalysedReceiver {
    @Override
    public void onTripReceived(Context context, PostGeneric postGeneric, PostGenericResponse postGenericResponse) {
        Log.i(Constants.LOG_CAT, "TripAnalysisReceiver.onTripReceived");

        try {
            WritableNativeMap params = getParams(postGenericResponse.getItinId(), "", true);

            sendEvent("tripReceived", params);

            Beacon beacon = postGeneric.getSmartphoneData().getBeacon();

            if(beacon != null) {
                Log.i(Constants.LOG_CAT, "BeaconUuid " + beacon.getProximityUuid());
            } else {
                Log.i(Constants.LOG_CAT, "No beacon registered on trip");
            }
        } catch (IllegalArgumentException iae) {
            Log.i(Constants.LOG_CAT, iae.getMessage());

            WritableNativeMap params = getParams("", iae.getMessage(), false);

            sendEvent("tripReceived", params);
        }
    }

    private WritableNativeMap getParams(String trip, String error, Boolean isFinished) {
        WritableNativeMap params = new WritableNativeMap();

        params.putString("itinId", trip);
        params.putString("error", error);
        params.putBoolean("isFinished", isFinished);

        return params;
    }
}