package com.example.android.moviejunkie.utilities;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import com.example.android.moviejunkie.Movie;

public class TrailersAndReviewsLoader extends AsyncTaskLoader<Movie> {

    /**
     * Query URL
     */
    private final String mTrailerUrl;
    private final String mReviewsUrl;

    /**
     * Constructs a new {@link TrailersAndReviewsLoader}.
     *
     * @param context of the activity
     * @param trailerUrl to load trailer keys data from API
     * @param reviewUrl to load review data from API
     */
    public TrailersAndReviewsLoader(Context context, String trailerUrl, String reviewUrl) {
        super(context);
        this.mTrailerUrl = trailerUrl;
        this.mReviewsUrl = reviewUrl;
    }

    @Override
    protected void onStartLoading() {
        Log.v(TrailersAndReviewsLoader.class.getName(), "onStartLoader called");
        forceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Override
    public Movie loadInBackground() {
        Log.v(MovieLoader.class.getName(), "onLoadInBackground called");
        // Don't perform the request if there are no URLs, or the first URL is null.
        if (this.mTrailerUrl == null || this.mReviewsUrl == null) {
            return null;
        }
        // Perform the HTTP request for movie data and return the response.
        return NetworkUtils.fetchMovieTrailersAndReviews(this.mTrailerUrl, this.mReviewsUrl);
    }
}
