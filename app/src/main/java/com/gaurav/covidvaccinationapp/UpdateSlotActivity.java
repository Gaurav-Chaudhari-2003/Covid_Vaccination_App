package com.gaurav.covidvaccinationapp;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class UpdateSlotActivity extends AppCompatActivity {
    private EditText dateEditText, timeEditText, locationEditText;
    private FirebaseFirestore firestore;
    private String documentId;
    private String vaccineName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_slot);

        dateEditText = findViewById(R.id.dateEditText);
        timeEditText = findViewById(R.id.timeEditText);
        locationEditText = findViewById(R.id.locationEditText);
        firestore = FirebaseFirestore.getInstance();

        // Get the intent data
        documentId = getIntent().getStringExtra("documentId");
        vaccineName = getIntent().getStringExtra("vaccineName");

        // Populate the UI with existing data
        dateEditText.setText(getIntent().getStringExtra("date"));
        timeEditText.setText(getIntent().getStringExtra("time"));
        locationEditText.setText(getIntent().getStringExtra("location"));

        // Implement your logic to update the slot data here
    }
}
