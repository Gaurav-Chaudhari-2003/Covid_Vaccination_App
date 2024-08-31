package com.gaurav.covidvaccinationapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Delay checking user status to show logo/splash screen
        new Handler().postDelayed(this::checkUserStatus, 500); // Show logo for 0.5 seconds
    }

    private void checkUserStatus() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // User is signed in, check their role
            db.collection("users").document(currentUser.getUid()).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String role = document.getString("role");
                        navigateToDashboard(role);
                    } else {
                        // User data not found
                        navigateToLogin();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Error fetching user data", Toast.LENGTH_SHORT).show();
                    navigateToLogin();
                }
            });
        } else {
            // No user is signed in
            navigateToLogin();
        }
    }

    private void navigateToDashboard(String role) {
        Intent intent;
        if ("admin".equals(role)) {
            intent = new Intent(MainActivity.this, AdminDashboardActivity.class);
        } else {
            intent = new Intent(MainActivity.this, UserDashboardActivity.class);
        }
        startActivity(intent);
        finish();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
