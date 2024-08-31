package com.gaurav.covidvaccinationapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class UserActivity extends AppCompatActivity {

    private Button btnLogin, btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        // Check if buttons are not null
        if (btnLogin != null && btnRegister != null) {
            btnLogin.setOnClickListener(v -> startActivity(new Intent(UserActivity.this, LoginActivity.class)));

            btnRegister.setOnClickListener(v -> startActivity(new Intent(UserActivity.this, RegisterActivity.class)));
        } else {
            // Handle the case where buttons could not be found
            // For example, logging the error or showing an alert
        }
    }
}
