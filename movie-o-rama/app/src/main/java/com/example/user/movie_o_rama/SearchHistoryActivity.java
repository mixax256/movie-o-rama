package com.example.user.movie_o_rama;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.user.movie_o_rama.DataBase.SearchHistoryBase;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by user on 27.03.2016.
 */
public class SearchHistoryActivity extends AppCompatActivity {
    private SearchHistoryBase searchHistoryBase;

    public SearchHistoryActivity() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_history_activity);

        searchHistoryBase = SearchHistoryBase.createSHBase(this);
        ArrayList<HashMap> searchHistory = searchHistoryBase.getSearchHistory();
        ArrayList<HashMap<String, Object>> listSearchHistory = new ArrayList<>();

        String[] queries = new String[searchHistory.size()];

        for (int i = searchHistory.size() - 1; i >= 0; i--) {
            HashMap<String, Object> temp = new HashMap<>();
            queries[searchHistory.size() - 1 - i] = (String) searchHistory.get(i).get(SearchHistoryBase.QUERY_COL);
            temp.put(SearchHistoryBase.QUERY_COL, queries[searchHistory.size() - 1 - i]);
            String path = (String) searchHistory.get(i).get(SearchHistoryBase.POSTER_COL);

            if (path.isEmpty())
                temp.put(SearchHistoryBase.POSTER_COL, R.drawable.not_found);
            else {
                Uri uri = new Uri.Builder().path(path).build();
                temp.put(SearchHistoryBase.POSTER_COL, uri);
            }

            listSearchHistory.add(temp);
        }
        String[] from = new String[] {SearchHistoryBase.QUERY_COL, SearchHistoryBase.POSTER_COL};
        int[] to = new int[] {R.id.query, R.id.poster_in_list};
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, listSearchHistory, R.layout.list_item, from, to);
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(simpleAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                TextView titleAtList = (TextView) view.findViewById(R.id.query);
                intent.putExtra("title", titleAtList.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.seach_history_menu, menu);

        menu.findItem(R.id.clean_history);

        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.clean_history) {
            searchHistoryBase.cleanSearchHistory();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
