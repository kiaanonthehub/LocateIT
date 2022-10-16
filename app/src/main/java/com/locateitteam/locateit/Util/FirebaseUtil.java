package com.locateitteam.locateit.Util;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.locateitteam.locateit.Model.SettingModel;
import com.locateitteam.locateit.SavedPlaceModel;

import java.util.List;

// generic class to read, write and update a node
public class FirebaseUtil {

    public static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Settings").child(CurrentUser.userId);
    public static DatabaseReference read_saved_locations = FirebaseDatabase.getInstance().getReference().child("Saved Locations").child(CurrentUser.userId);


    // method to write to firebase
    public static void WriteToFirebase(SettingModel settings) {
        mDatabase.setValue(settings);
    }

    public static void WriteToFirebase(SavedPlaceModel savedPlaceModel) {
        DatabaseReference write_saved_locations = FirebaseDatabase.getInstance().getReference().child("Saved Locations").child(CurrentUser.userId).child(savedPlaceModel.getAddress());
        write_saved_locations.setValue(savedPlaceModel);
    }
}
