package com.mychataclient.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.widget.Toast;

import com.mychataclient.entity.User;

/**
 * Created by ciprian.mare on 3/18/2015.
 */
public final class Utils {

    private Utils() {

    }

    /**
     * @param activity
     * @param message
     */
    public static void showToastNotify(final Activity activity, final String message) {
        Context context = activity.getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, message, duration);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 0);
        toast.show();
    }

    /**
     * @return
     */
    public static boolean isClientConnected(final Activity activity) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        return sharedPreferences.getBoolean("connectionStatus", false);
    }

    /**
     * @param context
     * @param user
     */
    public static void storeCredentials(Context context, User user) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("loggedUsername", user.getUsername());
        editor.putString("loggedPassword", user.getPassword());
        editor.commit();
    }

    /**
     * @param context
     * @return
     */
    public static User readCredentials(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return new User(sharedPreferences.getString("loggedUsername", ""), sharedPreferences.getString("loggedPassword", ""));
    }

    /**
     * @param context
     */
    public static void clearCredentials(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("loggedUsername");
        editor.remove("loggedPassword");
        editor.commit();
    }


}
