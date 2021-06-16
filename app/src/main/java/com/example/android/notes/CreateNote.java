package com.example.android.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreateNote extends AppCompatActivity {

    EditText mCreateNoteTitle, mCreateNoteContent;
    FloatingActionButton mSaveNote;

    ProgressBar mCreateNoteProgressBar;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        mCreateNoteTitle = findViewById(R.id.create_note_title);
        mCreateNoteContent = findViewById(R.id.create_note_content);
        mSaveNote = findViewById(R.id.save_note);
        mCreateNoteProgressBar = findViewById(R.id.create_note_progress_bar);

        Toolbar toolbar = findViewById(R.id.toolbar_of_create_note);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        mSaveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = mCreateNoteTitle.getText().toString();
                String content = mCreateNoteContent.getText().toString();
                if(title.isEmpty() || content.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Both fields are compulsory", Toast.LENGTH_SHORT).show();
                }
                else {
                    mCreateNoteProgressBar.setVisibility(View.VISIBLE);
                    DocumentReference documentReference = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").document();
                    Map<String, Object> note = new HashMap<>();
                    note.put("title", title);
                    note.put("content", content);

                    documentReference.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(), "Note created successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(CreateNote.this, Notes.class));
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Failed to create note", Toast.LENGTH_SHORT).show();
                            mCreateNoteProgressBar.setVisibility(View.INVISIBLE);
                            startActivity(new Intent(CreateNote.this, Notes.class));
                            finish();
                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(CreateNote.this, Notes.class));
        finish();
    }
}