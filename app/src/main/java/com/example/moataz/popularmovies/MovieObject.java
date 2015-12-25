package com.example.moataz.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Moataz on 12/5/2015.
 */
public class MovieObject implements Parcelable {



    int id;
    String title;
    String overview;
    String posterPath;
    double rating;
    String releaseDate;
public MovieObject(){

}

    public MovieObject (int movieId, String movieTitle, String movieOverview, String moviePosterPath, double movieRating, String movieReleaseDate){

        this.id = movieId;
        this.title = movieTitle;
        this.overview = movieOverview;
        this.posterPath="http://image.tmdb.org/t/p/w342/"+moviePosterPath;
        this.rating=movieRating;
        this.releaseDate = movieReleaseDate;

    }
    private MovieObject (Parcel in){
         id=in.readInt();
         title=in.readString();
         overview= in.readString();
         posterPath = in.readString();
         rating = in.readDouble();
         releaseDate = in.readString();


    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(overview);
        dest.writeString(posterPath);
        dest.writeDouble(rating);
        dest.writeString(releaseDate);

    }

    public final Parcelable.Creator<MovieObject> CREATOR = new Parcelable.Creator<MovieObject>() {
        @Override
        public MovieObject createFromParcel(Parcel parcel) {
            return new MovieObject(parcel);
        }

        @Override
        public MovieObject[] newArray(int i) {
            return new MovieObject[i];
        }

    };




}
