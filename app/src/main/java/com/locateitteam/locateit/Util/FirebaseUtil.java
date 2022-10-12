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
    private static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    // method to write to firebase
    public static void WriteToFirebase(SettingModel settings){

        mDatabase.child("Settings").child(CurrentUser.userId).setValue(settings);
    }

    // method to read from firebase
    public static SettingModel ReadFromFirebase(){

        // clear list - reusablility

        // isntantiate settings obj
        SettingModel settingModel = new SettingModel();

        // declare and initalise list
        List<SettingModel> lstSettingsModel = null;

        // get the current
        mDatabase = mDatabase.child("Settings").child(CurrentUser.userId);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                lstSettingsModel.add(snapshot.getValue(SettingModel.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // list will alway contain one element which is index 0
        return lstSettingsModel.get(0);
    }

}
