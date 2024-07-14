package com.gaurav.covidvaccinationapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FetchUserRecordsActivity extends AppCompatActivity {
    private EditText emailEditText;
    private Button fetchButton;
    private TextView userRecordsTextView;

    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch_user_records);

        emailEditText = findViewById(R.id.emailEditText);
        fetchButton = findViewById(R.id.fetchButton);
        userRecordsTextView = findViewById(R.id.userRecordsTextView);

        usersRef = FirebaseDatabase.getInstance().getReference("users");

        fetchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchUserRecords();
            }
        });
    }

    private void fetchUserRecords() {
        String email = emailEditText.getText().toString();

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Enter email");
            return;
        }

        usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    StringBuilder userInfo = new StringBuilder();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        userInfo.append("Name: ").append(user.getName()).append("\n")
                                .append("Mobile: ").append(user.getMobile()).append("\n")
                                .append("Address: ").append(user.getAddress()).append("\n")
                                .append("Pincode: ").append(user.getPincode()).append("\n")
                                .append("Email: ").append(user.getEmail()).append("\n")
                                .append("Vaccinated: ").append(user.isVaccinated() ? "Yes" : "No").append("\n\n");
                    }
                    userRecordsTextView.setText(userInfo.toString());
                } else {
                    userRecordsTextView.setText("No user found with the provided email.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(FetchUserRecordsActivity.this, "Error fetching user records", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
