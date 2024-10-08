package com.example.olivetheory.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.olivetheory.models.Message;
import com.example.olivetheory.R;

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
        ImageView imageViewMessage = listViewItem.findViewById(R.id.image_view_message);
        TextView textViewDate = listViewItem.findViewById(R.id.date);

        Message message = messageList.get(position);

        if (message.getContent() != null && !message.getContent().isEmpty()) {
            textViewMessage.setText(message.getContent());
            textViewMessage.setVisibility(View.VISIBLE);
            imageViewMessage.setVisibility(View.GONE);
        } else if (message.getImageUrl() != null && !message.getImageUrl().isEmpty()) {
            Glide.with(context).load(message.getImageUrl()).into(imageViewMessage);
            imageViewMessage.setVisibility(View.VISIBLE);
            textViewMessage.setVisibility(View.GONE);
        }

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
