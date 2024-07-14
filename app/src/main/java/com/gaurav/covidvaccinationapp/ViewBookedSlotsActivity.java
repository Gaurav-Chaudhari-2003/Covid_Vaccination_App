package com.gaurav.covidvaccinationapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class ViewBookedSlotsActivity extends AppCompatActivity {

    private TextView covishieldLabel, covaxinLabel, covishieldSlotDetails, covaxinSlotDetails;
    private LinearLayout covishieldSlotLayout, covaxinSlotLayout;
    private Button updateCovishieldButton, cancelCovishieldButton, updateCovaxinButton, cancelCovaxinButton;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_booked_slots);

        covishieldLabel = findViewById(R.id.covishieldLabel);
        covaxinLabel = findViewById(R.id.covaxinLabel);
        covishieldSlotDetails = findViewById(R.id.covishieldSlotDetails);
        covaxinSlotDetails = findViewById(R.id.covaxinSlotDetails);
        covishieldSlotLayout = findViewById(R.id.covishieldSlotLayout);
        covaxinSlotLayout = findViewById(R.id.covaxinSlotLayout);
        updateCovishieldButton = findViewById(R.id.updateCovishieldButton);
        cancelCovishieldButton = findViewById(R.id.cancelCovishieldButton);
        updateCovaxinButton = findViewById(R.id.updateCovaxinButton);
        cancelCovaxinButton = findViewById(R.id.cancelCovaxinButton);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        fetchBookedSlots();

        updateCovishieldButton.setOnClickListener(v -> showAvailableSlots("covishield"));
        cancelCovishieldButton.setOnClickListener(v -> cancelSlot("covishield"));
        updateCovaxinButton.setOnClickListener(v -> showAvailableSlots("covaxin"));
        cancelCovaxinButton.setOnClickListener(v -> cancelSlot("covaxin"));
    }

    private void fetchBookedSlots() {
        DocumentReference userRef = db.collection("users").document(currentUser.getUid());

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    Map<String, Object> vaccinatedMap = (Map<String, Object>) document.get("vaccinated");
                    if (vaccinatedMap != null) {
                        for (Map.Entry<String, Object> entry : vaccinatedMap.entrySet()) {
                            String vaccineType = entry.getKey();
                            Map<String, Object> vaccineDetails = (Map<String, Object>) entry.getValue();
                            String status = (String) vaccineDetails.get("status");
                            if ("booked".equals(status)) {
                                String slotId = (String) vaccineDetails.get("slotId");
                                fetchSlotDetails(vaccineType, slotId);
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Failed to fetch booked slots", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchSlotDetails(String vaccineType, String slotId) {
        DocumentReference slotRef = db.collection("slots").document("vaccine").collection(vaccineType).document(slotId);

        slotRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    String date = document.getString("date");
                    String time = document.getString("time");
                    String location = document.getString("location");

                    String details = "Date: " + date + "\nTime: " + time + "\nLocation: " + location;
                    if ("covishield".equals(vaccineType)) {
                        covishieldLabel.setVisibility(View.VISIBLE);
                        covishieldSlotLayout.setVisibility(View.VISIBLE);
                        covishieldSlotDetails.setText(details);
                    } else if ("covaxin".equals(vaccineType)) {
                        covaxinLabel.setVisibility(View.VISIBLE);
                        covaxinSlotLayout.setVisibility(View.VISIBLE);
                        covaxinSlotDetails.setText(details);
                    }
                }
            } else {
                Toast.makeText(this, "Failed to fetch slot details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAvailableSlots(String vaccineType) {
        //TODO: work on the update slot button from the ViewBookedSlotsActivity
        Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show();
    }



    private void cancelSlot(String vaccineType) {
        String currentUserId = currentUser.getUid();
        //TODO: work on the cancel slot button from the ViewBookedSlotsActivity
        Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show();
    }
}
