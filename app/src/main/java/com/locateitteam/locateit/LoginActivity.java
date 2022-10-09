package com.locateitteam.locateit;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private Button LoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LoginButton = findViewById(R.id.btnLogin);

    LoginButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
            startActivity(intent);
        }
    });


    }

    private void init()
    {
        //
    }

    // check if the user has the correct google play services version installed
    public boolean isServicesOK()
    {
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(LoginActivity.this);

        if(available == ConnectionResult.SUCCESS)
        {
            // the version is up to date and the user can make map requests
            Log.d(TAG,"isServicesOK: Google Play Services is working");
             return true;
        }else if(GoogleApiAvailability.getInstance().isUserResolvableError(available))
        {
            // an error occured but can be resolved
            Log.d(TAG,"isServicesOK: an error occured but can be resolved");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(LoginActivity.this,available,ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "Map request cannot be performed", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}