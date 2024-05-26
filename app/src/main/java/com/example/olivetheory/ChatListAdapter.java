package com.example.olivetheory;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ChatListAdapter extends ArrayAdapter<ChatListItem> {
    private final Activity context;
    private final List<ChatListItem> chatListItems;

    public ChatListAdapter(Activity context, List<ChatListItem> chatListItems) {
        super(context, R.layout.item_chat_list, chatListItems);
        this.context = context;
        this.chatListItems = chatListItems;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.item_chat_list, parent, false);
        }

        TextView textViewUsername = convertView.findViewById(R.id.text_view_username);
        TextView textViewLastMessage = convertView.findViewById(R.id.text_view_last_message);

        ChatListItem item = chatListItems.get(position);
        textViewUsername.setText(item.getName());
        textViewLastMessage.setText(item.getLastMessage());

        return convertView;
    }
}
