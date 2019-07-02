package com.portify.sdk;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;

public class StorageHelper {

    public static void ClearRestartInformation(Context context) {
        Log.i(Constants.LOG_CAT, "ClearRestartInformation reactApplicationContext");
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.STORAGE_SHAREDPREF_KEY, Context.MODE_PRIVATE);
        sharedPref.edit().remove(Constants.STORAGE_USERNAME).apply();
        Log.i(Constants.LOG_CAT, "Removed " + Constants.STORAGE_USERNAME);
        sharedPref.edit().remove(Constants.STORAGE_BEACONS).apply();
        Log.i(Constants.LOG_CAT, "Removed " + Constants.STORAGE_BEACONS);
    }

    public static ArrayList<String> LoadBeaconsFromStorage(Context context) {
        Log.i(Constants.LOG_CAT, "LoadBeaconsFromStorage");
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.STORAGE_SHAREDPREF_KEY, Context.MODE_PRIVATE);
        Log.i(Constants.LOG_CAT, "Got sharedpreferences");
        Boolean hasSavedBeacons = sharedPref.contains(Constants.STORAGE_BEACONS);
        Log.i(Constants.LOG_CAT, "HasSavedBeacons");
        if(hasSavedBeacons) {
            Log.i(Constants.LOG_CAT, "Beacons loaded from sharedprefs");
            String savedBeacons = sharedPref.getString(Constants.STORAGE_BEACONS, "");
            Log.i(Constants.LOG_CAT, "Beacons resolved to: " + savedBeacons);
            return new ArrayList<>(Arrays.asList(savedBeacons.split(";")));
        }

        Log.i(Constants.LOG_CAT, "didnt have saved beacons");
        return new ArrayList<>();
    }

    public static String LoadUserFromStorage(Context context){
        Log.i(Constants.LOG_CAT, "LoadUserFromStorage");
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.STORAGE_SHAREDPREF_KEY, Context.MODE_PRIVATE);
        Log.i(Constants.LOG_CAT, "Got sharedpreferences");
        Boolean hasSavedUsername = sharedPref.contains(Constants.STORAGE_USERNAME);
        Log.i(Constants.LOG_CAT, "HasSavedUsername");
        if(hasSavedUsername) {
            Log.i(Constants.LOG_CAT, "User was loaded from shared preferences");
            String savedUsername = sharedPref.getString(Constants.STORAGE_USERNAME, "");
            Log.i(Constants.LOG_CAT, "User resolved to: " + savedUsername);
            return savedUsername;
        }

        Log.i(Constants.LOG_CAT, "didnt have saved username");
        return "";
    }

    public static void ReplaceServiceStartInformationIfNeeded(Context context, String username, ArrayList<String> beaconStrings){
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.STORAGE_SHAREDPREF_KEY, Context.MODE_PRIVATE);

        Boolean hasSavedUsername = sharedPref.contains(Constants.STORAGE_USERNAME);

        // If we find a username stored
        if(hasSavedUsername) {
            Log.i(Constants.LOG_CAT, "User was loaded from shared preferences");
            String savedUsername = sharedPref.getString(Constants.STORAGE_USERNAME, "");
            Log.i(Constants.LOG_CAT, "User resolved to: " + savedUsername);

            // If the username equals the one logging in
            if(savedUsername.equals(username)) {
                Log.i(Constants.LOG_CAT, "The resolved user is the same as is logging in, returning early.");
                return;
            }
        }

        // We didnt find a username saved or the username did not match the one that was saved, overwrite!
        Log.i(Constants.LOG_CAT, "User loaded and user logging in is not the same, persisting the new user from " + hasSavedUsername + " to " + username);
        // Update the saved value in storage
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Constants.STORAGE_USERNAME, username);

        String stringOfBeacons = "";
        for (String beacon : beaconStrings) {
            stringOfBeacons = stringOfBeacons + beacon + ";";
        }
        editor.putString(Constants.STORAGE_BEACONS, stringOfBeacons);

        editor.apply();
        Log.i(Constants.LOG_CAT, "User change applied");
    }
}
