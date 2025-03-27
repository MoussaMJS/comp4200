package com.example.movieroulette;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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
    ArrayList<Movie> movieList = new ArrayList<>();

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
        while (cursor.moveToNext()) {
            int movieId = cursor.getInt(1);
            String title = cursor.getString(2);
            String genre = cursor.getString(3);
            String tmdbScore = cursor.getString(4);
            String posterPath = cursor.getString(5);

            Cursor ratingCursor = dbHelper.getUserRating(movieId);
            String userRating = "Not Rated";
            if (ratingCursor.moveToFirst()) {
                userRating = ratingCursor.getString(0);
            }
            ratingCursor.close();

            movieList.add(new Movie(movieId, title, genre, tmdbScore, userRating, posterPath));
        }
        cursor.close();

        FavoriteAdapter adapter = new FavoriteAdapter(this, movieList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Movie selectedMovie = movieList.get(position);
            Intent intent = new Intent(HistoryActivity.this, ResultActivity.class);
            intent.putExtra("movie_id", selectedMovie.movieId);
            intent.putExtra("fromList", true);
            startActivity(intent);
        });

        btnHome.setOnClickListener(v -> {
            startActivity(new Intent(HistoryActivity.this, MainActivity.class));
        });
    }
}
