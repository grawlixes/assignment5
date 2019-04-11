package com.example.assignment5;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class EditProfileActivity extends AppCompatActivity {

    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        username = getIntent().getStringExtra("username");

        FloatingActionButton backButton = findViewById(R.id.returnButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        FloatingActionButton saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editAvatar = findViewById(R.id.avatarEdit);
                EditText editBio = findViewById(R.id.bioEdit);

                String newAvatar = editAvatar.getText().toString();
                String newBio = editBio.getText().toString();

                UpdateUser uu = new UpdateUser(username, newAvatar, newBio);
                Thread t = new Thread(uu);
                t.start();

                Intent done = new Intent();
                done.putExtra("avatar", newAvatar);
                done.putExtra("bio", newBio);
                setResult(RESULT_OK, done);
                finish();
            }
        });
    }
}

// Updates a user's bio and avatar.
class UpdateUser implements Runnable {

    private String username;
    private String newAvatar;
    private String newBio;

    UpdateUser(String username, String newAvatar, String newBio) {
        this.username = username;
        this.newAvatar = newAvatar;
        this.newBio = newBio;
    }

    public void run() {
        try {
            final String PATH = "https://cs.binghamton.edu/~kfranke1/assignment5/";
            URL url = new URL(PATH + "update.php");
            HttpURLConnection connect = (HttpURLConnection) url
                    .openConnection();
            connect.setReadTimeout(15000);
            connect.setConnectTimeout(15000);
            connect.setRequestMethod("POST");
            connect.setDoInput(true);
            connect.setDoOutput(true);

            OutputStream os = connect.getOutputStream();
            String s = "username=" + username + "&newAvatar=" + newAvatar + "&newBio=" + newBio;
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