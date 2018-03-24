package com.example.abhi.moviesearch.Presenter;


import com.example.abhi.moviesearch.API.MovieApi;
import com.example.abhi.moviesearch.API.MovieSearch;
import com.example.abhi.moviesearch.API.MovieService;
import com.example.abhi.moviesearch.MainActivity;
import com.example.abhi.moviesearch.R;
import com.example.abhi.moviesearch.model.Result;
import com.example.abhi.moviesearch.model.TopRatedMovies;
import com.example.abhi.moviesearch.mpvView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;




/**
 * Created by abhi on 24/3/18.
 */

public class getMovies<V extends mpvView>  implements fetchMovies  {
    private final String str = "62d66706efe1d38f0a4dfd8dc32c1f0a";
    private MovieService movieservice = MovieApi.getClient().create(MovieService.class);
    private MovieSearch moviesearch = MovieApi.getClient().create(MovieSearch.class);
    private MainActivity mainActivity;
    public getMovies(MainActivity mainactivity){
        mainActivity = mainactivity;
    }


    public void loadPage () {
            callMovieApi().enqueue(new Callback<TopRatedMovies>() {
                @Override
                public void onResponse(Call<TopRatedMovies> call, Response<TopRatedMovies> response) {
               List<Result> results = fetchResults(response);
                mainActivity.updateUi(results);
                }

                @Override
                public void onFailure(Call<TopRatedMovies> call, Throwable t) {
                    t.printStackTrace();
                }
            });


        }



    public void loadSearchPage(String Query) {
        callSearchMovieApi(Query).enqueue(new Callback<TopRatedMovies>() {
            @Override
            public void onResponse(Call<TopRatedMovies> call, Response<TopRatedMovies> response) {
                List<Result> results = fetchResults(response);
                mainActivity.updateUiSearch(results);
            }

            @Override
            public void onFailure(Call<TopRatedMovies> call, Throwable t) {
            t.printStackTrace();
            }
        });
    }




    public retrofit2.Call<TopRatedMovies> callMovieApi() {
        int current_page = mainActivity.getCurrentPage();
        return movieservice.getTopRatedMovies(str, "en_us", current_page);

    }

    public retrofit2.Call<TopRatedMovies> callSearchMovieApi(String Query) {
        int currentSearchPage =mainActivity.getCurrentPageSearch();
        return moviesearch.getTopRatedMovies(str, Query, currentSearchPage);
    }

    private List<Result> fetchResults(Response<TopRatedMovies> response) {
        TopRatedMovies topRatedMovies = response.body();
        return topRatedMovies.getResults();
    }



}
