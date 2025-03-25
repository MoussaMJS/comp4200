package com.example.movieroulette;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class ResultActivity extends AppCompatActivity {

    TextView resultText, movieDescription, scoreNum, releaseDate;
    Button btnSave, btnBack;
    ImageView posterView;
    DBHelper dbHelper;
    String movieTitle = "";
    String posterPath = "";
    String movieDesc = "";
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


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean inserted = dbHelper.insertMovie(movieTitle, genre, score, tmdbId);
                Toast.makeText(ResultActivity.this, inserted ? "Saved to favorites!" : "Error saving.", Toast.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ResultActivity.this, MainActivity.class));
            }
        });
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
                resultText.setText("Failed to load movie info." + movieId);
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
                return null;
            }
        }

        @Override
        protected void onPostExecute(String json) {
            if (json != null) {
                try {
                    JSONObject obj = new JSONObject(json);
                    JSONArray results = obj.getJSONArray("results");
                    JSONArray filtered = new JSONArray();

                    if (results.length() > 0) {
                        for(int i = 0; i < results.length(); i++){
                            JSONObject movie = results.getJSONObject(i);

                            if (!movie.has("vote_average") || movie.isNull("vote_average")) continue;

                            int rating;
                            rating = movie.getInt("vote_average");

                            boolean scoreMatch = false;
                            switch (scorePref){
                                case "0 - 4.9": scoreMatch = rating >= 0 && rating <= 4.9;
                                    break;
                                case "5 - 6.9": scoreMatch = rating >= 5 && rating <= 6.9 ;
                                    break;
                                case "7 - 7.9": scoreMatch = rating >= 7 && rating <= 7.9;
                                    break;
                                case "8 - 8.9": scoreMatch = rating >= 8 && rating <= 8.9;
                                    break;
                                case "9 - 10": scoreMatch = rating >= 9 && rating <= 10;
                                    break;
                            }

                            if (!movie.has("release_date") || movie.isNull("release_date")) continue;

                            String release_date_str;
                            release_date_str = movie.getString("release_date");
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            Date releaseDate = sdf.parse(release_date_str);

                            boolean yearMatch = false;
                            Date startDate, endDate;

                            switch (year){
                                case "1950 - 1999":
                                    startDate = sdf.parse("1950-01-01");
                                    endDate = sdf.parse("1999-12-31");
                                    break;
                                case "2000 - 2009":
                                    startDate = sdf.parse("2000-01-01");
                                    endDate = sdf.parse("2009-12-31");
                                    break;
                                case "2010 - 2019":
                                    startDate = sdf.parse("2010-01-01");
                                    endDate = sdf.parse("2019-12-31");
                                    break;
                                case "2020 - current":
                                    startDate = sdf.parse("2020-01-01");
                                    endDate = new Date();
                                    break;
                                default:
                                    startDate = null;
                                    endDate = null;

                            }

                            if (startDate != null && endDate != null) {
                                yearMatch = releaseDate.after(startDate) && releaseDate.before(endDate);
                            }

                            if(scoreMatch && yearMatch){
                                filtered.put(movie);
                            }
                        }
                        if(filtered.length() > 0){
                            int randomIndex = new Random().nextInt(filtered.length());
                            JSONObject movie = filtered.getJSONObject(randomIndex);

                            movieTitle = movie.getString("title");
                            posterPath = movie.getString("poster_path");
                            movieDesc = movie.getString("overview");
                            score = movie.getDouble("vote_average");
                            tmdbId = movie.getInt("id");
                            String release_date = movie.getString("release_date");

                            releaseDate.setText("Release Date:" + release_date);
                            resultText.setText("🎬 " + movieTitle);
                            movieDescription.setText(movieDesc);
                            scoreNum.setText("Rating: " + score);
                            Glide.with(ResultActivity.this)
                                    .load("https://image.tmdb.org/t/p/w500" + posterPath)
                                    .into(posterView);
                            dbHelper.insertHistory(movieTitle, genre, score, tmdbId);
                        }else{
                            int randomIndex = new Random().nextInt(results.length());
                            JSONObject movie = results.getJSONObject(randomIndex);
                            movieTitle = movie.getString("title");
                            posterPath = movie.getString("poster_path");
                            movieDesc = movie.getString("overview");
                            score = movie.getDouble("vote_average");
                            tmdbId = movie.getInt("id");
                            String release_date = movie.getString("release_date");
                            releaseDate.setText("Release Date: " + release_date);
                            resultText.setText("Movie not found for filters. Random selection"+"\n🎬 " + movieTitle);
                            movieDescription.setText(movieDesc);
                            scoreNum.setText("Rating: " + score);
                            Glide.with(ResultActivity.this)
                                    .load("https://image.tmdb.org/t/p/w500" + posterPath)
                                    .into(posterView);
                            dbHelper.insertHistory(movieTitle, genre, score, tmdbId);
                        }
                    } else {
                        resultText.setText("No movie found for this genre.");
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