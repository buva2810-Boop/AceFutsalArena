package com.example.acefutsalarena;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class bookingSuccess extends AppCompatActivity {

    TextView txtBookingId;
    TextView txtSuccessCourt;
    TextView txtSuccessDate;
    TextView txtSuccessTime;

    MaterialButton btnViewBooking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_booking_success);

        // Connect XML
        txtBookingId =
                findViewById(R.id.txtBookingId);

        txtSuccessCourt =
                findViewById(R.id.txtSuccessCourt);

        txtSuccessDate =
                findViewById(R.id.txtSuccessDate);

        txtSuccessTime =
                findViewById(R.id.txtSuccessTime);

        btnViewBooking =
                findViewById(R.id.btnViewBooking);

        // Get actual booking information
        String bookingId =
                getIntent().getStringExtra("bookingId");

        String courtName =
                getIntent().getStringExtra("courtName");

        String date =
                getIntent().getStringExtra("date");

        String time =
                getIntent().getStringExtra("time");

        // Display Booking ID
        if (bookingId != null && !bookingId.isEmpty()) {

            txtBookingId.setText(
                    "Booking ID: " + bookingId
            );
        }

        // Display Court
        if (courtName != null && !courtName.isEmpty()) {

            txtSuccessCourt.setText(
                    "Court: " + courtName
            );
        }

        // Display Date
        if (date != null && !date.isEmpty()) {

            txtSuccessDate.setText(
                    "Date: " + date
            );
        }

        // Display Time
        if (time != null && !time.isEmpty()) {

            txtSuccessTime.setText(
                    "Time: " + time
            );
        }

        // View My Bookings
        btnViewBooking.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            bookingSuccess.this,
                            bookingHistory.class
                    );

            startActivity(intent);
        });
    }
}