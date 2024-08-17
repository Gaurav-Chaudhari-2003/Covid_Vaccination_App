package com.gaurav.covidvaccinationapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class FetchUserRecordsActivity extends AppCompatActivity {
    private EditText emailEditText;
    private Button fetchButton;
    private TextView userRecordsTextView;
    private ProgressBar progressBar;

    private CollectionReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch_user_records);

        emailEditText = findViewById(R.id.emailEditText);
        fetchButton = findViewById(R.id.fetchButton);
        userRecordsTextView = findViewById(R.id.userRecordsTextView);
        progressBar = findViewById(R.id.progressBar);

        usersRef = FirebaseFirestore.getInstance().collection("users");

        fetchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchUserRecords();
            }
        });
    }

    private void fetchUserRecords() {
        String email = emailEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Enter email");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Enter a valid email");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        userRecordsTextView.setText(""); // Clear previous results

        Query query = usersRef.whereEqualTo("email", email);
        query.get().addOnCompleteListener(task -> {
            progressBar.setVisibility(View.GONE);
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (!querySnapshot.isEmpty()) {
                    StringBuilder userInfo = new StringBuilder();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        String name = document.getString("name");
                        String mobile = document.getString("mobile");
                        String address = document.getString("address");
                        String pincode = document.getString("pincode");
                        String role = document.getString("role");

                        // Vaccination details
                        String covaxinSlot = document.getString("vaccinated.covaxin.slotId");
                        String covaxinStatus = document.getString("vaccinated.covaxin.status");
                        String covishieldSlot = document.getString("vaccinated.covishield.slotId");
                        String covishieldStatus = document.getString("vaccinated.covishield.status");

                        userInfo.append("Name: ").append(name).append("\n")
                                .append("Mobile: ").append(mobile).append("\n")
                                .append("Address: ").append(address).append("\n")
                                .append("Pincode: ").append(pincode).append("\n")
                                .append("Role: ").append(role).append("\n")
                                .append("Covaxin Slot: ").append(covaxinSlot).append("\n")
                                .append("Covaxin Status: ").append(covaxinStatus).append("\n")
                                .append("Covishield Slot: ").append(covishieldSlot).append("\n")
                                .append("Covishield Status: ").append(covishieldStatus).append("\n\n");
                    }
                    userRecordsTextView.setText(userInfo.toString());
                } else {
                    userRecordsTextView.setText("No user found with the provided email.");
                }
            } else {
                Toast.makeText(FetchUserRecordsActivity.this, "Error fetching user records: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
