package com.example.movieroulette;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class ResultActivity extends AppCompatActivity {

    TextView resultText, movieDescription, scoreNum, releaseDate;
    Button btnSave, btnBack, btnSubmitRating, btnWatchTrailer;;
    RatingBar ratingBar;
    ImageView posterView;
    DBHelper dbHelper;

    String movieTitle = "", posterPath = "", movieDesc = "";
    double score = 0.0;
    int tmdbId = 0, movieId = 0;
    String genre, scorePref, queryDesc, year;
    boolean fromList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_result);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        resultText = findViewById(R.id.txtResult);
        movieDescription = findViewById(R.id.movieDsc);
        scoreNum = findViewById(R.id.scoreNum);
        releaseDate = findViewById(R.id.release_date);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);
        posterView = findViewById(R.id.imgPoster);
        ratingBar = findViewById(R.id.ratingBar);
        btnSubmitRating = findViewById(R.id.btnSubmitRating);
        btnWatchTrailer = findViewById(R.id.btnWatchTrailer);
        dbHelper = new DBHelper(this);

        genre = getIntent().getStringExtra("genre");
        scorePref = getIntent().getStringExtra("score");
        queryDesc = getIntent().getStringExtra("DescQuery");
        year = getIntent().getStringExtra("year");

        Intent intent = getIntent();
        fromList = intent.getBooleanExtra("fromList", false);

        if (fromList) {
            movieId = intent.getIntExtra("movie_id", -1);
            if (movieId != -1) {
                new FetchMovieByIdTask().execute(movieId);
            } else {
                resultText.setText("Invalid movie ID.");
            }
        } else {
            new FetchMovieTask().execute(genre);
        }

        btnSave.setOnClickListener(v -> {
            boolean inserted = dbHelper.insertMovie(movieTitle, genre, score, tmdbId, posterPath);
            Toast.makeText(ResultActivity.this, inserted ? "Saved to favorites!" : "Error saving.", Toast.LENGTH_SHORT).show();
        });

        btnBack.setOnClickListener(v -> {
            startActivity(new Intent(ResultActivity.this, MainActivity.class));
        });

        btnSubmitRating.setOnClickListener(v -> {
            if (tmdbId == 0) {
                Toast.makeText(ResultActivity.this, "Movie not loaded yet.", Toast.LENGTH_SHORT).show();
                return;
            }
            float userRating = ratingBar.getRating();
            boolean ratingInserted = dbHelper.insertUserRating(tmdbId, userRating);
            Toast.makeText(ResultActivity.this, ratingInserted ? "Rating submitted!" : "Error submitting rating.", Toast.LENGTH_SHORT).show();
        });
        btnWatchTrailer.setOnClickListener(v -> {
            if (tmdbId == 0) {
                Toast.makeText(ResultActivity.this, "Movie not loaded yet.", Toast.LENGTH_SHORT).show();
                return;
            }
            new FetchTrailerTask().execute(tmdbId);
        });

    }
    class FetchTrailerTask extends AsyncTask<Integer, Void, String> {
        @Override
        protected String doInBackground(Integer... ids) {
            int movieId = ids[0];
            try {
                String apiKey = "075dc725f254ceb931012a1320c403b1";
                URL url = new URL("https://api.themoviedb.org/3/movie/" + movieId + "/videos?api_key=" + apiKey);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                return response.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String json) {
            if (json != null) {
                try {
                    JSONObject obj = new JSONObject(json);
                    JSONArray results = obj.getJSONArray("results");
                    String trailerKey = "";
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject video = results.getJSONObject(i);
                        String type = video.getString("type");
                        String site = video.getString("site");
                        if ("Trailer".equalsIgnoreCase(type) && "YouTube".equalsIgnoreCase(site)) {
                            trailerKey = video.getString("key");
                            break;
                        }
                    }
                    if (!trailerKey.isEmpty()) {
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                android.net.Uri.parse("https://www.youtube.com/watch?v=" + trailerKey));
                        startActivity(intent);
                    } else {
                        Toast.makeText(ResultActivity.this, "No trailer available.", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(ResultActivity.this, "Error parsing trailer data.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ResultActivity.this, "Failed to fetch trailer data.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    class FetchMovieByIdTask extends AsyncTask<Integer, Void, String> {
        @Override
        protected String doInBackground(Integer... ids) {
            try {
                int movieId = ids[0];
                String apiKey = "075dc725f254ceb931012a1320c403b1";
                URL url = new URL("https://api.themoviedb.org/3/movie/" + movieId + "?api_key=" + apiKey);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                return response.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String json) {
            if (json != null) {
                try {
                    JSONObject movie = new JSONObject(json);
                    tmdbId = movie.getInt("id");
                    movieTitle = movie.getString("title");
                    posterPath = movie.getString("poster_path");
                    movieDesc = movie.getString("overview");
                    score = movie.getDouble("vote_average");
                    String release_date = movie.getString("release_date");

                    resultText.setText("🎬 " + movieTitle);
                    movieDescription.setText(movieDesc);
                    scoreNum.setText("Rating: " + score);
                    releaseDate.setText("Release Date: " + release_date);

                    Glide.with(ResultActivity.this)
                            .load("https://image.tmdb.org/t/p/w500" + posterPath)
                            .into(posterView);
                } catch (Exception e) {
                    resultText.setText("Error parsing movie details.");
                }
            } else {
                resultText.setText("Failed to load movie info.");
            }
        }
    }

    class FetchMovieTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                String apiKey = "075dc725f254ceb931012a1320c403b1";
                String genreId = getGenreId(genre);
                URL url = new URL("https://api.themoviedb.org/3/discover/movie?api_key=" + apiKey + "&with_genres=" + genreId);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                return response.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String json) {
            if (json != null) {
                try {
                    JSONObject obj = new JSONObject(json);
                    JSONArray results = obj.getJSONArray("results");
                    if (results.length() > 0) {
                        int randomIndex = new Random().nextInt(results.length());
                        JSONObject movie = results.getJSONObject(randomIndex);

                        tmdbId = movie.getInt("id");
                        movieTitle = movie.getString("title");
                        posterPath = movie.getString("poster_path");
                        movieDesc = movie.getString("overview");
                        score = movie.getDouble("vote_average");
                        String release_date = movie.getString("release_date");

                        resultText.setText("🎬 " + movieTitle);
                        movieDescription.setText(movieDesc);
                        scoreNum.setText("Rating: " + score);
                        releaseDate.setText("Release Date: " + release_date);

                        Glide.with(ResultActivity.this)
                                .load("https://image.tmdb.org/t/p/w500" + posterPath)
                                .into(posterView);

                        dbHelper.insertHistory(movieTitle, genre, score, tmdbId, posterPath);
                    } else {
                        resultText.setText("No movies found.");
                    }
                } catch (Exception e) {
                    resultText.setText("Error parsing movie data.");
                }
            } else {
                resultText.setText("Failed to fetch movie data.");
            }
        }


        private String getGenreId(String genreName) {
            switch (genreName) {
                case "Action": return "28";
                case "Comedy": return "35";
                case "Drama": return "18";
                case "Horror": return "27";
                case "Sci-Fi": return "878";
                default: return "";
            }
        }
    }
}