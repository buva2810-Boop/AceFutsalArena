package com.example.acefutsalarena;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class bookingHistory extends AppCompatActivity {

    private RecyclerView recyclerBookings;
    private MaterialButton btnBackHome;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private BookingAdapter bookingAdapter;
    private ArrayList<BookingModel> bookingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_booking_history);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // XML
        recyclerBookings = findViewById(R.id.recyclerBookings);
        btnBackHome = findViewById(R.id.btnBackHome);

        // RecyclerView
        bookingList = new ArrayList<>();

        bookingAdapter = new BookingAdapter(bookingList);

        recyclerBookings.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recyclerBookings.setAdapter(bookingAdapter);

        // Back to Home
        btnBackHome.setOnClickListener(v -> {

            Intent intent = new Intent(
                    bookingHistory.this,
                    Home.class
            );

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_SINGLE_TOP
            );

            startActivity(intent);
            finish();
        });

        // Load booking history
        loadBookingHistory();
    }

    private void loadBookingHistory() {

        // Check Firebase login
        if (mAuth.getCurrentUser() == null) {

            Toast.makeText(
                    this,
                    "Please login first",
                    Toast.LENGTH_SHORT
            ).show();

            Intent intent = new Intent(
                    bookingHistory.this,
                    Login.class
            );

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);
            finish();

            return;
        }

        // Get current user's UID
        String userId = mAuth.getCurrentUser().getUid();

        // Get bookings for this user
        db.collection("bookings")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    bookingList.clear();

                    for (DocumentSnapshot document :
                            querySnapshot.getDocuments()) {

                        String courtName =
                                document.getString("courtName");

                        String date =
                                document.getString("date");

                        String time =
                                document.getString("time");

                        String status =
                                document.getString("status");

                        Double totalPrice =
                                document.getDouble("totalPrice");

                        // Prevent null values
                        if (courtName == null) {
                            courtName = "Unknown Court";
                        }

                        if (date == null) {
                            date = "-";
                        }

                        if (time == null) {
                            time = "-";
                        }

                        if (status == null) {
                            status = "Confirmed";
                        }

                        if (totalPrice == null) {
                            totalPrice = 0.0;
                        }

                        BookingModel booking =
                                new BookingModel(
                                        courtName,
                                        date,
                                        time,
                                        totalPrice,
                                        status
                                );

                        bookingList.add(booking);
                    }

                    bookingAdapter.notifyDataSetChanged();

                    if (bookingList.isEmpty()) {

                        Toast.makeText(
                                bookingHistory.this,
                                "No bookings found",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            bookingHistory.this,
                            "Failed to load bookings: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    public static class BookingModel {

        String courtName;
        String date;
        String time;
        double totalPrice;
        String status;

        public BookingModel(
                String courtName,
                String date,
                String time,
                double totalPrice,
                String status
        ) {
            this.courtName = courtName;
            this.date = date;
            this.time = time;
            this.totalPrice = totalPrice;
            this.status = status;
        }
    }

    public static class BookingAdapter
            extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

        private final List<BookingModel> bookingList;

        public BookingAdapter(List<BookingModel> bookingList) {
            this.bookingList = bookingList;
        }

        @NonNull
        @Override
        public BookingViewHolder onCreateViewHolder(
                @NonNull ViewGroup parent,
                int viewType
        ) {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(
                            R.layout.item_booking,
                            parent,
                            false
                    );

            return new BookingViewHolder(view);
        }

        @Override
        public void onBindViewHolder(
                @NonNull BookingViewHolder holder,
                int position
        ) {

            BookingModel booking =
                    bookingList.get(position);

            holder.txtCourtName.setText(
                    booking.courtName
            );

            holder.txtDate.setText(
                    "Date: " + booking.date
            );

            holder.txtTime.setText(
                    "Time: " + booking.time
            );

            holder.txtPrice.setText(
                    "Price: RM " +
                            String.format(
                                    Locale.getDefault(),
                                    "%.0f",
                                    booking.totalPrice
                            )
            );

            holder.txtStatus.setText(
                    "Status: " + booking.status
            );
        }

        @Override
        public int getItemCount() {
            return bookingList.size();
        }

        public static class BookingViewHolder
                extends RecyclerView.ViewHolder {

            TextView txtCourtName;
            TextView txtDate;
            TextView txtTime;
            TextView txtPrice;
            TextView txtStatus;

            public BookingViewHolder(
                    @NonNull View itemView
            ) {
                super(itemView);

                txtCourtName =
                        itemView.findViewById(
                                R.id.txtCourtName
                        );

                txtDate =
                        itemView.findViewById(
                                R.id.txtDate
                        );

                txtTime =
                        itemView.findViewById(
                                R.id.txtTime
                        );

                txtPrice =
                        itemView.findViewById(
                                R.id.txtPrice
                        );

                txtStatus =
                        itemView.findViewById(
                                R.id.txtStatus
                        );
            }
        }
    }
}