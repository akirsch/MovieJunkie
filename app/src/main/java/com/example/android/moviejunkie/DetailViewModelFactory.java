package com.example.android.moviejunkie;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.example.android.moviejunkie.database.MovieDatabase;

class DetailViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final MovieDatabase mDb;
    private final String mMovieApiId;

    public DetailViewModelFactory(MovieDatabase mDb, String mMovieApiId) {
        this.mDb = mDb;
        this.mMovieApiId = mMovieApiId;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new DetailViewModel(mDb, mMovieApiId);
    }
}
