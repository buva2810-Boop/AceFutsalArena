package com.example.acefutsalarena;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class adminDashboard extends AppCompatActivity {

    Button btnManageCourts;
    Button btnManageBookings;
    Button btnReports;
    Button btnLogout;

    TextView txtTotalBookings;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_admin_dashboard);

        // Firebase
        db = FirebaseFirestore.getInstance();

        // Connect XML
        btnManageCourts =
                findViewById(R.id.btnManageCourts);

        btnManageBookings =
                findViewById(R.id.btnManageBookings);

        btnReports =
                findViewById(R.id.btnReports);

        btnLogout =
                findViewById(R.id.btnLogout);

        txtTotalBookings =
                findViewById(R.id.txtTotalBookings);

        // Load total bookings
        loadTotalBookings();

        // Manage Courts
        btnManageCourts.setOnClickListener(v -> {

            Intent intent = new Intent(
                    adminDashboard.this,
                    manageCourts.class
            );

            startActivity(intent);
        });

        // Manage Bookings
        btnManageBookings.setOnClickListener(v -> {

            Intent intent = new Intent(
                    adminDashboard.this,
                    manageBookings.class
            );

            startActivity(intent);
        });

        // Reports
        btnReports.setOnClickListener(v -> {

            Intent intent = new Intent(
                    adminDashboard.this,
                    report.class
            );

            startActivity(intent);
        });

        // Logout
        btnLogout.setOnClickListener(v -> {

            Intent intent = new Intent(
                    adminDashboard.this,
                    Login.class
            );

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);
            finish();
        });
    }

    private void loadTotalBookings() {

        db.collection("bookings")
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    int totalBookings =
                            querySnapshot.size();

                    txtTotalBookings.setText(
                            String.valueOf(totalBookings)
                    );
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            adminDashboard.this,
                            "Failed to load booking information",
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }
}