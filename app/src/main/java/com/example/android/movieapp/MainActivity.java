package com.example.android.movieapp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {

    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;
    private int spanCount = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLayoutManager = new GridLayoutManager(this, spanCount
        , GridLayoutManager.VERTICAL, false);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_movies);

        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(this);

        mRecyclerView.setAdapter(mMovieAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemSelected = item.getItemId();
        switch (itemSelected){
            case R.id.action_sort_by_popular:
                Toast.makeText(this, "popular", Toast.LENGTH_LONG).show();
                break;
            case R.id.action_sort_by_highest_rated:
                Toast.makeText(this, "high rated", Toast.LENGTH_LONG).show();
                break;
        }
        return true;
    }


    @Override
    public void onClick(View v) {
        Toast.makeText(this, "click", Toast.LENGTH_LONG).show();
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final  View displayView = inflater.inflate(R.layout.detail_layout, null);
        builder.setView(displayView);
        builder.setMessage("test message");
        builder.setPositiveButton(R.string.dismiss, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });

        AlertDialog ad = builder.create();
        ad.show();


    }




}
