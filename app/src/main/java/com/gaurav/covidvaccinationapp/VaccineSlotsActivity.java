package com.gaurav.covidvaccinationapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VaccineSlotsActivity extends AppCompatActivity {
    private RecyclerView covaxinRecyclerView;
    private RecyclerView covishieldRecyclerView;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_slots);

        covaxinRecyclerView = findViewById(R.id.covaxinRecyclerView);
        covishieldRecyclerView = findViewById(R.id.covishieldRecyclerView);
        firestore = FirebaseFirestore.getInstance();

        covaxinRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        covishieldRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        fetchAllSlots();
    }

    private void fetchAllSlots() {
        CollectionReference slotsRef = firestore.collection("slots").document("vaccine").collection("covaxin");
        CollectionReference covishieldRef = firestore.collection("slots").document("vaccine").collection("covishield");

        fetchSlotsForVaccineType(slotsRef, "Covaxin", covaxinRecyclerView);
        fetchSlotsForVaccineType(covishieldRef, "Covishield", covishieldRecyclerView);
    }

    private void fetchSlotsForVaccineType(CollectionReference collectionRef, String vaccineName, RecyclerView recyclerView) {
        collectionRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null) {
                    List<VaccineSlotItem> slotItems = new ArrayList<>();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    String currentDate = sdf.format(new Date());

                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        String date = document.getString("date");
                        String time = document.getString("time");
                        String slotId = document.getString("slotId");
                        String slotBookedPersonID = document.getString("slotBookedPersonID");
                        String slotBooked = document.getString("slotBooked");

                        // Check if the slot is outdated and not booked by anyone
                        boolean isOutdated = false;
                        try {
                            Date slotDate = sdf.parse(date);
                            isOutdated = slotDate.before(sdf.parse(currentDate));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        boolean showModifyButton = (slotBookedPersonID == null || slotBookedPersonID.equals("null")) && isOutdated;

                        String summary = "Slot ID: " + slotId + "\nDate: " + date + "\tTime: " + time;
                        String details = "Date: " + date + "\n"
                                + "Time: " + time + "\n"
                                + "Location: " + document.getString("location") + "\n"
                                + "Slot Booked: " + slotBooked + "\n"
                                + "Slot Booked Person ID: " + slotBookedPersonID + "\n"
                                + "Slot Generator Person ID: " + document.getString("slotGeneratorPersonID") + "\n"
                                + "Slot ID: " + slotId + "\n"
                                + "Vaccine Type: " + vaccineName;

                        slotItems.add(new VaccineSlotItem(summary, details, showModifyButton, document.getId(), vaccineName));
                    }

                    VaccineSlotAdapter adapter = new VaccineSlotAdapter(slotItems);
                    recyclerView.setAdapter(adapter);
                }
            } else {
                Toast.makeText(VaccineSlotsActivity.this, "Error fetching slots", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
