package com.gaurav.covidvaccinationapp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class BookSlotsActivity extends AppCompatActivity {
    private TextView dateTextView;
    private Spinner countrySpinner, stateSpinner, divisionSpinner, subdivisionSpinner;
    private RadioGroup vaccineRadioGroup;
    private Button checkSlotsButton;
    private RecyclerView slotsRecyclerView;

    private FirebaseFirestore db;
    private SlotAdapter slotAdapter;
    private List<Slot> slotList;
    private ListenerRegistration slotsListenerRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_slots);

        dateTextView = findViewById(R.id.dateTextView);
        countrySpinner = findViewById(R.id.countrySpinner);
        stateSpinner = findViewById(R.id.stateSpinner);
        divisionSpinner = findViewById(R.id.divisionSpinner);
        subdivisionSpinner = findViewById(R.id.subdivisionSpinner);
        vaccineRadioGroup = findViewById(R.id.vaccineRadioGroup);
        checkSlotsButton = findViewById(R.id.checkSlotsButton);
        slotsRecyclerView = findViewById(R.id.slotsRecyclerView);

        db = FirebaseFirestore.getInstance();
        slotList = new ArrayList<>();
        slotAdapter = new SlotAdapter(slotList, this::bookSlot);

        slotsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        slotsRecyclerView.setAdapter(slotAdapter);

        // Set up date picker
        dateTextView.setOnClickListener(v -> showDatePicker());

        // Set up dropdown lists
        setupDropdownLists();

        checkSlotsButton.setOnClickListener(v -> checkAvailableSlots());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (slotsListenerRegistration != null) {
            slotsListenerRegistration.remove();
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String date = dayOfMonth + "/" + (month + 1) + "/" + year;
            dateTextView.setText(date);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        // Set minimum date to today
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
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

        // Set up division spinner
        ArrayAdapter<CharSequence> divisionAdapter = ArrayAdapter.createFromResource(this,
                R.array.divisions_array, android.R.layout.simple_spinner_item);
        divisionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        divisionSpinner.setAdapter(divisionAdapter);

        // Set up subdivision spinner
        ArrayAdapter<CharSequence> subdivisionAdapter = ArrayAdapter.createFromResource(this,
                R.array.subdivisions_array, android.R.layout.simple_spinner_item);
        subdivisionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subdivisionSpinner.setAdapter(subdivisionAdapter);
    }

    private void checkAvailableSlots() {
        int selectedVaccineId = vaccineRadioGroup.getCheckedRadioButtonId();
        if (selectedVaccineId == -1) {
            Toast.makeText(this, "Please select a vaccine type", Toast.LENGTH_SHORT).show();
            return;
        }

        String vaccineType = selectedVaccineId == R.id.radioCovishield ? "covishield" : "covaxin";
        String date = dateTextView.getText().toString();
        String country = countrySpinner.getSelectedItem().toString();
        String state = stateSpinner.getSelectedItem().toString();
        String division = divisionSpinner.getSelectedItem().toString();
        String subdivision = subdivisionSpinner.getSelectedItem().toString();
        String location = country + "/" + state + "/" + division + "/" + subdivision;

        if (TextUtils.isEmpty(date)) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
            return;
        }

        if (slotsListenerRegistration != null) {
            slotsListenerRegistration.remove();
        }

        slotsListenerRegistration = db.collection("slots").document("vaccine")
                .collection(vaccineType)
                .whereEqualTo("date", date)
                .whereEqualTo("location", location)
                .whereEqualTo("slotBooked", "false")
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Toast.makeText(BookSlotsActivity.this, "Failed to fetch available slots", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (snapshot != null && !snapshot.isEmpty()) {
                        slotList.clear();
                        for (DocumentSnapshot document : snapshot.getDocuments()) {
                            Slot slot = document.toObject(Slot.class);
                            slotList.add(slot);
                        }
                    } else {
                        Toast.makeText(BookSlotsActivity.this, "No slots available", Toast.LENGTH_SHORT).show();
                        slotList.clear();
                    }
                    slotAdapter.notifyDataSetChanged();
                });
    }

    private void bookSlot(Slot slot) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = firebaseUser.getUid();

        // Check if the user is already booked or vaccinated for this vaccine type
        db.collection("users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                Map<String, Object> vaccinated = (Map<String, Object>) task.getResult().get("vaccinated");
                if (vaccinated != null && vaccinated.containsKey(slot.getVaccineType())) {
                    Map<String, Object> vaccineDetails = (Map<String, Object>) vaccinated.get(slot.getVaccineType());
                    if (vaccineDetails.containsKey("status") && "booked".equals(vaccineDetails.get("status"))) {
                        Toast.makeText(this, "You have already booked a slot for this vaccine", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (vaccineDetails.containsKey("status") && "vaccinated".equals(vaccineDetails.get("status"))) {
                        Toast.makeText(this, "You have already been vaccinated with this vaccine", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                // Update the slot as booked
                db.collection("slots").document("vaccine")
                        .collection(slot.getVaccineType()).document(slot.getSlotId())
                        .update("slotBooked", "true", "slotBookedPersonID", userId)
                        .addOnCompleteListener(updateTask -> {
                            if (updateTask.isSuccessful()) {
                                // Update the user's vaccination status
                                db.collection("users").document(userId)
                                        .update("vaccinated." + slot.getVaccineType() + ".status", "booked",
                                                "vaccinated." + slot.getVaccineType() + ".slotId", slot.getSlotId())
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                Toast.makeText(this, "Slot booked successfully", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(this, "Failed to update user data", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                Toast.makeText(this, "Failed to book slot", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(this, "Failed to fetch user data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
