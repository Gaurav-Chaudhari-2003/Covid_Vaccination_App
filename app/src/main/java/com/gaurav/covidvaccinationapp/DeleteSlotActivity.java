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

public class DeleteSlotActivity extends AppCompatActivity {
    private EditText slotIdEditText;
    private Button deleteSlotButton;

    private DatabaseReference slotsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_slot);

        slotIdEditText = findViewById(R.id.slotIdEditText);
        deleteSlotButton = findViewById(R.id.deleteSlotButton);

        slotsRef = FirebaseDatabase.getInstance().getReference("slots");

        deleteSlotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSlot();
            }
        });
    }

    private void deleteSlot() {
        String slotId = slotIdEditText.getText().toString();

        if (TextUtils.isEmpty(slotId)) {
            slotIdEditText.setError("Enter slot ID");
            return;
        }

        slotsRef.child(slotId).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(DeleteSlotActivity.this, "Slot deleted successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(DeleteSlotActivity.this, "Failed to delete slot", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
