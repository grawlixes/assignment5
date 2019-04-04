package com.example.assignment5;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;

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
        ArrayList<Pair> userPostPairs = ret.getResult();
        Log.d("Size", String.valueOf(userPostPairs.size()));
        for (Pair p : userPostPairs) {
            Log.d("op", (String) p.first);
            Log.d("post text", (String) p.second);
        }
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
                Log.d("Line", line);
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
// filled with pairs - first is the op, second is the post
// text - every entry is a new post):

// RetrievePosts ret = new RetrievePosts();
// Thread t = new Thread(ret)
// t.start();
/* either */
//// ArrayList<Pair> userPostPairs = ret.getResult();
/* OR */
//// while (!ret.isDone()) { // do whatever you want in background, i.e. an animation }
//// ArrayList<Pair> userPostPairs = ret.getResult();

/* If you don't care about using the main thread while waiting, just do the first one. */
class RetrievePosts implements Runnable {

    private boolean done;
    private ArrayList<Pair> ret = new ArrayList<>();

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
            String user = br.readLine();
            String post = br.readLine();
            while (user != null && post != null) {
                Pair p = new Pair<>(user, post);
                ret.add(p);
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

    public ArrayList<Pair> getResult() {
        while (!done) {
            ;
        }
        return ret;
    }
}