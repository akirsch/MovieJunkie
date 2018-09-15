package com.example.android.moviejunkie.utilities;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import com.example.android.moviejunkie.Movie;

import java.util.ArrayList;

public class MovieLoader extends AsyncTaskLoader<ArrayList<Movie>> {

    /**
     * Query URL
     */
    private final String mUrl;


    /**
     * Constructs a new {@link MovieLoader}.
     *
     * @param context of the activity
     * @param url     to load data from
     */
    public MovieLoader(Context context, String url) {
        super(context);
        this.mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        Log.v(MovieLoader.class.getName(), "onStartLoader called");
        forceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Override
    public ArrayList<Movie> loadInBackground() {
        Log.v(MovieLoader.class.getName(), "onLoadInBackground called");
        // Don't perform the request if there are no URLs, or the first URL is null.
        if (this.mUrl == null) {
            return null;
        }
        // Perform the HTTP request for movie data and return the response.

        return NetworkUtils.fetchMovieData(this.mUrl);
    }


}
