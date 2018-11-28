package com.example.android.moviejunkie;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.example.android.moviejunkie.utilities.Constants;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private static final String LOG_TAG = MovieAdapter.class.getName();

    // Store a member variable for the News Items Array
    private List<Movie> mMovieList;
    final private Context mContext;
    private String posterUrl;


    // Pass in the news items array into the constructor
    MovieAdapter(Context context, List<Movie> movies) {
        mMovieList = movies;
        mContext = context;
    }


    /**
     * When data changes, this method updates the list of movies
     * and notifies the adapter to use the new values on it
     */
    public void setMovies(List<Movie> movies) {
        mMovieList = movies;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MovieAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View movieItemView = inflater.inflate(R.layout.list_item, parent, false);

        // Return a new holder instance
        return new ViewHolder(movieItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieAdapter.ViewHolder holder, final int position) {

        // Get the data model based on position
        final Movie currentMovie = mMovieList.get(holder.getAdapterPosition());

        // Set item views based on your views and data model
        final ImageView thumbnailPosterIv = holder.moviePosterImageView;

        posterUrl = currentMovie.getThumbnailUrl();

        Log.v(LOG_TAG, posterUrl);

        // use Glide to get image from url and put it in image view
        Glide.with(mContext)
                .load(posterUrl)
                .apply(new RequestOptions()
                        .override(206, 206)
                        .format(DecodeFormat.PREFER_ARGB_8888))
                .into(thumbnailPosterIv);

        // create click listener which will open the Detail Activity showing more information
        // about the film that user clicks on.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();

                // create implicit intent to open the Detail Activity
                Intent intent = new Intent(context, DetailActivity.class);
                // put extras in the intent containing the data stored in movie object user clicked on
                intent.putExtra(Constants.MOVIE_OBJECT_KEY, currentMovie);

                if (intent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(intent);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mMovieList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        // Provide a direct reference to each of the views within a data item
        // Used to cache the views within the item layout for fast access
        final View view;
        final ImageView moviePosterImageView;

        ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            view = itemView;
            moviePosterImageView = itemView.findViewById(R.id.thumbnail_iv);

        }

    }

}




