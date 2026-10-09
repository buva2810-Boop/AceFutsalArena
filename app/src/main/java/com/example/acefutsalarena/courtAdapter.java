package com.example.acefutsalarena;

import android.app.AlertDialog;
import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Locale;

public class courtAdapter
        extends RecyclerView.Adapter<courtAdapter.CourtViewHolder> {

    Context context;
    ArrayList<court> courtList;
    FirebaseFirestore db;

    public courtAdapter(
            Context context,
            ArrayList<court> courtList
    ) {
        this.context = context;
        this.courtList = courtList;
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public CourtViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater.from(context)
                .inflate(
                        R.layout.item_manage_court,
                        parent,
                        false
                );

        return new CourtViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull CourtViewHolder holder,
            int position
    ) {

        court selectedCourt = courtList.get(position);

        holder.txtCourtName.setText(
                selectedCourt.getCourtName()
        );

        holder.txtCourtPrice.setText(
                "Price: RM " +
                        String.format(
                                Locale.getDefault(),
                                "%.2f",
                                selectedCourt.getPrice()
                        ) +
                        " / hour"
        );

        if (selectedCourt.isAvailable()) {

            holder.txtCourtStatus.setText(
                    "AVAILABLE"
            );

            holder.radioAvailable.setChecked(true);

        } else {

            holder.txtCourtStatus.setText(
                    "UNAVAILABLE"
            );

            holder.radioUnavailable.setChecked(true);
        }

        holder.radioAvailable.setOnClickListener(v -> {

            updateAvailability(
                    selectedCourt,
                    true,
                    position
            );
        });

        holder.radioUnavailable.setOnClickListener(v -> {

            updateAvailability(
                    selectedCourt,
                    false,
                    position
            );
        });

        holder.btnEditCourt.setOnClickListener(v -> {

            showEditDialog(
                    selectedCourt,
                    position
            );
        });
    }

    private void updateAvailability(
            court selectedCourt,
            boolean available,
            int position
    ) {

        db.collection("courts")
                .document(selectedCourt.getCourtId())
                .update("available", available)
                .addOnSuccessListener(unused -> {

                    selectedCourt.setAvailable(
                            available
                    );

                    notifyItemChanged(position);

                    if (available) {

                        Toast.makeText(
                                context,
                                "Court is now available",
                                Toast.LENGTH_SHORT
                        ).show();

                    } else {

                        Toast.makeText(
                                context,
                                "Court is now unavailable",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            context,
                            "Failed to update availability",
                            Toast.LENGTH_SHORT
                    ).show();

                    notifyItemChanged(position);
                });
    }

    private void showEditDialog(
            court selectedCourt,
            int position
    ) {

        android.widget.LinearLayout layout =
                new android.widget.LinearLayout(context);

        layout.setOrientation(
                android.widget.LinearLayout.VERTICAL
        );

        layout.setPadding(
                30,
                10,
                30,
                0
        );

        EditText nameInput =
                new EditText(context);

        nameInput.setHint("Court name");
        nameInput.setSingleLine(true);
        nameInput.setText(
                selectedCourt.getCourtName()
        );

        layout.addView(nameInput);

        EditText priceInput =
                new EditText(context);

        priceInput.setHint("Price per hour");
        priceInput.setSingleLine(true);

        priceInput.setInputType(
                InputType.TYPE_CLASS_NUMBER |
                        InputType.TYPE_NUMBER_FLAG_DECIMAL
        );

        priceInput.setText(
                String.valueOf(
                        selectedCourt.getPrice()
                )
        );

        layout.addView(priceInput);

        AlertDialog dialog =
                new AlertDialog.Builder(context)
                        .setTitle(
                                "Edit " +
                                        selectedCourt.getCourtName()
                        )
                        .setView(layout)
                        .setPositiveButton(
                                "SAVE",
                                null
                        )
                        .setNegativeButton(
                                "CANCEL",
                                null
                        )
                        .create();

        dialog.setOnShowListener(
                dialogInterface -> {

                    dialog.getButton(
                            AlertDialog.BUTTON_POSITIVE
                    ).setOnClickListener(v -> {

                        String newName =
                                nameInput.getText()
                                        .toString()
                                        .trim();

                        String priceText =
                                priceInput.getText()
                                        .toString()
                                        .trim();

                        if (newName.isEmpty()) {

                            nameInput.setError(
                                    "Enter court name"
                            );

                            return;
                        }

                        if (priceText.isEmpty()) {

                            priceInput.setError(
                                    "Enter price"
                            );

                            return;
                        }

                        double newPrice;

                        try {

                            newPrice =
                                    Double.parseDouble(
                                            priceText
                                    );

                        } catch (Exception e) {

                            priceInput.setError(
                                    "Invalid price"
                            );

                            return;
                        }

                        updateCourt(
                                selectedCourt,
                                newName,
                                newPrice,
                                position,
                                dialog
                        );
                    });
                }
        );

        dialog.show();
    }

    private void updateCourt(
            court selectedCourt,
            String newName,
            double newPrice,
            int position,
            AlertDialog dialog
    ) {

        db.collection("courts")
                .document(
                        selectedCourt.getCourtId()
                )
                .update(
                        "name",
                        newName,
                        "price",
                        newPrice
                )
                .addOnSuccessListener(unused -> {

                    selectedCourt.setCourtName(
                            newName
                    );

                    selectedCourt.setPrice(
                            newPrice
                    );

                    notifyItemChanged(position);

                    dialog.dismiss();

                    Toast.makeText(
                            context,
                            "Court updated successfully",
                            Toast.LENGTH_SHORT
                    ).show();
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            context,
                            "Failed to update court: " +
                                    e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    @Override
    public int getItemCount() {
        return courtList.size();
    }

    public static class CourtViewHolder
            extends RecyclerView.ViewHolder {

        TextView txtCourtName;
        TextView txtCourtPrice;
        TextView txtCourtStatus;

        RadioGroup radioAvailability;

        RadioButton radioAvailable;
        RadioButton radioUnavailable;

        MaterialButton btnEditCourt;

        public CourtViewHolder(
                @NonNull View itemView
        ) {

            super(itemView);

            txtCourtName =
                    itemView.findViewById(
                            R.id.txtCourtName
                    );

            txtCourtPrice =
                    itemView.findViewById(
                            R.id.txtCourtPrice
                    );

            txtCourtStatus =
                    itemView.findViewById(
                            R.id.txtCourtStatus
                    );

            radioAvailability =
                    itemView.findViewById(
                            R.id.radioAvailability
                    );

            radioAvailable =
                    itemView.findViewById(
                            R.id.radioAvailable
                    );

            radioUnavailable =
                    itemView.findViewById(
                            R.id.radioUnavailable
                    );

            btnEditCourt =
                    itemView.findViewById(
                            R.id.btnEditCourt
                    );
        }
    }
}