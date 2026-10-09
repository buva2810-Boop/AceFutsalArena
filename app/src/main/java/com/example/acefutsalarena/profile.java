package com.example.acefutsalarena;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class profile extends AppCompatActivity {

    TextView txtProfileInitial;
    TextView txtProfileName;
    TextView txtProfileEmail;
    TextView txtProfilePhone;
    TextView txtEmailInfo;

    MaterialCardView cardMyBookings;
    MaterialButton btnEditProfile;
    MaterialButton btnLogout;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Connect XML views
        txtProfileInitial = findViewById(R.id.txtProfileInitial);
        txtProfileName = findViewById(R.id.txtProfileName);
        txtProfileEmail = findViewById(R.id.txtProfileEmail);
        txtProfilePhone = findViewById(R.id.txtProfilePhone);
        txtEmailInfo = findViewById(R.id.txtEmailInfo);

        cardMyBookings = findViewById(R.id.cardMyBookings);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnLogout = findViewById(R.id.btnLogout);

        // Load profile
        loadUserProfile();

        // My Bookings
        cardMyBookings.setOnClickListener(v -> {

            Intent intent = new Intent(
                    profile.this,
                    bookingHistory.class
            );

            startActivity(intent);
        });

        // Edit Phone Number
        btnEditProfile.setOnClickListener(v -> {

            showEditPhoneDialog();
        });

        // Logout
        btnLogout.setOnClickListener(v -> {

            mAuth.signOut();

            Intent intent = new Intent(
                    profile.this,
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

    private void loadUserProfile() {

        FirebaseUser currentUser =
                mAuth.getCurrentUser();

        // Check login
        if (currentUser == null) {

            Intent intent = new Intent(
                    profile.this,
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

        String uid =
                currentUser.getUid();

        // Get email from Firebase Authentication
        String email =
                currentUser.getEmail();

        if (email != null) {

            txtProfileEmail.setText(email);
            txtEmailInfo.setText(email);
        }

        // Get profile information from Firestore
        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot.exists()) {

                        // Name
                        String name =
                                documentSnapshot.getString("fullName");

                        if (name == null ||
                                name.isEmpty()) {

                            name = "User";
                        }

                        txtProfileName.setText(name);

                        // Profile initial
                        String initial =
                                name.substring(0, 1).toUpperCase();

                        txtProfileInitial.setText(initial);

                        // Phone
                        String phone =
                                documentSnapshot.getString("phone");

                        if (phone != null &&
                                !phone.isEmpty()) {

                            txtProfilePhone.setText(phone);

                        } else {

                            txtProfilePhone.setText(
                                    "Not provided"
                            );
                        }

                    } else {

                        txtProfileName.setText("User");
                        txtProfileInitial.setText("U");
                        txtProfilePhone.setText(
                                "Not provided"
                        );
                    }
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            profile.this,
                            "Failed to load profile",
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }

    // Show Edit Phone Dialog
    private void showEditPhoneDialog() {

        FirebaseUser currentUser =
                mAuth.getCurrentUser();

        if (currentUser == null) {
            return;
        }

        // Phone input
        EditText phoneInput =
                new EditText(this);

        phoneInput.setHint(
                "Enter phone number"
        );

        phoneInput.setSingleLine(true);

        phoneInput.setInputType(
                InputType.TYPE_CLASS_PHONE
        );

        // Show current phone number
        String currentPhone =
                txtProfilePhone.getText().toString();

        if (!currentPhone.equals("Not provided")) {

            phoneInput.setText(currentPhone);

            phoneInput.setSelection(
                    phoneInput.getText().length()
            );
        }

        // Dialog
        AlertDialog dialog =
                new AlertDialog.Builder(this)
                        .setTitle("Edit Phone Number")
                        .setView(phoneInput)
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

                        String newPhone =
                                phoneInput
                                        .getText()
                                        .toString()
                                        .trim();

                        // Check empty
                        if (newPhone.isEmpty()) {

                            phoneInput.setError(
                                    "Please enter phone number"
                            );

                            return;
                        }

                        updatePhoneNumber(
                                newPhone,
                                dialog
                        );
                    });
                }
        );

        dialog.show();
    }

    // Update phone in Firestore
    private void updatePhoneNumber(
            String newPhone,
            AlertDialog dialog
    ) {

        FirebaseUser currentUser =
                mAuth.getCurrentUser();

        if (currentUser == null) {
            return;
        }

        String uid =
                currentUser.getUid();

        // Disable SAVE button
        dialog.getButton(
                AlertDialog.BUTTON_POSITIVE
        ).setEnabled(false);

        // Update phone
        db.collection("users")
                .document(uid)
                .update(
                        "phone",
                        newPhone
                )
                .addOnSuccessListener(unused -> {

                    // Update screen immediately
                    txtProfilePhone.setText(
                            newPhone
                    );

                    dialog.dismiss();

                    Toast.makeText(
                            profile.this,
                            "Phone number updated successfully",
                            Toast.LENGTH_SHORT
                    ).show();
                })
                .addOnFailureListener(e -> {

                    dialog.getButton(
                            AlertDialog.BUTTON_POSITIVE
                    ).setEnabled(true);

                    Toast.makeText(
                            profile.this,
                            "Failed to update phone number: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
}