package com.gaurav.covidvaccinationapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SlotBookingActivity extends AppCompatActivity {

    private EditText etSlotId;
    private Button btnBookSlot;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slot_booking);

        etSlotId = findViewById(R.id.etSlotId);
        btnBookSlot = findViewById(R.id.btnBookSlot);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        btnBookSlot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String slotId = etSlotId.getText().toString().trim();
                bookSlot(slotId);
            }
        });
    }

    private void bookSlot(String slotId) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            Map<String, Object> bookingData = new HashMap<>();
            bookingData.put("slotId", slotId);
            bookingData.put("userId", user.getUid());

            db.collection("bookings").add(bookingData)
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SlotBookingActivity.this, "Slot booked successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SlotBookingActivity.this, "Failed to book slot", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(SlotBookingActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }
}
