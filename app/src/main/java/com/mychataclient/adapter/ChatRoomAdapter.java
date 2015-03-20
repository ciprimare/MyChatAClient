package com.mychataclient.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mychataclient.R;

import java.util.List;

/**
 * Created by ciprian.mare on 3/20/2015.
 */
public class ChatRoomAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private List<String> messageList;

    public ChatRoomAdapter(LayoutInflater layoutInflater, List<String> messageList) {
        this.layoutInflater = layoutInflater;
        this.messageList = messageList;
    }

    @Override
    public int getCount() {
        return messageList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.chat_room_list_item, null);
        }

        String message = messageList.get(position);
        if (message != null && !message.isEmpty()) {
            TextView chatRoomListItemText = (TextView) convertView.findViewById(R.id.chat_room_list_item_text);

            if (chatRoomListItemText != null) {
                chatRoomListItemText.setText(message);
            }
        }

        return convertView;

    }
}
