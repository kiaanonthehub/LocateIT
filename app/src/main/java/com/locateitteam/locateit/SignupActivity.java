package com.locateitteam.locateit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SignupActivity extends AppCompatActivity {

    // declare component fields
    private Button SignUpButton;
    private TextView TextViewLogin;

    // firebase authentication


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // initialise comoponent fields
        SignUpButton = findViewById(R.id.buttonSignup);
        TextViewLogin = findViewById(R.id.textViewLogin);

        // set actions - navigation
        SignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // ADD SIGN UP FIREBASE CODE HERE
            }
        });


        // navigate to logi view
        TextViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Navigate the user to the login screen
                NavigateToLogin();
            }
        });
    }

    // method to navigate the user to the login screen
    public void NavigateToLogin() {
        Intent intent = new Intent(SignupActivity.this, MapsActivity.class);
        startActivity(intent);
    }

    // method to sign the user into the app
    private void signIn(String email, String password) {

    }


}