package com.locateitteam.locateit.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // initialise components
        spinnerMetric = findViewById(R.id.spinnerMetricSetting);
        btnSave = findViewById(R.id.btnSaveSettings);
        spinnerPreferredMapType = findViewById(R.id.spinnerMapType);
        spinnerPreferredTransit = findViewById(R.id.spinnerTravelType);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(SettingsActivity.this, R.array.MetricSelections, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerMetric.setAdapter(adapter);

        ArrayAdapter<CharSequence> MapTypeAdapter = ArrayAdapter.createFromResource(SettingsActivity.this, R.array.MapType, android.R.layout.simple_spinner_item);
        MapTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerPreferredMapType.setAdapter(MapTypeAdapter);

        ArrayAdapter<CharSequence> TravelTypeAdapter = ArrayAdapter.createFromResource(SettingsActivity.this, R.array.ModeOfTravel, android.R.layout.simple_spinner_item);
        TravelTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerPreferredTransit.setAdapter(TravelTypeAdapter);

        FirebaseUtil.mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                List<String> list = new ArrayList<>();

                // iterate through the obj pulled from firebase
                for(DataSnapshot mySnapshot: snapshot.getChildren()){

                    list.add(mySnapshot.getValue().toString());
                }

                // comment this link https://stackoverflow.com/questions/2390102/how-to-set-selected-item-of-spinner-by-value-not-by-position
                if(list.size()>0){
                    int spinnerPosition = adapter.getPosition(list.get(0));
                    spinnerMetric.setSelection(spinnerPosition);

                    spinnerPosition = MapTypeAdapter.getPosition(list.get(1));
                    spinnerPreferredMapType.setSelection(spinnerPosition);

                    spinnerPosition = TravelTypeAdapter.getPosition(list.get(2));
                    spinnerPreferredTransit.setSelection(spinnerPosition);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // get the users selection from the ui and populate the current users settings
                CurrentUser.metricSelection = spinnerMetric.getSelectedItem().toString();
                CurrentUser.MapType = spinnerPreferredMapType.getSelectedItem().toString();
                CurrentUser.TravelType = spinnerPreferredTransit.getSelectedItem().toString();

                // write to firebase
                FirebaseUtil.WriteToFirebase(new SettingModel(CurrentUser.metricSelection, CurrentUser.MapType,CurrentUser.TravelType));

                Intent i = new Intent(SettingsActivity.this, MapsActivity.class);
                startActivity(i);
            }
        });

    }

}