package com.aaron.chau.index.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aaron.chau.index.R;
import com.aaron.chau.index.models.MySqlViaPHP;

public class Register extends AppCompatActivity {
    private static final String TAG = Register.class.getName();
    private EditText fullname;
    private EditText username;
    private EditText password;
    private EditText confirmPassword;
    private EditText email;
    private Button registerBtn;
    private TextView errorMsg;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fullname = (EditText) findViewById(R.id.registerFullName);
        username = (EditText) findViewById(R.id.registerUsername);
        password = (EditText) findViewById(R.id.registerPassword);
        confirmPassword = (EditText) findViewById(R.id.registerConfirmPassword);
        email = (EditText) findViewById(R.id.registerEmail);
        registerBtn = (Button) findViewById(R.id.registerRegisterBtn);
        errorMsg = (TextView) findViewById(R.id.registerErrorMsg);
        registerBtn.setOnClickListener(new RegisterClickListener());

    }

    private class RegisterClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            v.setEnabled(false);
            if (!validatePassword()) errorMsg.setText("Passwords and confirm password do not match.");
            if (!validateET(confirmPassword)) errorMsg.setText("Confirm password field cannot be blank.");
            if (!validateET(password)) errorMsg.setText("Password field cannot be blank.");
            if (!validateET(username)) errorMsg.setText("Username field cannot be blank.");
            if (!validateET(fullname)) errorMsg.setText("Full name field cannot be blank.");
            if (validateET(fullname) && validateET(username) && validateET(password)
                    && validateET(confirmPassword) && validatePassword()) {
                errorMsg.setVisibility(View.INVISIBLE);
                try {
                    new MySqlViaPHP().execute(
                            "INSERT INTO Users (fullname, username, password, email) " +
                                    "VALUES (" + getText(fullname) + "," + getText(username) +
                                    "," + getText(password) + "," + getText(email) + ")"
                    );
                    Toast.makeText(Register.this, "Account created!", Toast.LENGTH_LONG).show();
                    finish();
                } catch (Exception e) {
                    Log.e(TAG, "Error: " + e.getMessage());
                }
            } else {
                errorMsg.setVisibility(View.VISIBLE);
            }
            v.setEnabled(true);
        }
    }

    private boolean validateET(EditText theET) {
        return !theET.getText().toString().trim().isEmpty();
    }

    private boolean validatePassword() {
        return password.getText().toString().trim()
                .equals(confirmPassword.getText().toString().trim());
    }

    private String getText(EditText theET) {
        return AddItemActivity.view2String(theET);
    }
}
