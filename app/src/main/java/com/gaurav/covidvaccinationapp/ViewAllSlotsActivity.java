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

public class ViewAllSlotsActivity extends AppCompatActivity {
    private TextView slotsTextView;

    private DatabaseReference slotsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_slots);

        slotsTextView = findViewById(R.id.slotsTextView);

        slotsRef = FirebaseDatabase.getInstance().getReference("slots");

        fetchAllSlots();
    }

    private void fetchAllSlots() {
        slotsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                StringBuilder slotsInfo = new StringBuilder();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Slot slot = snapshot.getValue(Slot.class);
                    slotsInfo.append("Date: ").append(slot.getDate()).append("\n")
                            .append("Time: ").append(slot.getTime()).append("\n")
                            .append("Location: ").append(slot.getLocation()).append("\n\n");
                }
                slotsTextView.setText(slotsInfo.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ViewAllSlotsActivity.this, "Error fetching slots", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
