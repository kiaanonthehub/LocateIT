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
import androidx.recyclerview.widget.DefaultItemAnimator;
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
import com.locateitteam.locateit.Adapter.SavedLocationsAdapter;
import com.locateitteam.locateit.Model.SettingModel;
import com.locateitteam.locateit.R;
import com.locateitteam.locateit.SavedLocationInterface;
import com.locateitteam.locateit.SavedPlaceModel;
import com.locateitteam.locateit.Util.CurrentUser;
import com.locateitteam.locateit.Util.FirebaseUtil;
import com.locateitteam.locateit.databinding.CardviewSavedLocationsBinding;

import java.util.ArrayList;
import java.util.List;

public class SavedLocationsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    SavedLocationsAdapter savedLocationsAdapter;

    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_locations);

        List<SavedPlaceModel> itemModelList = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.savedRecyclerView);

        itemModelList.add(new SavedPlaceModel("Gateway", "1 Palm Blvd, Umhlanga Ridge, Umhlanga, 4021", " placeId", 4.86, 5, 37.41, -122.07));
        savedLocationsAdapter = new SavedLocationsAdapter(SavedLocationsActivity.this,itemModelList);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(savedLocationsAdapter);
        savedLocationsAdapter.notifyDataSetChanged();

    }


}
