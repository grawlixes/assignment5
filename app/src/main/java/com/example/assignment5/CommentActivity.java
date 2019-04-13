package com.example.assignment5;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static java.lang.Thread.sleep;

public class CommentActivity extends AppCompatActivity {

    String op;
    int post_id;
    String avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        op = getIntent().getStringExtra("op");
        post_id = Integer.parseInt(getIntent().getStringExtra("post_id"));
        avatar = getIntent().getStringExtra("avatar");

        FloatingActionButton goBack = findViewById(R.id.cancelButton);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        FloatingActionButton sendComment = findViewById(R.id.sendComment);
        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText et = findViewById(R.id.comment);
                MakeComment mc = new MakeComment(post_id, op, et.getText().toString(), avatar);
                Thread t = new Thread(mc);
                t.start();

                // Sleep so we have time to update after posting.
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                finish();
            }
        });
    }
}

// Almost identical to MakePost, but makes a comment instead.
class MakeComment implements Runnable {

    private int post_id;
    private String op;
    private String comment;
    private int type;
    private String avatar;

    MakeComment(int post_id, String op, String comment, String avatar) {
        this.post_id = post_id;
        this.op = op;
        this.comment = comment;
        if (comment.length() < 4 || !(comment.substring(comment.length()-4).equals(".jpg") ||
                                      comment.substring(comment.length()-4).equals(".png"))) {
            this.type = 2;
        } else {
            this.type = 3;
        }

        this.avatar = avatar;
    }

    public void run() {
        try {
            final String PATH = "https://cs.binghamton.edu/~kfranke1/assignment5/";
            URL url = new URL(PATH + "comment.php");
            HttpURLConnection connect = (HttpURLConnection) url
                    .openConnection();
            connect.setReadTimeout(15000);
            connect.setConnectTimeout(15000);
            connect.setRequestMethod("POST");
            connect.setDoInput(true);
            connect.setDoOutput(true);

            OutputStream os = connect.getOutputStream();
            String s = "post_id=" + String.valueOf(post_id) +
                    "&op=" + op + "&comment=" + comment + "&type=" + String.valueOf(type) +
                    "&avatar=" + avatar;
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