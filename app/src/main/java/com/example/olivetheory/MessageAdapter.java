package com.example.olivetheory;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class MessageAdapter extends ArrayAdapter<Message> {
    private final Activity context;
    private final List<Message> messageList;

    public MessageAdapter(Activity context, List<Message> messageList) {
        super(context, R.layout.item_message, messageList);
        this.context = context;
        this.messageList = messageList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.item_message, null, true);

        TextView textViewMessage = listViewItem.findViewById(R.id.text_view_message);
        TextView textViewFrom = listViewItem.findViewById(R.id.text_view_from);

        Message message = messageList.get(position);
        textViewMessage.setText(message.getContent());
        textViewFrom.setText(message.getSenderId());

        return listViewItem;
    }
}
