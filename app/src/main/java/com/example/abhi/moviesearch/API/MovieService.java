package com.example.abhi.moviesearch.API;

import com.example.abhi.moviesearch.model.TopRatedMovies;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by abhi on 12/3/18.
 */

public interface MovieService {
    @GET("discover/movie")
    Call<TopRatedMovies> getTopRatedMovies(
      @Query("api_key") String apiKey,
      @Query("language")String language,
      @Query("page") int pageindex
    );
}
