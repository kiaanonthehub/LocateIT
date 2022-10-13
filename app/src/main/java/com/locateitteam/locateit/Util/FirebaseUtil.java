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

import java.util.List;

// generic class to read, write and update a node
public class FirebaseUtil {

    // firebase obj
    public static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Settings").child(CurrentUser.userId);

    // method to write to firebase
    public static void WriteToFirebase(SettingModel settings){
        mDatabase.setValue(settings);
    }
}
