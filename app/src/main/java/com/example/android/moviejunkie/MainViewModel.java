package com.example.android.moviejunkie;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.example.android.moviejunkie.database.MovieDatabase;

import java.util.List;

class MainViewModel extends AndroidViewModel {

    private final LiveData<List<Movie>> favoriteMovies;

    public MainViewModel(@NonNull Application application) {
        super(application);
        MovieDatabase movieDatabase = MovieDatabase.getInstance(this.getApplication());
        favoriteMovies = movieDatabase.favoriteMovieDao().loadAllMovies();
    }

    public LiveData<List<Movie>> getFavoriteMovies() {
        return favoriteMovies;
    }
}
