package com.example.android.moviejunkie;

import android.app.LoaderManager;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.android.moviejunkie.database.MovieDatabase;
import com.example.android.moviejunkie.utilities.Constants;
import com.example.android.moviejunkie.utilities.TrailersAndReviewsLoader;

import java.util.ArrayList;
import java.util.Objects;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener, LoaderManager.LoaderCallbacks<Movie> {

    private static final String LOG_TAG = DetailActivity.class.getName();

    private final int MAX_NUMBER = 3;

    private ImageView thumbnailIv;
    private TextView titleTv;
    private TextView releaseDateTv;
    private TextView[] trailerViewArray = new TextView[3];
    private TextView[] reviewViewsArray = new TextView[3];
    private TextView review_oneTv;
    private TextView review_twoTv;
    private TextView review_threeTv;
    private TextView playTrailerOneTv;
    private TextView playTrailerTwoTv;
    private TextView playTrailerThreeTv;
    private RatingBar voterAverageRatingBar;
    private TextView synopsisTv;
    private Bundle movieDataBundle;
    private ArrayList<String> trailerKeys;
    private ArrayList<String> movieReviews;
    private Movie movieWithTrailerAndReviewData;
    private Movie selectedMovie;
    Boolean reviewOneIsShowing = false;
    Boolean reviewTwoIsShowing = false;
    Boolean reviewThreeIsShowing = false;

    private MovieDatabase mDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        thumbnailIv = findViewById(R.id.detail_image_view);
        titleTv = findViewById(R.id.title_tv);
        releaseDateTv = findViewById(R.id.release_date_tv);
        voterAverageRatingBar = findViewById(R.id.voter_rating_bar);
        synopsisTv = findViewById(R.id.synopsis_tv);
        playTrailerOneTv = findViewById(R.id.trailer_oneTv);
        playTrailerTwoTv = findViewById(R.id.trailer_twoTv);
        playTrailerThreeTv = findViewById(R.id.trailer_threeTv);
        review_oneTv = findViewById(R.id.review_oneTv);
        review_twoTv = findViewById(R.id.review_twoTv);
        review_threeTv = findViewById(R.id.review_threeTv);

        // set up array of textViews
        trailerViewArray[0] = playTrailerOneTv;
        trailerViewArray[1] = playTrailerTwoTv;
        trailerViewArray[2] = playTrailerThreeTv;

        reviewViewsArray[0] = review_oneTv;
        reviewViewsArray[1] = review_twoTv;
        reviewViewsArray[2] = review_threeTv;

        // get reference to database of favorite movies
        mDb = MovieDatabase.getInstance(getApplicationContext());


        final Intent intentThatStartedThisActivity = getIntent();

        // get movie item sent by intent from MainActivity
        selectedMovie = (Movie) intentThatStartedThisActivity.getSerializableExtra(Constants.MOVIE_OBJECT_KEY);

        movieDataBundle = new Bundle();
        movieDataBundle.putSerializable(Constants.MOVIE_OBJECT_KEY, selectedMovie);

        getLoaderManager().initLoader(0, movieDataBundle, this);

        setUpUI();

        // only show title off movie in app bar when collapsed
        final CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        AppBarLayout appBarLayout = findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0 && selectedMovie.getTitle() != null) {
                    collapsingToolbarLayout.setTitle(selectedMovie.getTitle());
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbarLayout.setTitle(" ");
                    isShow = false;
                }
            }
        });


        // set the views in this activity to display the data stored in movie object passed by the intent
        if (selectedMovie.getThumbnailUrl() != null) {
            // use Glide to get image from url and put it in image view
            Glide.with(this)
                    .load(selectedMovie.getThumbnailUrl())
                    .apply(new RequestOptions()
                            .format(DecodeFormat.PREFER_ARGB_8888)
                            .override(Target.SIZE_ORIGINAL))
                    .into(thumbnailIv);
        }
        if (selectedMovie.getTitle() != null) {
            titleTv.setText(selectedMovie.getTitle());
        }
        if (selectedMovie.getDate() != null) {
            releaseDateTv.setText(selectedMovie.getDate());
        }
        if (selectedMovie.getPlotSynopsis() != null) {
            synopsisTv.setText(selectedMovie.getPlotSynopsis());
        }

        // set vote average
        voterAverageRatingBar.setRating(selectedMovie.getVoteAverage());


    }

    @Override
    public Loader<Movie> onCreateLoader(int i, Bundle bundle) {

        Movie currentMovie = (Movie) bundle.getSerializable(Constants.MOVIE_OBJECT_KEY);

        assert currentMovie != null;
        String trailersUriString = getTrailerUriString(currentMovie);

        String reviewsUriString = getReviewsUriString(currentMovie);

        return new TrailersAndReviewsLoader(getApplicationContext(), trailersUriString, reviewsUriString);
    }

    @NonNull
    private String getTrailerUriString(Movie currentMovie) {
        Uri baseUri = Uri.parse(Constants.BASE_URL);

        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();

        // build correct Uri to get the trailers for this movie
        uriBuilder.appendEncodedPath(currentMovie.getMovieApiId());

        uriBuilder.appendEncodedPath("videos");

        uriBuilder.appendQueryParameter("api_key", Constants.API_KEY);

        return uriBuilder.toString();
    }

    @NonNull
    private String getReviewsUriString(Movie currentMovie) {
        Uri baseUri = Uri.parse(Constants.BASE_URL);

        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();

        // build correct Uri to get the trailers for this movie
        uriBuilder.appendEncodedPath(currentMovie.getMovieApiId());

        uriBuilder.appendEncodedPath("reviews");

        uriBuilder.appendQueryParameter("api_key", Constants.API_KEY);

        return uriBuilder.toString();
    }

    @Override
    public void onLoadFinished(Loader<Movie> loader, Movie movieWithTrailersAndReviews) {

        movieWithTrailerAndReviewData = movieWithTrailersAndReviews;


        trailerKeys = movieWithTrailerAndReviewData.getMovieTrailerKeys();

        // set the array of movie trailers to the Movie object passed by the intent
        // so that this object now contains all the movie data for this movie
        selectedMovie.setMovieTrailerKeys(movieWithTrailersAndReviews.getMovieTrailerKeys());

        movieReviews = movieWithTrailerAndReviewData.getMovieReviews();
        // set the array of movie reviews to the Movie object passed by the intent
        // so that this object now contains all the movie data for this movie
        selectedMovie.setMovieReviews(movieWithTrailersAndReviews.getMovieReviews());

        // for a maximum of 3 times show views to launch trailers and set onClickListeners
        // only if the trailer exists, and show reviews if they exist
        if (!trailerKeys.isEmpty()) {
            for (int i = 0; i < MAX_NUMBER; i++) {
                if (trailerKeys.size() >= (i + 1)) {
                    trailerViewArray[i].setVisibility(View.VISIBLE);
                    trailerViewArray[i].setOnClickListener(this);
                }
            }
        }

        if (!movieReviews.isEmpty()) {
            for (int i = 0; i < MAX_NUMBER; i++) {
                if (movieReviews.size() >= (i + 1)) {
                    reviewViewsArray[i].setVisibility(View.VISIBLE);
                    reviewViewsArray[i].setText(getResources().getStringArray(R.array.review_header_array)[i]);
                    reviewViewsArray[i].setOnClickListener(this);
                }
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Movie> loader) {
        movieWithTrailerAndReviewData = null;
    }

    @Override
    // This method initializes the contents of the Activity's options menu.
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Options Menu specified in XML
        getMenuInflater().inflate(R.menu.detail_activity_menu, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem favoritesIcon = menu.getItem(0);

        LiveData<Movie> movie = mDb.favoriteMovieDao().loadMovieById(selectedMovie.getMovieApiId());



        if (movie == null) {
            Drawable notSelectedAsFavoriteIcon = getDrawable(R.drawable.ic_favorite_border);
            assert notSelectedAsFavoriteIcon != null;
            notSelectedAsFavoriteIcon.setTint(getColor(R.color.colorAccent));
            favoritesIcon.setIcon(notSelectedAsFavoriteIcon);
        } else {
            Drawable selectedAsFavoriteIcon = getDrawable(R.drawable.ic_favorite);
            assert selectedAsFavoriteIcon != null;
            selectedAsFavoriteIcon.setTint(getColor(R.color.colorAccent));
            favoritesIcon.setIcon(selectedAsFavoriteIcon);
        }
        return true;

    }


    /**
     * This method will cause the app to navigate back to the Activity that started the Detail
     * Activity
     *
     * @param item the up icon arrow
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up button
            case android.R.id.home:
                finish();
                return true;
            case R.id.favorites:
                onFavoriteButtonClicked(item);
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    public void setUpUI() {
        // Enable up navigation to parent Activity
        // find Toolbar view in layout
        Toolbar myToolbar = findViewById(R.id.toolbar);
        // Set the Toolbar as Action Bar
        setSupportActionBar(myToolbar);
        // enable up navigation to parent activity
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        // set status bar to be transparent
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        // don't display any play trailer  or  review views in case there are no trailers available for this movie
        playTrailerOneTv.setVisibility(View.GONE);
        playTrailerTwoTv.setVisibility(View.GONE);
        playTrailerThreeTv.setVisibility(View.GONE);
        review_oneTv.setVisibility(View.GONE);
        review_twoTv.setVisibility(View.GONE);
        review_threeTv.setVisibility(View.GONE);



    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(LOG_TAG, "on Start of Detail Activity has been called");
        getLoaderManager().restartLoader(0, movieDataBundle, this);
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.trailer_oneTv:
                if (trailerKeys.get(0) != null) {
                    String firstTrailerKey = trailerKeys.get(0);

                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("vnd.youtube:" + firstTrailerKey));
                    startActivity(intent);
                }
                break;
            case R.id.trailer_twoTv:
                if (trailerKeys.get(1) != null) {
                    String secondTrailerKey = trailerKeys.get(1);

                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("vnd.youtube:" + secondTrailerKey));
                    startActivity(intent);
                }
                break;
            case R.id.trailer_threeTv:
                if (trailerKeys.get(2) != null) {
                    String thirdTrailerKey = trailerKeys.get(2);

                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("vnd.youtube:" + thirdTrailerKey));
                    startActivity(intent);
                }
            case R.id.review_oneTv:
                if (!reviewOneIsShowing) {
                    String firstMovieReview = movieReviews.get(0);
                    review_oneTv.setText(firstMovieReview);
                    review_oneTv.setAllCaps(false);
                    reviewOneIsShowing = true;
                } else {
                    review_oneTv.setText(getResources().getString(R.string.read_review_one));
                    review_oneTv.setAllCaps(true);
                    reviewOneIsShowing = false;
                }
                break;
            case R.id.review_twoTv:
                if (!reviewTwoIsShowing) {
                    String secondMovieReview = movieReviews.get(1);
                    review_twoTv.setText(secondMovieReview);
                    review_twoTv.setAllCaps(false);
                    reviewTwoIsShowing = true;
                } else {
                    review_twoTv.setText(getResources().getString(R.string.read_review_two));
                    review_twoTv.setAllCaps(true);
                    reviewTwoIsShowing = false;
                }
                break;
            case R.id.review_threeTv:
                if (!reviewThreeIsShowing) {
                    String thirdMovieReview = movieReviews.get(2);
                    review_threeTv.setText(thirdMovieReview);
                    review_threeTv.setAllCaps(false);
                    reviewThreeIsShowing = true;
                } else {
                    review_threeTv.setText(getResources().getString(R.string.read_review_three));
                    review_threeTv.setAllCaps(true);
                    reviewThreeIsShowing = false;
                }
                break;
        }
    }

    private void onFavoriteButtonClicked(MenuItem item) {

        LiveData<Movie> movie = mDb.favoriteMovieDao().loadMovieById(selectedMovie.getMovieApiId());

        if (movie == null) {
            Drawable selectedAsFavoriteIcon = getDrawable(R.drawable.ic_favorite);
            selectedAsFavoriteIcon.setTint(getColor(R.color.colorAccent));
            item.setIcon(selectedAsFavoriteIcon);
            selectedMovie.setFavorite(true);
            // If movie was not previously listed a favorite,
            // when favorites button is clicked add this movie to favorite movie database
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    mDb.favoriteMovieDao().insertMovie(selectedMovie);
                }
            });
        } else {
            Drawable notSelectedAsFavoriteIcon = getDrawable(R.drawable.ic_favorite_border);
            notSelectedAsFavoriteIcon.setTint(getColor(R.color.colorAccent));
            item.setIcon(notSelectedAsFavoriteIcon);
            selectedMovie.setFavorite(false);
            // If movie was previously listed a favorite,
            // when favorites button is clicked remove this movie from the favorite movie database
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    mDb.favoriteMovieDao().deleteMovie(selectedMovie);
                }
            });

        }
    }
}