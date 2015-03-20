package com.mychataclient.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mychataclient.R;
import com.mychataclient.utils.Connection;
import com.mychataclient.utils.Utils;

/**
 * Created by ciprian.mare on 3/17/2015.
 */
public class HomeActivity extends Activity implements View.OnClickListener, Connection.MessageReceivedListener {


    private EditText txtHostname;
    private EditText txtPort;

    private Button btnConnect;
    public static final String DEFAULT_HOSTNAME = "10.0.2.2";
    public static final int DEFAULT_PORT = 2221;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        txtHostname = (EditText) findViewById(R.id.txt_hostname);
        txtPort = (EditText) findViewById(R.id.txt_port);
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
        boolean bRet = true;
        if (hostname.length() == 0) {
            Utils.showToastNotify(this, getString(R.string.error_hostname));
            bRet = false;
        }
        if (port.length() == 0) {
            Utils.showToastNotify(this, getString(R.string.error_port));
            bRet = false;
        }
        return bRet;
    }


    @Override
    public void onMessageReceived(String message) {

    }

    @Override
    public void onConnectedStateChanged(boolean connected) {
        Log.d("MyChatAClient", "ConnectedStateChanged:" + connected);
        if (connected) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("connectionStatus", connected);
            editor.commit();
            goNextActivity();
        }
    }

}
