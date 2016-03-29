package com.example.user.movie_o_rama.utils;

import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ExecutionException;

/**
 * Created by user on 20.03.2016.
 */
public class MovieInfo {
    private JSONObject movieInfo = null;
    private Context context;
    private final String URL_NO_POSTER = "http://www.classicposters.com/images/nopicture.gif";

    public MovieInfo(JSONObject jsonObject, Context context) {
        movieInfo = jsonObject;
        this.context = context;
    }

    public boolean isNull() {
        return movieInfo == null;
    }

    public String getTitle() throws JSONException {
        return movieInfo.getString("Title");
    }

    public String getYear() throws JSONException {
        return movieInfo.getString("Year");
    }

    public String getDirector() throws JSONException {
        return movieInfo.getString("Director");
    }

    public String getActors() throws JSONException {
        return movieInfo.getString("Actors");
    }

    public String getIMDBRating() throws JSONException {
        return movieInfo.getString("imdbRating");
    }

    public String getPosterPath() throws JSONException {
        String url = movieInfo.getString("Poster");
        if (url.equals("N/A"))
            return saveImage(nameOfFile(getTitle()), loadPoster(URL_NO_POSTER));
        else
            return saveImage(nameOfFile(getTitle()), loadPoster(url));
    }

    private String nameOfFile(String name) {
        String nFile = "";
        for (int i = 0; i < name.length(); i++) {
            if (name.charAt(i) == ' ' || name.charAt(i) == '+' || name.charAt(i) == ':' || name.charAt(i) == '!')
                nFile += "";
            else
                nFile += name.charAt(i);
        }
        return nFile;
    }

    private Bitmap loadPoster(String url) {
        Bitmap poster = null;
        try {
            poster = Glide.
                    with(context).
                    load(url).
                    asBitmap().
                    into(-1, -1).
                    get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return poster;
    }

    private String saveImage(String name, Bitmap image) {
        File path = context.getFilesDir();
        OutputStream fOut = null;
        File file = new File(path, name + ".png");
        try {
            fOut = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fOut != null)
                    fOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file.getAbsolutePath();
    }
}
