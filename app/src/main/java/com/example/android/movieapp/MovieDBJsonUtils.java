package com.example.android.movieapp;

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

            movieTagObjects.add(new MovieTagObject(id, title, overview, imagePath));

        }
        return movieTagObjects;
    }
}
