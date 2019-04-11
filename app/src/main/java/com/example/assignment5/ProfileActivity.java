package com.example.assignment5;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
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

public class ProfileActivity extends AppCompatActivity {

    String clicker;
    String username;
    String avatar;
    String bio;
    ListView lv;
    CustomAdapter ca;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        clicker = getIntent().getStringExtra("clicker");
        username = getIntent().getStringExtra("username");
        avatar = getIntent().getStringExtra("avatar");
        bio = getIntent().getStringExtra("bio");
        set(true);

        FloatingActionButton edit = findViewById(R.id.editProfile);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Edit profile stuff here. Can just be two EditTexts for image url and bio.
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });
        if (!clicker.equals(username)) {
            edit.hide();
        }

        FloatingActionButton back = findViewById(R.id.returnButton);
        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });

        ImageView avatar = findViewById(R.id.avatar);
        if (this.avatar.length() > 0) {
            Picasso.get().load(this.avatar).into(avatar);
        }

        TextView u = findViewById(R.id.username);
        u.setText(username);

        TextView bioView = findViewById(R.id.bio);
        if (bio.length() > 0) {
            bioView.setText(bio);
        }
    }

    void set(boolean first) {
        RetrieveMyPosts ret = new RetrieveMyPosts(username);
        Thread t2 = new Thread(ret);
        t2.start();
        Log.d("Waiting for posts", "p");
        ArrayList<Post> newPosts = ret.getResult();
        Log.d("DONE for posts", String.valueOf(newPosts.size()));
        String[] ops = new String[newPosts.size()];
        String[] posts = new String[newPosts.size()];
        int[] likes = new int[newPosts.size()];
        int[] dislikes = new int[newPosts.size()];
        String[] avatars = new String[newPosts.size()];

        for (int i = 0; i < newPosts.size(); i++) {
            ops[i] = (newPosts.get(i).getOp());
            posts[i] = (newPosts.get(i).getPost());
            likes[i] = (newPosts.get(i).getLikes());
            dislikes[i] = (newPosts.get(i).getDislikes());
            avatars[i] = (newPosts.get(i).getAvatar());
        }

        if (first) {
            lv = findViewById(R.id.postView);
            ca = new CustomAdapter(getApplicationContext(), clicker, ops, posts, likes, dislikes, avatars);
            lv.setAdapter(ca);
        } else {
            ca.refresh(ops, posts, likes, dislikes, avatars);
        }
    }
}

// Retrieve the most recent posts FROM THIS USER, 10 max.
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
class RetrieveMyPosts implements Runnable {

    private boolean done;
    private ArrayList<Post> ret = new ArrayList<>();
    private String username;

    RetrieveMyPosts(String username) {
        done = false;
        this.username = username;
    }

    public void run() {
        try {
            final String PATH = "https://cs.binghamton.edu/~kfranke1/assignment5/";
            URL url = new URL(PATH + "profile.php");
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
            String id = br.readLine();
            String user = br.readLine();
            String post = br.readLine();
            String likes = br.readLine();
            String dislikes = br.readLine();
            String avatar = br.readLine();
            while (user != null && post != null) {
                Log.d("Fn id", id);
                Log.d("Fn user", user);
                Log.d("Fn post", post);
                Log.d("Fn likes", likes);
                Log.d("Fn dislikes", dislikes);
                Log.d("Fn avatar", avatar);
                Post p = new Post(Integer.parseInt(id), user, post, Integer.parseInt(likes),
                        Integer.parseInt(dislikes), avatar);
                ret.add(p);
                id = br.readLine();
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