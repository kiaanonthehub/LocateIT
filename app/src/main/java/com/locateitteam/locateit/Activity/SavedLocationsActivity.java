package com.locateitteam.locateit.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.locateitteam.locateit.Model.SettingModel;
import com.locateitteam.locateit.R;
import com.locateitteam.locateit.SavedLocationInterface;
import com.locateitteam.locateit.SavedPlaceModel;
import com.locateitteam.locateit.Util.CurrentUser;
import com.locateitteam.locateit.Util.FirebaseUtil;
import com.locateitteam.locateit.databinding.CardviewSavedLocationsBinding;

import java.util.ArrayList;
import java.util.List;

public class SavedLocationsActivity extends AppCompatActivity implements SavedLocationInterface {

    CardviewSavedLocationsBinding bind;
    SavedLocationsActivity binding;
    RecyclerView recyclerView;
    private FirebaseAuth firebaseAuth;
    private ArrayList<SavedPlaceModel> savedPlaceModelArrayList;
    //private LoadingDialog loadingDialog;
    private FirebaseRecyclerAdapter<String, ViewHolder> firebaseRecyclerAdapter;
    private SavedLocationInterface savedLocationInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_locations);
        recyclerView = findViewById(R.id.savedRecyclerView);

//        //loadingDialog = new LoadingDialog(requireActivity());
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        SnapHelper snapHelper = new LinearSnapHelper();
//        snapHelper.attachToRecyclerView(recyclerView);
//        getSavedPlaces();

    }

    private void getSavedPlaces() {
        //loadingDialog.startLoading();
        Query query = FirebaseDatabase.getInstance().getReference("Users")
                .child(firebaseAuth.getUid()).child("Saved Locations");

        FirebaseRecyclerOptions<String> options = new FirebaseRecyclerOptions.Builder<String>()
                .setQuery(query, String.class).build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<String, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull String savePlaceId) {

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Places").child(savePlaceId);
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {

                            SavedPlaceModel savedPlaceModel = snapshot.getValue(SavedPlaceModel.class);
                            holder.binding.setSavedPlaceModel(savedPlaceModel);
                            holder.binding.setListener(savedLocationInterface);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                CardviewSavedLocationsBinding binding = DataBindingUtil.inflate(LayoutInflater.from(SavedLocationsActivity.this),
                        R.layout.cardview_saved_locations, parent, false);
                return new ViewHolder(binding);
            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
        //binding.savedRecyclerView.setAdapter(firebaseRecyclerAdapter);
        //loadingDialog.stopLoading();
    }

    @Override
    public void onResume() {
        super.onResume();
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    public void onPause() {
        super.onPause();
        firebaseRecyclerAdapter.stopListening();
    }

    @Override
    public void onLocationClick(SavedPlaceModel savedPlaceModel) {
        if (savedPlaceModel.getLat() != null && savedPlaceModel.getLng() != null) {
            Intent intent = new Intent(this, DirectionActivity.class);
            intent.putExtra("placeId", savedPlaceModel.getPlaceId());
            intent.putExtra("lat", savedPlaceModel.getLat());
            intent.putExtra("lng", savedPlaceModel.getLng());

            startActivity(intent);

        } else {
            Toast.makeText(this, "Location Not Found", Toast.LENGTH_SHORT).show();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CardviewSavedLocationsBinding binding;

        public ViewHolder(@NonNull CardviewSavedLocationsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
