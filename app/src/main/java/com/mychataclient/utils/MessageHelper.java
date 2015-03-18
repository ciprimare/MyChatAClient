package com.mychataclient.utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ciprian.mare on 3/18/2015.
 */
public final class MessageHelper {

    private MessageHelper() {

    }

    public static JSONObject createCustomMessage(final Integer msgType, final Object msgContent) throws JSONException {
        JSONObject result = new JSONObject();
        result.put("msgType", msgType);
        result.put("msgContent", msgContent);
        return result;
    }
}
