package com.aaron.chau.index.activities;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.aaron.chau.index.MainActivity;
import com.aaron.chau.index.R;
import com.aaron.chau.index.models.MySqlViaPHP;

import org.json.JSONArray;

import java.util.Calendar;

public class Login extends AppCompatActivity {
    private static final String TAG = Login.class.getName();
    private EditText myUsername;
    private EditText myPassword;
    private Button myLoginBtn;
    private Button myRegisterBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Remove the back button on the login page. Not sure why it's there in the first place.
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Check if the user is already logged in.
        SharedPreferences userPref = getSharedPreferences("userSession", Context.MODE_PRIVATE);
        final String username = userPref.getString("username", "\"\"");
        final String session = userPref.getString("sessionId", "\"\"");
//        Log.d(TAG, "Session username: " + username + ", sessionId: " + session);
        if (sessionIsValid(username, session)) {
//            Log.d(TAG, "Session is valid.");
            goToMain();
        } else {
//            Log.d(TAG, "Session is invalid.");
            myUsername = (EditText) findViewById(R.id.loginUsername);
            myPassword = (EditText) findViewById(R.id.loginPassword);
            myLoginBtn = (Button) findViewById(R.id.loginLoginBtn);
            myRegisterBtn = (Button) findViewById(R.id.loginRegisterBtn);

            myLoginBtn.setOnClickListener(new LoginClickListener());
            myRegisterBtn.setOnClickListener(new RegisterClickListener());
        }
    }

    private void goToMain() {
        Intent mainIntent = new Intent(this, MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        Toast.makeText(this, "Welcome!", Toast.LENGTH_LONG).show();
        finish();
    }

    private boolean sessionIsValid(String theUsername, String theSession) {
        try {
            JSONArray results = new MySqlViaPHP().execute(
                    "SELECT * " +
                            "FROM Users " +
                            "WHERE username = " + theUsername + " " +
                            "AND sessionId = " + theSession
            ).get();
//            Log.d(TAG, "sessionIsValid: results = " + results.toString());
            return results.length() == 1;
        } catch (Exception e) {
//            Log.d(TAG, "Session check failed. - " + e.getMessage());
        }
        return false;
    }

    private int getRandomNumber() {
        return (int) (Math.random() * 1
                + Math.random() * 10
                + Math.random() * 100
                + Math.random() * 1000);
    }

    private String generateSessionId() {
        Calendar cal = Calendar.getInstance();
        return "" + cal.getTimeInMillis() + getRandomNumber();
    }

    private class LoginClickListener implements View.OnClickListener {
        @Override
        public void onClick(final View v) {
            v.setEnabled(false);
            myRegisterBtn.setEnabled(false);
            final String username = AddItemActivity.view2String(myUsername);
            final String password = AddItemActivity.view2String(myPassword);

            try {
                JSONArray results = new MySqlViaPHP().execute(
                        "SELECT * " +
                                "FROM Users " +
                                "WHERE username = " + username + " " +
                                "AND password = " + password
                ).get();
                if (results.length() == 0) {
                    // Error. User and password combination not found.
//                    Log.d(TAG, "User not found.");
                    Toast.makeText(Login.this, "Could not find this user and password combination.",
                            Toast.LENGTH_LONG).show();
                } else if (results.length() == 1) {
                    // Found the user.
//                    Log.d(TAG, "Found user. Logging in!");
                    final String sessionId = AddItemActivity.addQuotes2String(generateSessionId());
                    SharedPreferences userPref = getSharedPreferences("userSession", Context.MODE_PRIVATE);
                    SharedPreferences.Editor userPrefEditor = userPref.edit();
                    userPrefEditor.putString("username", username);
                    userPrefEditor.putString("sessionId",sessionId);
                    userPrefEditor.putInt("userId", results.getJSONObject(0).getInt("userId"));
                    userPrefEditor.apply();
                    new MySqlViaPHP().execute(
                            "UPDATE Users " +
                                    "SET sessionId = " + sessionId + " " +
                                    "WHERE Users.username = " + username
                    );
                    goToMain();
                }

            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
            v.setEnabled(true);
            myRegisterBtn.setEnabled(false);
        }
    }

    private class RegisterClickListener implements View.OnClickListener {
        @Override
        public void onClick(final View v) {
            Intent regIntent = new Intent(Login.this, Register.class);
            Login.this.startActivity(regIntent);
        }
    }
}
