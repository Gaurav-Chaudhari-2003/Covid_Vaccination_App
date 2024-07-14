package com.gaurav.covidvaccinationapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class AdminDashboardActivity extends AppCompatActivity {
    private Button addSlotButton;
    private Button fetchUserRecordsButton;
    private Button viewAllSlotsButton;
    private Button viewAllUsersButton;
    private Button updateSlotButton;
    private Button deleteSlotButton;
    private Button viewReportsButton;
    private Button sendNotificationsButton;
    private Button userDashboardButton;
    private Button logoutButton;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        mAuth = FirebaseAuth.getInstance();

        addSlotButton = findViewById(R.id.addSlotButton);
        fetchUserRecordsButton = findViewById(R.id.fetchUserRecordsButton);
        viewAllSlotsButton = findViewById(R.id.viewAllSlotsButton);
        viewAllUsersButton = findViewById(R.id.viewAllUsersButton);
        updateSlotButton = findViewById(R.id.updateSlotButton);
        deleteSlotButton = findViewById(R.id.deleteSlotButton);
        viewReportsButton = findViewById(R.id.viewReportsButton);
        sendNotificationsButton = findViewById(R.id.sendNotificationsButton);
        userDashboardButton = findViewById(R.id.userDashboardButton);
        logoutButton = findViewById(R.id.logoutButton);

        addSlotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminDashboardActivity.this, AddSlotActivity.class));
            }
        });

        fetchUserRecordsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminDashboardActivity.this, FetchUserRecordsActivity.class));
            }
        });

        viewAllSlotsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminDashboardActivity.this, ViewAllSlotsActivity.class));
            }
        });

        viewAllUsersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminDashboardActivity.this, ViewAllUsersActivity.class));
            }
        });

        updateSlotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminDashboardActivity.this, UpdateSlotActivity.class));
            }
        });

        deleteSlotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminDashboardActivity.this, DeleteSlotActivity.class));
            }
        });

        viewReportsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminDashboardActivity.this, ViewReportsActivity.class));
            }
        });

        sendNotificationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminDashboardActivity.this, SendNotificationsActivity.class));
            }
        });

        userDashboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminDashboardActivity.this, UserDashboardActivity.class));
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}
