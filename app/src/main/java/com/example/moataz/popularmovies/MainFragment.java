package com.example.moataz.popularmovies;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
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
public class MainFragment extends Fragment  {


    //    String[] moviesPosters = new String[20];
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    ArrayList<MovieObject> movieObjects = new ArrayList<>();
    ArrayList<String> PosterDB = new ArrayList<>();
    ArrayList<String> IDDB = new ArrayList<>();
    ArrayList<String> moviePosters = new ArrayList<>();
    CustomMoviesAdapter customMoviesAdapter;
    GridView gv;
    String popMovies = "popularity.desc";
    String highestRateMovies = "vote_average.desc";
    String SelectedSort = popMovies;
    String favoriteMovies;
    View rootView;
    String defaultUserCount = "";
    String userCount="vote_count.desc";
    String SelectedCount=defaultUserCount;
    int rowNum;
   boolean mDB = false;
    boolean hasConnection=true;
     final Uri CONTENT_URL =
            Uri.parse("content://com.example.moataz.popularmovies.MoviesProvider/cpmovies");


    // Provides access to other applications Content Providers
    ContentResolver resolver;

    /**
     +     * A callback interface that all activities containing this fragment must
     +     * implement. This mechanism allows activities to be notified of item
     +     * selections.
     +     */
        public interface Callback {
                /**
                 * DetailFragmentCallback for when an item has been selected.
                  */
                        public void onItemSelected(Uri msg);
            }


    public MainFragment() {
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {

       if (mDB) {
           outState.putString("moviesDB", favoriteMovies);
           outState.putStringArrayList("IDDB", IDDB);
           outState.putStringArrayList("PosterDB",PosterDB);

           Log.d("the state", "DB");

       }else{
        outState.putParcelableArrayList("movies", movieObjects);
           Log.d("the state","movies");}

        super.onSaveInstanceState(outState);
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }

        final AlertDialog.Builder networkDialog = new AlertDialog.Builder(getActivity());
        networkDialog.setTitle("Not Connected");
        networkDialog.setMessage("Please connect to internet to proceed");
        networkDialog.setCancelable(false);
        networkDialog.setPositiveButton("Okay!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                networkDialog.setCancelable(true);
            }
        });
        networkDialog.create().show();

        return false;
    }


    public void drawPosters(){
        int movieCount=movieObjects.size();
        for (int i = 0; i< movieCount;i++) {
            moviePosters.add(movieObjects.get(i).posterPath);
        }
        customMoviesAdapter.clear();;
        customMoviesAdapter.notifyDataSetChanged();
        customMoviesAdapter.addAll(moviePosters);


    }


    public void drawPostersDB(){

        customMoviesAdapter.clear();
        customMoviesAdapter.notifyDataSetChanged();
        customMoviesAdapter.addAll(PosterDB);


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);

        if(isOnline()) {


            if (savedInstanceState == null ) {
                FetchMoviesTask fetchMovies = new FetchMoviesTask();
                fetchMovies.execute();


            } else if(savedInstanceState.containsKey("movies")) {
                movieObjects = savedInstanceState.getParcelableArrayList("movies");
                final Handler handler = new Handler();
                handler.post(new Runnable() {

                    public void run() {
//                    FetchMoviesTask fetchMovies = new FetchMoviesTask();
                        drawPosters();

                    }
                });
            } else if(savedInstanceState.containsKey("moviesDB")){

                PosterDB = savedInstanceState.getStringArrayList("PosterDB");
                IDDB   = savedInstanceState.getStringArrayList("IDDB");


                final Handler handler = new Handler();
                handler.post(new Runnable() {

                    public void run() {

                        SelectedSort = favoriteMovies;

                        setSortToFavorite();
                        drawPostersDB();

                    }
                });
            }
        }

    }






//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//            inflater.inflate(R.menu.menu_fragment, menu);
//
//
//    }
//
//
//
//
//
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        int id = item.getItemId();
//
//
//        if (id == R.id.action_sort) {
//
//
//
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);



