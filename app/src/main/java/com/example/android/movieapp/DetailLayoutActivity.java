package com.example.android.movieapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeStandalonePlayer;

import java.net.URISyntaxException;

public class DetailLayoutActivity extends AppCompatActivity {

    /**
     * member variables for the views of the detail layout
     */

    private TextView detailText;
    private ImageView detailImage;
    private TextView playTrailerTV;

    /**
     * variables for using youtube to play trailer.
     */
    private static final String API_KEY = ApiKey.YOUTUBE_API_KEY;
    private String mYoutubePath;
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

        new GetYoutubePath().execute(id);

        try {
            detailImage = MovieDBJsonUtils.loadImageFromJson(detailImage, NetworkUtils.buildImageResUri(imagePath));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        String overViewTextViewData = getString(R.string.release_date) + releaseDate
                    + "\n" + getString(R.string.voter_average) + voterAverage
                    + "\n" + getString(R.string.plot) + "\n"+ plot;

        detailText.setText(overViewTextViewData);

        playTrailerTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = YouTubeStandalonePlayer.createVideoIntent(
                        DetailLayoutActivity.this, API_KEY, mYoutubePath, 0, true, true);
                startActivity(intent);
            }
        });


    }

    /**
     * AsyncTask used to get the youtube path for the trailer
     */
    public class GetYoutubePath extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {
            return MovieDBJsonUtils.getYouTubePath(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mYoutubePath = s;

        }
    }

}
