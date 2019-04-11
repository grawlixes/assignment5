package com.example.assignment5;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {
    private static final int REQCODE = 0;
    private String username;

    private ListView lv;
    private CustomAdapter ca;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Circle of Friends");
        // Start the login activity
        Intent logInIntent = new Intent(this, LoginActivity.class);
        startActivityForResult(logInIntent, REQCODE);

        FloatingActionButton post = findViewById(R.id.fabPost);
        post.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                EditText postText = findViewById(R.id.postText);
                String post = postText.getText().toString();
                // Decide the type of post. If it's a png or jpg, it's an image; otherwise, it's not.
                // Type 0 means regular post, type 1 means image post.
                int type = (post.length() < 4 || !(post.substring(post.length()-4).equals(".jpg") ||
                                                    post.substring(post.length()-4).equals(".png")))
                            ? 0 : 1;
                Log.d("Type is", String.valueOf(type));
                Log.d("Type back", post.substring(post.length()-4));
                MakePost mp = new MakePost(username, postText.getText().toString(), type);
                Thread t = new Thread(mp);
                t.start();

                postText.getText().clear();
                try {
                    sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                set(false);
            }
        });

        FloatingActionButton refresh = findViewById(R.id.fabRefresh);
        refresh.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                set(false);
            }
        });
    }

    void set(boolean first) {
        RetrievePosts ret = new RetrievePosts();
        Thread t2 = new Thread(ret);
        t2.start();
        Log.d("Waiting for posts", "p");
        ArrayList<Post> newPosts = ret.getResult();
        Log.d("DONE for posts", String.valueOf(newPosts.size()));
        String[] ops = new String[newPosts.size()];
        String[] posts = new String[newPosts.size()];
        int[] types = new int[newPosts.size()];
        int[] likes = new int[newPosts.size()];
        int[] dislikes = new int[newPosts.size()];
        String[] avatars = new String[newPosts.size()];

        for (int i = 0; i < newPosts.size(); i++) {
            ops[i] = (newPosts.get(i).getOp());
            posts[i] = (newPosts.get(i).getPost());
            types[i] = (newPosts.get(i).getType());
            likes[i] = (newPosts.get(i).getLikes());
            dislikes[i] = (newPosts.get(i).getDislikes());
            avatars[i] = (newPosts.get(i).getAvatar());
        }

        if (first) {
            lv = findViewById(R.id.postView);
            ca = new CustomAdapter(getApplicationContext(), username, ops, posts, types, likes, dislikes, avatars);
            lv.setAdapter(ca);
        } else {
            ca.refresh(ops, posts, types, likes, dislikes, avatars);
        }
    }

    // This is called when the login or account creation succeeds
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check that it is the SecondActivity with an OK result
        if (requestCode == REQCODE) {
            if (resultCode == RESULT_OK) {

                // Get String data from Intent
                username = data.getStringExtra("username");

                // Show the posts here for the first time.
                set(true);
            }
        }
    }

    void react(int postId, String reaction) {
        Reaction r = new Reaction(String.valueOf(postId), reaction);
        Thread rt = new Thread(r);
        rt.start();
    }
}

// Post a message.
// This is in its own class because you can't
// network on the main thread, so it's neater
// to use a private class for it instead.

// Here's how you make a post:
// MakePost myPost = new MakePost(String originalPoster, String postText);
// Thread t = new Thread(myPost)
// t.start();
class MakePost implements Runnable {

    private String op;
    private String post;
    private int type;

    MakePost(String op, String post, int type) {
        this.op = op;
        this.post = post;
        this.type = type;
    }