//    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        resolver = getActivity().getContentResolver();

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

        final FloatingActionButton programFab3 = new FloatingActionButton(getActivity());
        programFab3.setButtonSize(FloatingActionButton.SIZE_MINI);
        programFab3.setLabelText("Favorite Movies");
        programFab3.setImageResource(R.drawable.ic_star);
        programFab3.setColorNormal(Color.parseColor("#3F51B5"));
        menu.addMenuButton(programFab3);







            programFab1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                  Toast.makeText(getActivity(), programFab1.getLabelText(), Toast.LENGTH_SHORT).show();
                    SelectedSort = popMovies;
                    SelectedCount = defaultUserCount;

                   if (isOnline()) {
                       FetchMoviesTask fetchMovies = new FetchMoviesTask();
                       fetchMovies.execute();
                   }
                    menu.close(true);
                }
            });
            programFab2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                  Toast.makeText(getActivity(), programFab2.getLabelText(), Toast.LENGTH_SHORT).show();
                    SelectedSort = highestRateMovies;
                    SelectedCount = userCount;
                    if (isOnline()) {
                        FetchMoviesTask fetchMovies = new FetchMoviesTask();
                        fetchMovies.execute();
                    }
                    menu.close(true);
                }
            });
        programFab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                  Toast.makeText(getActivity(), programFab2.getLabelText(), Toast.LENGTH_SHORT).show();


                setSortToFavorite();
                drawPostersDB();
                menu.close(true);
            }
        });







        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int index = (int) id;


                if (SelectedSort == favoriteMovies)
                {
                    String idname = IDDB.get(index);
                    Log.d("faortited id",index+" : "+ idname);
                    Uri uri = Uri.parse(idname);
                    //send movie data goes here
                    ((Callback) getActivity()).onItemSelected(uri);


                } else {
                    String movieID = String.valueOf(movieObjects.get(index).id);
                    String title = movieObjects.get(index).title;
                    String overview = movieObjects.get(index).overview;
                    double rating = movieObjects.get(index).rating;
                    String release = movieObjects.get(index).releaseDate;
                    String imageURL = movieObjects.get(index).posterPath;

                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    Bundle bundle = new Bundle();

                    Log.d("json before", "before json");
                    JSONObject json = new JSONObject();
                    try {
                        json.put("movieID", movieID);
                        json.put("title", title);
                        json.put("release", release);
                        json.put("overview", overview);
                        json.put("rating", rating);
                        json.put("imageURL", imageURL);
                        Uri uri = Uri.parse(json.toString());
                        //send movie data goes here
                        ((Callback) getActivity()).onItemSelected(uri);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

//                json.put("uniqueArrays", new JSONArray(items));
//                String arrayList = json.toString();

                    Log.d("the pressed string", json.toString());
//               startActivity(intent);


                }

            }

        });

        return rootView;
    }



    public void setSortToFavorite(){
        SelectedSort = favoriteMovies;
        mDB=true;
        getMoviesFromDB();

        Toast.makeText(getActivity(), "feh aflam:" +rowNum, Toast.LENGTH_SHORT).show();
    }

    public void getMoviesFromDB(){

        // Projection contains the columns we want
        String[] projection = new String[]{"id", "mDBID","poster","videos","movie","reviews"};

        // Pass the URL, projection and I'll cover the other options below
        Cursor cursor = resolver.query(CONTENT_URL, projection, null, null, null);

        String movieList = "";
        rowNum = 0;
        String movie="";

        // Cycle through and display every row of data
        if(cursor.moveToFirst()){
PosterDB.clear();
IDDB.clear();
            do{

                String id = cursor.getString(cursor.getColumnIndex("id"));
                movie = cursor.getString(cursor.getColumnIndex("movie"));
                String idfromdb = cursor.getString(cursor.getColumnIndex("mDBID"));
                String reviews = cursor.getString(cursor.getColumnIndex("reviews"));
//                String Posterfromdb = cursor.getString(cursor.getColumnIndex("poster"));
                byte[] blov = cursor.getBlob(cursor.getColumnIndex("poster"));
                String Posterfromdb = blov.toString();
                String videos = cursor.getString(cursor.getColumnIndex("videos"));
                movieList = movieList + idfromdb+" : "+ id + " : "+ reviews + "\n";
                rowNum=rowNum+1;
                try {
                    JSONObject JSONobj = new JSONObject(movie);
                    String movieID=  JSONobj.getString("movieID");
                    String imageURL=JSONobj.getString("imageURL");
                    IDDB.add(idfromdb);
                    PosterDB.add(imageURL);

                } catch (Exception e)
                {e.printStackTrace();}



            }while (cursor.moveToNext());

        }
        Log.d("movieDB",movieList);
        cursor.close();



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
