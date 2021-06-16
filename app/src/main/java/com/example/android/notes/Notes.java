package com.example.android.notes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SearchEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Notes extends AppCompatActivity {

    RelativeLayout mEmpty;

    FloatingActionButton mCreateNotesFab;
    FirebaseAuth firebaseAuth;

    RecyclerView mRecyclerView;
    StaggeredGridLayoutManager staggeredGridLayoutManager;

    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;

    FirestoreRecyclerAdapter<firebaseModel, NoteViewHolder> noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        mEmpty =findViewById(R.id.empty);

        mCreateNotesFab = findViewById(R.id.create_note_fab);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        getSupportActionBar().setTitle("All Notes");

        mCreateNotesFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Notes.this, CreateNote.class));
                finish();
            }
        });

        Query query = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").orderBy("title", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<firebaseModel> allUserNotes = new FirestoreRecyclerOptions.Builder<firebaseModel>().setQuery(query, firebaseModel.class).build();

        noteAdapter = new FirestoreRecyclerAdapter<firebaseModel, NoteViewHolder>(allUserNotes) {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, int i, @NonNull firebaseModel firebaseModel) {

                ImageView popUpButton = noteViewHolder.itemView.findViewById(R.id.menu_pop_up);

                int colourCode = getRandomColor();
                noteViewHolder.mNote.setBackgroundColor(noteViewHolder.itemView.getResources().getColor(colourCode, null));

                noteViewHolder.noteTitle.setText(firebaseModel.getTitle());
                noteViewHolder.noteContent.setText(firebaseModel.getContent());

                String docId = noteAdapter.getSnapshots().getSnapshot(i).getId();

                noteViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(),NoteDetails.class);
                        intent.putExtra("title", firebaseModel.getTitle());
                        intent.putExtra("content", firebaseModel.getContent());
                        intent.putExtra("noteId", docId);
                        v.getContext().startActivity(intent);
                        finish();
                    }
                });

                popUpButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                        popupMenu.setGravity(Gravity.END);
                        popupMenu.getMenu().add("Edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                Intent intent = new Intent(v.getContext(), EditNote.class);
                                intent.putExtra("title", firebaseModel.getTitle());
                                intent.putExtra("content", firebaseModel.getContent());
                                intent.putExtra("noteId", docId);
                                v.getContext().startActivity(intent);
                                finish();
                                return false;
                            }
                        });

                        popupMenu.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                DocumentReference documentReference = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").document(docId);
                                documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(v.getContext(), "This note is deleted", Toast.LENGTH_SHORT).show();
                                        if(noteAdapter.getSnapshots().isEmpty()) {
                                            mEmpty.setVisibility(View.VISIBLE);
                                        }
                                        else {
                                            mEmpty.setVisibility(View.INVISIBLE);
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(v.getContext(), "Failed to delete", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return false;
                            }
                        });
                        popupMenu.show();
                    }
                });

                if(noteAdapter.getSnapshots().isEmpty()) {
                    mEmpty.setVisibility(View.VISIBLE);
                }
                else {
                    mEmpty.setVisibility(View.INVISIBLE);
                }
            }

            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_layout, parent, false);
                return new NoteViewHolder(view);
            }
        };

        mRecyclerView = findViewById(R.id.recyclerview);
        mRecyclerView.setHasFixedSize(true);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        mRecyclerView.setAdapter(noteAdapter);

    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {
        private TextView noteTitle;
        private TextView noteContent;
        LinearLayout mNote;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            noteTitle = itemView.findViewById(R.id.note_title);
            noteContent = itemView.findViewById(R.id.note_content);
            mNote = itemView.findViewById(R.id.note);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(Notes.this, MainActivity.class));
        }
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
        if(noteAdapter != null) {
            noteAdapter.stopListening();
        }
    }

    private int getRandomColor() {
        List<Integer> colorCode = new ArrayList<>();
        colorCode.add(R.color.pink);
        colorCode.add(R.color.green);
        colorCode.add(R.color.purple);
        colorCode.add(R.color.red);
        colorCode.add(R.color.indigo);
        colorCode.add(R.color.yellow);
        colorCode.add(R.color.teal);
        colorCode.add(R.color.blue);
        colorCode.add(R.color.orange);
        colorCode.add(R.color.lime);
        colorCode.add(R.color.cyan);
        colorCode.add(R.color.brown);
        colorCode.add(R.color.grey);

        Random random = new Random();
        int number = random.nextInt(colorCode.size());
        return colorCode.get(number);
    }
}