package com.example.moataz.popularmovies;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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
public class DetailFragment extends Fragment {

    public interface onUnFavUpdate {

        public void onMovieUnFav();
    }

    ListView listView;
    TextView reviewTextView;
    ArrayAdapter mVideosAdapter;
    ArrayList<String> videoNamesDB;
    // The URL used to target the content provider
    static final Uri CONTENT_URL =
            Uri.parse("content://com.example.moataz.popularmovies.MoviesProvider/cpmovies");

    static final String gotdatafromargs="args ddata is here";


    Activity mActivity;
    ContentResolver resolver;
    JSONObject JSONobj;
    StringBuilder allTheReviews;
    String movieID;
    String title;
    String overview;
    String release;
    String rating;
    String imageURL;
    ImageView posterImageView;
    ContentValues values;
    TextView titleTextView;
    TextView overviewTextView;
    TextView ratingTextView;
    TextView releaseDateTextView;
    ImageButton favorite;
    String movieFromFavorie;
    Uri mUri;
    String theURI;
    ArrayList<String> videokeyArray;
    Boolean isOnePane=false;
    Intent myShareIntent;
    ShareActionProvider myShareActionProvider;
    String urlStr;
    ArrayList<String> videoNAme;





    public DetailFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
       outState.putParcelable("theUri", mUri);
        outState.putBoolean("isOnePAne", isOnePane);



        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resolver = getActivity().getContentResolver();
        values = new ContentValues();
        Bundle arguments = getArguments();
        if (savedInstanceState != null ) {

            isOnePane = savedInstanceState.getBoolean("isOnePAne");

           Uri uri = savedInstanceState.getParcelable("theUri");
            if (uri != null) {

                setHasOptionsMenu(true);
                if (arguments != null) {
                    mUri = arguments.getParcelable(DetailFragment.gotdatafromargs);
                    theURI =   mUri.toString();
                    setHasOptionsMenu(true);

                }
            }

        }
else {
            if (arguments != null) {
                        mUri = arguments.getParcelable(DetailFragment.gotdatafromargs);
                        theURI =   mUri.toString();
                   setHasOptionsMenu(true);
                //
                isOnePane = getArguments().getBoolean("phone");


  }

        }

        setMovieData();



 }

