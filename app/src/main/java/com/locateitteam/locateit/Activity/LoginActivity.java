package com.locateitteam.locateit.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.locateitteam.locateit.R;
import com.locateitteam.locateit.Util.CurrentUser;
import com.locateitteam.locateit.Util.Global;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "EmailPassword";
    private static final int ERROR_DIALOG_REQUEST = 9001;

    // declare java variables
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvSignup, tvForgottenPassword;
    private TextInputLayout inputLayoutemail, inputLayoutpassword;

    private GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 123;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

//    Firebase Auth
//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        if(CurrentUser.userId!= " "){
//            Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
//            startActivity(intent);
//            getUsername();
//        }
//
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Force dark mode off
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // initialise mAuth
        mAuth = FirebaseAuth.getInstance();

        //Google Sign-In
        createRequest();

        findViewById(R.id.google_signIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                signIn();
            }
        });

        // initialise java variables
        etEmail = findViewById(R.id.editTextEmail);
        etPassword = findViewById(R.id.editTextPassword);
        btnLogin = findViewById(R.id.buttonLogin);
        tvSignup = findViewById(R.id.textViewSignUp);
        tvForgottenPassword = findViewById(R.id.textViewForgottenPassword);
        inputLayoutemail = findViewById(R.id.textInputLayout);
        inputLayoutpassword = findViewById(R.id.textInputLayout2);

        // textview click to switch to new view
        tvSignup.setOnClickListener(view -> {

            // instantiate new intent object
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });

        tvForgottenPassword.setOnClickListener(view -> {

            // instantiate new intent object
            Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
            startActivity(intent);
        });

        // login button click action
        btnLogin.setOnClickListener(view -> {

            if (!validateEmail() | !validatePassword()) {
                return;
            }

            // declare variables
            String email, password;

            // initialise variables
            email = etEmail.getText().toString().trim();
            CurrentUser.userId = getUsername();
            password = etPassword.getText().toString().trim();

            // method call to sign into an existing  account
            signIn(email, password);

        });
    }

    // method to sign the user into the app
    private void signIn (String email, String password){
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();

                                // instantiate new intent to navigate to new view
                                Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                                startActivity(intent);

                                // check if the user object is populated
                                if (user != null) {

                                    // initialise current user properties
                                    getUsername();
                                    Toast.makeText(LoginActivity.this, CurrentUser.displayName , Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                reload();
                            }
                        }
                    });
        }

    private void createRequest() {

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                // ...
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null) {
                                //Email address
                                String email = user.getEmail();
                                getGoogleUsername(email);
                                Toast.makeText(LoginActivity.this, CurrentUser.displayName , Toast.LENGTH_SHORT).show();
                            }

                            Log.d("User Id", user.getUid());

                            //CURRENT_USER.email=mAuth.getCurrentUser().getEmail();
                            Intent intent = new Intent(getApplicationContext(),MapsActivity.class);
                            startActivity(intent);


                        } else {
                            Toast.makeText(LoginActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();


                        }


                        // ...
                    }
                });
    }

    // reload the activity
    private void reload() {
        finish();
        startActivity(getIntent());
    }

    private String getUsername() {
        // get substring of @ and use as username for user

        String s = etEmail.getText().toString();
        String[] split = s.replace(".","").split("@");
        CurrentUser.displayName = split[0].toLowerCase();
        CurrentUser.email = etEmail.getText().toString().toLowerCase();
        return  split[0].toLowerCase();
    }

    private void getGoogleUsername(String email) {
        // get substring of @ and use as username for user

        String s = email;
        String[] split = s.replace(".","").split("@");
        CurrentUser.displayName = split[0].toLowerCase();
        CurrentUser.email = email.toLowerCase();
    }

    private boolean validateEmail() {

        String emailInput = inputLayoutemail.getEditText().getText().toString().trim();

        if (emailInput.isEmpty()) {
            inputLayoutemail.setError("Email Address is  required*");
            return false;
        } else {
            inputLayoutemail.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {

        String passwordInput = inputLayoutpassword.getEditText().getText().toString().trim();

        if (passwordInput.isEmpty()) {
            inputLayoutpassword.setError("Password is required*");
            return false;
        } else {
            inputLayoutpassword.setError(null);
            return true;
        }
    }

    // check if the user has the correct google play services version installed
    public boolean isServicesOK() {
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(LoginActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            // the version is up to date and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            // an error occurred but can be resolved
            Log.d(TAG, "isServicesOK: an error occurred but can be resolved");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(LoginActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "Map request cannot be performed", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}