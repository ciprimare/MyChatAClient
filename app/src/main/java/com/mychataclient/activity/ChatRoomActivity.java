package com.mychataclient.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.mychataclient.R;
import com.mychataclient.adapter.ChatRoomAdapter;
import com.mychataclient.entity.Message;
import com.mychataclient.entity.User;
import com.mychataclient.enums.MessageType;
import com.mychataclient.utils.Connection;
import com.mychataclient.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ciprian.mare on 3/20/2015.
 */
public class ChatRoomActivity extends ActionBarActivity implements View.OnClickListener, Connection.MessageReceivedListener {

    private ListView chatRoomList;
    private EditText editMessage;
    private Button btnSend;
    private ChatRoomAdapter chatRoomAdapter;
    private List<String> messageList;
    private User loggedUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        chatRoomList = (ListView) findViewById(R.id.chat_room_list);
        editMessage = (EditText) findViewById(R.id.chat_message);
        btnSend = (Button) findViewById(R.id.btn_send);

        btnSend.setOnClickListener(this);
        messageList = new ArrayList<String>();
        chatRoomAdapter = new ChatRoomAdapter(this.getLayoutInflater(), messageList);
        chatRoomList.setAdapter(chatRoomAdapter);
        loggedUser = Utils.readCredentials(this);

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
        if (v.getId() == R.id.btn_send) {
            if (!Utils.isClientConnected(this)) {
                Utils.showToastNotify(this, getString(R.string.msg_not_connected));
                return;
            }
            String newMessage = editMessage.getText().toString();
            if (!newMessage.isEmpty()) {
                Message message = new Message(MessageType.BROADCAST_MESSAGE, loggedUser, newMessage);
                Connection.getInstance().send(message);
                editMessage.setText("");
            }
        }
    }

    @Override
    public void onMessageReceived(String message) {
        if(message != null && !message.isEmpty()){
            messageList.add(message);
            chatRoomAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onConnectedStateChanged(boolean connected) {

    }
}
