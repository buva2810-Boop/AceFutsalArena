package com.example.acefutsalarena;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class report extends AppCompatActivity {

    TextView txtReportBookings;
    TextView txtRevenue;
    TextView txtPopularCourt;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_report);

        db = FirebaseFirestore.getInstance();

        txtReportBookings = findViewById(R.id.txtReportBookings);
        txtRevenue = findViewById(R.id.txtRevenue);
        txtPopularCourt = findViewById(R.id.txtPopularCourt);

        loadReport();
    }

    private void loadReport() {

        db.collection("bookings")
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    int totalBookings = 0;
                    double totalRevenue = 0;

                    Map<String, Integer> courtCount =
                            new HashMap<>();

                    for (QueryDocumentSnapshot document :
                            querySnapshot) {

                        String status =
                                document.getString("status");

                        /*
                         * Only confirmed bookings
                         * are included in the report.
                         */
                        if ("Confirmed".equalsIgnoreCase(status)) {

                            totalBookings++;

                            /*
                             * Get the real price from Firestore.
                             */
                            Double price =
                                    document.getDouble("totalPrice");

                            if (price != null) {
                                totalRevenue += price;
                            }

                            /*
                             * Get the real court name
                             * from Firestore.
                             */
                            String courtName =
                                    document.getString("courtName");

                            if (courtName != null &&
                                    !courtName.trim().isEmpty()) {

                                Integer count =
                                        courtCount.get(courtName);

                                if (count == null) {
                                    courtCount.put(
                                            courtName,
                                            1
                                    );
                                } else {
                                    courtCount.put(
                                            courtName,
                                            count + 1
                                    );
                                }
                            }
                        }
                    }

                    /*
                     * Display real values.
                     */
                    txtReportBookings.setText(
                            String.valueOf(totalBookings)
                    );

                    txtRevenue.setText(
                            "RM " +
                                    String.format(
                                            java.util.Locale
                                                    .getDefault(),
                                            "%.2f",
                                            totalRevenue
                                    )
                    );

                    /*
                     * Find the court with the most
                     * confirmed bookings.
                     */
                    if (courtCount.isEmpty()) {

                        txtPopularCourt.setText(
                                "No confirmed bookings"
                        );

                    } else {

                        String popularCourt =
                                getPopularCourt(courtCount);

                        txtPopularCourt.setText(
                                popularCourt
                        );
                    }

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            report.this,
                            "Failed to load report",
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }

    private String getPopularCourt(
            Map<String, Integer> courtCount
    ) {

        String popularCourt = "";
        int highestCount = 0;

        for (Map.Entry<String, Integer> entry :
                courtCount.entrySet()) {

            if (entry.getValue() > highestCount) {

                highestCount = entry.getValue();
                popularCourt = entry.getKey();
            }
        }

        return popularCourt;
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadReport();
    }
}