package com.gaurav.covidvaccinationapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class CertificateDownloadActivity extends AppCompatActivity {

    private Button btnDownloadCertificate;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certificate_download);

        btnDownloadCertificate = findViewById(R.id.btnDownloadCertificate);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        btnDownloadCertificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadCertificate();
            }
        });
    }

    private void downloadCertificate() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            db.collection("users").document(user.getUid()).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Toast.makeText(CertificateDownloadActivity.this, "Download successful: " + document.getData(), Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(CertificateDownloadActivity.this, "No certificate found", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(CertificateDownloadActivity.this, "Failed to download certificate", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(CertificateDownloadActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }
}
