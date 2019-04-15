package com.example.bucketnotes.bucketmemories;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class DetailActivity extends AppCompatActivity {
    //Constants
    private static final String TAG = DetailActivity.class.getSimpleName();
    private static final String DATABASE_COLLECTION = "Journal";
    private static final String TITLE = "title";
    private static final String AUTHOR = "author";
    private static final String TEXT = "text";
    private static final String TIMESTAMP = "timestamp";

    private FirebaseFirestore db;

    TextView tvTitle;
    TextView tvAuthor;
    TextView tvEntry;
    String searchTitle;
    String searchText;
    String searchAuthor;
    FloatingActionButton fabSetView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        db = FirebaseFirestore.getInstance();

        tvTitle = findViewById(R.id.text_title_view);
        tvAuthor = findViewById(R.id.text_author_view);
        tvEntry = findViewById(R.id.text_entry_view);
        fabSetView = findViewById(R.id.fab_edit_entry);

        String title = getIntent().getStringExtra("TITLE");
        String author = getIntent().getStringExtra("AUTHOR");
        String entry = getIntent().getStringExtra("TEXT");

        tvTitle.setText(title);
        tvAuthor.setText(author);
        tvEntry.setText(entry);
        //Retrieve initial fields so we can use them in a search

        searchTitle = tvTitle.getText().toString();
        searchAuthor = tvAuthor.getText().toString();
        searchText = tvEntry.getText().toString();
        UpdateEntry();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        UpdateEntry();
        return super.onOptionsItemSelected(item);
    }

    private void UpdateEntry()
    {
        final String titleUpdate = tvTitle.getText().toString();
        final String authorUpdate = tvAuthor.getText().toString();
        final String entryUpdate = tvEntry.getText().toString();
        //Check if they are equal to any of the documents already present
        //Low probability of an entry to have all 3 values equal
        db.collection(DATABASE_COLLECTION)
                .whereEqualTo("text", searchText)
                .whereEqualTo("title", searchTitle)
                .whereEqualTo("author", searchAuthor)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                //Update the fields in the documents that match
                                DocumentReference dbReference = db.collection(DATABASE_COLLECTION).document(document.getId());
                                dbReference.update(TITLE, titleUpdate);
                                dbReference.update(AUTHOR, authorUpdate);
                                dbReference.update(TIMESTAMP, FieldValue.serverTimestamp());
                                dbReference.update(TEXT, entryUpdate)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                tvAuthor.setText(authorUpdate);
                                                tvEntry.setText(entryUpdate);
                                                tvTitle.setText(titleUpdate);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error writing document", e);
                                            }
                                        });
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
