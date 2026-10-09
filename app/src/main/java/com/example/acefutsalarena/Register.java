package com.example.acefutsalarena;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    EditText edtName, edtEmail, edtPhone, edtPassword;
    Button btnRegister;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // FIREBASE
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // TEST FIREBASE INITIALIZATION
        Toast.makeText(
                Register.this,
                "Successfully Register",
                Toast.LENGTH_LONG
        ).show();

        // CONNECT XML
        edtName = findViewById(R.id.edtFullName);
        edtEmail = findViewById(R.id.edtRegisterEmail);
        edtPhone = findViewById(R.id.edtPhone);
        edtPassword = findViewById(R.id.edtRegisterPassword);
        btnRegister = findViewById(R.id.btnRegister);

        // REGISTER BUTTON
        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {

        // Get input values
        String name = edtName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        // VALIDATION

        if (name.isEmpty()) {
            edtName.setError("Enter your full name");
            edtName.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            edtEmail.setError("Enter your email");
            edtEmail.requestFocus();
            return;
        }

        if (phone.isEmpty()) {
            edtPhone.setError("Enter your phone number");
            edtPhone.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            edtPassword.setError("Enter your password");
            edtPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            edtPassword.setError("Password must be at least 6 characters");
            edtPassword.requestFocus();
            return;
        }

        // Disable button while registering
        btnRegister.setEnabled(false);

        // FIREBASE AUTHENTICATION

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        // Check Firebase user
                        if (mAuth.getCurrentUser() == null) {

                            btnRegister.setEnabled(true);

                            Toast.makeText(
                                    Register.this,
                                    "Registration error: User not found",
                                    Toast.LENGTH_LONG
                            ).show();

                            return;
                        }

                        // Get Firebase UID
                        String uid = mAuth.getCurrentUser().getUid();

                        // CREATE USER DATA

                        Map<String, Object> user = new HashMap<>();

                        user.put("name", name);
                        user.put("email", email);
                        user.put("phone", phone);
                        user.put("role", "customer");

                        // SAVE TO FIRESTORE

                        db.collection("users")
                                .document(uid)
                                .set(user)
                                .addOnSuccessListener(unused -> {

                                    Toast.makeText(
                                            Register.this,
                                            "Registration successful!",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    // Go to Home
                                    Intent intent = new Intent(
                                            Register.this,
                                            Home.class
                                    );

                                    startActivity(intent);
                                    finish();

                                })
                                .addOnFailureListener(e -> {

                                    btnRegister.setEnabled(true);

                                    Toast.makeText(
                                            Register.this,
                                            "Firestore Error:\n"
                                                    + e.getClass().getSimpleName()
                                                    + "\n"
                                                    + e.getMessage(),
                                            Toast.LENGTH_LONG
                                    ).show();
                                });

                    } else {

                        btnRegister.setEnabled(true);

                        String errorMessage = "Registration failed";

                        if (task.getException() != null) {
                            errorMessage =
                                    task.getException().getMessage();
                        }

                        // AUTHENTICATION ERROR
                        Toast.makeText(
                                Register.this,
                                "Firebase Authentication Error:\n"
                                        + errorMessage,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}