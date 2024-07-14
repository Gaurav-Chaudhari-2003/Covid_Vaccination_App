package com.gaurav.covidvaccinationapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class AddSlotActivity extends AppCompatActivity {

    private TextView dateTextView, fromTimeTextView, toTimeTextView;
    private Spinner countrySpinner, stateSpinner, divisionSpinner, subdivisionSpinner;
    private RadioGroup vaccineRadioGroup;
    private Button addSlotButton;

    private FirebaseFirestore db;
    private String currentCovishieldSlotId, currentCovaxinSlotId;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_slot);

        dateTextView = findViewById(R.id.dateTextView);
        fromTimeTextView = findViewById(R.id.fromTimeTextView);
        toTimeTextView = findViewById(R.id.toTimeTextView);
        countrySpinner = findViewById(R.id.countrySpinner);
        stateSpinner = findViewById(R.id.stateSpinner);
        divisionSpinner = findViewById(R.id.divisionSpinner);
        subdivisionSpinner = findViewById(R.id.subdivisionSpinner);
        vaccineRadioGroup = findViewById(R.id.vaccineRadioGroup);
        addSlotButton = findViewById(R.id.addSlotButton);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Set up date picker
        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        // Set up time pickers
        fromTimeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker(fromTimeTextView);
            }
        });

        toTimeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker(toTimeTextView);
            }
        });

        // Set up dropdown lists
        setupDropdownLists();

        addSlotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSlot();
            }
        });

        // Fetch current slot IDs for Covishield and Covaxin
        fetchCurrentSlotIds();
    }

    private void fetchCurrentSlotIds() {
        DocumentReference vaccineRef = db.collection("slots").document("vaccine");
        vaccineRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                currentCovishieldSlotId = task.getResult().getString("covishield");
                currentCovaxinSlotId = task.getResult().getString("covaxin");
            } else {
                Toast.makeText(AddSlotActivity.this, "Failed to fetch current slot IDs", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date = dayOfMonth + "/" + (month + 1) + "/" + year;
                dateTextView.setText(date);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void showTimePicker(final TextView timeTextView) {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String time = hourOfDay + ":" + String.format("%02d", minute);
                timeTextView.setText(time);
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }

    private void setupDropdownLists() {
        // Set up country spinner
        ArrayAdapter<CharSequence> countryAdapter = ArrayAdapter.createFromResource(this,
                R.array.countries_array, android.R.layout.simple_spinner_item);
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countrySpinner.setAdapter(countryAdapter);

        // Set up state spinner
        ArrayAdapter<CharSequence> stateAdapter = ArrayAdapter.createFromResource(this,
                R.array.states_array, android.R.layout.simple_spinner_item);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(stateAdapter);

        // Set up subdivision spinner
        ArrayAdapter<CharSequence> subdivisionAdapter = ArrayAdapter.createFromResource(this,
                R.array.divisions_array, android.R.layout.simple_spinner_item);
        subdivisionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        divisionSpinner.setAdapter(subdivisionAdapter);

        // Set up village spinner
        ArrayAdapter<CharSequence> villageAdapter = ArrayAdapter.createFromResource(this,
                R.array.subdivisions_array, android.R.layout.simple_spinner_item);
        villageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subdivisionSpinner.setAdapter(villageAdapter);
    }

    private void addSlot() {
        int selectedVaccineId = vaccineRadioGroup.getCheckedRadioButtonId();
        if (selectedVaccineId == -1) {
            Toast.makeText(this, "Please select a vaccine type", Toast.LENGTH_SHORT).show();
            return;
        }

        String vaccineType = selectedVaccineId == R.id.radioCovishield ? "covishield" : "covaxin";
        String slotId = vaccineType.equals("covishield") ? currentCovishieldSlotId : currentCovaxinSlotId;

        String date = dateTextView.getText().toString();
        String fromTime = fromTimeTextView.getText().toString();
        String toTime = toTimeTextView.getText().toString();
        String country = countrySpinner.getSelectedItem().toString();
        String state = stateSpinner.getSelectedItem().toString();
        String division = divisionSpinner.getSelectedItem().toString();
        String subdivision = subdivisionSpinner.getSelectedItem().toString();

        if (TextUtils.isEmpty(date) || TextUtils.isEmpty(fromTime) || TextUtils.isEmpty(toTime)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        String location = country + "/" + state + "/" + division + "/" + subdivision;

        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            String slotGeneratorPersonID = firebaseUser.getUid();
            Slot slot = new Slot(slotId, date, fromTime + " - " + toTime, location, "false", "null", slotGeneratorPersonID, vaccineType);
            db.collection("slots").document("vaccine")
                    .collection(vaccineType).document(slotId).set(slot)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(AddSlotActivity.this, "Slot added successfully", Toast.LENGTH_SHORT).show();
                            updateSlotId(vaccineType);
                            finish();
                        } else {
                            Toast.makeText(AddSlotActivity.this, "Failed to add slot", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            mAuth.signOut();
            Intent intent = new Intent(AddSlotActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void updateSlotId(String vaccineType) {
        DocumentReference vaccineRef = db.collection("slots").document("vaccine");
        vaccineRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String currentSlotId = task.getResult().getString(vaccineType);
                int newSlotId = Integer.parseInt(currentSlotId) + 1;
                vaccineRef.update(vaccineType, String.valueOf(newSlotId))
                        .addOnCompleteListener(updateTask -> {
                            if (!updateTask.isSuccessful()) {
                                Toast.makeText(AddSlotActivity.this, "Failed to update slot ID", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(AddSlotActivity.this, "Failed to fetch current slot ID", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
