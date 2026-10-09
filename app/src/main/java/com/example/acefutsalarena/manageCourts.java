package com.example.acefutsalarena;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class manageCourts extends AppCompatActivity {

    RecyclerView recyclerCourts;

    FirebaseFirestore db;

    ArrayList<court> courtList;

    courtAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_manage_courts);

        db = FirebaseFirestore.getInstance();

        recyclerCourts = findViewById(R.id.recyclerCourts);

        recyclerCourts.setLayoutManager(
                new LinearLayoutManager(this)
        );

        courtList = new ArrayList<>();

        adapter = new courtAdapter(
                this,
                courtList
        );

        recyclerCourts.setAdapter(adapter);

        createDefaultCourts();
    }

    private void createDefaultCourts() {

        createCourt(
                "courtA",
                "Court A",
                120.0
        );

        createCourt(
                "courtB",
                "Court B",
                120.0
        );

        createCourt(
                "courtC",
                "Court C",
                120.0
        );

        createCourt(
                "courtD",
                "Court D",
                120.0
        );
    }

    private void createCourt(
            String courtId,
            String courtName,
            double price
    ) {

        db.collection("courts")
                .document(courtId)
                .get()
                .addOnSuccessListener(document -> {

                    if (!document.exists()) {

                        db.collection("courts")
                                .document(courtId)
                                .set(new java.util.HashMap<String, Object>() {{
                                    put("name", courtName);
                                    put("price", price);
                                    put("available", true);
                                }})
                                .addOnSuccessListener(unused -> {

                                    loadCourts();

                                })
                                .addOnFailureListener(e -> {

                                    Toast.makeText(
                                            manageCourts.this,
                                            "Failed to create " + courtName,
                                            Toast.LENGTH_SHORT
                                    ).show();

                                });

                    } else {

                        loadCourts();

                    }

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            manageCourts.this,
                            "Failed to check " + courtName,
                            Toast.LENGTH_SHORT
                    ).show();

                });
    }

    private void loadCourts() {

        db.collection("courts")
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    courtList.clear();

                    String[] courtIds = {
                            "courtA",
                            "courtB",
                            "courtC",
                            "courtD"
                    };

                    for (String courtId : courtIds) {

                        DocumentSnapshot document =
                                querySnapshot.getDocuments()
                                        .stream()
                                        .filter(doc ->
                                                doc.getId().equals(courtId)
                                        )
                                        .findFirst()
                                        .orElse(null);

                        if (document == null) {
                            continue;
                        }

                        String courtName =
                                document.getString("name");

                        Double price =
                                document.getDouble("price");

                        Boolean available =
                                document.getBoolean("available");

                        if (courtName == null ||
                                courtName.isEmpty()) {

                            courtName = courtId;
                        }

                        if (price == null) {
                            price = 120.0;
                        }

                        if (available == null) {
                            available = false;
                        }

                        court selectedCourt =
                                new court(
                                        courtId,
                                        courtName,
                                        price,
                                        available
                                );

                        courtList.add(selectedCourt);
                    }

                    adapter.notifyDataSetChanged();

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            manageCourts.this,
                            "Failed to load courts: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();

                });
    }

    @Override
    protected void onResume() {

        super.onResume();

        if (db != null &&
                courtList != null) {

            loadCourts();
        }
    }
}