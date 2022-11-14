package com.locateitteam.locateit.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.locateitteam.locateit.R;

public class NotesActivity extends AppCompatActivity {

    // declare component
    FloatingActionButton notesFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        // initialise component
        notesFab = findViewById(R.id.noteFab);

        getSupportActionBar().setTitle("All Notes");

        // onclick listner
        notesFab.setOnClickListener(view -> {
            startActivity(new Intent(NotesActivity.this,CreateNotesActivity.class));
        });
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu,menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//
//
//
//        return super.onOptionsItemSelected(item);
//    }
}