package com.example.moataz.popularmovies;

/**
 * Created by Moataz on 12/25/2015.
 */
public class ReviewObject {

    String review;
    String author;
    int id;


    ReviewObject(int id,String author, String review){
        this.author=author;
        this.review=review;
        this.id=id;
    }

}
