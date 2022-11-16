package com.locateitteam.locateit.Activity;

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
                int number = itemModelList.size();
                if(number>=1 && number<3)
                {
                    Toast.makeText(SavedLocationsActivity.this, "Level 1", Toast.LENGTH_SHORT).show();
                }
                if(number>=3 && number<5)
                {
                    Toast.makeText(SavedLocationsActivity.this, "Level 2", Toast.LENGTH_SHORT).show();
                }
                if(number>=5 && number<7)
                {
                    Toast.makeText(SavedLocationsActivity.this, "Level 3", Toast.LENGTH_SHORT).show();
                }
                if(number>=7 && number<9)
                {
                    Toast.makeText(SavedLocationsActivity.this, "Level 4", Toast.LENGTH_SHORT).show();
                }
                if(number>=9 && number<11)
                {
                    Toast.makeText(SavedLocationsActivity.this, "Level 5", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
