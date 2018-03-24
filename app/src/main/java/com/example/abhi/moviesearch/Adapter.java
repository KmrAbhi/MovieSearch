package com.example.abhi.moviesearch;

import android.content.Context;
import android.graphics.Movie;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.abhi.moviesearch.model.Result;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abhi on 12/3/18.
 */

public class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int item = 0;
    private static final int loading = 1;
    private static final String base_url_img="https://image.tmdb.org/t/p/w150/";
    private List<Result> movieResults;
    private Context context;
    private boolean isLoadingAdded=false;

    public Adapter(Context context){
        this.context=context;
        movieResults=new ArrayList<>();
    }
    public List<Result> getMovies(){
        return movieResults;
    }
    public void setMovies(List<Result> movieResults){
        this.movieResults=movieResults;
    }
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
      RecyclerView.ViewHolder viewHolder = null;
      LayoutInflater inflater = LayoutInflater.from(parent.getContext());
      switch (viewType){
            case item:
                    viewHolder=getViewHolder(parent,inflater);
                    break;
            case loading:
                View v2 =inflater.inflate(R.layout.progressitem,parent,false);
                viewHolder=new LoadingVH(v2);
                break;


      }
      return viewHolder;
    }
    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent,LayoutInflater inflater){
        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.list_items, parent,false);

        viewHolder = new MovieVH(v1);
        return viewHolder;
    }
    public void onBindViewHolder(RecyclerView.ViewHolder holder,int position){
        Result result = movieResults.get(position);
        switch (getItemViewType(position)){
            case item:
                final MovieVH movieVH = (MovieVH) holder;
                movieVH.mMovieTitle.setText(result.getTitle());
                movieVH.mMovieDesc.setText(result.getOverview());
                Glide
                        .with(context)
                        .load(base_url_img+result.getPosterPath())
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                movieVH.mProgress.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                movieVH.mProgress.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .crossFade()
                        .into(movieVH.mPosterImg);
                break;
            case loading:

                break;
        }

    }
    public int getItemCount(){
        return movieResults==null?0:movieResults.size();
    }
    public int getItemViewType(int position){
        return (position==movieResults.size()-1&&isLoadingAdded)?loading:item;
    }


    public void add(Result r){
        movieResults.add(r);
        notifyItemInserted(movieResults.size()-1);
    }
    public void addAll(List<Result> movieResults) {
        for (Result result : movieResults) {
            add(result);
        }
    }
    public void remove(Result r) {
        int position = movieResults.indexOf(r);
        if (position > -1) {
            movieResults.remove(position);
            notifyItemRemoved(position);
        }
    }
    public void clear(){
     isLoadingAdded=false;
     while (getItemCount()>0){
         remove(getItem(0));
     }
    }
    public boolean isEmpty(){
        return getItemCount()==0;
    }
    public void addLoadingFooter(){
        isLoadingAdded=true;
        add(new Result());
    }
    public void removeLoadingFooter(){
        isLoadingAdded=false;
        int position =movieResults.size()-1;
        Result result =getItem(position);
        if (result != null){
            movieResults.remove(position);
            notifyItemRemoved(position);
        }
    }
    public Result getItem(int position){
        return movieResults.get(position);
    }

protected class MovieVH extends RecyclerView.ViewHolder{
    private TextView mMovieTitle;
    private TextView mMovieDesc;
    private ImageView mPosterImg;
    private ProgressBar mProgress;
    public MovieVH(View itemView){
        super(itemView);
        mMovieTitle=(TextView)itemView.findViewById(R.id.movie_title);
        mMovieDesc=(TextView)itemView.findViewById(R.id.movie_desc);
        mPosterImg = (ImageView)itemView.findViewById(R.id.movie_poster);
        mProgress = (ProgressBar)itemView.findViewById(R.id.progress_movies);
    }


}
protected class LoadingVH extends RecyclerView.ViewHolder{
    public LoadingVH (View itemView){
        super(itemView);
    }
}

}
