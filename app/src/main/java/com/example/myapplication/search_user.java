package com.example.myapplication;

import static androidx.constraintlayout.widget.Constraints.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.myapplication.adapter.search_user_recycler_adapter;
import com.example.myapplication.model.UserModel;
import com.example.myapplication.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class search_user extends AppCompatActivity {

    EditText searchInput;
    ImageButton searchButton;
    ImageButton backButton;
    RecyclerView recyclerView;

search_user_recycler_adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        searchInput = findViewById(R.id.search_username_input);
        searchButton = findViewById(R.id.search_button);
        backButton = findViewById(R.id.back_btn);
        recyclerView = findViewById(R.id.search_user_recycler_view);

        searchInput.requestFocus();

        backButton.setOnClickListener((view -> {

            Log.i(TAG, "onCreate: back button is pressed in search user ");
            onBackPressed();
        }));
    searchButton.setOnClickListener((view -> {
        String searchTerm = searchInput.getText().toString();
        if (searchTerm.isEmpty()||searchTerm.length()<3){
            searchInput.setError("Invalid Username");
            return;
        }

        setUpSearchRecyclerView(searchTerm);
    }));
    }

    void setUpSearchRecyclerView(String searchTerm){
//Query to search users from firestore database and show them on recyclerView
        Query query = FirebaseUtil.allUserCollectionRefernce().whereGreaterThanOrEqualTo("username",searchTerm);

        FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>()
                .setQuery(query, UserModel.class).build();

        adapter = new search_user_recycler_adapter(options,getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter!=null){
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter!=null){
            adapter.stopListening();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter!=null){
            adapter.startListening();
        }
    }
}