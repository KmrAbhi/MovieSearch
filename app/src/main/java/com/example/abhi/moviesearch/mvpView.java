package com.example.abhi.moviesearch;

import com.example.abhi.moviesearch.model.Result;

import java.util.List;

/**
 * Created by abhi on 25/3/18.
 */

public interface mvpView {
    void updateUi(List<Result> results);
    void updateUiSearch(List<Result> results);
    int getCurrentPage();
    int getCurrentPageSearch();
}
