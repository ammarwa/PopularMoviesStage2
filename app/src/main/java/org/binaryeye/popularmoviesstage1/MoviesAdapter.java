package org.binaryeye.popularmoviesstage1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.binaryeye.popularmoviesstage1.Models.Result;
import org.binaryeye.popularmoviesstage1.Models.TMDBJsonResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

/**
 * Created by ammar on 0019 19 Mar 17.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesAdapterViewHolder> {

    private TMDBJsonResponse moviesData;

    private final MoviesAdapterOnClickHandler mClickHandler;

    public MoviesAdapter(MoviesAdapterOnClickHandler clickHandler){
        mClickHandler = clickHandler;
    }

    public interface MoviesAdapterOnClickHandler{
        void onClick(Result currentMovie);
    }
    @Override
    public MoviesAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.movies_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new MoviesAdapterViewHolder(view);
    }

    public class LoadImage extends AsyncTask<String , Void, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Bitmap doInBackground(String... params) {

            try {
                URL url = new URL(params[0]);
                URI uri = new URI(url.getProtocol(), url.getHost(),
                        url.getPath(), url.getQuery(), null);
                HttpURLConnection connection = (HttpURLConnection) uri
                        .toURL().openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

                int bufferSize = 1024;
                byte[] buffer = new byte[bufferSize];
                int len = 0;
                while ((len = input.read(buffer)) != -1) {
                    byteBuffer.write(buffer, 0, len);
                }
                byte[] img = byteBuffer.toByteArray();
                byteBuffer.flush();
                byteBuffer.close();
                input.close();
                Bitmap bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
                return bitmap;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    @Override
    public void onBindViewHolder(MoviesAdapterViewHolder holder, int position) {
        Result currentMovie = moviesData.getResults()[position];
        LoadImage loadImage = new LoadImage();
        try {
            holder.movieImageButton.setImageBitmap(loadImage.execute("http://image.tmdb.org/t/p/w185" + currentMovie.getPoster_path()).get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        if (null == moviesData) return 0;
        return moviesData.getResults().length;
    }

    public class MoviesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView movieImageButton;

        public MoviesAdapterViewHolder(View view){
            super(view);
            movieImageButton = (ImageView) view.findViewById(R.id.movie_image_button);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Result currentMovie = moviesData.getResults()[adapterPosition];
            mClickHandler.onClick(currentMovie);
        }
    }

    public void setMoviesData(TMDBJsonResponse movieData) {
        moviesData = movieData;
        notifyDataSetChanged();
    }
}
