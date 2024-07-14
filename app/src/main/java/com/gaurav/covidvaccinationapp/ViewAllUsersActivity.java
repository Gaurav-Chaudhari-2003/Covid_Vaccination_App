package com.gaurav.covidvaccinationapp;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewAllUsersActivity extends AppCompatActivity {
    private TextView usersTextView;

    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_users);

        usersTextView = findViewById(R.id.usersTextView);

        usersRef = FirebaseDatabase.getInstance().getReference("users");

        fetchAllUsers();
    }

    private void fetchAllUsers() {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                StringBuilder usersInfo = new StringBuilder();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    usersInfo.append("Name: ").append(user.getName()).append("\n")
                            .append("Mobile: ").append(user.getMobile()).append("\n")
                            .append("Address: ").append(user.getAddress()).append("\n")
                            .append("Pincode: ").append(user.getPincode()).append("\n")
                            .append("Email: ").append(user.getEmail()).append("\n")
                            .append("Vaccinated: ").append(user.isVaccinated() ? "Yes" : "No").append("\n\n");
                }
                usersTextView.setText(usersInfo.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ViewAllUsersActivity.this, "Error fetching users", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
