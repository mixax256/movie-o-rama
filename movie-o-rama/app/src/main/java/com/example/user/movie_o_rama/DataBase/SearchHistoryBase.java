package com.example.user.movie_o_rama.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by user on 27.03.2016.
 */
public class SearchHistoryBase extends SQLiteOpenHelper {
    public static final String TABLE_NAME = "Search_history";
    public static final String QUERY_COL = "query";
    public static final String POSTER_COL = "poster";
    private static SearchHistoryBase sHBase = null;

    private SearchHistoryBase (Context context) {
        super(context, "SearchHistory", null, 1);
    }

    public static SearchHistoryBase createSHBase(Context context) {
        if (sHBase == null)
            sHBase = new SearchHistoryBase(context);
        return sHBase;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + "(" +
                "id integer primary key autoincrement, " +
                QUERY_COL + " text, " +
                POSTER_COL + " text);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addQueryAtBase(String query) {
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.query(TABLE_NAME, null, null, null, null, null, null);

        cv.put(QUERY_COL, query);
        cv.put(POSTER_COL, "");

        db.insert(TABLE_NAME, null, cv);
        c.close();
        db.close();
    }

    public void addPoster(String title, String path) {
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "select * from " + TABLE_NAME +
                " where " + QUERY_COL + " like '%" + queryRightInput(title) + "%';";
        Cursor c = db.rawQuery(query, null);
        cv.put(POSTER_COL, path);

        db.update(TABLE_NAME, cv, QUERY_COL + " = ?", new String[]{queryRightInput(title)});
        c.close();
        db.close();
    }

    private String queryRightInput(String query) {
        String result = "" + query.toUpperCase().charAt(0);
        for (int i = 1; i < query.length(); i++) {
            if (query.charAt(i) == '!')
                result += "";
            else if (query.charAt(i - 1) == ' ')
                result += query.toUpperCase().charAt(i);
            else
                result += query.toLowerCase().charAt(i);
        }
        return result;
    }

    public ArrayList<HashMap> getSearchHistory() {
        ArrayList<HashMap> result = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.query(TABLE_NAME, null, null, null, null, null, null);

        while (c.moveToNext()) {
            HashMap<String, String> temp = new HashMap<>();
            temp.put(QUERY_COL, c.getString(c.getColumnIndex(QUERY_COL)));
            temp.put(POSTER_COL, c.getString(c.getColumnIndex(POSTER_COL)));
            result.add(temp);
        }
        showDB(c);
        c.close();
        db.close();
        return result;
    }

    public void cleanSearchHistory() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
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
                Log.d("DB1", str);

            } while (c.moveToNext());
        }
    }
}
