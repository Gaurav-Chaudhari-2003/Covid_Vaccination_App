package com.gaurav.covidvaccinationapp;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ViewAllUsersActivity extends AppCompatActivity {

    private static final String TAG = "ViewAllUsersActivity";
    private RecyclerView usersRecyclerView;
    private UserAdapter userAdapter;
    private List<Map<String, Object>> userList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_users);

        db = FirebaseFirestore.getInstance();
        usersRecyclerView = findViewById(R.id.usersRecyclerView);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        userList = new ArrayList<>();
        userAdapter = new UserAdapter(this, userList);

        usersRecyclerView.setAdapter(userAdapter);

        fetchUsers();  // Fetch users with real-time updates
    }

    private void fetchUsers() {
        db.collection("users")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot snapshots, FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        userList.clear();
                        if (snapshots != null) {
                            for (QueryDocumentSnapshot document : snapshots) {
                                Map<String, Object> user = document.getData();
                                userList.add(user);
                            }
                            userAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }
}
