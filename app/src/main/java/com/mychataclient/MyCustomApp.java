package com.mychataclient;

import android.app.Application;
import android.os.Handler;

import com.mychataclient.utils.Connection;

/**
 * Created by ciprian.mare on 3/18/2015.
 */
public class MyCustomApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Connection.initInstance(new Handler());
    }
}
