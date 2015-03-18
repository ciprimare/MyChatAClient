package com.mychataclient;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mychataclient.utils.Connection;
import com.mychataclient.utils.MessageHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by ciprian.mare on 3/17/2015.
 */
public class HomeActivity extends Activity implements View.OnClickListener {


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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnConnect:
                connect(txtHostname, txtPort);
                break;
        }
    }

    private void connect(EditText txtHostname, EditText txtPort) {
        if (!validate(txtHostname, txtPort)) {
            return;
        }
        new ConnectionTask().execute(new String[]{txtHostname.getText().toString(), txtPort.getText().toString()});
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

    private class ConnectionTask extends AsyncTask<String, String, Boolean> {

        @Override
        protected Boolean doInBackground(String... args) {
            String hostname = args[0];
            int port = Integer.parseInt(args[1]);
            try {
                if(Connection.getInstance().hasConnection()){
                    Connection.getInstance().stop();
                }
                Connection.getInstance().connect(hostname, port);
                Connection.getInstance().send(1, "client connected from android");
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("MyChatAClient", e.getMessage());
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result)
                goNextActivity();
        }
    }
}
