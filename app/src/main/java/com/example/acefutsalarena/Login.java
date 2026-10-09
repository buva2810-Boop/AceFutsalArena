package com.example.acefutsalarena;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    EditText edtEmail, edtPassword;
    Button btnLogin;
    TextView txtRegister;

    FirebaseAuth mAuth;

    // ADMIN EMAIL
    private static final String ADMIN_EMAIL = "buvaadmin@gmail.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // FIREBASE AUTHENTICATION
        mAuth = FirebaseAuth.getInstance();

        // CONNECT XML
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtRegister = findViewById(R.id.txtRegister);

        // LOGIN BUTTON
        btnLogin.setOnClickListener(v -> loginUser());

        // REGISTER BUTTON
        txtRegister.setOnClickListener(v -> {

            Intent intent = new Intent(
                    Login.this,
                    Register.class
            );

            startActivity(intent);
        });
    }

    private void loginUser() {

        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        // VALIDATION
        if (email.isEmpty()) {
            edtEmail.setError("Enter your email");
            edtEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            edtPassword.setError("Enter your password");
            edtPassword.requestFocus();
            return;
        }

        btnLogin.setEnabled(false);

        // FIREBASE LOGIN
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        Toast.makeText(
                                Login.this,
                                "Login Successful!",
                                Toast.LENGTH_SHORT
                        ).show();

                        // CHECK ADMIN
                        if (email.equalsIgnoreCase(ADMIN_EMAIL)) {

                            // ADMIN
                            Intent intent = new Intent(
                                    Login.this,
                                    adminDashboard.class
                            );

                            startActivity(intent);

                        } else {

                            // CUSTOMER
                            Intent intent = new Intent(
                                    Login.this,
                                    Home.class
                            );

                            startActivity(intent);
                        }

                        finish();

                    } else {

                        btnLogin.setEnabled(true);

                        String errorMessage = "Login failed";

                        if (task.getException() != null) {
                            errorMessage =
                                    task.getException().getMessage();
                        }

                        Toast.makeText(
                                Login.this,
                                "Login Error:\n" + errorMessage,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}