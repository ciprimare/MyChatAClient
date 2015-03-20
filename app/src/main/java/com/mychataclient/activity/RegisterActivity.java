package com.mychataclient.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mychataclient.R;
import com.mychataclient.entity.Message;
import com.mychataclient.entity.User;
import com.mychataclient.enums.MessageType;
import com.mychataclient.utils.Connection;
import com.mychataclient.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ciprian.mare on 3/16/2015.
 */
public class RegisterActivity extends Activity implements View.OnClickListener, Connection.MessageReceivedListener {

    private EditText editUsername;
    private EditText editPassword;
    private EditText editRepeatedPassword;
    private Button btnSave;
    private Button btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editUsername = (EditText) findViewById(R.id.register_username);
        editPassword = (EditText) findViewById(R.id.register_password);
        editRepeatedPassword = (EditText) findViewById(R.id.register_repeated_password);
        btnSave = (Button) findViewById(R.id.btn_save);
        btnCancel = (Button) findViewById(R.id.btn_cancel);

        btnSave.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

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
            case R.id.btn_save:
                if (!Utils.isClientConnected(this)) {
                    Utils.showToastNotify(this, getString(R.string.msg_not_connected));
                    break;
                }
                final String username = editUsername.getText().toString();
                final String password = editPassword.getText().toString();
                final String repeatedPassword = editRepeatedPassword.getText().toString();
                if (!validateInputData(username, password, repeatedPassword))
                    break;
                Message message = new Message(MessageType.REGISTER_MESSAGE, new User(username, password), "");
                Connection.getInstance().send(message);
                break;
            case R.id.btn_cancel:
                finish();
                break;
        }
    }

    /**
     *
     * @param username
     * @param password
     * @param repeatedPassword
     * @return
     */
    private boolean validateInputData(String username, String password, String repeatedPassword) {
        boolean bRet = true;
        if (username.isEmpty()) {
            bRet = false;
            Utils.showToastNotify(this, getString(R.string.error_username));
        }
        if (password.isEmpty()) {
            bRet = false;
            Utils.showToastNotify(this, getString(R.string.error_password));
        }
        if (repeatedPassword.isEmpty()) {
            bRet = false;
            Utils.showToastNotify(this, getString(R.string.error_password_repeated));
        }

        if(bRet && !password.isEmpty() && !repeatedPassword.isEmpty()){
            if(!repeatedPassword.equalsIgnoreCase(password)){
                bRet = false;
                Utils.showToastNotify(this, getString(R.string.error_password_repeated_not_equal));
            }
        }

        return bRet;
    }

    @Override
    public void onMessageReceived(String message) {
        if(message != null && !message.isEmpty()){
            Log.d("MyChatAClient", "Registration: new message received");
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
                        Utils.showToastNotify(this, getString(R.string.msg_user_registred));
                        finish();
                        break;
                    case 1:
                        Utils.showToastNotify(this, getString(R.string.error_user_exist));
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
