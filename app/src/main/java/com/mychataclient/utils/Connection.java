package com.mychataclient.utils;

import android.os.Handler;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ciprian.mare on 3/18/2015.
 */
public class Connection {
    private static Connection instance;
    private final Handler mainThreadHandler;

    private List<MessageReceivedListener> listeners = new ArrayList<MessageReceivedListener>();
    private ConnectionThread connectionThread;


    public Connection(Handler mainThreadHandler) {
        this.mainThreadHandler = mainThreadHandler;
    }

    public static void initInstance(Handler mainThreadHandler) {
        instance = new Connection(mainThreadHandler);
    }

    public static Connection getInstance() {
        if (instance == null) {
            throw new RuntimeException("Call initInstance before getInstance");
        }
        return instance;
    }

    public void connect(String hostname, int port) {
        stopConnection();
        startConnection(hostname, port);
    }

    private void stopConnection() {
        if (connectionThread != null) {
            connectionThread.stopListening();
        }
    }

    private void startConnection(String hostname, int port) {
        connectionThread = new ConnectionThread(hostname, port);
        connectionThread.start();
    }

    public void addMessageReceivedListener(MessageReceivedListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeMessageReceivedListener(MessageReceivedListener listener) {
        while (listeners.contains(listener)) {
            listeners.remove(listener);
        }
    }

    public void send(final Integer msgType, final Object msgContent) {
        if (msgType == null || msgContent == null || connectionThread == null) {
            return;
        }
        JSONObject msg = null;
        try {
            msg = MessageHelper.createCustomMessage(msgType, msgContent);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        connectionThread.send(msg.toString());
    }

    private void dispatchMessage(String messageToDispatch) {
        for (MessageReceivedListener listener : listeners) {
            listener.onMessageReceived(messageToDispatch);
        }
    }

    private void dispatchConnectedStateChanged(boolean connected) {
        for (MessageReceivedListener listener : listeners) {
            listener.onConnectedStateChanged(connected);
        }
    }

    class ConnectionThread extends Thread {
        private final String serverAddress;
        private final int port;

        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;

        private volatile boolean listening = true;

        ConnectionThread(String serverAddress, int port) {
            this.serverAddress = serverAddress;
            this.port = port;
        }

        @Override
        public void run() {
            try {
                initiateConnection();
                receiveMessages();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                stopListening();
            }
        }

        private void initiateConnection() throws IOException {
            socket = new Socket(serverAddress, port);
            mainThreadHandler.post(new DispatchRunnable(DispatchType.CONNECTION, true));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        }

        private void receiveMessages() throws IOException {
            while (listening) {
                final String msg = in.readLine();
                mainThreadHandler.post(new DispatchRunnable(DispatchType.MESSAGE, msg));
                Log.e("MyChatAClient", "Received Message: '" + msg + "'");
            }
        }

        public void send(String message) {
            if (out != null && message != null) {
                out.println(message);
            }
        }

        public void stopListening() {
            listening = false;
            if (socket != null) {
                try {
                    socket.close();
                    mainThreadHandler.post(new DispatchRunnable(DispatchType.CONNECTION, false));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public interface MessageReceivedListener {
        public void onMessageReceived(String message);

        public void onConnectedStateChanged(boolean connected);
    }

//    public class MyRunnable<T> implements Runnable {
//        private final T t;
//
//        public MyRunnable(T t) {
//            this.t = t;
//        }
//
//        public void run() {
//            if(t instanceof Boolean){
//                dispatchConnectedStateChanged((Boolean) t);
//            } else if (t instanceof String) {
//                dispatchMessage((String) t);
//            }
//
//        }
//    }

    public class DispatchRunnable implements Runnable {
        private DispatchType dispatchType;
        private Object toDispatch;

        DispatchRunnable(DispatchType dispatchType, Object toDispatch) {
            this.dispatchType = dispatchType;
            this.toDispatch = toDispatch;
        }

        @Override
        public void run() {
            switch (dispatchType){
                case CONNECTION:
                    dispatchConnectedStateChanged((Boolean) toDispatch);
                    break;
                case MESSAGE:
                    dispatchMessage((String) toDispatch);
                    break;
            }


        }
    }

    private enum DispatchType{
        CONNECTION,
        MESSAGE;
    }

}


