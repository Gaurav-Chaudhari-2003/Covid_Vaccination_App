package com.gaurav.covidvaccinationapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class UserDashboardActivity extends AppCompatActivity {
    private Button bookSlotsButton;
    private Button viewBookedSlotsButton;
    private Button vaccinationHistoryButton;
    private Button notificationCenterButton;
    private Button profileManagementButton;
    private Button helpSupportButton;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        bookSlotsButton = findViewById(R.id.bookSlotsButton);
        viewBookedSlotsButton = findViewById(R.id.viewBookedSlotsButton);
        vaccinationHistoryButton = findViewById(R.id.vaccinationHistoryButton);
        notificationCenterButton = findViewById(R.id.notificationCenterButton);
        profileManagementButton = findViewById(R.id.profileManagementButton);
        helpSupportButton = findViewById(R.id.helpSupportButton);
        logoutButton = findViewById(R.id.logoutButton);

        bookSlotsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserDashboardActivity.this, BookSlotsActivity.class));
            }
        });

        viewBookedSlotsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserDashboardActivity.this, ViewBookedSlotsActivity.class));
            }
        });

        vaccinationHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserDashboardActivity.this, VaccinationHistoryActivity.class));
            }
        });

        notificationCenterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserDashboardActivity.this, NotificationCenterActivity.class));
            }
        });

        profileManagementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserDashboardActivity.this, ProfileManagementActivity.class));
            }
        });

        helpSupportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserDashboardActivity.this, HelpSupportActivity.class));
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(UserDashboardActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    Toast.makeText(UserDashboardActivity.this, "Logout failed. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
