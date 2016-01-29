package com.example.moataz.popularmovies;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if(savedInstanceState == null){

            // Create the detail fragment and add it to the activity
                               // using a fragment transaction.

                                        Bundle arguments = new Bundle();
                        arguments.putParcelable(DetailFragment.gotdatafromargs, getIntent().getData());

                                DetailFragment fragment = new DetailFragment();
                        fragment.setArguments(arguments);

                    getSupportFragmentManager().beginTransaction()

                                       .add(R.id.movie_detail_container, fragment)
                    .commit();
        }

    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public Intent getParentActivityIntent() {
        return super.getParentActivityIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);}

}
