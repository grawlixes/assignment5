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
        Post myPost = new Post(username, "Look at me I'm posting!");
        Thread t = new Thread(myPost);
        t.start();
    }
}

// Post a message.
// This is in its own class because you can't
// network on the main thread, so it's neater
// to use a private class for it instead.

// Here's how you make a post:
// Post myPost = new Post(String originalPoster, String postText);
// Thread t = new Thread(myPost)
// t.start();
class Post implements Runnable {

    private String op;
    private String post;

    Post(String op, String post) {
        this.op = op;
        this.post = post;
    }

    public void run() {
        try {
            URL url = new URL("https://cs.binghamton.edu/~kfranke1/" +
                    "assignment5/post.php");
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
            Log.d("Done posting", "asdf");

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