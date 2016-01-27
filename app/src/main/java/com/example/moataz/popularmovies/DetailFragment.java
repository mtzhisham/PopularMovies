package com.example.moataz.popularmovies;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
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

    ArrayList<TrailerObject> trailerObjects = new ArrayList<>();
    ArrayList<ReviewObject> reviewObjects = new ArrayList<>();
    ListView listView;
    TextView videoName;
    TextView review;
    ArrayAdapter mVideosAdapter;
    ArrayAdapter<String> itemsAdapter;

    // The URL used to target the content provider
    static final Uri CONTENT_URL =
            Uri.parse("content://com.example.moataz.popularmovies.MoviesProvider/cpmovies");


    CursorLoader cursorLoader;

    // Provides access to other applications Content Providers
    ContentResolver resolver;

    JSONObject JSONobj;


    String movieID;
    String title;
    String overview;
    String release;
    String rating;
    String imageURL;

    String mDBID;

    public DetailFragment() {
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Intent intent = getActivity().getIntent();
        Bundle b = intent.getExtras();

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        resolver = getActivity().getContentResolver();
        try {
            JSONobj = new JSONObject(b.getString("json"));

             movieID=  JSONobj.getString("movieID");

            title= JSONobj.getString("title");
            release= JSONobj.getString("release");
            overview = JSONobj.getString("overview");
            rating=JSONobj.getString("rating");
            imageURL=JSONobj.getString("imageURL");

        } catch (Exception e)
        {e.printStackTrace();}



        if (b != null) {
        if(itemsAdapter == null) {
            FetchTrailerTask trilerVideosTask = new FetchTrailerTask();
            FetchTrailerTask trileReviewsTask = new FetchTrailerTask();

            trilerVideosTask.execute(movieID, "videos");
            trileReviewsTask.execute(movieID, "reviews");



                TextView titleTextView = (TextView) rootView.findViewById(R.id.title_textView);
                titleTextView.setText(title);

                listView = (ListView) rootView.findViewById(R.id.listview_videos);
                mVideosAdapter =
                        new ArrayAdapter<String>(getActivity(), R.layout.video_list_item, R.id.list_item_video_textview, new ArrayList<String>());
                listView.setAdapter(mVideosAdapter);


//                videoName = (TextView) rootView.findViewById(R.id.videoLink_tv);
                review = (TextView) rootView.findViewById(R.id.review_textView);
                review.setMovementMethod(new ScrollingMovementMethod());

                TextView overviewTextView = (TextView) rootView.findViewById(R.id.overview_textView);
                overviewTextView.setText(overview);

                TextView ratingTextView = (TextView) rootView.findViewById(R.id.rating_textView);
                ratingTextView.setText(rating);

                TextView releaseDateTextView = (TextView) rootView.findViewById(R.id.releaseDate_textView);
                releaseDateTextView.setText(release);

                ImageView posterImageView = (ImageView) rootView.findViewById(R.id.poster_imageView);

                Picasso.with(getActivity()) //
                        .load(imageURL) //
                         //
                        .error(R.drawable.error) //
                        .fit() //
                        .tag(getActivity()) //
                        .into(posterImageView);


          ImageButton favorite = (ImageButton) rootView.findViewById(R.id.fav_btn);

            if(lookupContact(movieID)){
                favorite.setSelected(true);
            Toast.makeText(getActivity(),"found it",Toast.LENGTH_SHORT).show();
            }else {
                favorite.setSelected(false);
                Toast.makeText(getActivity(),"not there",Toast.LENGTH_SHORT).show();
            }

            favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (lookupContact(movieID)){
                        v.setSelected(false);
                        Toast.makeText(getActivity(),"already there shod be deleted",Toast.LENGTH_SHORT).show();
                        // Use the resolver to delete ids by passing the content provider url
                        // what you are targeting with the where and the string that replaces
                        // the ? in the where clause
                         resolver.delete(CONTENT_URL,
                                "id = ? ", new String[]{movieID});


                    }

                    else {
                        Toast.makeText(getActivity(),"not there added",Toast.LENGTH_SHORT).show();
                        v.setSelected(true);
                        ContentValues values = new ContentValues();
                        values.put("movie", JSONobj.toString());
                        values.put("mDBID",movieID);
                        // Insert the value into the Content Provider
                        resolver.insert(CONTENT_URL, values);

                        Toast.makeText(getActivity(), "New Movie Added", Toast.LENGTH_LONG)
                                .show();
                        v.setSelected(true);
                    }

                    getContacts();

                }
            });

            }


        }


        return rootView;
    }


    public boolean lookupContact(String movieID) {

        // The id we want to search for
        String idToFind = movieID;

        // Holds the column data we want to retrieve
        String[] projection = new String[]{"id", "mDBID","movie"};

        // Pass the URL for Content Provider, the projection,
        // the where clause followed by the matches in an array for the ?
        // null is for sort order
        Cursor cursor = resolver.query(CONTENT_URL,
                projection, "id = ? ", new String[]{idToFind}, null);

        String movie = "";

        // Cycle through our one result or print error
        if(cursor.moveToFirst()){

            String id = cursor.getString(cursor.getColumnIndex("id"));
            String moviefromdb = cursor.getString(cursor.getColumnIndex("movie"));
            String idfromdb = cursor.getString(cursor.getColumnIndex("mDBID"));

            movie = movie +idfromdb+" : "+ id + " : " + moviefromdb + "\n";
            Log.d("the movie",movie);
           return true;
        }else{

            return false;
        }





    }



    public void getContacts(){

        // Projection contains the columns we want
        String[] projection = new String[]{"id", "mDBID","movie"};

        // Pass the URL, projection and I'll cover the other options below
        Cursor cursor = resolver.query(CONTENT_URL, projection, null, null, null);

        String movieList = "";

        // Cycle through and display every row of data
        if(cursor.moveToFirst()){

            do{

                String id = cursor.getString(cursor.getColumnIndex("id"));
                String movie = cursor.getString(cursor.getColumnIndex("movie"));
                String idfromdb = cursor.getString(cursor.getColumnIndex("mDBID"));
                movieList = movieList + idfromdb+" : "+ id + " : " + movie + "\n";

            }while (cursor.moveToNext());

        }
 Log.d("movieDB",movieList);


    }



    public class FetchTrailerTask extends AsyncTask<String, Void, String> {
        HttpURLConnection urlConnection = null;

        BufferedReader reader = null;

        Uri builtUri;
        URL url;

        Uri builtReviewUri;
        URL trailerUrl;

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
                Log.d("the url: ", url.toString());
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
                    // Stream was empty.  No point in parsing.
                    trailerJsonStr = null;
                }
                trailerJsonStr = buffer.toString();
                Log.v("TASK", "Movies JSON string" + trailerJsonStr);
            } catch (IOException e) {
                Log.e("TASK", "Error", e);
                trailerJsonStr = null;

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

            Log.d("trailer josn ", trailerJsonStr);
            return trailerJsonStr;

        }





        @Override
        protected void onPostExecute(String result) {
            Intent tr = new Intent();


            try {


                if (returnTybe == "videos") {
                    ArrayList<TrailerObject> trailerObjects = new ArrayList<>();
                    trailerObjects.addAll(getTrailerDataFromJson(result));
                    int trailerCount = trailerObjects.size();
                    final ArrayList<TrailerObject> videoArray= new ArrayList<>();
                    ArrayList<String> videoNAme = new ArrayList<>();

                    if ( videoArray.isEmpty()){
                    for (int i = 0; i < trailerCount; i++) {
                        Log.d("from TRAILER OBJECT", trailerObjects.get(i).name + trailerObjects.get(i).key);

//                        videoName.setText(trailerObjects.get(i).name);
//                       videokey = trailerObjects.get(i).key;
                       videoArray.add(trailerObjects.get(i));
                        videoNAme.add(videoArray.get(i).name);
                    }}
                    mVideosAdapter.clear();
                    mVideosAdapter.addAll(videoNAme);


       listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               String videokey = videoArray.get(position).getKey(position);
               final String urlStr = "http://www.youtube.com/watch?v="+videokey;
               Intent intent = new Intent(Intent.ACTION_VIEW);
               intent.setData(Uri.parse(urlStr));
                            startActivity(intent);
           }
       });



                }
                else {
                    ArrayList<ReviewObject> reviewObjects = new ArrayList<>();
                    reviewObjects.addAll(getTrailerDataFromJson(result));
                    int reviewCount = reviewObjects.size();

                    for (int i = 0; i < reviewCount; i++) {
                        Log.d("from TRAILER OBJECT", reviewObjects.get(i).author + reviewObjects.get(i).review);

                        review.setText("Author: "+reviewObjects.get(i).author+": "+ reviewObjects.get(i).review);
                    }
                }






            } catch (JSONException e) {
                e.printStackTrace();
            }




        }


        private ArrayList getTrailerDataFromJson(String trailerJsonStr) throws JSONException {


            JSONObject trailerJson = new JSONObject(trailerJsonStr);
            JSONArray trailerArray = trailerJson.getJSONArray("results");

            ArrayList<TrailerObject> arrayOfTrailers = new ArrayList<>();
            ArrayList<ReviewObject> arrayOfReviews = new ArrayList<>();
            Log.d("TASK trailer", trailerJson.toString());
            int numberOfTrailers = trailerArray.length();

            Log.d("TASK num videos thione",String.valueOf(numberOfTrailers) );

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
