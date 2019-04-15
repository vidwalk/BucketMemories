package com.example.bucketnotes.bucketmemories;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    //shared preference
    SharedPreferences quotePreferences;
    public static final String MyPREFERENCES = "quoteOfTheDay" ;
    //Constants
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String TITLE = "title";
    private static final String DATABASE_COLLECTION = "Journal";
    private static final String USER_ID = "current_user";
    List<JournalEntry> journalEntryList;
    FirebaseFirestore db;
    RecyclerView mRecyclerView;
    JournalEntry journalEntry;
    public String text,author,title;

    ClickListener clickListener;
    private FirebaseAuth mFirebaseAuthentication;

    //Firestore Recycleradapter
    private FirestoreRecyclerAdapter<JournalEntry, journalViewHolder> adapter;
    String userId;

    static final String REQUEST_URL = "https://quotes.rest/qod.json";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        db = FirebaseFirestore.getInstance();
        //Get current user
        mFirebaseAuthentication = FirebaseAuth.getInstance();


        //methods
        activityBegin();
        if (mFirebaseAuthentication.getCurrentUser() != null) {
            firestoreRecycler();

        }else{
            Toast.makeText(MainActivity.this,"Sign Up",Toast.LENGTH_LONG).show();
            Intent Login = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(Login);
        }
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                final String documentId = (String) viewHolder.itemView.getTag();
                db.collection(DATABASE_COLLECTION)
                        .document(documentId)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, getString(R.string.entry_delete_snapshot));

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: " + e.getLocalizedMessage());
                            }
                        });
            }
        }).attachToRecyclerView(mRecyclerView);

        adapter.startListening();
        mRecyclerView.setAdapter(adapter);

    }


    private void firestoreRecycler() {
        mFirebaseAuthentication = FirebaseAuth.getInstance();
        //GET Current User Id
        userId = mFirebaseAuthentication.getCurrentUser().getUid();

        //Retrieval Query  order
        Query query = db.collection(DATABASE_COLLECTION).whereEqualTo(USER_ID,userId)
                .orderBy(TITLE, Query.Direction.ASCENDING);


        //Journal Entry -model containing data
        FirestoreRecyclerOptions<JournalEntry> options = new FirestoreRecyclerOptions.Builder<JournalEntry>()
                .setQuery(query, new SnapshotParser<JournalEntry>() {
                    @NonNull
                    @Override
                    public JournalEntry parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        JournalEntry jE = snapshot.toObject(JournalEntry.class);
                         jE.setDocumentId(snapshot.getId());
                        return jE;

                    }
                }).setLifecycleOwner(this)
                .build();

        //Adapter takes in model and view holder which is bound to layout
        adapter = new FirestoreRecyclerAdapter<JournalEntry, journalViewHolder>(options) {
            @Override
            protected void onBindViewHolder(final journalViewHolder holder, final int position, final JournalEntry model) {
                holder.setIsRecyclable(false);
                holder.itemView.setTag(model.getDocumentId());
                holder.setJournalEntryTitle(model.getTitle());
                holder.setJournalEntryAuthor(model.getAuthor());
                holder.setJournalEntryText(model.getText());

                author = model.getAuthor();
                text=model.getText();
                title= model.getTitle();

                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent yt = new Intent(MainActivity.this, DetailActivity.class);

                        yt.putExtra("TITLE",model.getTitle());
                        yt.putExtra("AUTHOR",model.getAuthor());
                        yt.putExtra("TEXT",model.getText());
                        yt.putExtra("FROM","MainActivity");

                        MainActivity.this.startActivity(yt);
                    }
                });

            }


            @NonNull
            @Override
            public journalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                //inflated with recycler_view layout
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_recycler_items, parent, false);
                return new journalViewHolder(view);
            }
        };

    }


    private void activityBegin() {

        //Linear Layout Manager
        LinearLayoutManager mLinearLayout = new LinearLayoutManager(this);
        mLinearLayout.setOrientation(LinearLayoutManager.VERTICAL);


        journalEntryList = new ArrayList<>();
        journalEntry = new JournalEntry();


        //RecyclerView of Journal Adapter
        mRecyclerView = findViewById(R.id.recycler_journal_items);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLinearLayout);

        //opens add entry activity
        addEntryStart();

    }


    private void addEntryStart() {
        FloatingActionButton fabAddCard;
        fabAddCard = findViewById(R.id.floatingbutton_add_entry);
        fabAddCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Starts the add activity
                Intent enterEntries = new Intent(MainActivity.this, EnterEntriesActivity.class);
                startActivity(enterEntries);
            }
        });

    }


    //viewholder class - inner class for use by recycler view adapter to hold views
    class journalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private View view;

        journalViewHolder(View itemView) {
            super(itemView);
            view = itemView;


        }

        void setJournalEntryTitle(String journalEntryTitle) {
            if (!journalEntryTitle.isEmpty()) {
                TextView tvTitleRecyclerList = view.findViewById(R.id.text_title_recycler_list);
                tvTitleRecyclerList.setText(journalEntryTitle);
            }

        }

        void setJournalEntryAuthor(String journalEntryAuthor) {
            if (!journalEntryAuthor.isEmpty()) {
                TextView tvAuthorRecyclerList = view.findViewById(R.id.text_author_recycler_list);
                tvAuthorRecyclerList.setText(journalEntryAuthor);
            }
        }

        void setJournalEntryText(String journalEntryText) {
            if (!journalEntryText.isEmpty()) {
                TextView tvTextRecyclerList = view.findViewById(R.id.text_entry_recycler_list);
                tvTextRecyclerList.setText(journalEntryText);
            }
        }


        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
        }
    }



        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.menu_main, menu);
            return super.onCreateOptionsMenu(menu);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            switch (id) {
                case R.id.action_signout:
                        signOut();
                        break;
                case R.id.action_quote:
                    //Add quote to show the quote of the day with a toast
                    //Reason: as inspiration for the stories
                    GetQuoteAsync task = new GetQuoteAsync();
                    task.execute(REQUEST_URL);

                    break;
            }
            return super.onOptionsItemSelected(item);
        }

        public void signOut() {
        //sign out
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.signOut();
            Intent out = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(out);
        }

        @Override
        protected void onStart() {
            super.onStart();
            //adapter starts listening on start
            if (mFirebaseAuthentication.getCurrentUser() != null) {
                adapter.startListening();
            }else{
                Intent Login = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(Login);
            }

        }

        @Override
        protected void onStop() {
            super.onStop();
            //on stop the adapter stops listening
            if (mFirebaseAuthentication.getCurrentUser() != null) {
                adapter.stopListening();
            }
        }
        
        //Networking side to get the Quote from the web api

    private JSONObject makeHttpRequest(URL url) throws IOException {
        JSONObject jsonResponse = null;

        if (url == null)
            return jsonResponse;

        HttpURLConnection urlConnection = null;
        InputStream is = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                is = urlConnection.getInputStream();
                String response = readFromStream(is);
                jsonResponse = new JSONObject(response);
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
            if (is != null)
                is.close();
        }
        return jsonResponse;
    }

    private String readFromStream(InputStream is) {

        StringBuilder output = new StringBuilder();
        if (is != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(is, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = null;

            try {
                line = reader.readLine();

            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return output.toString();

    }

    private class GetQuoteAsync extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            URL url;
            String result = "";
            try {
                url = new URL(strings[0]);
                JSONObject contents = makeHttpRequest(url);
                result = contents.getJSONObject("contents").getJSONArray("quotes").getJSONObject(0).getString("quote");

            } catch (IOException| NullPointerException | JSONException e) {
                e.printStackTrace();
            }
            if(result.equals("")) {
                quotePreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                result = quotePreferences.getString(MyPREFERENCES, "The limit of 10 per hour has been exceeded");
            }
            else {
                SharedPreferences.Editor editor = quotePreferences.edit();
                editor.putString(MyPREFERENCES, result);
                editor.commit();
            }
            return result ;
        }
        protected void onPostExecute(String result) {
                Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();
        }
    }

    }




