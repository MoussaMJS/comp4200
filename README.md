MovieRoulette – Android App MovieRoulette is a fun and interactive Android application that helps users discover random movies based on their preferences — such as genre, rating, and release year. 
It also saves favorites and history, and allows users to revisit movie results later with full details.

Features: Random Movie Spinner – Pick a movie by filtering genre, rating, and release year. 
Discover Movies – Integrates with The Movie Database (TMDb) API to fetch real movie info. 
Save to Favorites – Keep a personal list of favorite picks. 
View History – Track previously spun movies. 
Mobile-first design – Built for smooth use on Android devices. 
Clickable history/favorites – Tap to revisit any movie’s full details.

Tech Stack: 
Java - App logic and Android development 
XML - UI layout design 
TMDb - API Movie data and images 
SQLite - Local database for favorites and history 
Glide - Image loading (poster display)

Getting Started: Clone the repo

git clone https://github.com/your-username/MovieRoulette.git Open in Android Studio

Add your TMDb API key

Inside ResultActivity.java (and FetchMovieTask, FetchMovieByIdTask), replace:

String apiKey = "YOUR_API_KEY"; With your actual API key from https://www.themoviedb.org/settings/api
