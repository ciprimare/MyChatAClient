package com.mychataclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mychataclient.utils.Connection;

import java.io.IOException;

/**
 * Created by ciprian.mare on 3/17/2015.
 */
public class HomeActivity extends Activity implements View.OnClickListener, Connection.ServerConnectionListener {


    private EditText txtHostname;
    private EditText txtPort;
    private Button btnConnect;
    public static final String DEFAULT_HOSTNAME = "10.0.2.2";
    public static final int DEFAULT_PORT = 2221;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        btnConnect = (Button) findViewById(R.id.btnConnect);
        btnConnect.setOnClickListener(this);
        txtHostname = (EditText) findViewById(R.id.txtHostname);
        txtHostname.setText(DEFAULT_HOSTNAME);
        txtPort = (EditText) findViewById(R.id.txtPort);
        txtPort.setText(String.valueOf(DEFAULT_PORT));
        Connection.getInstance().setListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnConnect:
                connect(txtHostname, txtPort);
                break;
        }
    }

    private void connect(final EditText txtHostname, final EditText txtPort) {
        if (!validate(txtHostname, txtPort)) {
            return;
        }
        Thread thread = new Thread(new HomeRunnable());
        thread.start();
    }

    private void goNextActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private boolean validate(EditText hostname, EditText port) {
        boolean isValid = true;
        if (hostname.getText().toString().length() == 0) {
            hostname.setError("Hostname is required");
            isValid = false;
        }
        if (port.getText().toString().length() == 0) {
            hostname.setError("Port is required");
            isValid = false;
        }
        return isValid;
    }


    @Override
    public void onMessageReceived(String message) {
        Log.d("TCP SERVER RESPONSE","SUCKER");
    }

    class HomeRunnable implements Runnable{

        @Override
        public void run() {
            if(Connection.getInstance().hasConnection()){
                try {
                    Connection.getInstance().stop();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Connection.getInstance().connect(txtHostname.getText().toString(), Integer.parseInt(txtPort.getText().toString()));
            Connection.getInstance().readingMessagesListener();
            sendHandlerMessage("SUCCESS");
        }


        private void sendHandlerMessage(String msg) {

            if (!msg.equals(null) && !msg.equals("")) {
                Message msgObj = handler.obtainMessage();
                Bundle b = new Bundle();
                b.putString("message", msg);
                msgObj.setData(b);
                handler.sendMessage(msgObj);
            }
        }

        private final Handler handler = new Handler() {

            public void handleMessage(Message msg) {

                String aResponse = msg.getData().getString("message");

                if ((null != aResponse)) {

                    goNextActivity();
                } else {
                    // ALERT MESSAGE
//                        Toast.makeText(
//                                getBaseContext(),
//                                "Server Response: " + aResponse,
//                                Toast.LENGTH_SHORT).show();
                }

            }
        };
    }
}
