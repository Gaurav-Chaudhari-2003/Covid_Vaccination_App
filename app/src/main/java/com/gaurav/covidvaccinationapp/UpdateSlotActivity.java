package com.gaurav.covidvaccinationapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UpdateSlotActivity extends AppCompatActivity {
    private EditText slotIdEditText, dateEditText, timeEditText, locationEditText;
    private Button updateSlotButton;

    private DatabaseReference slotsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_slot);

        slotIdEditText = findViewById(R.id.slotIdEditText);
        dateEditText = findViewById(R.id.dateEditText);
        timeEditText = findViewById(R.id.timeEditText);
        locationEditText = findViewById(R.id.locationEditText);
        updateSlotButton = findViewById(R.id.updateSlotButton);

        slotsRef = FirebaseDatabase.getInstance().getReference("slots");

        updateSlotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSlot();
            }
        });
    }

    private void updateSlot() {
        String slotId = slotIdEditText.getText().toString();
        String date = dateEditText.getText().toString();
        String time = timeEditText.getText().toString();
        String location = locationEditText.getText().toString();

        if (TextUtils.isEmpty(slotId) || TextUtils.isEmpty(date) || TextUtils.isEmpty(time) || TextUtils.isEmpty(location)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        slotsRef.child(slotId).child("date").setValue(date);
        slotsRef.child(slotId).child("time").setValue(time);
        slotsRef.child(slotId).child("location").setValue(location);

        Toast.makeText(this, "Slot updated successfully", Toast.LENGTH_SHORT).show();
        finish();
    }
}
