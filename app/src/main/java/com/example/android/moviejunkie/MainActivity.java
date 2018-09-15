package com.example.android.moviejunkie;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.moviejunkie.utilities.MovieLoader;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Movie>> {

    /**
     * target url for a Movie DB API query
     */
    private static final String BASE_URL = "https://api.themoviedb.org/3/discover/movie?";

    // initialize String constant to store value of private api key the the Movie DB Api
    private static final String API_KEY = "";

    private static final String LOG_TAG = MainActivity.class.getName();

    // initialize global variables
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView emptyTextView;
    private ArrayList<Movie> movieItems;
    private MovieAdapter movieAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set App Bar title to name of project
        // find Toolbar view in layout
        Toolbar myToolbar = findViewById(R.id.toolbar);
        // Set the Toolbar as Action Bar
        setSupportActionBar(myToolbar);
        // Set title of action bar to appropriate label for this Activity
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.app_name);

        // Set the padding to match the Status Bar height (to avoid title being cut off by
        // transparent toolbar
        myToolbar.setPadding(0, 25, 0, 0);

        recyclerView = findViewById(R.id.moviesRecyclerView);
        progressBar = findViewById(R.id.loading_spinner);
        emptyTextView = findViewById(R.id.empty_list_view);

        movieItems = new ArrayList<>();
        // Create adapter passing in this ArrayList as the data source

        movieAdapter = new MovieAdapter(this, movieItems);

        // Attach the adapter to the RecyclerView to populate items
        recyclerView.setAdapter(movieAdapter);

        // Set grid layout manager to position the items in a grid of two vertical columns
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        // check there is a network connection
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = Objects.requireNonNull(cm).getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            // If there is a network connection,
            // create new instance of load manager and instantiate new Loader object , or renew existing one.
            getLoaderManager().initLoader(0, null, this);
            Log.v(LOG_TAG, "load manager initialized");
        } else {
            // if no connection, display message explaining the issue to users
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            emptyTextView.setText(R.string.user_offline);
            Drawable img = getDrawable(R.drawable.ic_signal_wifi_off);
            emptyTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, img);
        }


    }

    @Override
    public Loader<ArrayList<Movie>> onCreateLoader(int i, Bundle bundle) {

        Log.v(LOG_TAG, "onCreateLoader called");

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));

        Uri baseUri = Uri.parse(BASE_URL);

        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("sort_by", orderBy);
        uriBuilder.appendQueryParameter("api_key", API_KEY);

        Log.v(LOG_TAG, uriBuilder.toString());

        return new MovieLoader(this, uriBuilder.toString());


    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> movies) {

        Log.v(LOG_TAG, "onLoadFinished is called");

        // make progress bar disappear when background thread finishes loading
        progressBar.setVisibility(View.GONE);

        if (movies == null) {
            return;
        }
        if (movies.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyTextView.setVisibility(View.VISIBLE);
            emptyTextView.setText(R.string.no_movies_found);
        }
        if (!movies.isEmpty()) {
            emptyTextView.setVisibility(View.GONE);
            movieItems.addAll(movies);
            movieAdapter.notifyDataSetChanged();
        }


    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {
        // Loader reset, so we can clear out our existing data.
        movieItems.clear();
        movieAdapter.notifyDataSetChanged();
    }

    @Override
    // This method initializes the contents of the Activity's options menu.
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Options Menu specified in XML
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
