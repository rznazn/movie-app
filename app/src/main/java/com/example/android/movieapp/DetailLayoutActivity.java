package com.example.android.movieapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import java.net.URISyntaxException;

public class DetailLayoutActivity extends AppCompatActivity {

    /**
     * member variables for the views of the detail layout
     */

    private TextView detailText;
    private ImageView detailImage;
    private TextView playTrailerTV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_layout);

        playTrailerTV = (TextView) findViewById(R.id.play_trailer);
        detailText = (TextView) findViewById(R.id.tv_movie_detail);
        detailImage = (ImageView) findViewById(R.id.alert_dialog_imageView);
        detailImage.setAlpha(.40f);

        Intent intent = getIntent();
        String title = intent.getStringExtra(getString(R.string.title));
        getSupportActionBar().setTitle(title);
        String id = intent.getStringExtra("id");
        String voterAverage = intent.getStringExtra(getString(R.string.voter_average));
        String plot = intent.getStringExtra(getString(R.string.plot));
        String releaseDate = intent.getStringExtra(getString(R.string.release_date));
        String imagePath = intent.getStringExtra(getString(R.string.image_path));

        try {
            detailImage = MovieDBJsonUtils.loadImageFromJson(detailImage, NetworkUtils.buildImageResUri(imagePath));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        String overViewTextViewData = getString(R.string.release_date) + releaseDate
                    + "\n" + getString(R.string.voter_average) + voterAverage
                    + "\n" + getString(R.string.plot) + "\n"+ plot;

        detailText.setText(overViewTextViewData);


    }

}
