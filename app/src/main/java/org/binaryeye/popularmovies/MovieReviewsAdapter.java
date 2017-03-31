package org.binaryeye.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.binaryeye.popularmovies.Models.MovieReviews;
import org.binaryeye.popularmovies.Models.MovieReviewsList;

/**
 * Created by ammar on 0019 19 Mar 17.
 */

public class MovieReviewsAdapter extends RecyclerView.Adapter<MovieReviewsAdapter.MoviesAdapterViewHolder> {

    private MovieReviewsList moviesData;

    public MovieReviewsAdapter() {
    }

    @Override
    public MoviesAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.movie_reviews_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new MoviesAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MoviesAdapterViewHolder holder, int position) {
        MovieReviews currentReview = moviesData.getResults()[position];
        holder.movieReviewContent.setText(currentReview.getContent());
        holder.movieReviewAuthor.setText(currentReview.getAuthor());
    }

    @Override
    public int getItemCount() {
        if (null == moviesData) return 0;
        return moviesData.getResults().length;
    }

    public void setMoviesData(MovieReviewsList movieData) {
        moviesData = movieData;
        notifyDataSetChanged();
    }

    public interface MoviesAdapterOnClickHandler {
        void onClick(MovieReviews currentMovie);
    }

    public class MoviesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView movieReviewAuthor;
        public final TextView movieReviewContent;

        public MoviesAdapterViewHolder(View view) {
            super(view);
            movieReviewAuthor = (TextView) view.findViewById(R.id.author_review);
            movieReviewContent = (TextView) view.findViewById(R.id.content_review);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            MovieReviews currentReview = moviesData.getResults()[adapterPosition];
        }
    }
}
