package com.example.abhi.moviesearch.API;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by abhi on 12/3/18.
 */

public class MovieApi {
    private static Retrofit retrofit = null;
    public static Retrofit getClient(){
        if(retrofit==null){
            retrofit=new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("https://api.themoviedb.org/3/")
                    .build();
        }
        return retrofit;
    }
}
