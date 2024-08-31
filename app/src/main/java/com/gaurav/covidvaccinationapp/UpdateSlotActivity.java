package com.gaurav.covidvaccinationapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class UpdateSlotActivity extends AppCompatActivity {
    private EditText dateEditText, timeEditText, locationEditText;
    private Button updateButton;
    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;
    private String documentId;
    private String vaccineName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_slot);

        dateEditText = findViewById(R.id.dateEditText);
        timeEditText = findViewById(R.id.timeEditText);
        locationEditText = findViewById(R.id.locationEditText);
        updateButton = findViewById(R.id.updateButton);

        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Get the intent data
        documentId = getIntent().getStringExtra("slotId");
        vaccineName = getIntent().getStringExtra("vaccineType");

        // Populate the UI with existing data
        dateEditText.setText(getIntent().getStringExtra("date"));
        timeEditText.setText(getIntent().getStringExtra("time"));
        locationEditText.setText(getIntent().getStringExtra("location"));

        updateButton.setOnClickListener(v -> updateSlot());
    }

    private void updateSlot() {
        String newDate = dateEditText.getText().toString();
        String newTime = timeEditText.getText().toString();
        String newLocation = locationEditText.getText().toString();

        if (TextUtils.isEmpty(newDate) || TextUtils.isEmpty(newTime) || TextUtils.isEmpty(newLocation)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            String slotModifierPersonID = firebaseUser.getUid();

            // Create a map for the updated data
            DocumentReference slotRef = firestore.collection("slots").document("vaccine")
                    .collection(vaccineName).document(documentId);
            slotRef.update("date", newDate,
                            "time", newTime,
                            "location", newLocation,
                            "slotModifierPersonID", slotModifierPersonID)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(UpdateSlotActivity.this, "Slot updated successfully", Toast.LENGTH_SHORT).show();
                            finish(); // Close the activity
                        } else {
                            Toast.makeText( UpdateSlotActivity.this, "Failed to update slot", Toast.LENGTH_SHORT).show();
                            locationEditText.setText((CharSequence) task.getException());
                        }
                    });
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            mAuth.signOut();
            Intent intent = new Intent(UpdateSlotActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }
}
