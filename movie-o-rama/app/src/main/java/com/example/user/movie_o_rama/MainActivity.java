package com.example.user.movie_o_rama;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.movie_o_rama.DataBase.DBase;
import com.example.user.movie_o_rama.DataBase.SearchHistoryBase;
import com.example.user.movie_o_rama.utils.API;
import com.example.user.movie_o_rama.utils.MovieInfo;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    private ImageView poster;
    private TextView title;
    private TextView director;
    private TextView actors;
    private TextView imdbRating;
    private String path = "";

    private String KEY_POSTER = "poster";
    private String KEY_TITLE = "title";
    private String KEY_DIRECTOR = "director";
    private String KEY_ACTORS = "actors";
    private String KEY_RATING = "rating";

    private SearchHistoryBase searchHistoryBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        poster = (ImageView) findViewById(R.id.poster);
        title = (TextView) findViewById(R.id.title);
        director = (TextView) findViewById(R.id.director);
        actors = (TextView) findViewById(R.id.actors);
        imdbRating = (TextView) findViewById(R.id.imdbRating);

        searchHistoryBase = SearchHistoryBase.createSHBase(this);

        if (savedInstanceState != null) {
            String directorStr = savedInstanceState.getString(KEY_DIRECTOR);
            String actorsStr = savedInstanceState.getString(KEY_ACTORS);
            String rating = savedInstanceState.getString(KEY_RATING);
            path = savedInstanceState.getString(KEY_POSTER);

            title.setText(savedInstanceState.getString(KEY_TITLE));
            director.setText(Html.fromHtml("<b>" + directorStr.substring(0, 9) + "</b>" +
                    directorStr.substring(9)));
            actors.setText(Html.fromHtml("<b>" + actorsStr.substring(0, 7) + "</b>" +
                    actorsStr.substring(7)));
            imdbRating.setText(Html.fromHtml("<b>" + rating.substring(0, 11) + "</b>" +
                    rating.substring(11)));

            Uri uri = new Uri.Builder().path(path).build();
            poster.setImageURI(uri);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_TITLE, title.getText().toString());
        outState.putString(KEY_ACTORS, actors.getText().toString());
        outState.putString(KEY_DIRECTOR, director.getText().toString());
        outState.putString(KEY_RATING, imdbRating.getText().toString());
        outState.putString(KEY_POSTER, path);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager) MainActivity.this.getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(MainActivity.this.getComponentName()));
        }
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_search_history) {
            //SearchHistoryActivity sHActivity = new SearchHistoryActivity(searchHistoryBase);
            Intent searchHistoryIntent = new Intent(this, SearchHistoryActivity.class);
            this.startActivityForResult(searchHistoryIntent, 1);
        } else if (id == R.id.nav_exit) {
            System.exit(0);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {return;}
        String title = data.getStringExtra("title");
        new FilmInfo().execute(title);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.user.movie_o_rama/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.user.movie_o_rama/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        FilmInfo filmInfo = new FilmInfo();
        String title = queryRightInput(query);
        searchHistoryBase.addQueryAtBase(title);
        filmInfo.execute(title);
        return true;
    }

    private String queryRightInput(String query) {
        String result = "" + query.toUpperCase().charAt(0);
        for (int i = 1; i < query.length(); i++) {
            if (query.charAt(i - 1) == ' ')
                result += query.toUpperCase().charAt(i);
            else
                result += query.charAt(i);
        }
        return result;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    private void noInternetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.no_internet_dialog_title))
                .setMessage(getResources().getString(R.string.no_internet))
                .setCancelable(true)
                .setNegativeButton("ОК",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        builder.create().show();
    }

    private class FilmInfo extends AsyncTask<String, Void, ArrayList> {

        @Override
        protected ArrayList doInBackground(String... params) {
            API.ApiResponse ap = null;
            try {
                ap = API.execute(MainActivity.this, API.HttpMethod.GET, "", "t", params[0], "plot",
                        "short", "r", "json");
            } catch (Exception e) {
                e.printStackTrace();
            }
            JSONObject result = null;
            ArrayList<Object> res = new ArrayList<>();
            boolean hasInternet = true; //флаг на наличие интернета
            try {
                result = ap.getJson();
            } catch (Exception e) {
                hasInternet = false; //интернета нет
            }

            MovieInfo movieInfo = new MovieInfo(result, MainActivity.this);
            DBase dataBase = new DBase(MainActivity.this);
            boolean movieFounded = true; //предполагаем, что фильм с таким названием есть
            try {
                dataBase.fillDataBase(movieInfo);
            } catch (JSONException e) {
                movieFounded = false; // фильма с таким названием не нашлось (при наличии интернета)
            }
            res.add(hasInternet);
            res.add(movieFounded);
            res.add(dataBase);
            res.add(params[0]);
            return res;
        }

        protected void onPostExecute(ArrayList result) {
            boolean hasInternet = (boolean) result.get(0);
            DBase dataBase = (DBase) result.get(2);
            String titleOfFilm = (String) result.get(3);
            boolean movieFounded = (boolean) result.get(1) && dataBase.movieInDataBase(titleOfFilm);

            if (!hasInternet)
                noInternetDialog();
            if (movieFounded) {
                ArrayList<String> movieInfo = dataBase.getMovieInfo(titleOfFilm);

                title.setText(movieInfo.get(0) + " (" + movieInfo.get(1) + ")");
                director.setText(Html.fromHtml("<b>" + getResources().getString(R.string.director)
                        + "</b> " + movieInfo.get(2)));
                actors.setText(Html.fromHtml("<b>" + getResources().getString(R.string.actors)
                        + "</b> " + movieInfo.get(3)));
                imdbRating.setText(Html.fromHtml("<b>" + getResources().getString(R.string.imdbRating)
                        + "</b> " + movieInfo.get(4)));

                path = movieInfo.get(movieInfo.size() - 1);
                searchHistoryBase.addPoster(movieInfo.get(0), path);
                Uri uri = new Uri.Builder().path(path).build();
                poster.setImageURI(uri);
            }
            else {
                poster.setImageResource(R.drawable.not_found);
                title.setText("");
                director.setText("");
                actors.setText("");
                imdbRating.setText("");
            }
        }
    }
}
