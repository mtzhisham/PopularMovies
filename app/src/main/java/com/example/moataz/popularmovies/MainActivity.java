package com.example.moataz.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MainActivity extends AppCompatActivity  implements MainFragment.Callback,DetailFragment.onUnFavUpdate {
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private boolean mTwoPane;


    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("true", 1);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Stetho.initializeWithDefaults(this);


        if (findViewById(R.id.movie_detail_container) != null) {

            mTwoPane = true;

            if (savedInstanceState == null) {

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();


            }
        } else {
            mTwoPane = false;



        }
    }



    @Override
    public void onItemSelected(Uri msg) {



        if (mTwoPane) {

            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.gotdatafromargs, msg);

                    DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

                    getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();

        } else {

            Intent intent = new Intent(this, DetailActivity.class).setData(msg);
            intent.putExtra("phone",true);
            startActivity(intent);

        }
    }

    @Override
    public void onMovieUnFav() {
        updateUI();
    }


    public void updateUI(){

        if(mTwoPane) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MainFragment mainFragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_main);


                    if (mainFragment.mDB) {
                        mainFragment.getMoviesFromDB();
                        mainFragment.drawPostersDB();

                    }
                }
            });


        }


        }






}
