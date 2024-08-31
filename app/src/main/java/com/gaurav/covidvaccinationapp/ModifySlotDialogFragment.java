package com.gaurav.covidvaccinationapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class ModifySlotDialogFragment extends DialogFragment {

    private TextView dateEditText, startTimeEditText, endTimeEditText;
    private Spinner countrySpinner;
    private Spinner stateSpinner;
    private Spinner divisionSpinner;
    private Spinner subdivisionSpinner;
    private Button updateButton;

    private String slotId;
    private String vaccineType;

    public static ModifySlotDialogFragment newInstance(String slotId, String vaccineType) {
        ModifySlotDialogFragment fragment = new ModifySlotDialogFragment();
        Bundle args = new Bundle();
        args.putString("slotId", slotId);
        args.putString("vaccineType", vaccineType);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_modify_slot, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dateEditText = view.findViewById(R.id.dateEditText);
        startTimeEditText = view.findViewById(R.id.startTimeEditText);
        endTimeEditText = view.findViewById(R.id.endTimeEditText);
        countrySpinner = view.findViewById(R.id.countrySpinner);
        stateSpinner = view.findViewById(R.id.stateSpinner);
        divisionSpinner = view.findViewById(R.id.divisionSpinner);
        subdivisionSpinner = view.findViewById(R.id.subdivisionSpinner);
        updateButton = view.findViewById(R.id.updateButton);

        if (getArguments() != null) {
            slotId = getArguments().getString("slotId");
            vaccineType = getArguments().getString("vaccineType");
        }

        setupSpinners();
        fetchSlotDetails();

        dateEditText.setOnClickListener(v -> showDatePicker());
        startTimeEditText.setOnClickListener(v -> showStartTimePicker());
        endTimeEditText.setOnClickListener(v -> showEndTimePicker());

        updateButton.setOnClickListener(v -> updateSlot());
    }

    private void setupSpinners() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.countries_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countrySpinner.setAdapter(adapter);

        ArrayAdapter<CharSequence> stateAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.states_array, android.R.layout.simple_spinner_item);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(stateAdapter);

        ArrayAdapter<CharSequence> divisionAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.divisions_array, android.R.layout.simple_spinner_item);
        divisionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        divisionSpinner.setAdapter(divisionAdapter);

        ArrayAdapter<CharSequence> subdivisionAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.subdivisions_array, android.R.layout.simple_spinner_item);
        subdivisionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subdivisionSpinner.setAdapter(subdivisionAdapter);
    }

    private void fetchSlotDetails() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference docRef = firestore.collection("slots").document("vaccine").collection(vaccineType).document(slotId);

        docRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                // Handle error
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                String date = snapshot.getString("date");
                String time = snapshot.getString("time");
                String location = snapshot.getString("location");

                if (date != null) {
                    dateEditText.setText(date);
                }
                if (time != null) {
                    String[] timeParts = time.split(" - ");
                    if (timeParts.length == 2) {
                        startTimeEditText.setText(timeParts[0]);
                        endTimeEditText.setText(timeParts[1]);
                    }
                }
                if (location != null) {
                    String[] locationParts = location.split(", ");
                    if (locationParts.length == 4) {
                        countrySpinner.setSelection(((ArrayAdapter<String>) countrySpinner.getAdapter()).getPosition(locationParts[0]));
                        stateSpinner.setSelection(((ArrayAdapter<String>) stateSpinner.getAdapter()).getPosition(locationParts[1]));
                        divisionSpinner.setSelection(((ArrayAdapter<String>) divisionSpinner.getAdapter()).getPosition(locationParts[2]));
                        subdivisionSpinner.setSelection(((ArrayAdapter<String>) subdivisionSpinner.getAdapter()).getPosition(locationParts[3]));
                    }
                }
            }
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(getContext(), (view, year1, month1, dayOfMonth) -> {
            dateEditText.setText(String.format("%02d/%02d/%d", dayOfMonth, month1 + 1, year1));
        }, year, month, day).show();
    }

    private void showStartTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        new TimePickerDialog(getContext(), (view, hourOfDay, minute1) -> {
            startTimeEditText.setText(String.format("%02d:%02d", hourOfDay, minute1));
        }, hour, minute, true).show();
    }

    private void showEndTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        new TimePickerDialog(getContext(), (view, hourOfDay, minute1) -> {
            endTimeEditText.setText(String.format("%02d:%02d", hourOfDay, minute1));
        }, hour, minute, true).show();
    }

    private void updateSlot() {
        String date = dateEditText.getText().toString();
        String time = startTimeEditText.getText().toString() + " - " + endTimeEditText.getText().toString();
        String country = countrySpinner.getSelectedItem().toString();
        String state = stateSpinner.getSelectedItem().toString();
        String division = divisionSpinner.getSelectedItem().toString();
        String subdivision = subdivisionSpinner.getSelectedItem().toString();

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("slots").document("vaccine").collection(vaccineType).document(slotId)
                .update("date", date,
                        "time", time,
                        "location", country + ", " + state + ", " + division + ", " + subdivision)
                .addOnSuccessListener(aVoid -> dismiss())
                .addOnFailureListener(e -> {
                    // Handle the error
                });
    }
}
