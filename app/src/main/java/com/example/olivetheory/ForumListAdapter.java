package com.example.olivetheory;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;
public class ForumListAdapter extends ArrayAdapter<ForumListItem> {

    private final Activity context;

    private final List<ForumListItem> forumListItems;

    public ForumListAdapter(Activity context, List<ForumListItem> forumListItems) {
        super(context, R.layout.item_furom_list, forumListItems);
        this.context = context;
        this.forumListItems = forumListItems;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.item_furom_list, parent, false);
        }

        TextView name = convertView.findViewById(R.id.name);
        TextView post = convertView.findViewById(R.id.post);
        TextView date = convertView.findViewById(R.id.date);
        TextView time = convertView.findViewById(R.id.time);
        TextView dislikes = convertView.findViewById(R.id.dislikes);

        ForumListItem item = forumListItems.get(position);
        name.setText(item.getName());
        post.setText(item.getPost());
        date.setText(item.getDate());
        time.setText(item.getTime());
        dislikes.setText(item.getDislikes());

        return convertView;
    }
}
