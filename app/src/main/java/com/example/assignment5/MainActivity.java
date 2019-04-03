package com.example.assignment5;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

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
        // Do whatever you want here
    }
}
