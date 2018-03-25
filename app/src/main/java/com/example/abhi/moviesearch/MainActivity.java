package com.example.abhi.moviesearch;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import com.example.abhi.moviesearch.Presenter.getMoviesImplem;
import com.example.abhi.moviesearch.model.Result;
import com.example.abhi.moviesearch.util.ScrollListener;

import java.util.List;


public class MainActivity extends AppCompatActivity implements mvpView {

    private static final String TAG = "MainActivity";
    private static final int PAGE_START = 1;
    Adapter adapter,adapter_search;
    LinearLayoutManager linearLayoutManager;
    RecyclerView rv;
    ProgressBar progressBar;
    private boolean isOnMain=true;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private boolean isLoadingSearch =false;
    private boolean isLastPageSearch = false;
    private int TOTAL_PAGES;
    private int TOTAL_PAGES_SEARCH;//restricting to 20 pages
    private int currentPage = PAGE_START;
    private int currentPageSearch=PAGE_START;
    private String query;
    private getMoviesImplem getmovies;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        super.onCreate(savedInstanceState);
        getmovies=new getMoviesImplem(this);
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

                                getmovies.loadPage();
                            }
                        }, 1000);

                    } else {
                        isLoadingSearch = true;
                        currentPageSearch += 1;
                        //taking care of network call for API call
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getmovies.loadSearchPage(query);

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

            getmovies.loadPage();
        }
else {
            Toast.makeText(this, "no internet", Toast.LENGTH_LONG).show();
        }



    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            getmovies.loadSearchPage(query);

        }
    }

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
    public void updateUi(List<Result> results,int totalPages) {
        if(isLoading==false) {
            TOTAL_PAGES = totalPages;
            progressBar.setVisibility(View.GONE);
            adapter.addAll(results);
            if (currentPage <= TOTAL_PAGES){
                adapter.addLoadingFooter();}
            else
                isLastPage = true;
        }
        else{
            adapter.removeLoadingFooter();
            isLoading = false;
            adapter.addAll(results);
            if (currentPage != TOTAL_PAGES)
                adapter.addLoadingFooter();
            else
                isLastPage = true;
        }
    }
    @Override
    public void updateUiSearch(List<Result> results,int totalPagesSearch) {
        if(isLoadingSearch==false) {
            TOTAL_PAGES_SEARCH=totalPagesSearch;
            adapter_search = new Adapter(MainActivity.this);
            adapter_search.addAll(results);
            rv.setAdapter(adapter_search);

            if (currentPageSearch <= TOTAL_PAGES_SEARCH) {
                adapter_search.addLoadingFooter();
            }
            else
                isLastPageSearch = true;

            isOnMain = false;
        }
        else{
            adapter_search.removeLoadingFooter();
            isLoadingSearch = false;
            adapter_search.addAll(results);
            if (currentPageSearch != TOTAL_PAGES_SEARCH)
                adapter_search.addLoadingFooter();
            else
                isLastPageSearch = true;
        }
    }

@Override
public int getCurrentPage(){
        return currentPage;
}
@Override
public int getCurrentPageSearch(){
    return currentPageSearch;
}


}
