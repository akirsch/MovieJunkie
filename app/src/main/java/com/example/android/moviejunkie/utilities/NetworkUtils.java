package com.example.android.moviejunkie.utilities;

import android.net.Uri;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;

import com.example.android.moviejunkie.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

class NetworkUtils {

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();

    // constants for parsing the json response
    private final static String RESPONSE_ARRAY = "results";
    private final static String TITLE_STRING = "title";
    private final static String RELEASE_DATE_STRING = "release_date";
    private final static String POSTER_PATH_STRING = "poster_path";
    private final static String VOTER_AVERAGE_STRING = "vote_average";
    private final static String SYNOPSIS_STRING = "overview";

    private final static String GET_REQUEST = "GET";

    // constants for creating complete url for thumbnail image
    private final static String THUMBNAIL_BASE_URL = "http://image.tmdb.org/t/p/";
    private final static String SIZE_VALUE = "w185";

    private NetworkUtils() {
    }

    /**
     * Query the Movie DB API and return a {@link ArrayList<Movie>} object to represent an array of movie items.
     */
    public static ArrayList<Movie> fetchMovieData(String requestUrl) {
        Log.v(NetworkUtils.class.getName(), "fetchMovieData called");

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        // Return the ArrayList of movie items
        return extractMovies(jsonResponse);

    }

    /**
     * Return a list of {@link Movie} objects that has been built up from
     * parsing a JSON response.
     */
    private static ArrayList<Movie> extractMovies(String jsonResponse) {

        Log.v(LOG_TAG, "extractMovies called");

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding news items to
        ArrayList<Movie> movieListItems = new ArrayList<>();

        // Try to parse the jsonResponse. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // get root JSONObject
            JSONObject jsonRootObject = new JSONObject(jsonResponse);

            // get array with key value "response" which contains array with movie data.
            JSONArray jsonResponseArray = jsonRootObject.optJSONArray(RESPONSE_ARRAY);

            //Iterate the jsonArray and print the info of JSONObjects
            for (int i = 0; i < jsonResponseArray.length(); i++) {

                // get current object in results Array
                JSONObject currentMovieJSONObject = jsonResponseArray.optJSONObject(i);

                // get data for this movie in order that they are passed into Movie constructor
                String title = currentMovieJSONObject.optString(TITLE_STRING);
                String releaseDate = currentMovieJSONObject.optString(RELEASE_DATE_STRING);
                String thumbnailFilePath = currentMovieJSONObject.optString(POSTER_PATH_STRING);
                String voteAverageAsString = currentMovieJSONObject.optString(VOTER_AVERAGE_STRING);
                String synopsisHTML = currentMovieJSONObject.getString(SYNOPSIS_STRING);

                // create complete Url for thumbnail image using helper method
                String thumbnailUrl = createThumbnailUrl(thumbnailFilePath);

                // remove html tags from this string
                String synopsis = Html.fromHtml(synopsisHTML).toString();

                // convert voter average String from Json response into Float to store in movie object
                float voteAverage = Float.parseFloat(voteAverageAsString);

                // create new Movie Item object with these values as parameters and pass it into
                // the MovieListItems array
                movieListItems.add(new Movie(title, releaseDate, thumbnailUrl, voteAverage, synopsis));
                Log.v(LOG_TAG, movieListItems.get(i).getTitle());
            }

            // Return the list of movie items
            return movieListItems;

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("NetworkUtils", "Problem parsing the movie JSON results", e);
        }

        return null;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod(GET_REQUEST);
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the movie JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static String createThumbnailUrl(String filePath) {

        // create base Uri for accessing the thumbnail URL
        Uri thumbnailBaseUrl = Uri.parse(THUMBNAIL_BASE_URL);

        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = thumbnailBaseUrl.buildUpon();

        // append width parameter to the Uri
        uriBuilder.appendEncodedPath(SIZE_VALUE);

        // append file path to access this thumbnail image
        uriBuilder.appendEncodedPath(filePath);

        Log.v(LOG_TAG, uriBuilder.toString());

        return uriBuilder.toString();

    }


}
