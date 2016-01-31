package com.example.moataz.popularmovies;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity  {
    boolean value;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);
        if(savedInstanceState == null){

            Bundle extras = getIntent().getExtras();
            //already two pane
            if (extras != null){
                value = extras.getBoolean("phone"); //two pane set to fasle

            }

                                        Bundle arguments = new Bundle();
                        arguments.putParcelable(DetailFragment.gotdatafromargs, getIntent().getData());
                        arguments.putBoolean("phone",value);

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


//    @Override
//    public void onMovieUnFav(final String movieID) {
//
//
//
//        Intent i = new Intent(getApplicationContext(), MainActivity.class);
//        i.putExtra("movieID",movieID);
//
////        MainActivity mainActivity = new MainActivity();
////        mainActivity.updateUI();
//
//
//    }


}
