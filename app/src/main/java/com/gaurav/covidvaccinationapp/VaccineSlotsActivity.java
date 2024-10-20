package com.gaurav.covidvaccinationapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
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
    private VaccineSlotAdapter covaxinAdapter;
    private VaccineSlotAdapter covishieldAdapter;
    private List<VaccineSlotItem> covaxinSlotList;
    private List<VaccineSlotItem> covishieldSlotList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_slots);

        covaxinRecyclerView = findViewById(R.id.covaxinRecyclerView);
        covishieldRecyclerView = findViewById(R.id.covishieldRecyclerView);
        firestore = FirebaseFirestore.getInstance();

        covaxinSlotList = new ArrayList<>();
        covishieldSlotList = new ArrayList<>();

        covaxinAdapter = new VaccineSlotAdapter(covaxinSlotList, firestore);
        covishieldAdapter = new VaccineSlotAdapter(covishieldSlotList, firestore);


        covaxinRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        covaxinRecyclerView.setAdapter(covaxinAdapter);

        covishieldRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        covishieldRecyclerView.setAdapter(covishieldAdapter);

        fetchAllSlots();
    }

    private void fetchAllSlots() {
        CollectionReference covaxinRef = firestore.collection("slots").document("vaccine").collection("covaxin");
        CollectionReference covishieldRef = firestore.collection("slots").document("vaccine").collection("covishield");

        covaxinRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot snapshot, FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(VaccineSlotsActivity.this, "Error fetching Covaxin slots", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (snapshot != null) {
                    List<VaccineSlotItem> slotItems = new ArrayList<>();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    String currentDate = sdf.format(new Date());

                    for (DocumentSnapshot document : snapshot.getDocuments()) {
                        String date = document.getString("date");
                        String time = document.getString("time");
                        String slotId = document.getString("slotId");
                        String slotBookedPersonID = document.getString("slotBookedPersonID");
                        String slotBooked = document.getString("slotBooked");

                        boolean isOutdated = false;
                        try {
                            Date slotDate = sdf.parse(date);
                            isOutdated = slotDate.before(sdf.parse(currentDate)) || slotDate.equals(sdf.parse(currentDate));
                        } catch (ParseException ex) {
                            ex.printStackTrace();
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
                                + "Vaccine Type: Covaxin";

                        slotItems.add(new VaccineSlotItem(summary, details, showModifyButton, slotId, "covaxin"));
                    }

                    covaxinSlotList.clear();
                    covaxinSlotList.addAll(slotItems);
                    covaxinAdapter.notifyDataSetChanged();
                }
            }
        });

        covishieldRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot snapshot, FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(VaccineSlotsActivity.this, "Error fetching Covishield slots", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (snapshot != null) {
                    List<VaccineSlotItem> slotItems = new ArrayList<>();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    String currentDate = sdf.format(new Date());

                    for (DocumentSnapshot document : snapshot.getDocuments()) {
                        String date = document.getString("date");
                        String time = document.getString("time");
                        String slotId = document.getString("slotId");
                        String slotBookedPersonID = document.getString("slotBookedPersonID");
                        String slotBooked = document.getString("slotBooked");

                        boolean isOutdated = false;
                        try {
                            Date slotDate = sdf.parse(date);
                            isOutdated = slotDate.before(sdf.parse(currentDate));
                        } catch (ParseException ex) {
                            ex.printStackTrace();
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
                                + "Vaccine Type: Covishield";

                        slotItems.add(new VaccineSlotItem(summary, details, showModifyButton, slotId, "covishield"));
                    }

                    covishieldSlotList.clear();
                    covishieldSlotList.addAll(slotItems);
                    covishieldAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}
