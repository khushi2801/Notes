package com.example.android.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class NoteDetails extends AppCompatActivity {

    public static final int FLAG_ACTIVITY_RESET_TASK_IF_NEEDED = 0;


    private TextView mNoteDetailTitle, mNoteDetailContent;
    FloatingActionButton mGoToEditNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        mNoteDetailTitle = findViewById(R.id.create_note_detail_title);
        mNoteDetailContent = findViewById(R.id.create_note_detail_content);
        mGoToEditNote = findViewById(R.id.go_to_edit_note);

        Toolbar toolbar = findViewById(R.id.toolbar_of_note_details);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);

        Intent data = getIntent();
        mGoToEditNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), EditNote.class);
                intent.putExtra("title", data.getStringExtra("title"));
                intent.putExtra("content", data.getStringExtra("content"));
                intent.putExtra("noteId", data.getStringExtra("noteId"));
                v.getContext().startActivity(intent);
                finish();
            }
        });

        mNoteDetailContent.setText(data.getStringExtra("content"));
        mNoteDetailTitle.setText(data.getStringExtra("title"));
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
        startActivity(new Intent(NoteDetails.this, Notes.class));
        finish();
    }
}