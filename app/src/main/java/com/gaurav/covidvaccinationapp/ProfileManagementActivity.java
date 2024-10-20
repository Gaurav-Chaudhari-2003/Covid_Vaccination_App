package com.gaurav.covidvaccinationapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileManagementActivity extends AppCompatActivity {

    private CircleImageView profileImageView; // Circular ImageView
    private TextView nameTextView, emailTextView, phoneNumberTextView, vaccinationStatusTextView;
    private Button saveButton;
    private ProgressBar progressBar;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    private Uri selectedImageUri;

    private ProgressDialog progressDialog; // Progress dialog for saving profile

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
        progressBar = findViewById(R.id.progressBar);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        fetchUserProfile();

        // Save button listener
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedImageUri != null) {
                    uploadProfileImage(); // Save the new profile image
                } else {
                    Toast.makeText(ProfileManagementActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Fetch user profile data from Firestore
    private void fetchUserProfile() {
        String userId = mAuth.getCurrentUser().getUid();
        DocumentReference docRef = db.collection("users").document(userId);

        progressBar.setVisibility(View.VISIBLE); // Show progress while loading image

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Display user data
                    String name = document.getString("name");
                    String email = document.getString("email");
                    String phone = document.getString("mobile");
                    String vaccinationStatus = document.getString("vaccinated.status");
                    String profileImageUrl = document.getString("profileImageUrl");

                    nameTextView.setText(name);
                    emailTextView.setText(email);
                    phoneNumberTextView.setText(phone);
                    vaccinationStatusTextView.setText(vaccinationStatus != null ? vaccinationStatus : "Not Vaccinated");

                    // Load profile image
                    if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                        Picasso.get()
                                .load(profileImageUrl)
                                .into(profileImageView, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        progressBar.setVisibility(View.GONE); // Hide progress bar after loading
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        profileImageView.setImageResource(R.drawable.baseline_person_24); // Default placeholder
                                        progressBar.setVisibility(View.GONE); // Hide progress bar even if there's an error
                                    }
                                });
                    } else {
                        profileImageView.setImageResource(R.drawable.baseline_person_24); // Default placeholder
                        progressBar.setVisibility(View.GONE); // Hide progress bar
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

    // Method to handle profile image click (to update image)
    public void onProfileImageClick(View view) {
        // Intent to select an image from gallery
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1001);
    }

    // Handle the result of image selection
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

    // Upload the new profile image to Firebase Storage
    private void uploadProfileImage() {
        if (selectedImageUri != null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Saving profile...");
            progressDialog.show();

            String userEmail = mAuth.getCurrentUser().getEmail();
            StorageReference profileRef = storageReference.child("profile_images/" + userEmail + ".jpg");

            profileRef.putFile(selectedImageUri)
                    .addOnSuccessListener(taskSnapshot -> profileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        updateProfileImageUrl(imageUrl);
                    }))
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(ProfileManagementActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    // Update the profile image URL in Firestore
    private void updateProfileImageUrl(String imageUrl) {
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("users").document(userId)
                .update("profileImageUrl", imageUrl)
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    Toast.makeText(ProfileManagementActivity.this, "Profile image updated", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(ProfileManagementActivity.this, "Failed to update profile image URL", Toast.LENGTH_SHORT).show();
                });
    }
}
