package com.example.abhi.moviesearch.Presenter;
import com.example.abhi.moviesearch.mvpView;

/**
 * Created by abhi on 24/3/18.
 */

public interface getMoviesInterface<V extends mvpView> {
    void loadPage();
    void loadSearchPage(String Query);



}
