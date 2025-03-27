package com.example.movieroulette;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MovieDB.db";
    private static final int DATABASE_VERSION = 8;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE movies (id INTEGER PRIMARY KEY AUTOINCREMENT, movie_id INTEGER, title TEXT, genre TEXT, score TEXT, poster_path TEXT)");
        db.execSQL("CREATE TABLE history (id INTEGER PRIMARY KEY AUTOINCREMENT, movie_id INTEGER, title TEXT, genre TEXT, score TEXT, poster_path TEXT)");
        db.execSQL("CREATE TABLE user_ratings (id INTEGER PRIMARY KEY AUTOINCREMENT, movie_id INTEGER, rating REAL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS movies");
        db.execSQL("DROP TABLE IF EXISTS history");
        db.execSQL("DROP TABLE IF EXISTS user_ratings");
        onCreate(db);
    }

    public boolean insertMovie(String title, String genre, Double score, int tmdbId, String posterPath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("genre", genre);
        values.put("score", score);
        values.put("movie_id", tmdbId);
        values.put("poster_path", posterPath);
        long result = db.insert("movies", null, values);
        return result != -1;
    }

    public boolean insertHistory(String title, String genre, Double score, int tmdbId, String posterPath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("genre", genre);
        values.put("score", score);
        values.put("movie_id", tmdbId);
        values.put("poster_path", posterPath);
        long result = db.insert("history", null, values);
        return result != -1;
    }

    public Cursor getAllMovies() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM movies", null);
    }

    public Cursor getAllHistory() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM history", null);
    }

    public boolean insertUserRating(int movieId, double rating) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("movie_id", movieId);
        values.put("rating", rating);
        long result = db.insert("user_ratings", null, values);
        return result != -1;
    }

    public Cursor getUserRating(int movieId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT rating FROM user_ratings WHERE movie_id = ?", new String[]{String.valueOf(movieId)});
    }
}
