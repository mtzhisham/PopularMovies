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
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_detail, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_share) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

}
