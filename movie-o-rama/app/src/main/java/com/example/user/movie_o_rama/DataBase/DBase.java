package com.example.user.movie_o_rama.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.user.movie_o_rama.utils.MovieInfo;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by user on 20.03.2016.
 */
public class DBase extends SQLiteOpenHelper {
    public final String TABLE_NAME = "Movie_Info";
    public final String TITLE_COL = "title";
    public final String YEAR_COL = "year";
    public final String DIRECTOR_COL = "director";
    public final String ACTORS_COL = "actors";
    public final String IMDB_RATING_COL = "imdbRating";
    public final String POSTER_COL = "poster";

    public DBase (Context context) {
        super(context, "DBase", null, 1);
    }
    public DBase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + "(" +
                "id integer primary key autoincrement, " +
                TITLE_COL + " text, " +
                YEAR_COL + " text, " +
                DIRECTOR_COL + " text, " +
                ACTORS_COL + " text, " +
                IMDB_RATING_COL + " text, " +
                POSTER_COL + " text);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void fillDataBase(MovieInfo movieInfo) throws JSONException{
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.query(TABLE_NAME, null, null, null, null, null, null);

        if (!movieInfo.isNull()) {
            String title = movieInfo.getTitle();

            cv.put(TITLE_COL, title);
            cv.put(YEAR_COL, movieInfo.getYear());
            cv.put(DIRECTOR_COL, movieInfo.getDirector());
            cv.put(ACTORS_COL, movieInfo.getActors());
            cv.put(IMDB_RATING_COL, movieInfo.getIMDBRating());
            if (!movieInDataBase(title)) {
                cv.put(POSTER_COL, movieInfo.getPosterPath()); // вставляем постер, если его нет в БД
                db.insert(TABLE_NAME, null, cv);
            }
            else
                db.update(TABLE_NAME, cv, TITLE_COL + " = ?", new String[] {title});
        }
        showDB(c);
        c.close();
        db.close();
    }

    public ArrayList<String> getMovieInfo(String title) {
        ArrayList<String> result = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "select * from " + TABLE_NAME +
                " where " + TITLE_COL + " like '%" + title + "%';";

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        for (int i = 1; i < c.getColumnCount(); i++) {
            result.add(c.getString(i));
        }
        c.close();
        db.close();
        return result;
    }

    public boolean movieInDataBase(String title) {
        ArrayList<String> result = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "select * from " + TABLE_NAME +
                " where " + TITLE_COL + " like '%" + title + "%';";
        Cursor c = db.rawQuery(query, null);
        int count = c.getCount();
        c.close();
        return count != 0;
    }

    private void showDB(Cursor c) {
        if (c.moveToFirst()) {
            String str;
            do {
                str = "";
                for (String cn : c.getColumnNames()) {
                    str = str.concat(cn + " = "
                            + c.getString(c.getColumnIndex(cn)) + "; ");
                }
                Log.d("DB", str);

            } while (c.moveToNext());
        }
    }
}
