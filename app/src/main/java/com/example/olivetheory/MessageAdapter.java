package com.example.olivetheory;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends ArrayAdapter<Message> {
    private final Activity context;
    private final List<Message> messageList;
    private final String currentUserId;

    public MessageAdapter(Activity context, List<Message> messageList, String currentUserId) {
        super(context, R.layout.item_message, messageList);
        this.context = context;
        this.messageList = messageList;
        this.currentUserId = currentUserId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.item_message, null, true);

        TextView textViewMessage = listViewItem.findViewById(R.id.text_view_message);
        TextView textViewDate = listViewItem.findViewById(R.id.date);

        Message message = messageList.get(position);
        textViewMessage.setText(message.getContent());

        // Set background drawable based on sender (current user or other user)
        if (message.getSenderId().equals(currentUserId)) {
            listViewItem.setBackgroundResource(R.drawable.message_bubble_right);
        } else {
            listViewItem.setBackgroundResource(R.drawable.message_bubble_left);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String formattedDate = sdf.format(new Date(message.getTimestamp()));
        textViewDate.setText(formattedDate);

        return listViewItem;
    }
}
