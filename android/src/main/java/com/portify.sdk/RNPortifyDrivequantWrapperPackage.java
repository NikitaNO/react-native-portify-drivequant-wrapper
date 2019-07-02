
package com.portify.sdk;

import android.util.Log;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import com.facebook.react.bridge.JavaScriptModule;
public class RNPortifyDrivequantWrapperPackage implements ReactPackage {
    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
        Log.i(Constants.LOG_CAT, "createNativeModules");
        return Arrays.<NativeModule>asList(new com.portify.sdk.RNPortifyDrivequantWrapperModule(reactContext));
    }

    // Deprecated from RN 0.47
    public List<Class<? extends JavaScriptModule>> createJSModules() {
        Log.i(Constants.LOG_CAT, "createJSModules");
        return Collections.emptyList();
    }

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
        Log.i(Constants.LOG_CAT, "createViewManagers");
        return Collections.emptyList();
    }
}