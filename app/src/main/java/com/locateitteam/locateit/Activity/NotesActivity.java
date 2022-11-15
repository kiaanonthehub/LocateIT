package com.locateitteam.locateit.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.locateitteam.locateit.Model.FirebaseModel;
import com.locateitteam.locateit.R;
import com.locateitteam.locateit.Util.CurrentUser;

public class NotesActivity extends AppCompatActivity {

    // declare component
    FloatingActionButton notesFab;
    private FirebaseAuth firebaseAuth;

    RecyclerView mrecyclerview;
    StaggeredGridLayoutManager staggeredGridLayoutManager;

    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;

    FirestoreRecyclerAdapter<FirebaseModel, NoteViewHolder> noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        // initialise component
        notesFab = findViewById(R.id.noteFab);
        firebaseAuth=FirebaseAuth.getInstance();

        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore=FirebaseFirestore.getInstance();

        getSupportActionBar().setTitle("All Notes");

        mrecyclerview=findViewById(R.id.notesRecyclerView);
        mrecyclerview.setHasFixedSize(true);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,staggeredGridLayoutManager.VERTICAL);
        mrecyclerview.setLayoutManager(staggeredGridLayoutManager);

        mrecyclerview.setAdapter(noteAdapter);

        // onclick listner
        notesFab.setOnClickListener(view -> {
            startActivity(new Intent(NotesActivity.this,CreateNotesActivity.class));
        });

        Query query=firebaseFirestore.collection("Notes").document(CurrentUser.userId).collection("myNotes").orderBy("Title",Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<FirebaseModel> allusernotes = new FirestoreRecyclerOptions.Builder<FirebaseModel>().setQuery(query,FirebaseModel.class).build();

        noteAdapter = new FirestoreRecyclerAdapter<FirebaseModel, NoteViewHolder>(allusernotes) {

            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder holder, int position, @NonNull FirebaseModel model) {
            holder.noteTile.setText(model.getTitle());
            holder.noteContent.setText(model.getContent());

            }

            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_layout,parent,false);
            return new NoteViewHolder(view);
            }
        };
    }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
       getMenuInflater().inflate(R.menu.menu,menu);
      return true;
   }

    @Override
   public boolean onOptionsItemSelected(@NonNull MenuItem item) {
       return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(noteAdapter!=null){
            noteAdapter.stopListening();
        }
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {

        private TextView noteTile;
        private TextView noteContent;
        LinearLayout mnote;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            noteTile = itemView.findViewById(R.id.noteTitle);
            noteContent = itemView.findViewById(R.id.notecontent);
            mnote =itemView.findViewById(R.id.note);
        }
    }
}