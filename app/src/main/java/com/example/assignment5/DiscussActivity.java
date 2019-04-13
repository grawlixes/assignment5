package com.example.assignment5;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DiscussActivity extends AppCompatActivity {

    String username;
    int id;
    String avatar;
    String op;
    String post;
    int likes;
    int dislikes;

    ListView lv;
    CustomAdapter ca;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discuss);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        avatar = intent.getStringExtra("avatar");
        op = intent.getStringExtra("op");
        post = intent.getStringExtra("post");
        id = Integer.parseInt(intent.getStringExtra("id"));
        likes = Integer.parseInt(intent.getStringExtra("likes"));
        dislikes = Integer.parseInt(intent.getStringExtra("dislikes"));

        final Button like = findViewById(R.id.likeButton);
        like.setText(String.valueOf(likes));
        like.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Reaction r = new Reaction(String.valueOf(id), "like", "1");
                Thread t = new Thread(r);
                t.start();

                like.setText(String.valueOf(Integer.parseInt(like.getText().toString())+1));
            }
        });

        final Button dislike = findViewById(R.id.dislikeButton);
        dislike.setText(String.valueOf(dislikes));
        dislike.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Reaction r = new Reaction(String.valueOf(id), "dislike", "1");
                Thread t = new Thread(r);
                t.start();

                dislike.setText(String.valueOf(Integer.parseInt(dislike.getText().toString())+1));
            }
        });

        if (avatar.length() > 0) {
            ImageView avatar = findViewById(R.id.avatar);
            Picasso.get().load(this.avatar).into(avatar);
        }

        TextView op = findViewById(R.id.username);
        op.setText(this.op);

        TextView post = findViewById(R.id.post);
        post.setText(this.post);

        FloatingActionButton returnButton = findViewById(R.id.returnButton);
        returnButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });

        FloatingActionButton commentButton = findViewById(R.id.addComment);
        commentButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(DiscussActivity.this, CommentActivity.class);
                intent.putExtra("op", username);
                intent.putExtra("post_id", String.valueOf(id));
                intent.putExtra("avatar", avatar);
                startActivityForResult(intent, 0);
            }
        });

        set(true);
    }

    void set(boolean first) {
        RetrieveComments ret = new RetrieveComments(id, username);
        Thread t2 = new Thread(ret);
        t2.start();
        Log.d("Waiting for posts", "p");
        ArrayList<Comment> newPosts = ret.getResult();
        Log.d("DONE for posts", String.valueOf(newPosts.size()));
        int[] ids = new int[newPosts.size()];
        String[] ops = new String[newPosts.size()];
        String[] posts = new String[newPosts.size()];
        int[] types = new int[newPosts.size()];
        int[] likes = new int[newPosts.size()];
        int[] dislikes = new int[newPosts.size()];
        String[] avatars = new String[newPosts.size()];

        for (int i = 0; i < newPosts.size(); i++) {
            ids[i] = newPosts.get(i).getId();
            ops[i] = (newPosts.get(i).getOp());
            posts[i] = (newPosts.get(i).getComment());
            types[i] = (newPosts.get(i).getType());
            likes[i] = (newPosts.get(i).getLikes());
            dislikes[i] = (newPosts.get(i).getDislikes());
            avatars[i] = (newPosts.get(i).getAvatar());
        }

        if (first) {
            lv = findViewById(R.id.postView);
            ca = new CustomAdapter(getApplicationContext(), ids, username, ops, posts, types, likes, dislikes, avatars);
            lv.setAdapter(ca);
        } else {
            ca.refresh(ids, ops, posts, types, likes, dislikes, avatars);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // TODO refresh the comments to show the new one.
        set(false);
    }
}

class RetrieveComments implements Runnable {

    private boolean done;
    private ArrayList<Comment> ret = new ArrayList<>();
    private int post_id;
    private String username;

    RetrieveComments(int post_id, String username) {
        done = false;
        this.post_id = post_id;
        this.username = username;
    }

    public void run() {
        try {
            final String PATH = "https://cs.binghamton.edu/~kfranke1/assignment5/";
            URL url = new URL(PATH + "refreshComments.php");
            HttpURLConnection connect = (HttpURLConnection) url
                    .openConnection();
            connect.setReadTimeout(15000);
            connect.setConnectTimeout(15000);
            connect.setRequestMethod("POST");
            connect.setDoInput(true);
            connect.setDoOutput(true);

            OutputStream os = connect.getOutputStream();
            String s = "post_id=" + String.valueOf(post_id) + "&username=" + username;
            os.write(s.getBytes());
            os.close();

            InputStream is = connect.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String id = br.readLine();
            String type = br.readLine();
            String user = br.readLine();
            String post = br.readLine();
            String likes = br.readLine();
            String dislikes = br.readLine();
            String avatar = br.readLine();
            while (user != null && post != null) {
                Log.d("Fn id", id);
                Log.d("Fn type", type);
                Log.d("Fn user", user);
                Log.d("Fn post", post);
                Log.d("Fn likes", likes);
                Log.d("Fn dislikes", dislikes);
                Log.d("fn avatar", avatar);
                Comment c = new Comment(Integer.parseInt(id), post_id, Integer.parseInt(type), user, post,
                        Integer.parseInt(likes),
                        Integer.parseInt(dislikes), avatar);
                ret.add(c);
                id = br.readLine();
                type = br.readLine();
                user = br.readLine();
                post = br.readLine();
                likes = br.readLine();
                dislikes = br.readLine();
                avatar = br.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d("Done is", "true");
        this.done = true;
        Log.d("Done iss", String.valueOf(this.done));

    }

    public boolean isDone() {
        return done;
    }

    public ArrayList<Comment> getResult() {
        while (!isDone());

        return ret;
    }
}

class Comment {

    private int id;
    private int post_id;
    private int type;
    private String op;
    private String comment;
    private int likes;
    private int dislikes;
    private String avatar;

    Comment(int id, int post_id, int type, String op, String comment, int likes, int dislikes, String avatar) {
        this.id = id;
        this.post_id = post_id;
        this.type = type;
        this.op = op;
        this.comment = comment;
        this.likes = likes;
        this.dislikes = dislikes;
        this.avatar = avatar;
    }

    public int getId() { return id; }
    public int getPostId() { return post_id; }
    public int getType() { return type; }
    public String getOp() { return op; }
    public String getComment() { return comment; }
    public int getLikes() { return likes; }
    public void addLike() { likes++; }
    public int getDislikes() { return dislikes; }
    public void addDislike() { dislikes++; }
    public String getAvatar() { return avatar; }
}