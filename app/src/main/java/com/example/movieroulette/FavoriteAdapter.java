package com.example.movieroulette;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import java.util.List;



public class FavoriteAdapter extends ArrayAdapter<Movie> {

    public FavoriteAdapter(@NonNull Context context, List<Movie> movies) {
        super(context, 0, movies);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.favorite_list_item, parent, false);
        }

        Movie movie = getItem(position);

        ImageView imgPoster = convertView.findViewById(R.id.imgPoster);
        TextView movieTitle = convertView.findViewById(R.id.movieTitle);
        TextView movieGenre = convertView.findViewById(R.id.movieGenre);
        TextView movieTmdbRating = convertView.findViewById(R.id.movieTmdbRating);
        TextView movieUserRating = convertView.findViewById(R.id.movieUserRating);

        movieTitle.setText(movie.title);
        movieGenre.setText("Genre: " + movie.genre);
        movieTmdbRating.setText("TMDB: " + movie.tmdbRating);
        movieUserRating.setText("Your Rating: " + movie.userRating);

        Glide.with(getContext())
                .load("https://image.tmdb.org/t/p/w500" + movie.posterPath)
                .placeholder(android.R.color.darker_gray)
                .into(imgPoster);

        return convertView;
    }
}
