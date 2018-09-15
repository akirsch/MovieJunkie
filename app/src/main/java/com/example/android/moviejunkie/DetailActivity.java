package com.example.android.moviejunkie;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.android.moviejunkie.utilities.Constants;

import java.util.Objects;

public class DetailActivity extends AppCompatActivity {

    private ImageView thumbnailIv;
    private TextView titleTv;
    private TextView releaseDateTv;
    private RatingBar voterAverageRatingBar;
    private TextView synopsisTv;

    private String posterUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        thumbnailIv = findViewById(R.id.detail_image_view);
        titleTv = findViewById(R.id.title_tv);
        releaseDateTv = findViewById(R.id.release_date_tv);
        voterAverageRatingBar = findViewById(R.id.voter_rating_bar);
        synopsisTv = findViewById(R.id.synopsis_tv);

        final Intent intentThatStartedThisActivity = getIntent();

        // Enable up navigation to parent Activity
        // find Toolbar view in layout
        Toolbar myToolbar = findViewById(R.id.toolbar);
        // Set the Toolbar as Action Bar
        setSupportActionBar(myToolbar);
        // enable up navigation to parent activity
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        // set status bar to be transparent
        getWindow().setStatusBarColor(Color.TRANSPARENT);

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
                if (scrollRange + verticalOffset == 0 && intentThatStartedThisActivity != null) {
                    collapsingToolbarLayout.setTitle(intentThatStartedThisActivity.getStringExtra(Constants.TITLE_KEY));
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbarLayout.setTitle(" ");
                    isShow = false;
                }
            }
        });


        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra(Constants.POSTER_URL_KEY)) {
                posterUrl = intentThatStartedThisActivity.getStringExtra(Constants.POSTER_URL_KEY);

                // use Glide to get image from url and put it in image view
                Glide.with(this)
                        .load(posterUrl)
                        .apply(new RequestOptions()
                                .centerCrop()
                                .format(DecodeFormat.PREFER_ARGB_8888)
                                .override(Target.SIZE_ORIGINAL))
                        .into(thumbnailIv);
            }
            if (intentThatStartedThisActivity.hasExtra(Constants.TITLE_KEY)) {
                titleTv.setText(intentThatStartedThisActivity.getStringExtra(Constants.TITLE_KEY));
            }
            if (intentThatStartedThisActivity.hasExtra(Constants.RELEASE_DATE_KEY)) {
                releaseDateTv.setText(intentThatStartedThisActivity.getStringExtra(Constants.RELEASE_DATE_KEY));
            }
            if (intentThatStartedThisActivity.hasExtra(Constants.SYNOPSIS_KEY)) {
                synopsisTv.setText(intentThatStartedThisActivity.getStringExtra(Constants.SYNOPSIS_KEY));
            }
            if (intentThatStartedThisActivity.hasExtra(Constants.VOTER_RATING_KEY)) {
                voterAverageRatingBar
                        .setRating(intentThatStartedThisActivity
                                .getFloatExtra(Constants.VOTER_RATING_KEY, 0));
            }

        }


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
        }
        return super.onOptionsItemSelected(item);
    }
}
