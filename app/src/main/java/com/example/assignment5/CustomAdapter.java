package com.example.assignment5;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {
    private Context context;
    private int ids[];
    private String username;
    private String ops[];
    private Button buttons[];
    private String posts[];
    private int types[];
    private int likes[];
    private int dislikes[];
    private String avatars[];
    private LayoutInflater inflter;

    CustomAdapter(Context applicationContext, int ids[], String username, String[] ops,
                         String[] posts, int[] types, int[] likes, int[] dislikes, String[] avatars) {
        this.ids = ids;
        this.context = applicationContext;
        this.username = username;
        this.ops = ops;
        this.posts = posts;
        this.types = types;
        this.likes = likes;
        this.dislikes = dislikes;
        this.avatars = avatars;

        inflter = (LayoutInflater.from(applicationContext));
    }

    void refresh(int ids[], String[] ops, String[] posts, int[] types, int[] likes, int[] dislikes,
                        String[] avatars) {
        this.ids = ids;
        this.ops = ops;
        this.posts = posts;
        this.likes = likes;
        this.types = types;
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
        if (types[i] % 2 == 0) {
            view = inflter.inflate(R.layout.activity_list_item, null);
            TextView post = (TextView) view.findViewById(R.id.post);
            post.setText(posts[i]);
        } else {
            view = inflter.inflate(R.layout.activity_list_image, null);
            ImageView post = (ImageView) view.findViewById(R.id.post);
            Picasso.get().load(posts[i]).into(post);
        }
        final Button op = (Button) view.findViewById(R.id.op);

        final Button like = (Button) view.findViewById(R.id.likeButton);
        like.setText(String.valueOf(likes[i]));
        like.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String ty = "1";
                if (types[i] > 1) ty = "0";
                Reaction r = new Reaction(String.valueOf(ids[i]), "like", ty);
                Thread t = new Thread(r);
                t.start();

                like.setText(String.valueOf(Integer.parseInt((String) like.getText()) + 1));
            }
        });

        final Button dislike = (Button) view.findViewById(R.id.dislikeButton);
        dislike.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String ty = "1";
                if (types[i] > 1) ty = "0";
                Reaction r = new Reaction(String.valueOf(ids[i]), "dislike", ty);
                Thread t = new Thread(r);
                t.start();

                dislike.setText(String.valueOf(Integer.parseInt((String) dislike.getText()) + 1));
            }
        });
        dislike.setText(String.valueOf(dislikes[i]));

        final Button comment = (Button) view.findViewById(R.id.commentButton);
        if (types[i] < 2) {
            comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, DiscussActivity.class);
                    intent.putExtra("username", username);
                    intent.putExtra("id", String.valueOf(ids[i]));
                    intent.putExtra("avatar", avatars[i]);
                    intent.putExtra("op", ops[i]);
                    intent.putExtra("post", posts[i]);
                    intent.putExtra("likes", like.getText().toString());
                    intent.putExtra("dislikes", dislike.getText().toString());

                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
        } else {
            comment.setVisibility(View.INVISIBLE);
        }

        op.setText(ops[i]);
        op.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("clicker", username);
                intent.putExtra("username", op.getText());
                intent.putExtra("avatar", avatars[i]);

                GetBio gb = new GetBio((String) op.getText());
                Thread t = new Thread(gb);
                t.start();

                String bio = gb.getBio();
                intent.putExtra("bio", bio);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        ImageView image = (ImageView) view.findViewById(R.id.image);
        // Module by Square which allows loading images on the fly. Useful!
        if (avatars[i].length() > 0) {
            Picasso.get().load(avatars[i]).into(image);
        }

        return view;
    }
}

// Gets bio for a user.
class GetBio implements Runnable {

    private String username;
    private String bio;
    private boolean done;

    GetBio(String username) {
        this.done = false;
        this.username = username;
    }

    public void run() {
        try {
            final String PATH = "https://cs.binghamton.edu/~kfranke1/assignment5/";
            URL url = new URL(PATH + "bio.php");
            HttpURLConnection connect = (HttpURLConnection) url
                    .openConnection();
            connect.setReadTimeout(15000);
            connect.setConnectTimeout(15000);
            connect.setRequestMethod("POST");
            connect.setDoInput(true);
            connect.setDoOutput(true);

            OutputStream os = connect.getOutputStream();
            String s = "username=" + username;
            os.write(s.getBytes());
            os.close();

            InputStream is = connect.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = br.readLine();
            bio = line;
            while (line != null) {
                Log.d("Response from post", line);
                line = br.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        done = true;
    }

    boolean isDone() { return done; }

    String getBio() {
        while (!isDone()) { }

        return bio;
    }
}