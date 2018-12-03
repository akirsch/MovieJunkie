package com.example.android.moviejunkie;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.android.moviejunkie.database.MovieDatabase;

class DetailViewModel extends ViewModel {

    //private final LiveData<Movie> movie;

    public DetailViewModel(MovieDatabase mDb, String mMovieApiId) {
       // movie = mDb.favoriteMovieDao().loadMovieById(mMovieApiId);
    }

    //public LiveData<Movie> getMovie() {
        //return movie;
    }
//}
