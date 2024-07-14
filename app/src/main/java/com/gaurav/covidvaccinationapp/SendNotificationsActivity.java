package com.gaurav.covidvaccinationapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SendNotificationsActivity extends AppCompatActivity {
    private EditText messageEditText;
    private Button sendNotificationButton;

    private DatabaseReference notificationsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_notifications);

        messageEditText = findViewById(R.id.messageEditText);
        sendNotificationButton = findViewById(R.id.sendNotificationButton);

        notificationsRef = FirebaseDatabase.getInstance().getReference("notifications");

        sendNotificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotification();
            }
        });
    }

    private void sendNotification() {
        String message = messageEditText.getText().toString();

        if (TextUtils.isEmpty(message)) {
            messageEditText.setError("Enter a message");
            return;
        }

        String notificationId = notificationsRef.push().getKey();
        Map<String, String> notification = new HashMap<>();
        notification.put("message", message);

        if (notificationId != null) {
            notificationsRef.child(notificationId).setValue(notification)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(SendNotificationsActivity.this, "Notification sent successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(SendNotificationsActivity.this, "Failed to send notification", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
