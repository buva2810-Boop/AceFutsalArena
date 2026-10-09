package com.example.acefutsalarena;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Locale;

public class AdminBookingAdapter
        extends RecyclerView.Adapter<AdminBookingAdapter.BookingViewHolder> {

    Context context;
    ArrayList<Admin_Booking> bookingList;

    FirebaseFirestore db;

    public AdminBookingAdapter(
            Context context,
            ArrayList<Admin_Booking> bookingList
    ) {

        this.context = context;
        this.bookingList = bookingList;

        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view =
                LayoutInflater.from(context)
                        .inflate(
                                R.layout.item_admin_booking,
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

        Admin_Booking booking =
                bookingList.get(position);

        holder.txtCustomerName.setText(
                booking.getCustomerName()
        );

        holder.txtCustomerEmail.setText(
                booking.getCustomerEmail()
        );

        holder.txtCourt.setText(
                "Court: " +
                        booking.getCourtName()
        );

        holder.txtDate.setText(
                "Date: " +
                        booking.getDate()
        );

        holder.txtTime.setText(
                "Time: " +
                        booking.getTime()
        );

        holder.txtPrice.setText(
                "Total: RM " +
                        String.format(
                                Locale.getDefault(),
                                "%.0f",
                                booking.getTotalPrice()
                        )
        );

        holder.txtStatus.setText(
                "Status: " +
                        booking.getStatus()
        );

        // Pending booking
        if (booking.getStatus().equalsIgnoreCase(
                "Pending"
        )) {

            holder.btnAccept.setVisibility(
                    View.VISIBLE
            );

            holder.btnDecline.setVisibility(
                    View.VISIBLE
            );

        } else {

            holder.btnAccept.setVisibility(
                    View.GONE
            );

            holder.btnDecline.setVisibility(
                    View.GONE
            );
        }

        // Accept
        holder.btnAccept.setOnClickListener(v -> {

            updateBookingStatus(
                    booking.getBookingId(),
                    "Confirmed",
                    position
            );
        });

        // Decline
        holder.btnDecline.setOnClickListener(v -> {

            updateBookingStatus(
                    booking.getBookingId(),
                    "Declined",
                    position
            );
        });
    }

    private void updateBookingStatus(
            String bookingId,
            String newStatus,
            int position
    ) {

        db.collection("bookings")
                .document(bookingId)
                .update(
                        "status",
                        newStatus
                )
                .addOnSuccessListener(unused -> {

                    bookingList
                            .get(position)
                            .setStatus(newStatus);

                    notifyItemChanged(position);

                    Toast.makeText(
                            context,
                            "Booking " +
                                    newStatus,
                            Toast.LENGTH_SHORT
                    ).show();
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            context,
                            "Failed to update booking: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    @Override
    public int getItemCount() {

        return bookingList.size();
    }

    public static class BookingViewHolder
            extends RecyclerView.ViewHolder {

        TextView txtCustomerName;
        TextView txtCustomerEmail;
        TextView txtCourt;
        TextView txtDate;
        TextView txtTime;
        TextView txtPrice;
        TextView txtStatus;

        MaterialButton btnAccept;
        MaterialButton btnDecline;

        public BookingViewHolder(
                @NonNull View itemView
        ) {

            super(itemView);

            txtCustomerName =
                    itemView.findViewById(
                            R.id.txtAdminCustomerName
                    );

            txtCustomerEmail =
                    itemView.findViewById(
                            R.id.txtAdminCustomerEmail
                    );

            txtCourt =
                    itemView.findViewById(
                            R.id.txtAdminCourt
                    );

            txtDate =
                    itemView.findViewById(
                            R.id.txtAdminDate
                    );

            txtTime =
                    itemView.findViewById(
                            R.id.txtAdminTime
                    );

            txtPrice =
                    itemView.findViewById(
                            R.id.txtAdminPrice
                    );

            txtStatus =
                    itemView.findViewById(
                            R.id.txtAdminStatus
                    );

            btnAccept =
                    itemView.findViewById(
                            R.id.btnAccept
                    );

            btnDecline =
                    itemView.findViewById(
                            R.id.btnDecline
                    );
        }
    }
}