package com.example.movieroulette;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PreferencesActivity extends AppCompatActivity {

    Spinner genreSpinner, scoreSpinner, yearSpinner;
    Button btnSpin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_preferences);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        genreSpinner = findViewById(R.id.spinnerGenre);
        scoreSpinner = findViewById(R.id.spinnerScore);
        yearSpinner = findViewById(R.id.spinnerYear);
        btnSpin = findViewById(R.id.btnSpin);

        String[] genres = {"Action", "Comedy", "Drama", "Horror", "Sci-Fi"};
        String[] scores = {"0 - 4.9", "5 - 6.9", "7 - 7.9", "8 - 8.9", "9 -10"};
        String[] years = {"1950 - 1999", "2000 - 2009", "2010 - 2019", "2020 - current"};

        genreSpinner.setAdapter(new ArrayAdapter<>(PreferencesActivity.this, android.R.layout.simple_spinner_dropdown_item, genres));
        scoreSpinner.setAdapter(new ArrayAdapter<>(PreferencesActivity.this, android.R.layout.simple_spinner_dropdown_item, scores));
        yearSpinner.setAdapter(new ArrayAdapter<>(PreferencesActivity.this, android.R.layout.simple_spinner_dropdown_item, years));

        btnSpin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String genre = genreSpinner.getSelectedItem().toString();
                String score = scoreSpinner.getSelectedItem().toString();
                String year = yearSpinner.getSelectedItem().toString();
                Intent intent = new Intent(PreferencesActivity.this, ResultActivity.class);
                intent.putExtra("genre", genre);
                intent.putExtra("score", score);
                intent.putExtra("year", year);
                startActivity(intent);
            }
        });
    }
}