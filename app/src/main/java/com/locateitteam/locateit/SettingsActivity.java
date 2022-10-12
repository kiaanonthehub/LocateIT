package com.locateitteam.locateit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.locateitteam.locateit.Model.SettingModel;
import com.locateitteam.locateit.Util.CurrentUser;
import com.locateitteam.locateit.Util.FirebaseUtil;
import com.locateitteam.locateit.Util.Global;

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

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // get the users selection from the ui and populate the current users settings
                CurrentUser.metricSelection = spinnerMetric.getSelectedItem().toString();
                CurrentUser.filteredLocation = spinnerPreferredLandmark.getSelectedItem().toString();

                SettingModel s = new SettingModel(CurrentUser.metricSelection , CurrentUser.filteredLocation);
                // write to firebase
                FirebaseUtil.WriteToFirebase(s);

                //new SettingModel(CurrentUser.metricSelection, CurrentUser.filteredLocation)

                // test
                Toast.makeText(SettingsActivity.this, "Saved as: " + spinnerMetric.getSelectedItem().toString() + " ,Preferred Landmark: "+ spinnerPreferredLandmark.getSelectedItem().toString() , Toast.LENGTH_SHORT).show();
//                Intent i = new Intent(SettingsActivity.this, MapsActivity.class);
//                startActivity(i);
            }
        });

    }

}