    public void run() {
        try {
            final String PATH = "https://cs.binghamton.edu/~kfranke1/assignment5/";
            URL url = new URL(PATH + "post.php");
            HttpURLConnection connect = (HttpURLConnection) url
                    .openConnection();
            connect.setReadTimeout(15000);
            connect.setConnectTimeout(15000);
            connect.setRequestMethod("POST");
            connect.setDoInput(true);
            connect.setDoOutput(true);

            OutputStream os = connect.getOutputStream();
            String s = "username=" + op + "&post=" + post + "&type=" + String.valueOf(type);
            os.write(s.getBytes());
            os.close();

            InputStream is = connect.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = br.readLine();
            while (line != null) {
                Log.d("Response from post", line);
                line = br.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

// Retrieve the most recent posts, 10 max.
// Pretty much the same as above.

// Here's how you retrieve new posts (the arraylist is
// filled with Posts - every entry is a new post):

// RetrievePosts ret = new RetrievePosts();
// Thread t = new Thread(ret)
// t.start();
/* either */
//// ArrayList<Post> newPosts = ret.getResult();
/* OR */
//// while (!ret.isDone()) { // do whatever you want in background, i.e. an animation }
//// ArrayList<Post> newPosts = ret.getResult();

/* If you don't care about using the main thread while waiting, just do the first one. */
class RetrievePosts implements Runnable {

    private boolean done;
    private ArrayList<Post> ret = new ArrayList<>();

    RetrievePosts() {done = false;}

    public void run() {
        try {
            final String PATH = "https://cs.binghamton.edu/~kfranke1/assignment5/";
            URL url = new URL(PATH + "refresh.php");
            HttpURLConnection connect = (HttpURLConnection) url
                    .openConnection();
            connect.setReadTimeout(15000);
            connect.setConnectTimeout(15000);
            connect.setRequestMethod("POST");
            connect.setDoOutput(true);

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
                Log.d("Fn avatar", avatar);
                Post p = new Post(Integer.parseInt(id), Integer.parseInt(type), user, post,
                                  Integer.parseInt(likes),
                                  Integer.parseInt(dislikes), avatar);
                ret.add(p);
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

    public ArrayList<Post> getResult() {
        while (!isDone()) {
        }
        return ret;
    }
}

// Like or dislike a post. Same story as above.
// Here's how to use it:

// Reaction react = new Reaction(int postId, String reaction);
// Thread t = new Thread(ret);
// t.start();
/* if reaction is "dislike," you'll dislike it - otherwise, you'll like it */
class Reaction implements Runnable {

    private String postId;
    // True if you're liking, false if you're disliking.
    private boolean like;

    Reaction(String postId, String reaction) {
        if (reaction.equals("dislike")) {
            Log.d("Disliking", "d");
            like = false;
        } else {
            Log.d("Liking", "d");
            like = true;
        }

        this.postId = postId;
    }

    public void run() {
        try {
            final String PATH = "https://cs.binghamton.edu/~kfranke1/assignment5/";
            URL url = new URL(PATH + "react.php");
            HttpURLConnection connect = (HttpURLConnection) url
                    .openConnection();
            connect.setReadTimeout(15000);
            connect.setConnectTimeout(15000);
            connect.setRequestMethod("POST");
            connect.setDoOutput(true);

            OutputStream os = connect.getOutputStream();
            String s = "id=" + postId + "&reaction=" + ((like ? "y" : "n"));
            os.write(s.getBytes());
            os.close();

            InputStream is = connect.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = br.readLine();
            while (line != null) {
                Log.d("Reaction response", line);
                line = br.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

// For debugging.
class ShowUsersAndPosts implements Runnable {

    ShowUsersAndPosts() {}

    public void run() {
        try {
            final String PATH = "https://cs.binghamton.edu/~kfranke1/assignment5/";
            URL url = new URL(PATH + "get.php");
            HttpURLConnection connect = (HttpURLConnection) url
                    .openConnection();
            connect.setReadTimeout(15000);
            connect.setConnectTimeout(15000);
            connect.setRequestMethod("POST");
            connect.setDoOutput(true);

            InputStream is = connect.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String data = br.readLine();
            while (data != null) {
                Log.d("ShowUsersAndPosts data:", data);
                data = br.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

// Post class.
class Post {

    private int id;
    private int type;
    private String op;
    private String post;
    private int likes;
    private int dislikes;
    private String avatar;

    Post(int id, int type, String op, String post, int likes, int dislikes, String avatar) {
        this.id = id;
        this.type = type;
        this.op = op;
        this.post = post;
        this.likes = likes;
        this.dislikes = dislikes;
        this.avatar = avatar;
    }

    public int getId() { return id; }
    public int getType() { return type; }
    public String getOp() { return op; }
    public String getPost() { return post; }
    public int getLikes() { return likes; }
    public void addLike() { likes++; }
    public int getDislikes() { return dislikes; }
    public void addDislike() { dislikes++; }
    public String getAvatar() { return avatar; }
}