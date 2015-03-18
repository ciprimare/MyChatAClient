package com.mychataclient.utils;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by ciprian.mare on 3/18/2015.
 */
public class Connection {


    private static Connection instance = new Connection();

    private Socket channel;
    private PrintWriter out;
    private BufferedReader in;

    private Connection(){}


    public static Connection getInstance() {
        return instance;
    }

    public void connect(final String hostname, final int port) throws IOException {
        if(channel == null || channel.isClosed()){
            channel = new Socket(hostname, port);
        }
        Log.d("MyChatAClient", "ServerAddress: " + hostname);
        Log.d("MyChatAClient", "ServerPort: " + port);
        out = new PrintWriter(channel.getOutputStream(),true);
        in = new BufferedReader(new InputStreamReader(channel.getInputStream()));
    }

    public void send(final Integer msgType, final Object msgContent){
        if(msgType == null || msgContent == null){
            return;
        }
        JSONObject msg = null;
        try {
            msg = MessageHelper.createCustomMessage(msgType, msgContent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(out != null && msg != null){
            out.println(msg.toString());
        }
    }

    public boolean hasConnection(){
        return channel != null && channel.isConnected();
    }

    public void stop() throws IOException {
        channel.close();
    }



}
