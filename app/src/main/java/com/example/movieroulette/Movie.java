package com.example.movieroulette;

public class Movie {
    int movieId;
    String title, genre, tmdbRating, userRating, posterPath;

    public Movie(int movieId, String title, String genre, String tmdbRating, String userRating, String posterPath) {
        this.movieId = movieId;
        this.title = title;
        this.genre = genre;
        this.tmdbRating = tmdbRating;
        this.userRating = userRating;
        this.posterPath = posterPath;
    }
}
