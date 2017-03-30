package org.binaryeye.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.binaryeye.popularmovies.Models.MovieVideos;
import org.binaryeye.popularmovies.Models.MoviesVideosList;

/**
 * Created by ammar on 0019 19 Mar 17.
 */

public class MovieVideosAdapter extends RecyclerView.Adapter<MovieVideosAdapter.MoviesAdapterViewHolder> {

    private final MoviesAdapterOnClickHandler mClickHandler;
    private MoviesVideosList moviesData;

    public MovieVideosAdapter(MoviesAdapterOnClickHandler clickHandler){
        mClickHandler = clickHandler;
    }

    @Override
    public MoviesAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.movie_videos_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new MoviesAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MoviesAdapterViewHolder holder, int position) {
        MovieVideos currentMovie = moviesData.getResults()[position];
        holder.movieVideoTitle.setText(currentMovie.getName());
        holder.movieVideoTitle.setTag(currentMovie.getKey());
    }

    @Override
    public int getItemCount() {
        if (null == moviesData) return 0;
        return moviesData.getResults().length;
    }

    public void setMoviesData(MoviesVideosList movieData) {
        moviesData = movieData;
        notifyDataSetChanged();
    }

    public interface MoviesAdapterOnClickHandler {
        void onClick(MovieVideos currentMovie);
    }

    public class MoviesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView movieVideoTitle;

        public MoviesAdapterViewHolder(View view){
            super(view);
            movieVideoTitle = (TextView) view.findViewById(R.id.trailer_title);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            MovieVideos currentMovie = moviesData.getResults()[adapterPosition];
            mClickHandler.onClick(currentMovie);
        }
    }
}
