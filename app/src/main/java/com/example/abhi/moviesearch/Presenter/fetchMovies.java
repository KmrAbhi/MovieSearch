package com.example.abhi.moviesearch.Presenter;
import com.example.abhi.moviesearch.model.TopRatedMovies;
import com.example.abhi.moviesearch.mpvView;

import retrofit2.Response;

/**
 * Created by abhi on 24/3/18.
 */

public interface fetchMovies  <V extends mpvView> {
    void loadPage();
    void loadSearchPage(String Query);



}
