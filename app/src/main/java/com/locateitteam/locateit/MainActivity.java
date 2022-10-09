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
import com.google.android.gms.common.GoogleApiAvailabilityLight;

public class MainActivity extends AppCompatActivity {

    Button login;
    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login = findViewById(R.id.btnlogin);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(i);

                if(isServicesOK())
                {
                    init();
                }

            }
        });

    }

    private void init()
    {

    }

    // check if the user has the correct google play services version installed
    public boolean isServicesOK()
    {
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if(available == ConnectionResult.SUCCESS)
        {
            // the version is up to date and the user can make map requests
            Log.d(TAG,"isServicesOK: Google Play Services is working");
             return true;
        }else if(GoogleApiAvailability.getInstance().isUserResolvableError(available))
        {
            // an error occured but can be resolved
            Log.d(TAG,"isServicesOK: an error occured but can be resolved");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this,available,ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "Map request cannot be performed", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}