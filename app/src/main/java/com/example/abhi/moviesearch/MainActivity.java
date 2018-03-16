package com.example.abhi.moviesearch;

import android.accessibilityservice.AccessibilityService;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
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
    private int total;
    private int totalSearch;
    private boolean isOnMain=true;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private boolean isLoadingSearch =false;
    private boolean isLastPageSearch = false;
    private int TOTAL_PAGES=20;
    private int TOTAL_PAGES_SEARCH=20;//restricting to 20 pages
    private int currentPage = PAGE_START;
    private int currentPageSearch=PAGE_START;
    private MovieService movieService;
    private MovieSearch movieSearch;
    private String query;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        Log.e("is connected",Boolean.toString(isConnected));
        super.onCreate(savedInstanceState);
        if (isConnected){
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
                    if (isOnMain == true) {
                        isLoading = true;
                        currentPage += 1;
                        //taking care of network call for API call
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                loadNextPage();
                            }
                        }, 1000);

                    } else {
                        isLoadingSearch = true;
                        currentPageSearch += 1;
                        //taking care of network call for API call
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                loadNextSearchPage(query);

                            }
                        }, 1000);
                    }
                }

                public boolean getIsOnMain() {
                    return isOnMain;
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

                public int getTotalSearchPageCount() {
                    return TOTAL_PAGES_SEARCH;
                }

                public boolean isLastPageSearch() {
                    return isLastPageSearch;
                }

                public boolean isLoadingSearch() {
                    return isLoadingSearch;
                }

            });
//initiate service and load data
            movieService = MovieApi.getClient().create(MovieService.class);
            movieSearch = MovieApi.getClient().create(MovieSearch.class);

            loadFirstPAge();
        }
else {
            Log.e("no internet", "no internet");
            Toast.makeText(this, "no internet", Toast.LENGTH_LONG).show();
        }



    }

    private void loadFirstPAge() {
        Log.d(TAG, "load_first_page");
        callTopRatedMoviesApi().enqueue(new Callback<TopRatedMovies>() {
            @Override
            public void onResponse(retrofit2.Call<TopRatedMovies> call,
                                   Response<TopRatedMovies> response) {

                List<Result> results = fetchResults(response);
                Log.e("total", Integer.toString(TOTAL_PAGES));
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
            query = intent.getStringExtra(SearchManager.QUERY);
            loadFirstSearchPage(query);

        }
    }


    private void loadFirstSearchPage(String query){
        Toast.makeText(this, "fired", Toast.LENGTH_SHORT).show();

        callSearchedMoviesApi(query).enqueue(new Callback<TopRatedMovies>() {
            @Override
            public void onResponse(Call<TopRatedMovies> call, Response<TopRatedMovies> response) {
                List<Result> results = fetchResultsSearch(response);

                adapter_search=new Adapter(MainActivity.this);
                adapter_search.addAll(results);
                rv.setAdapter(adapter_search);

                if(currentPageSearch<=TOTAL_PAGES_SEARCH){
                    adapter_search.addLoadingFooter();
                }
                else
                    isLastPageSearch=true;

                isOnMain=false;

            }

            @Override
            public void onFailure(Call<TopRatedMovies> call, Throwable t) {
                Toast.makeText(MainActivity.this,"Couldnot fetch",Toast.LENGTH_SHORT).show();

            }
        });

    }
    private void loadNextSearchPage(String query) {
        Log.d(TAG, "load next page" + currentPageSearch);
        callSearchedMoviesApi(query).enqueue(new Callback<TopRatedMovies>() {

            @Override
            public void onResponse(retrofit2.Call<TopRatedMovies> call, Response<TopRatedMovies> response) {
                adapter_search.removeLoadingFooter();
                isLoadingSearch = false;
                List<Result> results = fetchResultsSearch(response);
                adapter_search.addAll(results);
                if (currentPageSearch != TOTAL_PAGES_SEARCH)
                    adapter_search.addLoadingFooter();
                else
                    isLastPageSearch = true;
            }

            @Override
            public void onFailure(retrofit2.Call<TopRatedMovies> call, Throwable t) {
                t.printStackTrace();
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







    private List<Result> fetchResults(Response<TopRatedMovies> response) {
        TopRatedMovies topRatedMovies = response.body();
        total = topRatedMovies.getTotalPages();

        return topRatedMovies.getResults();
    }

    private List<Result> fetchResultsSearch(Response<TopRatedMovies> response) {
        TopRatedMovies topRatedMovies = response.body();
        totalSearch = topRatedMovies.getTotalPages();
        Log.e("total pages", Integer.toString(totalSearch));
        return topRatedMovies.getResults();
    }


    private retrofit2.Call<TopRatedMovies> callTopRatedMoviesApi() {
        return movieService.getTopRatedMovies(
                getString(R.string.my_api_key),
                "en_US",
                currentPage
        );
    }

    private retrofit2.Call<TopRatedMovies> callSearchedMoviesApi(String query) {
        return movieSearch.getTopRatedMovies(
                getString(R.string.my_api_key),
                query,
                currentPageSearch

        );
    }


}
