package com.example.assignment5;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final int REQCODE = 0;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Start the login activity
        Intent logInIntent = new Intent(this, LoginActivity.class);
        startActivityForResult(logInIntent, REQCODE);
    }

    // This is called when the login or account creation succeeds
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("RCRQ", String.valueOf(requestCode) + " " + String.valueOf(REQCODE));
        // Check that it is the SecondActivity with an OK result
        if (requestCode == REQCODE) {
            if (resultCode == RESULT_OK) {

                // Get String data from Intent
                username = data.getStringExtra("username");

                // Now you have the username. Congrats.
                Log.d("Username", username);
                main();
            }
        }
    }

    void main() {
        RetrievePosts ret = new RetrievePosts();
        Thread t = new Thread(ret);
        t.start();
        // Remember that getResult blocks the main thread automatically if you don't wait for
        // the database retrieval to be done. If you wait manually, you can do stuff while you wait.
        ArrayList<Triplet> userPostTriplets = ret.getResult();
        Log.d("Size", String.valueOf(userPostTriplets.size()));
        for (Triplet p : userPostTriplets) {
            Log.d("id", String.valueOf(p.getFirst()));
            Log.d("op", (String) p.getSecond());
            Log.d("post text", (String) p.getThird());
        }
        Reaction r = new Reaction("4", "dislike");
        Thread th = new Thread(r);
        th.start();
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
// filled with Triplets - first is the id, second is the op,
// and third is the post text - every entry is a new post):

// RetrievePosts ret = new RetrievePosts();
// Thread t = new Thread(ret)
// t.start();
/* either */
//// ArrayList<Triplet> userPostTriplets = ret.getResult();
/* OR */
//// while (!ret.isDone()) { // do whatever you want in background, i.e. an animation }
//// ArrayList<Triplet> userPostTriplets = ret.getResult();

/* If you don't care about using the main thread while waiting, just do the first one. */
class RetrievePosts implements Runnable {

    private boolean done;
    private ArrayList<Triplet> ret = new ArrayList<>();

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
            while (user != null && post != null) {
                Triplet<String, String, String> t = new Triplet<>(id, user, post);
                ret.add(t);
                id = br.readLine();
                user = br.readLine();
                post = br.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        done = true;
    }

    public boolean isDone() {
        return done;
    }

    public ArrayList<Triplet> getResult() {
        while (!done) {
            ;
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
            like = false;
        } else {
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

// TODO make this a quintuplet for likes and dislikes, too. Not sure how I forgot that.
// Triplet class found on StackOverflow.
// I just need it for retrieving posts because it's neater.
// I use it to store post ids as 'first', ops as 'second', and post text as 'third'.
class Triplet<T, U, V> {

    private final T first;
    private final U second;
    private final V third;

    public Triplet(T first, U second, V third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public T getFirst() { return first; }
    public U getSecond() { return second; }
    public V getThird() { return third; }
}