public void setMovieData(){

    ;
    movieID = theURI;
    if (movieID!= null){

    if (lookupMovies(movieID)){

        try {
            JSONobj = new JSONObject(movieFromFavorie);

            movieID = JSONobj.getString("movieID");

            title = JSONobj.getString("title");
            release = JSONobj.getString("release");
            overview = JSONobj.getString("overview");
            rating = JSONobj.getString("rating");
            imageURL = JSONobj.getString("imageURL");


            }
               catch(Exception e){
                e.printStackTrace();
            }
        FetchTrailerTask trilerVideosTask = new FetchTrailerTask();
        FetchTrailerTask trileReviewsTask = new FetchTrailerTask();

        trilerVideosTask.execute(movieID, "videos");
        trileReviewsTask.execute(movieID, "reviews");

        }

    else {

        try {
            JSONobj = new JSONObject(theURI);

            movieID = JSONobj.getString("movieID");

            title = JSONobj.getString("title");
            release = JSONobj.getString("release");
            overview = JSONobj.getString("overview");
            rating = JSONobj.getString("rating");
            imageURL = JSONobj.getString("imageURL");



        } catch (Exception e) {
            e.printStackTrace();
        }

    }
        FetchTrailerTask trilerVideosTask = new FetchTrailerTask();
        FetchTrailerTask trileReviewsTask = new FetchTrailerTask();

        trilerVideosTask.execute(movieID, "videos");
        trileReviewsTask.execute(movieID, "reviews");

    }





}

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_detail, menu);
        MenuItem shareItem = menu.findItem(R.id.action_share);
        myShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        myShareIntent = new Intent(Intent.ACTION_SEND);
        myShareIntent.setType("text/plain");

       if (isOnePane) {
           myShareIntent.putExtra(Intent.EXTRA_TEXT, urlStr);
       }
        myShareActionProvider.setShareIntent(myShareIntent);
    }











    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);



        favorite = (ImageButton) rootView.findViewById(R.id.fav_btn);




                titleTextView = (TextView) rootView.findViewById(R.id.title_textView);


                listView = (ListView) rootView.findViewById(R.id.listview_videos);
                mVideosAdapter =
                        new ArrayAdapter<String>(getActivity(), R.layout.video_list_item, R.id.list_item_video_textview, new ArrayList<String>());
                listView.setAdapter(mVideosAdapter);
        makeMyScrollSmart(listView);


            reviewTextView = (TextView) rootView.findViewById(R.id.review_textView);
              makeMyScrollSmart(reviewTextView);

                overviewTextView = (TextView) rootView.findViewById(R.id.overview_textView);


                ratingTextView = (TextView) rootView.findViewById(R.id.rating_textView);


                releaseDateTextView = (TextView) rootView.findViewById(R.id.releaseDate_textView);


                 posterImageView = (ImageView) rootView.findViewById(R.id.poster_imageView);
        listView.setAdapter(mVideosAdapter);

        reviewTextView.setMovementMethod(new ScrollingMovementMethod());


        if(movieID != null){




            if( lookupMovies(movieID)){
            favorite.setSelected(true);

        }else {
            favorite.setSelected(false);

        }
            setMovieDataViews();






        }


            favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if ( lookupMovies(movieID)) {
                        v.setSelected(false);
                        Toast.makeText(getActivity(), "Removed from favorites", Toast.LENGTH_SHORT).show();

                        deleteMovie();
                        if(!isOnePane){
                        if (!lookupMovies(movieID)) {
                            ((onUnFavUpdate) getActivity()).onMovieUnFav();
                        }}


                    } else {
                        Toast.makeText(getActivity(), "Favorited", Toast.LENGTH_SHORT).show();
                        v.setSelected(true);
                        values = new ContentValues();
                        values.put("movie", JSONobj.toString());
                        values.put("mDBID", movieID);

                        resolver.insert(CONTENT_URL, values);
                        v.setSelected(true);

                        if(!isOnePane){
                            ((onUnFavUpdate) getActivity()).onMovieUnFav();}

                    }


                }
            });



        return rootView;
    }

    public void deleteMovie(){
        resolver.delete(CONTENT_URL,
                "mDBID = ? ", new String[]{movieID});


    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity  = getActivity();

    }
    public void setMovieDataViews(){

        favorite.setVisibility(View.VISIBLE);
        titleTextView.setText(title);
        overviewTextView.setText(overview);
        ratingTextView.setText(rating);
        releaseDateTextView.setText(release);
        Picasso.with(getActivity())
                .load(imageURL)

                .error(R.drawable.error)
                .fit()
                .tag(getActivity())
                .into(posterImageView);

    }


    private void makeMyScrollSmart(View view) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View __v, MotionEvent __event) {
                if (__event.getAction() == MotionEvent.ACTION_DOWN) {
                    //  Disallow the touch request for parent scroll on touch of child view
                    requestDisallowParentInterceptTouchEvent(__v, true);
                } else if (__event.getAction() == MotionEvent.ACTION_UP || __event.getAction() == MotionEvent.ACTION_CANCEL) {
                    // Re-allows parent events
                    requestDisallowParentInterceptTouchEvent(__v, false);
                }
                return false;
            }
        });
    }

    private void requestDisallowParentInterceptTouchEvent(View __v, Boolean __disallowIntercept) {
        while (__v.getParent() != null && __v.getParent() instanceof View) {
            if (__v.getParent() instanceof ScrollView) {
                __v.getParent().requestDisallowInterceptTouchEvent(__disallowIntercept);
            }
            __v = (View) __v.getParent();
        }
    }



    public boolean lookupMovies(String movieID) {


        String[] projection = new String[]{"id", "mDBID","movie"};


        Cursor cursor = resolver.query(CONTENT_URL,
                projection, "mDBID = ? ", new String[]{movieID}, null);


        if(cursor.moveToFirst()){
            String moviefromdb = cursor.getString(cursor.getColumnIndex("movie"));
            movieFromFavorie = moviefromdb;

            cursor.close();
            return true;
        }else{
            cursor.close();
           return false;
        }
 }


    public class FetchTrailerTask extends AsyncTask<String, Void, String> {
        HttpURLConnection urlConnection = null;

        BufferedReader reader = null;

        Uri builtUri;
        URL url;

        String returnTybe;





        @Override
        protected String doInBackground(String... params) {
            String trailerJsonStr = null;
            String movieID = params[0];
            returnTybe = params[1];
            try {
                final String BASE_URL = "https://api.themoviedb.org/3/movie/";

                final String APIKEY_PARAM = "api_key";

                builtUri = Uri.parse(BASE_URL).buildUpon().appendPath(movieID).appendPath(returnTybe)
                        .appendQueryParameter(APIKEY_PARAM, BuildConfig.MOVIE_DATABASE_APIKEY)
                        .build();


                url = new URL(builtUri.toString());

                urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();





                InputStream inputStream = urlConnection.getInputStream();

                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    trailerJsonStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {

                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {

                    trailerJsonStr = null;
                }
                trailerJsonStr = buffer.toString();

            } catch (IOException e) {

                trailerJsonStr = null;

            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                      e.printStackTrace();
                    }
                }
            }

            return trailerJsonStr;

        }





        @Override
        protected void onPostExecute(String result) {



            try {


                if (returnTybe == "videos") {
                    ArrayList<TrailerObject> trailerObjects = new ArrayList<>();
                    trailerObjects.addAll(getTrailerDataFromJson(result));

                    int trailerCount = trailerObjects.size();
                    final ArrayList<TrailerObject> videoArray= new ArrayList<>();
                    videoNAme = new ArrayList<>();
                    videokeyArray = new ArrayList<>();


                    if ( videoArray.isEmpty()){
                    for (int i = 0; i < trailerCount; i++) {

                        videoArray.add(trailerObjects.get(i));
                        videokeyArray.add((videoArray.get(i).key)) ;
                        videoNAme.add(videoArray.get(i).name);
                    }}
                    mVideosAdapter.clear();
                    mVideosAdapter.addAll(videoNAme);
                    yTVideo();









                }
                else {

                    allTheReviews = new StringBuilder();
                    ArrayList<ReviewObject> reviewObjects = new ArrayList<>();
                    reviewObjects.addAll(getTrailerDataFromJson(result));
                    int reviewCount = reviewObjects.size();

                    for (int i = 0; i < reviewCount; i++) {

                        allTheReviews.append("Author: "+reviewObjects.get(i).author+": "+ reviewObjects.get(i).review);
                    }
                    reviewTextView.setText(allTheReviews);
                    makeMyScrollSmart(reviewTextView);
                }





            } catch (JSONException e) {
                e.printStackTrace();
            }




        }

        public void yTVideo(){
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String videokey = videokeyArray.get(position);
                    final String urlStr = "http://www.youtube.com/watch?v="+videokey;
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(urlStr));
                    startActivity(intent);

                }
            });

            if (!videokeyArray.isEmpty()) {
                String videokey = videokeyArray.get(0);
                String videoName = videoNAme.get(0);
                urlStr = title + ": " + videoName + " - " + "http://www.youtube.com/watch?v=" + videokey + " #PopularMovies ";

            }
        }

        private ArrayList getTrailerDataFromJson(String trailerJsonStr) throws JSONException {


            JSONObject trailerJson = new JSONObject(trailerJsonStr);
            JSONArray trailerArray = trailerJson.getJSONArray("results");

            ArrayList<TrailerObject> arrayOfTrailers = new ArrayList<>();
            ArrayList<ReviewObject> arrayOfReviews = new ArrayList<>();

            int numberOfTrailers = trailerArray.length();



            if(returnTybe == "videos"){
                for (int i = 0; i < numberOfTrailers; i++) {
                    JSONObject trailer = trailerArray.getJSONObject(i);

                    String name = trailer.getString("name");
                    String key = trailer.getString("key");
                    int id = trailerJson.getInt("id");
                    TrailerObject formatedTrailer = new TrailerObject(id,name, key);
                    arrayOfTrailers.add(formatedTrailer);

                }

                return arrayOfTrailers;

            }
            else {
                for (int i = 0; i < numberOfTrailers; i++) {
                    JSONObject trailer = trailerArray.getJSONObject(i);
                    int id = trailerJson.getInt("id");
                    String content = trailer.getString("content");
                    String author = trailer.getString("author");

                    ReviewObject formatedReview = new ReviewObject(id,author, content);
                    arrayOfReviews.add(formatedReview);
                }

                return arrayOfReviews;
            }



        }



    }

}
