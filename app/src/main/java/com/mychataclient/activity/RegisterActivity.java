package com.mychataclient.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mychataclient.R;
import com.mychataclient.utils.Connection;

/**
 * Created by ciprian.mare on 3/16/2015.
 */
public class RegisterActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener(

        ) {
            @Override
            public void onClick(View v) {
                EditText username = (EditText) findViewById(R.id.txtUsername);
                EditText password = (EditText) findViewById(R.id.txtPassword);

                Connection.getInstance().send(1, username.getText().toString(), password.getText().toString());

            }
        });
    }
}
