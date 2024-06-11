package com.example.olivetheory.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.olivetheory.models.Post;
import com.example.olivetheory.R;

import java.util.List;

public class ForumAdapter extends ArrayAdapter<Post>{

    private final Activity context;
    private final List<Post> forumList;

    public ForumAdapter(Activity context, List<Post> forumList){
        super(context, R.layout.item_forum, forumList);
        this.context = context;
        this.forumList = forumList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.item_forum, null, true);

        ImageView user = listViewItem.findViewById(R.id.user);
        TextView textViewName = listViewItem.findViewById(R.id.name);
        TextView textViewAnswer = listViewItem.findViewById(R.id.Answer);
        TextView textViewDate = listViewItem.findViewById(R.id.date);
        TextView textViewTime = listViewItem.findViewById(R.id.time);
        
        Post forum = forumList.get(position);
        user.setTextDirection(Integer.parseInt(forum.getUserImage()));
        textViewName.setText(forum.getName());
        textViewAnswer.setText(forum.getAnswer());
        textViewDate.setText(forum.getDate());
        textViewTime.setText(forum.getTime());
        return listViewItem;
    }
}


