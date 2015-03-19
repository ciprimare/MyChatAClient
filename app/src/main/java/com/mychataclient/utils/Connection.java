package com.mychataclient.utils;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by ciprian.mare on 3/18/2015.
 */
public class Connection {


    private static Connection instance = new Connection();

    private Socket channel;
    private PrintWriter out;
    private BufferedReader in;
    private ServerConnectionListener listener;
    private Thread readMessagesThread;


    private Connection() {
    }


    public static Connection getInstance() {
        return instance;
    }

    public void send(final Integer msgType, final Object msgContent) {
        if (msgType == null || msgContent == null) {
            return;
        }
        JSONObject msg = null;
        try {
            msg = MessageHelper.createCustomMessage(msgType, msgContent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (out != null && msg != null) {
            out.println(msg.toString());
        }
    }

    public void connect(final String hostname, final int port) {
        try {
            InetAddress serverAddress = InetAddress.getByName(hostname);
            Log.e("MyChatAClient", "serverAddress:" + serverAddress.toString());
            Log.e("MyChatAClient", "serverPort:" + port);
            Log.e("MyChatAClient", "Connecting...");
            if (channel == null || !channel.isConnected()) {
                channel = new Socket(serverAddress, port);
            }
            try {
                out = new PrintWriter(channel.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(
                        channel.getInputStream()));


            } catch (IOException e) {
                Log.e("MyChatAClient", "Streams error", e);
            }
        } catch (IOException e) {
            Log.e("MyChatAClient", "Connection error", e);
        }
    }

    public void readingMessagesListener() {
        readMessagesThread = new Thread(new ReadListener(in));
        readMessagesThread.start();
    }

    public boolean hasConnection() {
        return channel != null && channel.isConnected();
    }

    public void stop() throws IOException {
        out.close();
        in.close();
        channel.close();
    }

    private void dispatchMessage(final String message) {
        if (listener != null) {
            listener.onMessageReceived(message);
        }
    }

    public void setListener(ServerConnectionListener listener) {
        this.listener = listener;
    }

    public interface ServerConnectionListener {
        public void onMessageReceived(String message);
    }


    class ReadListener implements Runnable {

        private BufferedReader in;

        ReadListener(final BufferedReader in) {
            this.in = in;
        }

        @Override
        public void run() {
            while (true) {
                String msg = null;
                try {
                    msg = in.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (msg != null) {
                    dispatchMessage(msg);
                    Log.e("MyChatAClient", "Received Message: '"
                            + msg + "'");
                }
            }
        }
    }
}


