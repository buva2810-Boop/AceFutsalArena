package com.example.acefutsalarena;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class manageBookings extends AppCompatActivity {

    RecyclerView recyclerAdminBookings;

    FirebaseFirestore db;

    ArrayList<Admin_Booking> bookingList;

    AdminBookingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_manage_bookings
        );

        // Firebase
        db = FirebaseFirestore.getInstance();

        // Connect RecyclerView
        recyclerAdminBookings =
                findViewById(
                        R.id.recyclerAdminBookings
                );

        // RecyclerView setup
        recyclerAdminBookings.setLayoutManager(
                new LinearLayoutManager(this)
        );

        bookingList =
                new ArrayList<>();

        adapter =
                new AdminBookingAdapter(
                        this,
                        bookingList
                );

        recyclerAdminBookings.setAdapter(
                adapter
        );

        // Load bookings
        loadBookings();
    }

    private void loadBookings() {

        db.collection("bookings")
                .orderBy(
                        "date",
                        Query.Direction.ASCENDING
                )
                .get()
                .addOnSuccessListener(
                        querySnapshot -> {

                            bookingList.clear();

                            for (
                                    com.google.firebase.firestore.DocumentSnapshot document
                                    : querySnapshot.getDocuments()
                            ) {

                                String bookingId =
                                        document.getId();

                                String customerName =
                                        document.getString(
                                                "customerName"
                                        );

                                String customerEmail =
                                        document.getString(
                                                "customerEmail"
                                        );

                                String courtName =
                                        document.getString(
                                                "courtName"
                                        );

                                String date =
                                        document.getString(
                                                "date"
                                        );

                                String time =
                                        document.getString(
                                                "time"
                                        );

                                String status =
                                        document.getString(
                                                "status"
                                        );

                                Double totalPrice =
                                        document.getDouble(
                                                "totalPrice"
                                        );

                                if (customerName == null) {
                                    customerName = "Customer";
                                }

                                if (customerEmail == null) {
                                    customerEmail = "No email";
                                }

                                if (courtName == null) {
                                    courtName = "Court";
                                }

                                if (date == null) {
                                    date = "-";
                                }

                                if (time == null) {
                                    time = "-";
                                }

                                if (status == null) {
                                    status = "Pending";
                                }

                                if (totalPrice == null) {
                                    totalPrice = 0.0;
                                }

                                Admin_Booking booking =
                                        new Admin_Booking(
                                                bookingId,
                                                customerName,
                                                customerEmail,
                                                courtName,
                                                date,
                                                time,
                                                status,
                                                totalPrice
                                        );

                                bookingList.add(
                                        booking
                                );
                            }

                            adapter.notifyDataSetChanged();

                            if (bookingList.isEmpty()) {

                                Toast.makeText(
                                        manageBookings.this,
                                        "No bookings found",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                )
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            manageBookings.this,
                            "Failed to load bookings: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (db != null &&
                bookingList != null) {

            loadBookings();
        }
    }
}