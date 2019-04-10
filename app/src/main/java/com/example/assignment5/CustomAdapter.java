package com.example.assignment5;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class CustomAdapter extends BaseAdapter {
    private Context context;
    private String ops[];
    private String posts[];
    private int likes[];
    private int dislikes[];
    private String avatars[];
    private LayoutInflater inflter;

    CustomAdapter(Context applicationContext, String[] ops,
                         String[] posts, int[] likes, int[] dislikes, String[] avatars) {
        this.context = applicationContext;
        this.ops = ops;
        this.posts = posts;
        this.likes = likes;
        this.dislikes = dislikes;
        this.avatars = avatars;
        inflter = (LayoutInflater.from(applicationContext));
    }

    void refresh(String[] ops, String[] posts, int[] likes, int[] dislikes,
                        String[] avatars) {
        this.ops = ops;
        this.posts = posts;
        this.likes = likes;
        this.dislikes = dislikes;
        this.avatars = avatars;

        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return ops.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.activity_list_item, null);
        TextView op = (TextView) view.findViewById(R.id.op);
        TextView post = (TextView) view.findViewById(R.id.post);
        ImageView image = (ImageView) view.findViewById(R.id.image);
        op.setText(ops[i]);
        post.setText(posts[i]);
        // Module by Square which allows loading images on the fly. Useful!
        Picasso.get().load(avatars[i]).into(image);

        return view;
    }
}