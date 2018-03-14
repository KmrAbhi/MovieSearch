package com.example.abhi.moviesearch;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.abhi.moviesearch.API.MovieApi;
import com.example.abhi.moviesearch.API.MovieSearch;
import com.example.abhi.moviesearch.API.MovieService;
import com.example.abhi.moviesearch.model.Result;
import com.example.abhi.moviesearch.model.TopRatedMovies;
import com.example.abhi.moviesearch.util.ScrollListener;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int PAGE_START = 1;
    Adapter adapter;
    Adapter adapter_search;
    LinearLayoutManager linearLayoutManager;
    RecyclerView rv;
    ProgressBar progressBar;
    private boolean isOnMain=true;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 20;//restricting to 20 pages
    private int currentPage = PAGE_START;
    private MovieService movieService;
    private MovieSearch moviesearch;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            loadSearchPage(query);

        }
    }
private void loadSearchPage(String query){
    Toast.makeText(this, "fired", Toast.LENGTH_SHORT).show();
    callSearchedMoviesApi(query).enqueue(new Callback<TopRatedMovies>() {
        @Override
        public void onResponse(Call<TopRatedMovies> call, Response<TopRatedMovies> response) {
            List<Result> results = fetchResults(response);
            adapter_search=new Adapter(MainActivity.this);
            adapter_search.addAll(results);
            rv.setAdapter(adapter_search);
            isOnMain=false;
        }

        @Override
        public void onFailure(Call<TopRatedMovies> call, Throwable t) {
            Toast.makeText(MainActivity.this,"Couldnot fetch",Toast.LENGTH_SHORT).show();

        }
    });

    }

    @Override
    public void onBackPressed() {
        if(isOnMain==false){
            rv.setAdapter(adapter);
            isOnMain=true;
        }
        else{
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rv = (RecyclerView) findViewById(R.id.main_recycle_view);
        progressBar = (ProgressBar) findViewById(R.id.main_progress);
        adapter = new Adapter(this);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(linearLayoutManager);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(adapter);

        rv.addOnScrollListener(new ScrollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;
                //taking care of network call for API call
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadNextPage();
                    }
                }, 1000);
            }

            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            public boolean isLastPage() {
                return isLastPage;
            }

            public boolean isLoading() {
                return isLoading;
            }

        });
//initiate service and load data
        movieService = MovieApi.getClient().create(MovieService.class);
        moviesearch = MovieApi.getClient().create(MovieSearch.class);

        loadFirstPAge();
    }

    private void loadFirstPAge() {
        Log.d(TAG, "load_first_page");
        callTopRatedMoviesApi().enqueue(new Callback<TopRatedMovies>() {
            @Override
            public void onResponse(retrofit2.Call<TopRatedMovies> call, Response<TopRatedMovies> response) {
                List<Result> results = fetchResults(response);
                progressBar.setVisibility(View.GONE);
                adapter.addAll(results);
                if (currentPage <= TOTAL_PAGES)
                    adapter.addLoadingFooter();
                else
                    isLastPage = true;
            }

            @Override
            public void onFailure(retrofit2.Call<TopRatedMovies> call, Throwable t) {
                t.printStackTrace();
            }


        });
    }

    private List<Result> fetchResults(Response<TopRatedMovies> response) {
        TopRatedMovies topRatedMovies = response.body();
        return topRatedMovies.getResults();
    }

    private void loadNextPage() {
        Log.d(TAG, "load next page" + currentPage);
        callTopRatedMoviesApi().enqueue(new Callback<TopRatedMovies>() {

            @Override
            public void onResponse(retrofit2.Call<TopRatedMovies> call, Response<TopRatedMovies> response) {
                adapter.removeLoadingFooter();
                isLoading = false;
                List<Result> results = fetchResults(response);
                adapter.addAll(results);
                if (currentPage != TOTAL_PAGES)
                    adapter.addLoadingFooter();
                else
                    isLastPage = true;
            }

            @Override
            public void onFailure(retrofit2.Call<TopRatedMovies> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private retrofit2.Call<TopRatedMovies> callTopRatedMoviesApi() {
        return movieService.getTopRatedMovies(
                getString(R.string.my_api_key),
                "en_US",
                currentPage
        );
    }
    private retrofit2.Call<TopRatedMovies> callSearchedMoviesApi(String query) {
        return moviesearch.getTopRatedMovies(
                getString(R.string.my_api_key),
                query

        );
    }

}
