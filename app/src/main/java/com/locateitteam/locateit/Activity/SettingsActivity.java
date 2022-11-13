package com.locateitteam.locateit.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.locateitteam.locateit.Model.SettingModel;
import com.locateitteam.locateit.R;
import com.locateitteam.locateit.Util.CurrentUser;
import com.locateitteam.locateit.Util.FirebaseUtil;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {
    //Variables
    public boolean isMiles = false;
    //Components
    Spinner spinnerMetric,spinnerPreferredMapType,spinnerPreferredTransit;
    Button btnSave;
    Button  btnLogout;
    Button btnShare;
    Switch swActivate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // initialise components
        spinnerMetric = findViewById(R.id.spinnerMetricSetting);
        btnSave = findViewById(R.id.btnSaveSettings);
        spinnerPreferredMapType = findViewById(R.id.spinnerMapType);
        spinnerPreferredTransit = findViewById(R.id.spinnerTravelType);
        swActivate = findViewById(R.id.switchActivtate);
        btnLogout = findViewById(R.id.btnLogout);
        btnShare = findViewById(R.id.btnShare);

        // initilise adapter to read current settings from user in firebase
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(SettingsActivity.this, R.array.MetricSelections, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerMetric.setAdapter(adapter);

        // initilise adapter to read current settings from user in firebase
        ArrayAdapter<CharSequence> MapTypeAdapter = ArrayAdapter.createFromResource(SettingsActivity.this, R.array.MapType, android.R.layout.simple_spinner_item);
        MapTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerPreferredMapType.setAdapter(MapTypeAdapter);

        // initilise adapter to read current settings from user in firebase
        ArrayAdapter<CharSequence> TravelTypeAdapter = ArrayAdapter.createFromResource(SettingsActivity.this, R.array.ModeOfTravel, android.R.layout.simple_spinner_item);
        TravelTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerPreferredTransit.setAdapter(TravelTypeAdapter);

        // read from firebase
        FirebaseUtil.mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // declare list
                List<String> list = new ArrayList<>();

                // iterate through the obj pulled from firebase
                for (DataSnapshot mySnapshot : snapshot.getChildren()) {

                    list.add(mySnapshot.getValue().toString());
                }

                // code attribution
                // https://stackoverflow.com/questions/2390102/how-to-set-selected-item-of-spinner-by-value-not-by-position
                if(list.size()>0){
                    int spinnerPosition = adapter.getPosition(list.get(1));
                    spinnerMetric.setSelection(spinnerPosition);

                    spinnerPosition = MapTypeAdapter.getPosition(list.get(2));
                    spinnerPreferredMapType.setSelection(spinnerPosition);

                    spinnerPosition = TravelTypeAdapter.getPosition(list.get(3));
                    spinnerPreferredTransit.setSelection(spinnerPosition);

                    swActivate.setChecked(Boolean.parseBoolean(list.get(0)));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // get the users selection from the ui and populate the current users settings
                CurrentUser.metricSelection = spinnerMetric.getSelectedItem().toString();
                CurrentUser.MapType = spinnerPreferredMapType.getSelectedItem().toString();
                CurrentUser.TravelType = spinnerPreferredTransit.getSelectedItem().toString();
                CurrentUser.activateSettings = swActivate.isChecked();
                // write to firebase
                FirebaseUtil.WriteToFirebase(new SettingModel(CurrentUser.metricSelection, CurrentUser.MapType,CurrentUser.TravelType,CurrentUser.activateSettings));

                finish();
            }
        });

        btnLogout.setOnClickListener(view13 -> {
            //promptLogoutConfirmation();
            logout();
        });

    }
//    private void promptLogoutConfirmation() {
//        //Use the context of current activity
//        final AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
//        builder.setTitle("Logout");
//        builder.setIcon(R.drawable.ic_logout);
//        builder.setMessage("Are you sure you want to logout ?");
//        builder.setCancelable(true);
//
//        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                FirebaseAuth.getInstance().signOut();
//                GoogleSignIn.getClient(
//                        SettingsActivity.this,
//                        new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
//                ).signOut();
//                Intent logoutIntent = new Intent(SettingsActivity.this, LoginActivity.class);
//                logoutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);//flags to clear your history
//                startActivity(logoutIntent);
//                dialogInterface.dismiss();
//                finish();
//                //dont forget to clear any user related data in your preferences
//            }
//        });
//
//        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                dialogInterface.dismiss();
//            }
//        });
//
//
//        AlertDialog alertDialog = builder.create();
//        alertDialog.show();
//    }

    private void logout() {
            // instantiate alert dialog object
            AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);

            // set properties
            builder.setTitle("Logout");
            builder.setIcon(R.drawable.ic_logout);
            builder.setMessage("Are you sure?");

            // if user selects yes
            builder.setPositiveButton("YES", (dialog, which) -> {

                // instantiate intent object to navigate to the sign in screen
                FirebaseAuth.getInstance().signOut();
                GoogleSignIn.getClient(
                        this,
                        new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
                ).signOut();
                startActivity(new Intent(this, LoginActivity.class));
                finish();

                // close dialog
                dialog.dismiss();

            });

            // if user selects no
            builder.setNegativeButton("NO", (dialog, which) -> {

                // instantiate intent object to navigate to the navigation home screen
                Intent intent = new Intent(getApplication(), SettingsActivity.class);
                startActivity(intent);

                // close dialog
                dialog.dismiss();

            });

            // share app
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // @Tyler
            }
        });

            // create and display the dialog
            AlertDialog alert = builder.create();
            alert.show();

        }
    }
