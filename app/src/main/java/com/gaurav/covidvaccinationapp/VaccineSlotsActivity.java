package com.gaurav.covidvaccinationapp;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
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
        // Reference to the top-level slots collection
        CollectionReference slotsRef = firestore.collection("slots").document("vaccine").collection("covaxin");
        CollectionReference covishieldRef = firestore.collection("slots").document("vaccine").collection("covishield");

        // Fetch slots for covaxin
        fetchSlotsForVaccineType(slotsRef, "Covaxin", covaxinRecyclerView);

        // Fetch slots for covishield
        fetchSlotsForVaccineType(covishieldRef, "Covishield", covishieldRecyclerView);
    }

    private void fetchSlotsForVaccineType(CollectionReference collectionRef, String vaccineName, RecyclerView recyclerView) {
        collectionRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null) {
                    List<VaccineSlotItem> slotItems = new ArrayList<>();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        String date = document.getString("date");
                        String time = document.getString("time");
                        String slotId = document.getString("slotId");

                        String summary = vaccineName + " - Date: " + date + "\nTime: " + time;
                        String details = "Date: " + date + "\n"
                                + "Time: " + time + "\n"
                                + "Location: " + document.getString("location") + "\n"
                                + "Slot Booked: " + document.getString("slotBooked") + "\n"
                                + "Slot Booked Person ID: " + document.getString("slotBookedPersonID") + "\n"
                                + "Slot Generator Person ID: " + document.getString("slotGeneratorPersonID") + "\n"
                                + "Slot ID: " + slotId + "\n"
                                + "Vaccine Type: " + vaccineName;

                        slotItems.add(new VaccineSlotItem(summary, details));
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
