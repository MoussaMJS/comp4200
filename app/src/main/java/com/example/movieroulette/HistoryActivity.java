package com.example.movieroulette;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    ListView listView;
    DBHelper dbHelper;
    Button btnHome;

    ArrayList<Integer> movieIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_history);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnHome = findViewById(R.id.btnHisHome);
        listView = findViewById(R.id.listView);
        dbHelper = new DBHelper(this);


        Cursor cursor = dbHelper.getAllHistory();
        ArrayList<String> historyList = new ArrayList<>();
        while (cursor.moveToNext()) {
            int movieId = cursor.getInt(1);
            String title = cursor.getString(2);
            String genre = cursor.getString(3);
            String tmdbScore = cursor.getString(4);
            Cursor ratingCursor = dbHelper.getUserRating(movieId);
            String userRating = "Not Rated";
            if (ratingCursor.moveToFirst()) {
                userRating = ratingCursor.getString(0);
            }
            ratingCursor.close();

            historyList.add(title + " | " + genre + " | TMDB: " + tmdbScore + " | Your Rating: " + userRating + " | " + movieId);
            movieIds.add(movieId);
        }
        listView.setAdapter(new ArrayAdapter<>(HistoryActivity.this, android.R.layout.simple_list_item_1, historyList));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < movieIds.size()) {
                    int movieId = movieIds.get(position);
                    Intent intent = new Intent(HistoryActivity.this, ResultActivity.class);
                    intent.putExtra("movie_id", movieId);
                    intent.putExtra("fromList", true);
                    startActivity(intent);
                } else {
                    Toast.makeText(HistoryActivity.this, "Movie ID not found for this item.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HistoryActivity.this, MainActivity.class));
            }
        });
    }
}