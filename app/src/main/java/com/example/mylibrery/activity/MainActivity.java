package com.example.mylibrery.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mylibrery.R;
import com.example.mylibrery.adaptador.RecyclerAdapter;
import com.example.mylibrery.model.ItemList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements RecyclerAdapter.RecyclerItemClick, SearchView.OnQueryTextListener {

    private RecyclerView rvLista;
    private SearchView svSearch;
    private RecyclerAdapter adapter;
    private List<ItemList> items;
    private List<ItemList> books = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        //SearchBooksWs("mongo");
        initListener();
    }

    private void initViews(){
        rvLista = findViewById(R.id.listBooks);
        svSearch = findViewById(R.id.searchBooks);
    }

    private void initValues() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        rvLista.setLayoutManager(manager);

        items = getItems();
        adapter = new RecyclerAdapter(items, this);
        rvLista.setAdapter(adapter);
    }

    private void initListener() {
        svSearch.setOnQueryTextListener(this);
    }

    private List<ItemList> getItems() {
        List<ItemList> itemLists = new ArrayList<>();
        for (int i = 0; i < books.size(); i++ ) {
            //
            itemLists.add(books.get(i));
        }
        return itemLists;
    }

    @Override
    public void itemClick(ItemList item) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("itemDetail", item);
        startActivity(intent);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // adapter.filter(newText);
        SearchBooksWs(newText);
        return false;
    }

    private void SearchBooksWs(String filter) {

        String url = "https://api.itbook.store/1.0/search/" + filter;

        StringRequest postRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray booksJsonArray = jsonObject.getJSONArray("books");
                    books = new ArrayList<ItemList>();
                    for (int i = 0; i < booksJsonArray.length(); i++) {
                        JSONObject object =(JSONObject) booksJsonArray.get(i);
                        books.add(new ItemList(object.get("title").toString(),object.get("subtitle").toString(),object.get("image").toString()));
                    }
                    Log.i("listaitems", books.toString());
                    initValues();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ERROR2", String.valueOf(error));
            }
        });
        Volley.newRequestQueue(this).add(postRequest);
    }


}
