package com.example.moataz.popularmovies;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {


    //    String[] moviesPosters = new String[20];
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    ArrayList<MovieObject> movieObjects = new ArrayList<>();

    ArrayList<String> moviePosters = new ArrayList<>();
    CustomMoviesAdapter customMoviesAdapter;
    GridView gv;
    String popMovies = "popularity.desc";
    String highestRateMovies = "vote_average.desc";
    String SelectedSort = popMovies;
    View rootView;
    String defaultUserCount = "";
    String userCount="vote_count.desc";
    String SelectedCount=defaultUserCount;


    public MainActivityFragment() {
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("movies", movieObjects);
        super.onSaveInstanceState(outState);
    }
    public void drawPosters(){
        int movieCount=movieObjects.size();
        for (int i = 0; i< movieCount;i++) {
            moviePosters.add(movieObjects.get(i).posterPath);
        }
        customMoviesAdapter.clear();;
        customMoviesAdapter.addAll(moviePosters);


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);

        if (savedInstanceState ==null || !savedInstanceState.containsKey("movies")){
            FetchMoviesTask fetchMovies = new FetchMoviesTask();
            fetchMovies.execute();


        } else {
            movieObjects = savedInstanceState.getParcelableArrayList("movies");
            final Handler handler = new Handler();
            handler.post(new Runnable(){

                public void run(){
                    FetchMoviesTask fetchMovies = new FetchMoviesTask();
                    drawPosters();

                }
            });
        }


    }






    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
            inflater.inflate(R.menu.menu_fragment, menu);


    }






    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.action_sort) {



            return true;
        }

        return super.onOptionsItemSelected(item);



    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        customMoviesAdapter = new CustomMoviesAdapter(getContext(),new ArrayList<String>());
        gv = (GridView) rootView.findViewById(R.id.movies_grid);
        gv.setAdapter(customMoviesAdapter);




        final FloatingActionMenu menu = (FloatingActionMenu) rootView.findViewById(R.id.menu);
        final FloatingActionButton programFab1 = new FloatingActionButton(getActivity());
        programFab1.setButtonSize(FloatingActionButton.SIZE_MINI);
        programFab1.setLabelText("Popular Movies");
        programFab1.setImageResource(R.drawable.ic_star);
        programFab1.setColorNormal(Color.parseColor("#3F51B5"));
        menu.addMenuButton(programFab1);


        final FloatingActionButton programFab2 = new FloatingActionButton(getActivity());
        programFab2.setButtonSize(FloatingActionButton.SIZE_MINI);
        programFab2.setLabelText("Highest Rated Movies");
        programFab2.setImageResource(R.drawable.ic_edit);
        programFab2.setColorNormal(Color.parseColor("#3F51B5"));
        menu.addMenuButton(programFab2);






            programFab1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                  Toast.makeText(getActivity(), programFab1.getLabelText(), Toast.LENGTH_SHORT).show();
                    SelectedSort = popMovies;
                    SelectedCount = defaultUserCount;
                    FetchMoviesTask fetchMovies = new FetchMoviesTask();
                    fetchMovies.execute();
                    menu.close(true);
                }
            });
            programFab2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                  Toast.makeText(getActivity(), programFab2.getLabelText(), Toast.LENGTH_SHORT).show();
                    SelectedSort = highestRateMovies;
                    SelectedCount = userCount;
                    FetchMoviesTask fetchMovies = new FetchMoviesTask();
                    fetchMovies.execute();
                    menu.close(true);
                }
            });








        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                int index = (int) id;
                Integer movieID = movieObjects.get(index).id;
                String trailer = movieID.toString();



                String title = movieObjects.get(index).title;
                String overview= movieObjects.get(index).overview;
                double rating = movieObjects.get(index).rating;
                String release = movieObjects.get(index).releaseDate;
                String imageURL = movieObjects.get(index).posterPath;

                Intent intent = new Intent(getActivity(),DetailActivity.class);
                intent.putExtra("MOVIE_RELEASE_DATE",release);
                intent.putExtra("MOVIE_TITLE",title);
                intent.putExtra("MOVIE_OVERVIEW", overview);
                intent.putExtra("MOVIE_RATING", rating);
                intent.putExtra("MOVIE_POSTER", imageURL);
                intent.putExtra("TRAILER",trailer);







                startActivity(intent);
                Toast.makeText(getActivity(), "movie id" + movieID, Toast.LENGTH_SHORT).show();


            }
        });

        return rootView;
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, String> {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        Uri builtUri;
        URL url;

        Uri TrailerBuiltUri;
        Uri trailerUrl;

        final String TRAILER_BASE_URL = "http://api.themoviedb.org/3/movie/";





        @Override
        protected String doInBackground(String... params) {
            String moviesJsonStr = null;

            try {
                final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/discover/movie?";

                final String SORT_BY_PARAM = "sort_by";
                final String APIKEY_PARAM = "api_key";
                if (SelectedCount.equals(defaultUserCount)) {
                    builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                            .appendQueryParameter(SORT_BY_PARAM, SelectedSort)
                            .appendQueryParameter(APIKEY_PARAM, BuildConfig.MOVIE_DATABASE_APIKEY)
                            .build();

                }
                else {
                    builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                            .appendQueryParameter(SORT_BY_PARAM, SelectedSort)
                            .appendQueryParameter(SORT_BY_PARAM, SelectedCount)
                            .appendQueryParameter(APIKEY_PARAM, BuildConfig.MOVIE_DATABASE_APIKEY)
                            .build();
                }

                url = new URL(builtUri.toString());
                Log.d("the url: ", url.toString());
                urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();


                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    moviesJsonStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {

                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    moviesJsonStr = null;
                }
                moviesJsonStr = buffer.toString();
                Log.v("TASK", "Movies JSON string" + moviesJsonStr);
            } catch (IOException e) {
                Log.e("TASK", "Error", e);
                moviesJsonStr = null;

            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("TASK", "Error closing stream", e);
                    }
                }
            }


            return moviesJsonStr;
        }





        @Override
        protected void onPostExecute(String result) {

            movieObjects = new ArrayList<>();
            moviePosters = new ArrayList<>();
            try {
                movieObjects.addAll(getMoviesDataFromJson(result));
                drawPosters();

            } catch (JSONException e) {
                e.printStackTrace();
            }




        }


        private ArrayList getMoviesDataFromJson(String moviesJsonStr) throws JSONException {


            JSONObject movieJson = new JSONObject(moviesJsonStr);
            JSONArray movieArray = movieJson.getJSONArray("results");
            ArrayList<MovieObject> arrayOfMovies = new ArrayList<>();
            Log.d("TASK", movieJson.toString());
            int numberOfMovies = movieArray.length();
            for (int i = 0; i < numberOfMovies; i++) {
                JSONObject movie = movieArray.getJSONObject(i);
                int id = movie.getInt("id");
                String title = movie.getString("title");
                String overview = movie.getString("overview");
                String posterPath = movie.getString("poster_path");
                double rating = movie.getDouble("vote_average");
                String relesaeDate = movie.getString("release_date");

                MovieObject formatedMovie = new MovieObject(id, title, overview, posterPath, rating,relesaeDate);
                arrayOfMovies.add(formatedMovie);



            }


            return arrayOfMovies;
        }



    }




}
