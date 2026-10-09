package com.example.acefutsalarena;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Booking extends AppCompatActivity {

    CalendarView calendarView;
    RadioGroup timeSlotGroup;
    MaterialButton btnConfirmBooking;

    TextView txtSelectedCourt;
    TextView txtTotalPrice;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    String courtId;
    String courtName;

    double price;

    String selectedDate;
    String selectedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_booking);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Connect XML
        calendarView = findViewById(R.id.calendarView);
        timeSlotGroup = findViewById(R.id.timeSlotGroup);
        btnConfirmBooking = findViewById(R.id.btnConfirmBooking);

        txtSelectedCourt = findViewById(R.id.txtSelectedCourt);
        txtTotalPrice = findViewById(R.id.txtTotalPrice);

        // Get court information from CourtDetails
        courtId = getIntent().getStringExtra("courtId");
        courtName = getIntent().getStringExtra("courtName");

        // Get court price
        price = getIntent().getDoubleExtra("price", 120.0);

        // Default date = today
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat dateFormat =
                new SimpleDateFormat(
                        "dd/MM/yyyy",
                        Locale.getDefault()
                );

        selectedDate =
                dateFormat.format(calendar.getTime());

        // Display court information
        if (courtName != null && !courtName.isEmpty()) {

            txtSelectedCourt.setText(
                    courtName
                            + " • RM "
                            + String.format(
                            Locale.getDefault(),
                            "%.0f",
                            price
                    )
                            + "/hour"
            );

        } else {

            txtSelectedCourt.setText(
                    "Court • RM "
                            + String.format(
                            Locale.getDefault(),
                            "%.0f",
                            price
                    )
                            + "/hour"
            );
        }

        // Display total price
        txtTotalPrice.setText(
                "Total: RM "
                        + String.format(
                        Locale.getDefault(),
                        "%.0f",
                        price
                )
        );

        // Calendar
        calendarView.setOnDateChangeListener(
                (view, year, month, dayOfMonth) -> {

                    selectedDate =
                            String.format(
                                    Locale.getDefault(),
                                    "%02d/%02d/%04d",
                                    dayOfMonth,
                                    month + 1,
                                    year
                            );
                }
        );

        // Confirm booking
        btnConfirmBooking.setOnClickListener(
                v -> createBooking()
        );
    }

    private void createBooking() {

        // Make sure court exists
        if (courtId == null || courtId.isEmpty()) {

            Toast.makeText(
                    Booking.this,
                    "Court information is missing",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        // Check time selection
        int selectedRadioId =
                timeSlotGroup.getCheckedRadioButtonId();

        if (selectedRadioId == -1) {

            Toast.makeText(
                    Booking.this,
                    "Please select a time slot",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        RadioButton selectedRadioButton =
                findViewById(selectedRadioId);

        selectedTime =
                selectedRadioButton
                        .getText()
                        .toString();

        // Check login
        if (mAuth.getCurrentUser() == null) {

            Toast.makeText(
                    Booking.this,
                    "Please login first",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Disable button while checking
        btnConfirmBooking.setEnabled(false);

        String uid =
                mAuth.getCurrentUser().getUid();

        String email =
                mAuth.getCurrentUser().getEmail();

        // Get customer information
        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(document -> {

                    if (!document.exists()) {

                        btnConfirmBooking.setEnabled(true);

                        Toast.makeText(
                                Booking.this,
                                "User information not found",
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }

                    String customerName =
                            document.getString("name");

                    if (customerName == null ||
                            customerName.isEmpty()) {

                        customerName = "Customer";
                    }

                    // Check availability
                    checkBookingAvailability(
                            uid,
                            email,
                            customerName
                    );
                })
                .addOnFailureListener(e -> {

                    btnConfirmBooking.setEnabled(true);

                    Toast.makeText(
                            Booking.this,
                            "Unable to load user information: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void checkBookingAvailability(
            String uid,
            String email,
            String customerName
    ) {

        db.collection("bookings")
                .whereEqualTo(
                        "courtId",
                        courtId
                )
                .whereEqualTo(
                        "date",
                        selectedDate
                )
                .whereEqualTo(
                        "time",
                        selectedTime
                )
                .whereEqualTo(
                        "status",
                        "Confirmed"
                )
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    // Already booked
                    if (!querySnapshot.isEmpty()) {

                        btnConfirmBooking.setEnabled(true);

                        Toast.makeText(
                                Booking.this,
                                "NOT AVAILABLE\n"
                                        + "This time slot has already been booked.",
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }

                    // Available
                    saveBooking(
                            uid,
                            email,
                            customerName
                    );
                })
                .addOnFailureListener(e -> {

                    btnConfirmBooking.setEnabled(true);

                    Toast.makeText(
                            Booking.this,
                            "Unable to check availability:\n"
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void saveBooking(
            String uid,
            String email,
            String customerName
    ) {

        Map<String, Object> booking =
                new HashMap<>();

        // User information
        booking.put(
                "userId",
                uid
        );

        booking.put(
                "customerName",
                customerName
        );

        booking.put(
                "customerEmail",
                email
        );

        // Court information
        booking.put(
                "courtId",
                courtId
        );

        booking.put(
                "courtName",
                courtName
        );

        // Booking information
        booking.put(
                "date",
                selectedDate
        );

        booking.put(
                "time",
                selectedTime
        );

        booking.put(
                "duration",
                1
        );

        // Price
        booking.put(
                "totalPrice",
                price
        );

        // Booking status
        booking.put(
                "status",
                "Confirmed"
        );

        // Save booking to Firestore
        db.collection("bookings")
                .add(booking)
                .addOnSuccessListener(
                        documentReference -> {

                            Toast.makeText(
                                    Booking.this,
                                    "Booking successful!",
                                    Toast.LENGTH_SHORT
                            ).show();

                            // Open Booking Success page
                            Intent intent =
                                    new Intent(
                                            Booking.this,
                                            bookingSuccess.class
                                    );

                            // Send actual booking information
                            intent.putExtra(
                                    "bookingId",
                                    documentReference.getId()
                            );

                            intent.putExtra(
                                    "courtName",
                                    courtName
                            );

                            intent.putExtra(
                                    "date",
                                    selectedDate
                            );

                            intent.putExtra(
                                    "time",
                                    selectedTime
                            );

                            startActivity(intent);

                            finish();
                        }
                )
                .addOnFailureListener(e -> {

                    btnConfirmBooking.setEnabled(true);

                    Toast.makeText(
                            Booking.this,
                            "Booking failed:\n"
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
}