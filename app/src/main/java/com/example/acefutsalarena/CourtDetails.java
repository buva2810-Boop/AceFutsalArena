package com.example.acefutsalarena;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.util.Locale;

public class CourtDetails extends AppCompatActivity {

    ImageView imgCourt;
    TextView txtCourtName;
    TextView txtCourtPrice;
    TextView txtCourtDescription;
    MaterialButton btnBookCourt;

    String courtId;
    String courtName;
    double courtPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_court_details);

        // Connect XML views
        imgCourt = findViewById(R.id.imgCourt);
        txtCourtName = findViewById(R.id.txtCourtName);
        txtCourtPrice = findViewById(R.id.txtCourtPrice);
        txtCourtDescription = findViewById(R.id.txtCourtDescription);
        btnBookCourt = findViewById(R.id.btnBookCourt);

        // Get information from Home
        courtId = getIntent().getStringExtra("courtId");
        courtName = getIntent().getStringExtra("courtName");
        courtPrice = getIntent().getDoubleExtra("courtPrice", 120.0);

        // Check if court ID is missing
        if (courtId == null || courtId.isEmpty()) {

            Toast.makeText(
                    CourtDetails.this,
                    "Court ID is missing",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Display court information
        if (courtName != null && !courtName.isEmpty()) {
            txtCourtName.setText(courtName);
        }

        txtCourtPrice.setText(
                "RM " +
                        String.format(
                                Locale.getDefault(),
                                "%.0f",
                                courtPrice
                        ) +
                        " / hour"
        );

        // Change image
        setCourtImage();

        // Change description
        setCourtDescription();

        // Book button
        btnBookCourt.setOnClickListener(v -> {

            Intent intent = new Intent(
                    CourtDetails.this,
                    Booking.class
            );

            intent.putExtra("courtId", courtId);
            intent.putExtra("courtName", courtName);
            intent.putExtra("price", courtPrice);

            startActivity(intent);
        });
    }

    private void setCourtImage() {

        switch (courtId) {

            case "courtA":
                imgCourt.setImageResource(R.drawable.futsal_1);
                break;

            case "courtB":
                imgCourt.setImageResource(R.drawable.futsal_2);
                break;

            case "courtC":
                imgCourt.setImageResource(R.drawable.futsal_3);
                break;

            case "courtD":
                imgCourt.setImageResource(R.drawable.futsal_3);
                break;

            default:
                imgCourt.setImageResource(R.drawable.futsal_1);
                break;
        }
    }

    private void setCourtDescription() {

        switch (courtId) {

            case "courtA":
                txtCourtDescription.setText(
                        "A clean and comfortable futsal court suitable for friendly matches, training sessions and tournaments."
                );
                break;

            case "courtB":
                txtCourtDescription.setText(
                        "A spacious futsal court with a comfortable playing area, suitable for friendly matches and training sessions."
                );
                break;

            case "courtC":
                txtCourtDescription.setText(
                        "A well-maintained futsal court designed for recreational games, team training and competitive matches."
                );
                break;

            case "courtD":
                txtCourtDescription.setText(
                        "A modern and comfortable futsal court suitable for group games, training sessions and tournaments."
                );
                break;

            default:
                txtCourtDescription.setText(
                        "A clean and comfortable futsal court suitable for friendly matches, training sessions and tournaments."
                );
                break;
        }
    }
}