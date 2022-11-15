package com.locateitteam.locateit.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.locateitteam.locateit.R;
import com.locateitteam.locateit.Util.CurrentUser;
import com.locateitteam.locateit.Util.Global;

import java.util.HashMap;
import java.util.Map;

public class CreateNotesActivity extends AppCompatActivity {

    // declare var
    EditText createTitle, createContent;
    FloatingActionButton saveNote;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_notes);

        // initialise components
        createTitle = findViewById(R.id.createtitleofnote);
        createContent = findViewById(R.id.createcontentofnote);
        saveNote = findViewById(R.id.savenote);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbarofcreatenote);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        saveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // write to firestore
                String Title = createTitle.getText().toString();
                String Content = createContent.getText().toString();

                if (Title.isEmpty() || Content.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Tile and Content is requried", Toast.LENGTH_SHORT).show();
                } else {
                    // store data
                    DocumentReference documentReference = firebaseFirestore.collection("Notes").document(CurrentUser.userId).collection("myNotes").document();

                    Map<String, Object> note = new HashMap<>();

                    note.put("Title", Title);
                    note.put("Content", Content);

                    documentReference.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getApplicationContext(), "Note created successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(CreateNotesActivity.this, NotesActivity.class));

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Failed to create note.", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}