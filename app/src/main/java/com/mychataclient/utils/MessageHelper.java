package com.mychataclient.utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ciprian.mare on 3/18/2015.
 */
public final class MessageHelper {

    private MessageHelper() {

    }

    public static JSONObject createCustomMessage(final Integer msgType, final String username, final String password) throws JSONException {
        JSONObject result = new JSONObject();
        result.put("msgType", msgType);
        result.put("username", username);
        result.put("password", password);
        return result;
    }
}
