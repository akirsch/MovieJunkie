package com.example.android.moviejunkie;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;

@Entity(tableName = "favorite_movies")
public class Movie implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "database_id")
    private int movieDbID;
    @ColumnInfo(name = "movieApiId")
    private String movieApiId;
    private String title;
    private String date;
    @ColumnInfo(name = "thumbnail_url")
    private String thumbnailUrl;
    @ColumnInfo(name = "vote_average")
    private float voteAverage;
    @ColumnInfo(name = "plot_synopsis")
    private String plotSynopsis;
    @ColumnInfo(name = "is_favorite")
    private boolean isFavorite;
    @ColumnInfo(name = "movie_trailer_keys")
    private ArrayList<String> movieTrailerKeys = new ArrayList<>();
    @ColumnInfo(name = "movie_reviews")
    private ArrayList<String> movieReviews = new ArrayList<>();

    @Ignore
    public Movie (@NonNull String movieApiId, String title, String date, String thumbnailUrl, float voteAverage, String plotSynopsis) {
        this.movieApiId = movieApiId;
        this.title = title;
        this.date = date;
        this.thumbnailUrl = thumbnailUrl;
        this.voteAverage = voteAverage;
        this.plotSynopsis = plotSynopsis;
        // for each new Movie object that is created, set default isFavorite value to false
        this.isFavorite = false;
    }


    public Movie (int movieDbID, @NonNull String movieApiId, String title, String date, String thumbnailUrl, float voteAverage, String plotSynopsis) {
        this.movieDbID = movieDbID;
        this.movieApiId = movieApiId;
        this.title = title;
        this.date = date;
        this.thumbnailUrl = thumbnailUrl;
        this.voteAverage = voteAverage;
        this.plotSynopsis = plotSynopsis;
        // for each new Movie object that is created, set default isFavorite value to false
        this.isFavorite = false;
    }

    @Ignore
    public Movie (ArrayList<String> trailerKeys, ArrayList<String> reviews){
        this.movieTrailerKeys.addAll(trailerKeys);
        this.movieReviews.addAll(reviews);
    }

    public int getMovieDbID() {
        return movieDbID;
    }

    public String getMovieApiId() {
        return movieApiId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }


    public float getVoteAverage() {
        return voteAverage;
    }

    public String getPlotSynopsis() {
        return plotSynopsis;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public ArrayList<String> getMovieReviews() {
        return movieReviews;
    }

    public void setMovieReviews(ArrayList<String> movieReviews) {
        this.movieReviews = movieReviews;
    }

    public ArrayList<String> getMovieTrailerKeys() {
        return movieTrailerKeys;
    }

    public void setMovieTrailerKeys(ArrayList<String> movieTrailerKeys) {
        this.movieTrailerKeys = movieTrailerKeys;
    }
}
