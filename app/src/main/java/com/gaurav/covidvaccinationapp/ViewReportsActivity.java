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

public class ViewReportsActivity extends AppCompatActivity {
    private TextView reportsTextView;

    private DatabaseReference usersRef, slotsRef;
    private long vaccinatedUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_reports);

        reportsTextView = findViewById(R.id.reportsTextView);

        usersRef = FirebaseDatabase.getInstance().getReference("users");
        slotsRef = FirebaseDatabase.getInstance().getReference("slots");

        fetchReports();
    }

    private void fetchReports() {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long totalUsers = dataSnapshot.getChildrenCount();
                vaccinatedUsers = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (user.isVaccinated()) {
                        vaccinatedUsers++;
                    }
                }

                slotsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        long totalSlots = dataSnapshot.getChildrenCount();

                        String report = "Total Users: " + totalUsers + "\n" +
                                "Vaccinated Users: " + vaccinatedUsers + "\n" +
                                "Total Slots: " + totalSlots + "\n";
                        reportsTextView.setText(report);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(ViewReportsActivity.this, "Error fetching reports", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ViewReportsActivity.this, "Error fetching reports", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
