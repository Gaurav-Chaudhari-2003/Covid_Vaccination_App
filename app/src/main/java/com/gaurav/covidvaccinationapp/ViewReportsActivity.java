package com.gaurav.covidvaccinationapp;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ViewReportsActivity extends AppCompatActivity {
    private TextView reportsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_reports);

        reportsTextView = findViewById(R.id.reportsTextView);
    }
}
