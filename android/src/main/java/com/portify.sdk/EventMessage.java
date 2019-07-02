package com.portify.sdk;

import android.util.Log;

import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.ArrayList;
import java.util.List;

public class EventMessage {
    private List<String> messages = new ArrayList<String>();
    private String eventName;

    private EventMessage(String eventType) {
        this.eventName = eventType;
    }

    public static EventMessage Create(String eventType) {
        EventMessage message = new EventMessage(eventType);
        return message;
    }

    public EventMessage Add(String value) {
        Log.i(eventName, value); // Output to log and not only to react bridge.
        messages.add(value);
        return this;
    }

    public void Emit() {
        WritableNativeMap map = new WritableNativeMap();
        for (String item: messages) {
            map.putString(eventName, item);
        }

        RNPortifyDrivequantWrapperModule.sendEvent(eventName, map);
    }
}
