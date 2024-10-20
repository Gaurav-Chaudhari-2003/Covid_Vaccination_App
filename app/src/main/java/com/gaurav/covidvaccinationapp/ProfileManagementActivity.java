package com.gaurav.covidvaccinationapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileManagementActivity extends AppCompatActivity {

    private CircleImageView profileImageView;
    private TextView nameTextView, emailTextView, phoneNumberTextView, vaccinationStatusTextView;
    private Button saveButton, downloadCertificateButton;
    private ProgressBar progressBar;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    private Uri selectedImageUri;
    private ProgressDialog progressDialog;

    private boolean isCovaxinVaccinated = false;
    private boolean isCovishieldVaccinated = false;

    private String covishieldVaccinationStatus, covaxinVaccinationStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_management);

        profileImageView = findViewById(R.id.profileImageView);
        nameTextView = findViewById(R.id.nameTextView);
        emailTextView = findViewById(R.id.emailTextView);
        phoneNumberTextView = findViewById(R.id.phoneNumberTextView);
        vaccinationStatusTextView = findViewById(R.id.vaccinationStatusTextView);
        saveButton = findViewById(R.id.saveButton);
        downloadCertificateButton = findViewById(R.id.downloadCertificateButton);
        progressBar = findViewById(R.id.progressBar);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        fetchUserProfile();

        saveButton.setOnClickListener(view -> {
            if (selectedImageUri != null) {
                uploadProfileImage(); // Save the new profile image
            } else {
                Toast.makeText(ProfileManagementActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
            }
        });

        downloadCertificateButton.setOnClickListener(v -> generateAndDownloadCertificate());
    }

    private void fetchUserProfile() {
        String userId = mAuth.getCurrentUser().getUid();
        DocumentReference docRef = db.collection("users").document(userId);

        progressBar.setVisibility(View.VISIBLE);

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String name = document.getString("name");
                    String email = document.getString("email");
                    String phone = document.getString("mobile");
                    String covaxinStatus = document.getString("vaccinated.covaxin.status");
                    String covishieldStatus = document.getString("vaccinated.covishield.status");
                    String profileImageUrl = document.getString("profileImageUrl");

                    nameTextView.setText(name);
                    emailTextView.setText(email);
                    phoneNumberTextView.setText(phone);

                    // Check vaccination status
                    isCovaxinVaccinated = "vaccinated".equalsIgnoreCase(covaxinStatus);
                    covaxinVaccinationStatus = isCovaxinVaccinated ? "Vaccinated" : "Not Vaccinated";
                    isCovishieldVaccinated = "vaccinated".equalsIgnoreCase(covishieldStatus);
                    covishieldVaccinationStatus = isCovishieldVaccinated ? "Vaccinated" : "Not Vaccinated";

                    if (isCovaxinVaccinated || isCovishieldVaccinated) {
                        vaccinationStatusTextView.setText("Vaccinated");
                        downloadCertificateButton.setVisibility(View.VISIBLE);
                    } else {
                        vaccinationStatusTextView.setText("Not Vaccinated");
                        downloadCertificateButton.setVisibility(View.GONE);
                    }

                    if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                        Picasso.get().load(profileImageUrl).into(profileImageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Exception e) {
                                profileImageView.setImageResource(R.drawable.baseline_person_24);
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    } else {
                        profileImageView.setImageResource(R.drawable.baseline_person_24);
                        progressBar.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(ProfileManagementActivity.this, "User profile not found.", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            } else {
                Toast.makeText(ProfileManagementActivity.this, "Error fetching profile.", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public void onProfileImageClick(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1001);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                profileImageView.setImageURI(selectedImageUri);
            }
        }
    }

    private void uploadProfileImage() {
        if (selectedImageUri != null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Saving profile...");
            progressDialog.show();

            String userEmail = mAuth.getCurrentUser().getEmail();
            StorageReference profileRef = storageReference.child("profile_images/" + userEmail + ".jpg");

            profileRef.putFile(selectedImageUri).addOnSuccessListener(taskSnapshot -> profileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();
                updateProfileImageUrl(imageUrl);
            })).addOnFailureListener(e -> {
                progressDialog.dismiss();
                Toast.makeText(ProfileManagementActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void updateProfileImageUrl(String imageUrl) {
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("users").document(userId).update("profileImageUrl", imageUrl).addOnSuccessListener(aVoid -> {
            progressDialog.dismiss();
            Toast.makeText(ProfileManagementActivity.this, "Profile image updated", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(ProfileManagementActivity.this, "Failed to update profile image URL", Toast.LENGTH_SHORT).show();
        });
    }

    private void generateAndDownloadCertificate() {
        String userName = nameTextView.getText().toString();
        String userEmail = emailTextView.getText().toString();
        String userPhone = phoneNumberTextView.getText().toString();

        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300, 600, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        paint.setTextSize(12);

        int x = 10, y = 25;
        canvas.drawText("COVID-19 Vaccination Certificate", x, y, paint);
        y += (int) (paint.descent() - paint.ascent());
        canvas.drawText("Name: " + userName, x, y, paint);
        y += (int) (paint.descent() - paint.ascent());
        canvas.drawText("Email: " + userEmail, x, y, paint);
        y += (int) (paint.descent() - paint.ascent());
        canvas.drawText("Phone: " + userPhone, x, y, paint);
        y += (int) (paint.descent() - paint.ascent());
        canvas.drawText("Covaxin Status: " + covaxinVaccinationStatus, x, y, paint);
        y += (int) (paint.descent() - paint.ascent());
        canvas.drawText("Covishield Status: " + covishieldVaccinationStatus, x, y, paint);

        pdfDocument.finishPage(page);

        File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(downloadDir, "Vaccine_Certificate.pdf");

        try {
            pdfDocument.writeTo(new FileOutputStream(file));
            Toast.makeText(this, "PDF saved to Downloads: " + file.getPath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        pdfDocument.close();
    }
}
