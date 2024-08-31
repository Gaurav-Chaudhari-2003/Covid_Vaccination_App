package com.gaurav.covidvaccinationapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private EditText nameEditText, mobileEditText, otpEditText, addressEditText, pincodeEditText, emailEditText, passwordEditText;
    private Button sendOtpButton, verifyOtpButton, submitButton;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initializeUI();

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        setOnClickListeners();
    }

    private void initializeUI() {
        nameEditText = findViewById(R.id.nameEditText);
        mobileEditText = findViewById(R.id.mobileEditText);
        otpEditText = findViewById(R.id.otpEditText);
        addressEditText = findViewById(R.id.addressEditText);
        pincodeEditText = findViewById(R.id.pincodeEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        sendOtpButton = findViewById(R.id.sendOtpButton);
        verifyOtpButton = findViewById(R.id.verifyOtpButton);
        submitButton = findViewById(R.id.submitButton);
    }

    private void setOnClickListeners() {
        sendOtpButton.setOnClickListener(v -> {
            String mobile = mobileEditText.getText().toString();
            if (validateMobile(mobile)) {
                sendOtp(mobile);
            }
        });

        verifyOtpButton.setOnClickListener(v -> {
            String otp = otpEditText.getText().toString();
            if (validateOtp(otp)) {
                verifyOtp(otp);
            }
        });

        submitButton.setOnClickListener(v -> registerUser());
    }

    private boolean validateMobile(String mobile) {
        if (TextUtils.isEmpty(mobile)) {
            mobileEditText.setError("Enter mobile number");
            return false;
        }
        return true;
    }

    private boolean validateOtp(String otp) {
        if (TextUtils.isEmpty(otp)) {
            otpEditText.setError("Enter OTP");
            return false;
        }
        return true;
    }

    private void sendOtp(String mobile) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber("+91" + mobile)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                        signInWithPhoneAuthCredential(credential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Log.w(TAG, "onVerificationFailed", e);
                        Toast.makeText(RegisterActivity.this, "Verification failed", Toast.LENGTH_SHORT).show();
                        addressEditText.setText(e.toString());
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                        RegisterActivity.this.verificationId = verificationId;
                        Toast.makeText(RegisterActivity.this, "OTP sent", Toast.LENGTH_SHORT).show();
                        otpEditText.setVisibility(View.VISIBLE);
                        verifyOtpButton.setVisibility(View.VISIBLE);
                        verifyOtpButton.setEnabled(true);
                        otpEditText.setEnabled(true);
                    }
                })
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyOtp(String otp) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                mAuth.signOut();
                Toast.makeText(RegisterActivity.this, "Phone number verified", Toast.LENGTH_SHORT).show();
                toggleOtpVerificationUI(false);
            } else {
                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(RegisterActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void toggleOtpVerificationUI(boolean isEnabled) {
        submitButton.setEnabled(isEnabled);
        otpEditText.setVisibility(isEnabled ? View.INVISIBLE : View.VISIBLE);
        verifyOtpButton.setVisibility(isEnabled ? View.INVISIBLE : View.VISIBLE);
        otpEditText.setEnabled(!isEnabled);
        verifyOtpButton.setEnabled(!isEnabled);
    }

    private void registerUser() {
        String name = nameEditText.getText().toString();
        String mobile = mobileEditText.getText().toString();
        String address = addressEditText.getText().toString();
        String pincode = pincodeEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (areFieldsValid(name, mobile, address, pincode, email, password)) {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        saveUserData(firebaseUser.getUid(), name, mobile, address, pincode, email);
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                    addressEditText.setText(Objects.requireNonNull(task.getException()).toString());
                }
            });
        } else {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean areFieldsValid(String name, String mobile, String address, String pincode, String email, String password) {
        return !TextUtils.isEmpty(name) && !TextUtils.isEmpty(mobile) && !TextUtils.isEmpty(address) &&
                !TextUtils.isEmpty(pincode) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password);
    }

    private void saveUserData(String userId, String name, String mobile, String address, String pincode, String email) {
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("mobile", mobile);
        user.put("address", address);
        user.put("pincode", pincode);
        user.put("email", email);
        user.put("role", "user");

        db.collection("users").document(userId).set(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(RegisterActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            } else {
                Toast.makeText(RegisterActivity.this, "Failed to register user", Toast.LENGTH_SHORT).show();
                addressEditText.setText(Objects.requireNonNull(task.getException()).toString());
            }
        });
    }
}
