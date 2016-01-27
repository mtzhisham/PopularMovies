package com.example.moataz.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if(savedInstanceState == null){

            getSupportFragmentManager().beginTransaction().add(R.id.movie_detail_container,new DetailActivityFragment()).commit();
        }

    }

}
