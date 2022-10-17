package com.locateitteam.locateit.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.locateitteam.locateit.Model.SettingModel;
import com.locateitteam.locateit.Model.UserModel;
import com.locateitteam.locateit.R;
import com.locateitteam.locateit.Util.CurrentUser;
import com.locateitteam.locateit.Util.FirebaseUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "EmailPassword";

    // digit + lowercase char + uppercase char + punctuation + symbol
    private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$";
    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

    // declare componenets
    private TextInputLayout inputLayoutName, inputLayoutSurname, inputLayoutEmail, inputLayoutPassword;
    private EditText etFirstname, etLastname, etEmail, etPassword;
    private FirebaseAuth mAuth;
    boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // initialise mAuth
        mAuth = FirebaseAuth.getInstance();

        // initialise java variables
        etFirstname = findViewById(R.id.editTextFirstname);
        etLastname = findViewById(R.id.editTextLastname);
        etEmail = findViewById(R.id.editTextSignupEmail);
        etPassword = findViewById(R.id.editTextSignupPassword);
        Button btnSignup = findViewById(R.id.buttonSignup);
        TextView tvLogin = findViewById(R.id.textViewLogin);
        inputLayoutName = findViewById(R.id.textInputLayout3);
        inputLayoutSurname = findViewById(R.id.textInputLayout4);
        inputLayoutEmail = findViewById(R.id.textInputLayout5);
        inputLayoutPassword = findViewById(R.id.textInputLayout6);

        // textview click to switch to new view
        tvLogin.setOnClickListener(view -> {

            // instantiate new intent object
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        // login button click action
        btnSignup.setOnClickListener(view -> {


            if (!validateName() | !validateSurname() | !validateEmail() | !validatePassword()) {
                return;
            }

            // declare variables
            String firstname, lastname, email, password;

            // initialise variables
            firstname = etFirstname.getText().toString();
            lastname = etLastname.getText().toString();
            email = etEmail.getText().toString();
            password = etPassword.getText().toString();


            // method call to create a account
            createAccount(email, password);

            // instantiate a user object
            UserModel u = new UserModel(firstname, lastname, email);

            // initialise the current user
            getUsername();

            Toast.makeText(this, CurrentUser.displayName, Toast.LENGTH_SHORT).show();

            // instantiate new intent object
            Intent intent = new Intent(SignupActivity.this, MapsActivity.class);
            startActivity(intent);
        });
    }

    // method used to check if the password is valid
    public static boolean isValid(final String password) {
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    // method to create an account for the user to use for the app
    private void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {

                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        getUsername();
                        FirebaseUtil.WriteToFirebase(new SettingModel("Kilometers","Default","Driving",false));
                        flag = true;

                    } else {

                        // if sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(SignupActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        reload();
                    }
                });
    }

    // reload the activity
    private void reload() {
        Intent i = new Intent(SignupActivity.this, SignupActivity.class);
        finish();
        overridePendingTransition(0, 0);
        startActivity(i);
        overridePendingTransition(0, 0);
    }

    // get the username from the email
    private void getUsername() {
        // get substring of @ and use as username for user

        String s = etEmail.getText().toString();
        String[] split = s.replace(".", "").split("@");
        CurrentUser.displayName = split[0].toLowerCase();
        CurrentUser.email = etEmail.getText().toString().toLowerCase();
        CurrentUser.userId = split[0].toLowerCase();
    }

    // validate the users name
    private boolean validateName() {

        String input = inputLayoutName.getEditText().getText().toString().trim();

        if (input.isEmpty()) {
            inputLayoutName.setError("First Name  is  required*");
            return false;
        } else {
            inputLayoutName.setError(null);
            return true;
        }
    }

    // validate the users surname input
    private boolean validateSurname() {

        String input = inputLayoutSurname.getEditText().getText().toString().trim();

        if (input.isEmpty()) {
            inputLayoutSurname.setError("Surname is  required*");
            return false;
        } else {
            inputLayoutSurname.setError(null);
            return true;
        }
    }

    // validate the users email
    private boolean validateEmail() {

        String input = inputLayoutEmail.getEditText().getText().toString().trim();

        if (input.isEmpty()) {
            inputLayoutEmail.setError("Email Address is  required*");
            return false;
        } else {
            inputLayoutEmail.setError(null);
            return true;
        }
    }

    // validate the users password
    private boolean validatePassword() {

        String input = inputLayoutPassword.getEditText().getText().toString().trim();

        if (input.isEmpty() || input.length() < 8 || input.matches("[0-9]")) {
            inputLayoutPassword.setError("Password is weak."
                    + "\n1. At least 8 characters"
                    + "\n2. A mixture of both uppercase and lowercase letters"
                    + "\n3. A mixture of letters and numbers"
                    + "\n4. Inclusion of at least one special character, e.g., ! @ # ? ]");
            input = "";
            etPassword.getText().clear();

            return false;

        } else {
            inputLayoutPassword.setError(null);
            return true;
        }
    }
}