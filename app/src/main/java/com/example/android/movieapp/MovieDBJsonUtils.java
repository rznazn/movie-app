package com.example.android.movieapp;

import android.net.Uri;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sport on 3/11/2017.
 */

public class MovieDBJsonUtils {


    public static ArrayList<MovieTagObject> translateStringToArrayList(String jsonString) throws JSONException {

        ArrayList<MovieTagObject> movieTagObjects = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(jsonString);
        JSONArray jsonArray = jsonObject.getJSONArray("results");
        for (int i = 0; i < jsonArray.length(); i++){
            JSONObject object = jsonArray.getJSONObject(i);
            String id = object.getString("id");
            String title = object.getString("original_title");
            String overview = object.getString("overview");
            String imagePath = object.getString("poster_path");
            String releaseDate = object.getString("release_date");
            String voterAverage = object.getString("vote_average");

            movieTagObjects.add(new MovieTagObject(id, title, overview, imagePath, releaseDate,voterAverage));

        }
        return movieTagObjects;
    }

    public static ImageView loadImageFromJson(ImageView view, Uri uri){
        Picasso.with(view.getContext()).load(uri).into(view);
        return view;
    }
}
