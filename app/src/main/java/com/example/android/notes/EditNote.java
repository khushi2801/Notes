package com.example.android.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
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

public class EditNote extends AppCompatActivity {

    Intent data;
    EditText mEditNoteTitle, mEditNoteContent;
    FloatingActionButton mSaveNote;

    ProgressBar mEditNoteProgressBar;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        mEditNoteTitle = findViewById(R.id.edit_note_title);
        mEditNoteContent = findViewById(R.id.edit_note_content);
        mSaveNote = findViewById(R.id.update_note);
        mEditNoteProgressBar = findViewById(R.id.edit_note_progress_bar);

        data = getIntent();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar_of_edit_note);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);

        mSaveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newTitle = mEditNoteTitle.getText().toString();
                String newContent = mEditNoteContent.getText().toString();
                if(newTitle.isEmpty() || newContent.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Both fields are compulsory", Toast.LENGTH_SHORT).show();
                }
                else {
                    mEditNoteProgressBar.setVisibility(View.VISIBLE);
                    DocumentReference documentReference = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").document(data.getStringExtra("noteId"));
                    Map<String, Object> note = new HashMap<>();
                    note.put("title", newTitle);
                    note.put("content", newContent);

                    documentReference.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(), "Note updated successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(EditNote.this, Notes.class));
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Failed to update note", Toast.LENGTH_SHORT).show();
                            mEditNoteProgressBar.setVisibility(View.INVISIBLE);

                        }
                    });
                }
            }
        });
        String noteTitle = data.getStringExtra("title");
        String noteContent = data.getStringExtra("content");
        mEditNoteTitle.setText(noteTitle);
        mEditNoteContent.setText(noteContent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(EditNote.this, Notes.class));
        finish();
    }
}