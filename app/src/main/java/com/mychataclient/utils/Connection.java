package com.mychataclient.utils;

import android.os.Handler;
import android.util.Log;

import com.mychataclient.entity.Message;

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

    public void send(final Message message) {
        if (message == null || connectionThread == null) {
            return;
        }
        JSONObject msg = null;
        try {
            msg = createJsonMessage(message);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        connectionThread.send(msg.toString());
    }

    /**
     *
     * @param message
     * @return
     * @throws JSONException
     */
    private JSONObject createJsonMessage(Message message) throws JSONException {
            JSONObject json = new JSONObject();
            json.put("msgType", message.getMessageType().getCod());
            json.put("username", message.getUser().getUsername());
            json.put("password", message.getUser().getPassword());
            json.put("message", message.getMessage());
            return json;
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
            mainThreadHandler.post(new MainThreadRunnable(true));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        }

        private void receiveMessages() throws IOException {
            while (listening) {
                final String msg = in.readLine();
                mainThreadHandler.post(new MainThreadRunnable(msg));
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
                    mainThreadHandler.post(new MainThreadRunnable(false));
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


    /**
     * a inner runnable class used to dispatch on UI main thread
     */
    public class MainThreadRunnable implements Runnable {
        private HandlerPostEnum handlerPostEnum;
        private String messageToDispatch;
        private boolean connectionStateChanged;

        MainThreadRunnable(String messageToDispatch){
            this.messageToDispatch = messageToDispatch;
            handlerPostEnum = HandlerPostEnum.MESSAGE_RECEIVED;

        }

        MainThreadRunnable(boolean connectionStateChanged){
            this.connectionStateChanged = connectionStateChanged;
            handlerPostEnum = HandlerPostEnum.CONNECTION_STATUS_CHANGED;
        }

        @Override
        public void run() {
            switch (handlerPostEnum){
                case CONNECTION_STATUS_CHANGED:
                    dispatchConnectedStateChanged(connectionStateChanged);
                    break;
                case MESSAGE_RECEIVED:
                    dispatchMessage(messageToDispatch);
                    break;
            }


        }
    }

    /**
     * enums to handle multiple possibilities to dispatch something on UI main thread
     */
    private enum HandlerPostEnum{
        CONNECTION_STATUS_CHANGED,
        MESSAGE_RECEIVED;
    }

}


