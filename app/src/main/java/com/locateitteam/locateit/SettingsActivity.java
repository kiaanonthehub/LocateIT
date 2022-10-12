package com.locateitteam.locateit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.locateitteam.locateit.Model.SettingModel;
import com.locateitteam.locateit.Util.CurrentUser;
import com.locateitteam.locateit.Util.FirebaseUtil;
import com.locateitteam.locateit.Util.Global;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {
    //Variables
    public boolean isMiles = false;
    //Components
    Spinner spinnerMetric,spinnerPreferredLandmark;
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // initialise components
        spinnerMetric = findViewById(R.id.spinnerMetricSetting);
        btnSave = findViewById(R.id.btnSaveSettings);
        spinnerPreferredLandmark = findViewById(R.id.spinnerFilteredLocation);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(SettingsActivity.this, R.array.MetricSelections, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerMetric.setAdapter(adapter);

        ArrayAdapter<CharSequence> landmarkAdapter = ArrayAdapter.createFromResource(SettingsActivity.this, R.array.FilteredLocations, android.R.layout.simple_spinner_item);
        landmarkAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerPreferredLandmark.setAdapter(landmarkAdapter);

        FirebaseUtil.mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                List<String> list = new ArrayList<>();

                // iterate through the obj pulled from firebase
                for(DataSnapshot mySnapshot: snapshot.getChildren()){

                    list.add(mySnapshot.getValue().toString());
                }

                // comment this link https://stackoverflow.com/questions/2390102/how-to-set-selected-item-of-spinner-by-value-not-by-position
                int spinnerPosition = adapter.getPosition(list.get(0));
                spinnerMetric.setSelection(spinnerPosition);

                spinnerPosition = landmarkAdapter.getPosition(list.get(1));
                spinnerPreferredLandmark.setSelection(spinnerPosition);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // get the users selection from the ui and populate the current users settings
                CurrentUser.metricSelection = spinnerMetric.getSelectedItem().toString();
                CurrentUser.filteredLocation = spinnerPreferredLandmark.getSelectedItem().toString();

                // write to firebase
                FirebaseUtil.WriteToFirebase(new SettingModel(CurrentUser.metricSelection, CurrentUser.filteredLocation));

                Intent i = new Intent(SettingsActivity.this, MapsActivity.class);
                startActivity(i);
            }
        });

    }

}