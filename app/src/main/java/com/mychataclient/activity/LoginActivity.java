package com.mychataclient.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mychataclient.R;
import com.mychataclient.entity.Message;
import com.mychataclient.entity.User;
import com.mychataclient.enums.MessageType;
import com.mychataclient.utils.Connection;
import com.mychataclient.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends ActionBarActivity implements View.OnClickListener, Connection.MessageReceivedListener {


    private TextView connectionStatus;
    private EditText editUsername;
    private EditText editPassword;
    private Button btnRegister;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        connectionStatus = (TextView) findViewById(R.id.login_connection_status);
        editUsername = (EditText) findViewById(R.id.login_username);
        editPassword = (EditText) findViewById(R.id.login_password);
        btnRegister = (Button) findViewById(R.id.btn_register);
        btnLogin = (Button) findViewById(R.id.btn_login);


        if (!Utils.isClientConnected(this))
            connectionStatus.setText(getString(R.string.msg_not_connected));

        connectionStatus.setText(getString(R.string.msg_connected));
        Utils.showToastNotify(this, getString(R.string.msg_connected));

        btnRegister.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register:
                if (!Utils.isClientConnected(this)) {
                    Utils.showToastNotify(this, getString(R.string.msg_not_connected));
                    break;
                }
                Intent intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
                break;

            case R.id.btn_login:
                if (!Utils.isClientConnected(this)) {
                    Utils.showToastNotify(this, getString(R.string.msg_not_connected));
                    break;
                }
                final String username = this.editUsername.getText().toString();
                final String password = editPassword.getText().toString();
                if (!validateInputData(username, password))
                    break;
                Message message = new Message(MessageType.LOGIN_MESSAGE, new User(username, password), "");
                Connection.getInstance().send(message);
                break;
        }
    }

    /**
     * @param username
     * @param password
     * @return
     */
    private boolean validateInputData(String username, String password) {
        boolean bRet = true;
        if (username.length() == 0) {
            bRet = false;
            Utils.showToastNotify(this, getString(R.string.error_username));
        }
        if (password.length() == 0) {
            bRet = false;
            Utils.showToastNotify(this, getString(R.string.error_password));
        }
        return bRet;
    }

    @Override
    public void onMessageReceived(String message) {
        if(message != null && !message.isEmpty()){
            Log.d("MyChatAClient", "Login: new message received");
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(message);
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }

            try {
                int errorCode = Integer.parseInt(jsonObject.getString("errorCode"));
                switch (errorCode){
                    case 0:
                        Utils.showToastNotify(this, getString(R.string.msg_user_authenticated));
                        //TODO: implement chat room
                        break;
                    case 2:
                        Utils.showToastNotify(this, getString(R.string.error_user_not_exist));
                        break;
                    case 3:
                        Utils.showToastNotify(this, getString(R.string.error_bad_credentials));
                        break;

                    default:
                        Utils.showToastNotify(this, getString(R.string.error_common));
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onConnectedStateChanged(boolean connected) {

    }


}
