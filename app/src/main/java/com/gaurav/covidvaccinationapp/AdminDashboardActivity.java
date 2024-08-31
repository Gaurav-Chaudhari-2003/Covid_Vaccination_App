package com.gaurav.covidvaccinationapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class AdminDashboardActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        mAuth = FirebaseAuth.getInstance();

        // Initialize buttons
        Button addSlotButton = findViewById(R.id.addSlotButton);
        Button fetchUserRecordsButton = findViewById(R.id.fetchUserRecordsButton);
        Button viewAllSlotsButton = findViewById(R.id.viewAllSlotsButton);
        Button viewAllUsersButton = findViewById(R.id.viewAllUsersButton);
        Button updateSlotButton = findViewById(R.id.updateSlotButton);
        Button deleteSlotButton = findViewById(R.id.deleteSlotButton);
        Button viewReportsButton = findViewById(R.id.viewReportsButton);
        Button sendNotificationsButton = findViewById(R.id.sendNotificationsButton);
        Button userDashboardButton = findViewById(R.id.userDashboardButton);
        Button logoutButton = findViewById(R.id.logoutButton);

        // Set up button click listeners
        addSlotButton.setOnClickListener(v -> navigateTo(AddSlotActivity.class));
        fetchUserRecordsButton.setOnClickListener(v -> navigateTo(FetchUserRecordsActivity.class));
        viewAllSlotsButton.setOnClickListener(v -> navigateTo(VaccineSlotsActivity.class));
        viewAllUsersButton.setOnClickListener(v -> navigateTo(ViewAllUsersActivity.class));
        updateSlotButton.setOnClickListener(v -> navigateTo(UpdateSlotActivity.class));
        deleteSlotButton.setOnClickListener(v -> navigateTo(DeleteSlotActivity.class));
        viewReportsButton.setOnClickListener(v -> navigateTo(ViewReportsActivity.class));
        sendNotificationsButton.setOnClickListener(v -> navigateTo(SendNotificationsActivity.class));
        userDashboardButton.setOnClickListener(v -> navigateTo(UserDashboardActivity.class));
        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void navigateTo(Class<?> activityClass) {
        Intent intent = new Intent(AdminDashboardActivity.this, activityClass);
        startActivity(intent);
    }
}
