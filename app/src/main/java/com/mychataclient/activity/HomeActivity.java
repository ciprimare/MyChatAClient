package com.mychataclient.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mychataclient.R;
import com.mychataclient.utils.Connection;

import java.io.IOException;

/**
 * Created by ciprian.mare on 3/17/2015.
 */
public class HomeActivity extends Activity implements View.OnClickListener, Connection.MessageReceivedListener {


    private EditText txtHostname;
    private EditText txtPort;
    private TextView errorText;

    private Button btnConnect;
    public static final String DEFAULT_HOSTNAME = "10.0.2.2";
    public static final int DEFAULT_PORT = 2221;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        txtHostname = (EditText) findViewById(R.id.txt_hostname);
        txtPort = (EditText) findViewById(R.id.txt_port);
        errorText = (TextView) findViewById(R.id.error_text);
        btnConnect = (Button) findViewById(R.id.btn_connect);

        btnConnect.setOnClickListener(this);
        txtHostname.setText(DEFAULT_HOSTNAME);
        txtPort.setText(String.valueOf(DEFAULT_PORT));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Connection.getInstance().addMessageReceivedListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Connection.getInstance().removeMessageReceivedListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_connect:
                connect(txtHostname.getText().toString(), txtPort.getText().toString());
                break;
        }
    }

    private void connect(String txtHostname, String txtPort) {
        if (!validate(txtHostname, txtPort)) {
            return;
        }
        Connection.getInstance().connect(txtHostname, Integer.parseInt(txtPort));
    }

    private void goNextActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private boolean validate(String hostname, String port) {
        boolean isValid = true;
        if (hostname.length() == 0) {
            errorText.setError("Hostname is required");
            isValid = false;
        } else if (port.length() == 0) {
            errorText.setError("Port is required");
            isValid = false;
        } else {
            errorText.setError("");
        }
        return isValid;
    }


    @Override
    public void onMessageReceived(String message) {
        Log.d("TCP SERVER RESPONSE", "MESSAGE RECEIVED IN ACTIVITY");
    }
}
