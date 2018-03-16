package com.example.abhi.moviesearch.util;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by abhi on 14/3/18.
 */

public abstract class ScrollListener extends RecyclerView.OnScrollListener{
    LinearLayoutManager layoutManager;
    public ScrollListener (LinearLayoutManager layoutManager){
        this.layoutManager=layoutManager;
    }
    public void onScrolled(RecyclerView recyclerView,int dx,int dy){
        super.onScrolled(recyclerView,dx,dy);
        int visibleItemCount=layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        int firstVisibleItemPosition =layoutManager.findFirstVisibleItemPosition();
        if(getIsOnMain()==true){
        if(!isLoading()&&!isLastPage()){
            if((visibleItemCount+firstVisibleItemPosition)>=totalItemCount&&
                    firstVisibleItemPosition>=0&&
                    totalItemCount>=getTotalPageCount()){
                loadMoreItems();
            }
        }
    }
    else{
        if(!isLoadingSearch()&&!isLastPageSearch()){
            if((visibleItemCount+firstVisibleItemPosition)>=totalItemCount&&
                    firstVisibleItemPosition>=0&&
                    totalItemCount>=getTotalSearchPageCount()){
                loadMoreItems();
            }  }
    }}
    protected abstract void loadMoreItems();
    public abstract int getTotalPageCount();
    public abstract int getTotalSearchPageCount();
    public abstract boolean isLastPage();
    public abstract boolean isLastPageSearch();
    public abstract boolean isLoading();
    public abstract boolean isLoadingSearch();
    public abstract boolean getIsOnMain();

}
