package com.example.moataz.popularmovies;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.ByteArrayInputStream;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

import static android.widget.ImageView.ScaleType.CENTER_CROP;

/**
 * Created by Moataz on 1/28/2016.
 */
public class CustomDBPosterAdapter extends BaseAdapter {
    private final Context context;
    private List<Blob> blobs = new ArrayList<>();
    final Uri CONTENT_URL =
            Uri.parse("content://com.example.moataz.popularmovies.MoviesProvider/cpmovies");
    // Provides access to other applications Content Providers
    ContentResolver resolver;
    private int Counter;
    private ArrayList<Bitmap> Images = new ArrayList<Bitmap>();
    private boolean ImagesLoaded = false;

    public CustomDBPosterAdapter(Context context,List<Blob> blobs) {
        this.blobs=blobs;
        this.context = context;

        resolver =context.getContentResolver();

    }



    @Override public View getView(int position, View convertView, ViewGroup parent) {
        SquaredImageView view = (SquaredImageView) convertView;
        if (view == null) {
            view = new SquaredImageView(context);
            view.setScaleType(CENTER_CROP);
        }

        // Projection contains the columns we want
        String[] projection = new String[]{"poster"};

        // Pass the URL, projection and I'll cover the other options below
        Cursor cursor = resolver.query(CONTENT_URL, projection, null, null, null);


        if(ImagesLoaded == false){

        if(cursor.moveToFirst()){

            do{


                byte[] image = cursor.getBlob(cursor.getColumnIndex("poster"));
                ByteArrayInputStream inputStream = new ByteArrayInputStream(image);
                Bitmap mBitmap = BitmapFactory.decodeStream(inputStream);
                Images.add(mBitmap);
                cursor.moveToNext();

            }while (cursor.moveToNext());

        }



            ImagesLoaded = true;
        }




        ImageView imageView;
        if (convertView == null){
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        }

        else{
            imageView = (ImageView) convertView;
        }

        imageView.setImageBitmap(Images.get(Counter));
        Counter++;



        return view;
    }


    @Override public int getCount() {
        return blobs.size();
    }

    @Override public Blob getItem(int position) {
        return blobs.get(position);
    }

    @Override public long getItemId(int position) {
        return position;
    }
}
