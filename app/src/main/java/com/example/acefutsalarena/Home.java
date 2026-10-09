package com.example.acefutsalarena;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.HashMap;
import java.util.Map;

public class Home extends AppCompatActivity {

    MaterialButton btnCourtA;
    MaterialButton btnCourtB;
    MaterialButton btnCourtC;
    MaterialButton btnCourtD;

    MaterialButton btnHistory;
    MaterialButton btnProfile;

    TextView txtCourtA;
    TextView txtCourtB;
    TextView txtCourtC;
    TextView txtCourtD;

    TextView txtPriceA;
    TextView txtPriceB;
    TextView txtPriceC;
    TextView txtPriceD;

    TextView txtStatusA;
    TextView txtStatusB;
    TextView txtStatusC;
    TextView txtStatusD;

    FirebaseFirestore db;

    ListenerRegistration courtListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        db = FirebaseFirestore.getInstance();

        // Buttons
        btnCourtA = findViewById(R.id.btnCourtA);
        btnCourtB = findViewById(R.id.btnCourtB);
        btnCourtC = findViewById(R.id.btnCourtC);
        btnCourtD = findViewById(R.id.btnCourtD);

        btnHistory = findViewById(R.id.btnHistory);
        btnProfile = findViewById(R.id.btnProfile);

        // Court names
        txtCourtA = findViewById(R.id.txtCourtA);
        txtCourtB = findViewById(R.id.txtCourtB);
        txtCourtC = findViewById(R.id.txtCourtC);
        txtCourtD = findViewById(R.id.txtCourtD);

        // Prices
        txtPriceA = findViewById(R.id.txtPriceA);
        txtPriceB = findViewById(R.id.txtPriceB);
        txtPriceC = findViewById(R.id.txtPriceC);
        txtPriceD = findViewById(R.id.txtPriceD);

        // Status
        txtStatusA = findViewById(R.id.txtStatusA);
        txtStatusB = findViewById(R.id.txtStatusB);
        txtStatusC = findViewById(R.id.txtStatusC);
        txtStatusD = findViewById(R.id.txtStatusD);


        // Start real-time connection
        listenToCourts();


        // Booking History
        btnHistory.setOnClickListener(v -> {

            Intent intent = new Intent(
                    Home.this,
                    bookingHistory.class
            );

            startActivity(intent);
        });


        // Profile
        btnProfile.setOnClickListener(v -> {

            Intent intent = new Intent(
                    Home.this,
                    profile.class
            );

            startActivity(intent);
        });
    }


    private void listenToCourts() {

        courtListener = db.collection("courts")
                .addSnapshotListener((querySnapshot, error) -> {

                    if (error != null) {
                        return;
                    }

                    if (querySnapshot == null) {
                        return;
                    }


                    Map<String, com.google.firebase.firestore.DocumentSnapshot> courts
                            = new HashMap<>();


                    // Get all court documents
                    for (com.google.firebase.firestore.DocumentSnapshot document
                            : querySnapshot.getDocuments()) {

                        courts.put(
                                document.getId(),
                                document
                        );
                    }


                    // Update Court A
                    updateCourt(
                            "courtA",
                            courts.get("courtA"),
                            txtCourtA,
                            txtPriceA,
                            txtStatusA,
                            btnCourtA
                    );


                    // Update Court B
                    updateCourt(
                            "courtB",
                            courts.get("courtB"),
                            txtCourtB,
                            txtPriceB,
                            txtStatusB,
                            btnCourtB
                    );


                    // Update Court C
                    updateCourt(
                            "courtC",
                            courts.get("courtC"),
                            txtCourtC,
                            txtPriceC,
                            txtStatusC,
                            btnCourtC
                    );


                    // Update Court D
                    updateCourt(
                            "courtD",
                            courts.get("courtD"),
                            txtCourtD,
                            txtPriceD,
                            txtStatusD,
                            btnCourtD
                    );
                });
    }


    private void updateCourt(
            String courtId,
            com.google.firebase.firestore.DocumentSnapshot document,
            TextView courtNameText,
            TextView priceText,
            TextView statusText,
            MaterialButton button
    ) {

        // Court document not found
        if (document == null || !document.exists()) {

            courtNameText.setText("Court not found");
            priceText.setText("");
            statusText.setText("● Unavailable");

            statusText.setTextColor(
                    getResources().getColor(
                            android.R.color.holo_red_dark
                    )
            );

            button.setText("UNAVAILABLE");
            button.setEnabled(false);

            return;
        }


        // Get name
        String courtName =
                document.getString("name");


        // Get price
        Double price =
                document.getDouble("price");


        // Get availability
        Boolean available =
                document.getBoolean("available");


        // Default name
        if (courtName == null ||
                courtName.isEmpty()) {

            courtName = courtId;
        }


        // Default price
        if (price == null) {

            price = 0.0;
        }


        // Default availability
        if (available == null) {

            available = false;
        }


        // Show name
        courtNameText.setText(courtName);


        // Show price
        priceText.setText(
                "RM " +
                        String.format(
                                java.util.Locale.getDefault(),
                                "%.2f",
                                price
                        ) +
                        " / hour"
        );


        if (available) {

            statusText.setText(
                    "● Available"
            );

            statusText.setTextColor(
                    getResources().getColor(
                            android.R.color.holo_green_dark
                    )
            );

            button.setText(
                    "VIEW DETAILS"
            );

            button.setEnabled(true);


            String finalCourtName = courtName;
            double finalPrice = price;


            button.setOnClickListener(v -> {

                Intent intent = new Intent(
                        Home.this,
                        CourtDetails.class
                );

                intent.putExtra(
                        "courtId",
                        courtId
                );

                intent.putExtra(
                        "courtName",
                        finalCourtName
                );

                intent.putExtra(
                        "courtPrice",
                        finalPrice
                );

                startActivity(intent);
            });


        } else {

            statusText.setText(
                    "● Unavailable"
            );

            statusText.setTextColor(
                    getResources().getColor(
                            android.R.color.holo_red_dark
                    )
            );

            button.setText(
                    "UNAVAILABLE"
            );

            button.setEnabled(false);
        }
    }


    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (courtListener != null) {

            courtListener.remove();
        }
    }
}