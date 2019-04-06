package com.example.assignment5;

import android.content.Intent;
import android.graphics.Paint;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    private ArrayList<Slot> slots = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Circle of Friends");
        // Start the login activity
        Intent logInIntent = new Intent(this, LoginActivity.class);
        startActivityForResult(logInIntent, REQCODE);

        slots.add(new Slot(0, (TextView) findViewById(R.id.op1), (TextView) findViewById(R.id.post1),
                           (Button) findViewById(R.id.like1), (Button) findViewById(R.id.dislike1)));
        slots.add(new Slot(0, (TextView) findViewById(R.id.op2), (TextView) findViewById(R.id.post2),
                (Button) findViewById(R.id.like2), (Button) findViewById(R.id.dislike2)));
        slots.add(new Slot(0, (TextView) findViewById(R.id.op3), (TextView) findViewById(R.id.post3),
                (Button) findViewById(R.id.like3), (Button) findViewById(R.id.dislike3)));

        FloatingActionButton post = findViewById(R.id.fabPost);
        post.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                EditText postText = findViewById(R.id.postText);
                MakePost mp = new MakePost(username, postText.getText().toString());
                Thread t= new Thread(mp);
                t.start();

                postText.getText().clear();
                try {
                    sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                set(slots);
            }
        });

        FloatingActionButton refresh = findViewById(R.id.fabRefresh);
        refresh.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                set(slots);
            }
        });
    }

    void set(ArrayList<Slot> slots) {
        RetrievePosts ret = new RetrievePosts();
        Thread t2 = new Thread(ret);
        t2.start();
        Log.d("Waiting for posts", "p");
        ArrayList<Post> newPosts = ret.getResult();
        Log.d("DONE for posts", "p");

        for (int i = 0; i < newPosts.size(); i++) {
            final Slot s = slots.get(i);
            final Post p = newPosts.get(i);
            s.setId(p.getId());
            s.getOp().setVisibility(View.VISIBLE);
            s.getOp().setPaintFlags(s.getOp().getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            s.getPost().setVisibility(View.VISIBLE);
            s.getLike().setVisibility(View.VISIBLE);
            s.getDislike().setVisibility(View.VISIBLE);
            s.getOp().setText(p.getOp());
            s.getPost().setText(p.getPost());
            s.getLike().setText(String.valueOf(p.getLikes()));
            s.getLike().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Reaction r = new Reaction(String.valueOf(p.getId()), "like");
                    Thread rt = new Thread(r);
                    rt.start();
                    p.addLike();
                    s.getLike().setText(String.valueOf(p.getLikes()));
                }
            });
            s.getDislike().setText(String.valueOf(p.getDislikes()));
            s.getDislike().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Reaction r = new Reaction(String.valueOf(p.getId()), "dislike");
                    Thread rt = new Thread(r);
                    rt.start();
                    p.addDislike();
                    s.getDislike().setText(String.valueOf(p.getDislikes()));
                }
            });
        }

        for (int i = newPosts.size(); i < 3; i++) {
            Slot s = slots.get(i);
            s.getOp().setVisibility(View.INVISIBLE);
            s.getPost().setVisibility(View.INVISIBLE);
            s.getLike().setVisibility(View.INVISIBLE);
            s.getDislike().setVisibility(View.INVISIBLE);
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

                // Show the posts here.
                set(slots);

                main();
            }
        }
    }

    void main() {
        /*
        ShowUsersAndPosts suap = new ShowUsersAndPosts();
        Thread s = new Thread(suap);
        s.start();

        MakePost mp = new MakePost(username, "I'm kyle duhhh");
        Thread t= new Thread(mp);
        t.start();

        RetrievePosts ret = new RetrievePosts();
        Thread t2 = new Thread(ret);
        t2.start();

        ArrayList<Post> pal = ret.getResult();
        for (Post p : pal) {
            Log.d("id+1", String.valueOf(p.getId()+1));
            Log.d("op", p.getOp());
            Log.d("post", p.getPost());
            Log.d("likes+1", String.valueOf(p.getLikes()+1));
            Log.d("dislikes+1", String.valueOf(p.getDislikes()+1));
        }
        // Remember that getResult blocks the main thread automatically if you don't wait for
        // the database retrieval to be done. If you wait manually, you can do stuff while you wait.

        Reaction r = new Reaction("4", "dislike");
        Thread th = new Thread(r);
        th.start();
        */
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

    MakePost(String op, String post) {
        this.op = op;
        this.post = post;
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
            String s = "username=" + op + "&post=" + post;
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
            String user = br.readLine();
            String post = br.readLine();
            String likes = br.readLine();
            String dislikes = br.readLine();
            while (user != null && post != null) {
                Log.d("Fn id", id);
                Log.d("Fn user", user);
                Log.d("Fn post", post);
                Log.d("Fn likes", likes);
                Log.d("Fn dislikes", dislikes);
                Post p = new Post(Integer.parseInt(id), user, post, Integer.parseInt(likes),
                                  Integer.parseInt(dislikes));
                ret.add(p);
                id = br.readLine();
                user = br.readLine();
                post = br.readLine();
                likes = br.readLine();
                dislikes = br.readLine();
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
        Log.d("Running reaction", "a");
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
    private String op;
    private String post;
    private int likes;
    private int dislikes;

    Post(int id, String op, String post, int likes, int dislikes) {
        this.id = id;
        this.op = op;
        this.post = post;
        this.likes = likes;
        this.dislikes = dislikes;
    }

    public int getId() { return id; }
    public String getOp() { return op; }
    public String getPost() { return post; }
    public int getLikes() { return likes; }
    public void addLike() { likes++; }
    public int getDislikes() { return dislikes; }
    public void addDislike() { dislikes++; }
}

// Slot class.
class Slot {
    private int id;
    private TextView op;
    private TextView post;
    private Button like;
    private Button dislike;

    Slot(int id, TextView op, TextView post, Button like, Button dislike) {
        this.id = id;
        this.op = op;
        this.post = post;
        this.like = like;
        this.dislike = dislike;
    }

    public void setId(int id) { this.id = id; }
    public int getId() { return id; }
    public TextView getOp() { return op; }
    public TextView getPost() { return post; }
    public Button getLike() { return like; }
    public Button getDislike() { return dislike; }
}