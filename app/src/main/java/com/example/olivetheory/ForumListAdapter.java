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
    private List<ForumListItem> forumListItems;

    public ForumListAdapter(Activity context, List<ForumListItem> forumListItems) {
        super(context, R.layout.item_furom_list, forumListItems);
        this.context = context;
        this.forumListItems = forumListItems;
    }

    public void setData(List<ForumListItem> forumListItems) {
        this.forumListItems.clear();  // Clear existing data
        this.forumListItems.addAll(forumListItems);  // Add new data
        notifyDataSetChanged();  // Notify adapter to refresh the ListView
    }


    static class ViewHolder {
        TextView name;
        TextView post;
        TextView date;
        TextView time;
        TextView dislikes;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.item_furom_list, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.name = convertView.findViewById(R.id.name);
            viewHolder.post = convertView.findViewById(R.id.post);
            viewHolder.date = convertView.findViewById(R.id.date);
            viewHolder.time = convertView.findViewById(R.id.time);
            viewHolder.dislikes = convertView.findViewById(R.id.dislikes);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ForumListItem item = forumListItems.get(position);
        viewHolder.name.setText(item.getName());
        viewHolder.post.setText(item.getPost());
        viewHolder.date.setText(item.getDate());
        viewHolder.time.setText(item.getTime());
        viewHolder.dislikes.setText(item.getDislikes());

        return convertView;
    }

}