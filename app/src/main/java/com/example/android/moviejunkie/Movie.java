package com.example.android.moviejunkie;

import java.io.Serializable;

public class Movie implements Serializable {

    private String title;
    private String date;
    private String thumbnailUrl;
    private float voteAverage;
    private String plotSynopsis;

    public Movie (String title, String date, String tumbnailUrl, float voteAverage, String plotSynopsis) {
        this.title = title;
        this.date = date;
        this.thumbnailUrl = tumbnailUrl;
        this.voteAverage = voteAverage;
        this.plotSynopsis = plotSynopsis;
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

    public void setDate(String date) {
        this.date = date;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String tumbnailUrl) {
        this.thumbnailUrl = tumbnailUrl;
    }

    public float getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(float voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getPlotSynopsis() {
        return plotSynopsis;
    }

    public void setPlotSynopsis(String plotSynopsis) {
        this.plotSynopsis = plotSynopsis;
    }
}
