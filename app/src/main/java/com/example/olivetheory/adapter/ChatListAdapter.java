package com.example.olivetheory.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.olivetheory.models.ChatListItem;
import com.example.olivetheory.R;

import java.util.List;

public class ChatListAdapter extends ArrayAdapter<ChatListItem> {

    public ChatListAdapter(Context context, List<ChatListItem> chatListItems) {
        super(context, 0, chatListItems);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_chat_list, parent, false);
        }

        ChatListItem chatListItem = getItem(position);

        TextView userNameTextView = convertView.findViewById(R.id.user_name);
        TextView lastMessageTextView = convertView.findViewById(R.id.last_message);

        // Bind data to views
        if (chatListItem != null) {
            userNameTextView.setText(chatListItem.getName());
            lastMessageTextView.setText(chatListItem.getLastMessage());
        }

        return convertView;
    }
}
