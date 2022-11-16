package com.locateitteam.locateit.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.locateitteam.locateit.Adapter.SavedLocationsAdapter;
import com.locateitteam.locateit.Model.ViewModel.SavedPlacesViewModel;
import com.locateitteam.locateit.R;
import com.locateitteam.locateit.SavedPlaceModel;
import com.locateitteam.locateit.Util.FirebaseUtil;

import java.util.ArrayList;
import java.util.List;

public class SavedLocationsActivity extends AppCompatActivity {

    // declare components
    RecyclerView recyclerView;
    SavedLocationsAdapter savedLocationsAdapter;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_locations);

        // declare list
        List<SavedPlaceModel> itemModelList = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.savedRecyclerView);

        int number_of_saved_locations = itemModelList.size();
        Intent LevelIntent = new Intent(SavedLocationsActivity.this,SettingsActivity.class);
        LevelIntent.putExtra("intVariableName", number_of_saved_locations);
        startActivity(LevelIntent);

        // read from firebase
        FirebaseUtil.read_saved_locations.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // array list
                ArrayList<SavedPlacesViewModel> lstSavedPlaceModelsVm = new ArrayList<>();

                // iterate through the obj pulled from firebase
                for (DataSnapshot mySnapshot : snapshot.getChildren()) {
                    lstSavedPlaceModelsVm.clear();
                    lstSavedPlaceModelsVm.add(mySnapshot.getValue(SavedPlacesViewModel.class));

                    for (SavedPlacesViewModel i : lstSavedPlaceModelsVm) {

                        itemModelList.add(new SavedPlaceModel(i.getName(), i.getAddress(), " placeId", i.getRating(), 0, i.getLat(), i.getLng()));
                        savedLocationsAdapter = new SavedLocationsAdapter(SavedLocationsActivity.this, itemModelList);

                        layoutManager = new LinearLayoutManager(SavedLocationsActivity.this);
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());

                        recyclerView.setAdapter(savedLocationsAdapter);
                        savedLocationsAdapter.notifyDataSetChanged();
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
