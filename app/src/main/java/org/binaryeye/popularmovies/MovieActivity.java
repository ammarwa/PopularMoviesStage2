package org.binaryeye.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.binaryeye.popularmovies.Models.MovieReviews;
import org.binaryeye.popularmovies.Models.MovieReviewsList;
import org.binaryeye.popularmovies.Models.MovieVideos;
import org.binaryeye.popularmovies.Models.MoviesVideosList;
import org.binaryeye.popularmovies.Utilities.NetworkUtils;
import org.binaryeye.popularmovies.data.MoviesContract;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class MovieActivity extends AppCompatActivity implements MovieVideosAdapter.MoviesAdapterOnClickHandler {

    String currentMovie;
    TextView movieTitle;
    ImageView movieImage;
    TextView movieOverview;
    RatingBar movieRating;
    TextView movieReleaseDate;
    ImageButton favImageBtn;
    String movieID;
    MovieVideosAdapter movieVideosAdapter;
    MovieReviewsAdapter movieReviewsAdapter;
    RecyclerView mRecyclerView;
    RecyclerView mRecyclerViewReviews;
    String inFav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        movieTitle = (TextView) findViewById(R.id.movie_title);
        movieImage = (ImageView) findViewById(R.id.movie_image);
        movieOverview = (TextView) findViewById(R.id.movie_overview);
        movieRating = (RatingBar) findViewById(R.id.movie_rating);
        movieReleaseDate = (TextView) findViewById(R.id.movie_release_date);
        favImageBtn = (ImageButton) findViewById(R.id.fav_btn);
        mRecyclerView = (RecyclerView) findViewById(R.id.movie_trailers);
        mRecyclerViewReviews = (RecyclerView) findViewById(R.id.movie_reviews);

        Intent intent = getIntent();
        if (intent != null) {
            currentMovie = intent.getStringExtra("Details");
            inFav = intent.getStringExtra("fav");
        }
        if (inFav.equals("true")) {
            favImageBtn.setTag(1);
            favImageBtn.setImageResource(R.drawable.ic_favorite_48px);
        } else {
            favImageBtn.setTag(0);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.hasFixedSize();
        movieVideosAdapter = new MovieVideosAdapter(this);
        movieReviewsAdapter = new MovieReviewsAdapter();
        mRecyclerView.setAdapter(movieVideosAdapter);
        try {
            movieTitle.setText(currentMovie.split(":")[3]);
            movieOverview.setText(currentMovie.split(":")[1]);
            movieRating.setEnabled(false);
            movieRating.setRating((Float.parseFloat(currentMovie.split(":")[4]) * 5.0f) / 10.0f);
            movieReleaseDate.setText(currentMovie.split(":")[2]);
            new FetchImage().execute(currentMovie.split(":")[0]);
            movieID = currentMovie.split(":")[5];
        }catch (Exception e){}

        LinearLayoutManager layoutManagerR = new LinearLayoutManager(this);
        mRecyclerViewReviews.setLayoutManager(layoutManagerR);
        mRecyclerViewReviews.hasFixedSize();
        mRecyclerViewReviews.setAdapter(movieReviewsAdapter);

        loadMovieVideos();
        loadMovieReviews();

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                onSwipe(((MovieVideosAdapter.MoviesAdapterViewHolder) viewHolder).movieVideoTitle.getTag().toString());
                loadMovieVideos();
            }
        }).attachToRecyclerView(mRecyclerView);
    }

    private void loadMovieVideos(){
        new FetchMovieVideosTask().execute();
    }

    private void loadMovieReviews() {
        new FetchMovieReviewsTask().execute();
    }

    @Override
    public void onClick(MovieVideos currentVideo) {
        if (currentVideo.getKey() != null) {
            Uri uri = Uri.parse("https://www.youtube.com/watch?v=" + currentVideo.getKey());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }

    public void onSwipe(String currentVideo) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "https://www.youtube.com/watch?v=" + currentVideo);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "Share via"));
    }

    public void favBtnOnClick(View view) {
        if (Integer.parseInt(favImageBtn.getTag().toString()) == 0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_ID, currentMovie.split(":")[5]);
            contentValues.put(MoviesContract.MoviesEntry.COLUMN_OVERVIEW, currentMovie.split(":")[1]);
            contentValues.put(MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE, currentMovie.split(":")[4]);
            contentValues.put(MoviesContract.MoviesEntry.COLUMN_POSTER_PATH, currentMovie.split(":")[0]);
            contentValues.put(MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE, currentMovie.split(":")[2]);
            contentValues.put(MoviesContract.MoviesEntry.COLUMN_TITLE, currentMovie.split(":")[3]);
            Uri uri = getContentResolver().insert(MoviesContract.MoviesEntry.CONTENT_URI, contentValues);
            if (uri != null) {
                Toast.makeText(getBaseContext(), "Added to Favourites!", Toast.LENGTH_LONG).show();
                favImageBtn.setImageResource(R.drawable.ic_favorite_48px);
                favImageBtn.setTag(1);
            }
        } else {
            Uri uri = MoviesContract.MoviesEntry.CONTENT_URI;
            uri = uri.buildUpon().appendPath(currentMovie.split(":")[5]).build();
            getContentResolver().delete(uri, null, null);
            favImageBtn.setImageResource(R.drawable.ic_nfavorite_border_48px);
            favImageBtn.setTag(0);
        }
    }

    public class FetchMovieVideosTask extends AsyncTask<Void, Void, MoviesVideosList>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected MoviesVideosList doInBackground(Void... params) {
            try {
                URL moviesRequestUrl = NetworkUtils.buildUrl(2, 0, Long.parseLong(movieID));
                String jsonMoviesResponse = NetworkUtils
                        .getResponseFromHttpUrl(moviesRequestUrl);

                JSONObject movieJson = new JSONObject(jsonMoviesResponse);

                MoviesVideosList response = new MoviesVideosList();
                JSONArray movieJSONResults = movieJson.getJSONArray("results");
                MovieVideos[] results = new MovieVideos[movieJSONResults.length()];
                for (int i = 0; i < movieJSONResults.length(); i++) {
                    results[i] = new MovieVideos();
                    if (movieJSONResults.getJSONObject(i) != null) {
                        results[i].setId((movieJSONResults.getJSONObject(i).getString("id").toString() == null) ? "N/A" : movieJSONResults.getJSONObject(i).getString("id").toString());
                        results[i].setIso_639_1((movieJSONResults.getJSONObject(i).get("iso_639_1").toString() == null) ? "N/A" : movieJSONResults.getJSONObject(i).get("iso_639_1").toString());
                        results[i].setIso_3166_1((movieJSONResults.getJSONObject(i).get("iso_3166_1").toString() == null) ? "N/A" : movieJSONResults.getJSONObject(i).get("iso_3166_1").toString());
                        results[i].setKey((movieJSONResults.getJSONObject(i).get("key").toString() == null) ? "N/A" : movieJSONResults.getJSONObject(i).get("key").toString());
                        results[i].setName((movieJSONResults.getJSONObject(i).get("name").toString() == null) ? "N/A" : movieJSONResults.getJSONObject(i).get("name").toString());
                        results[i].setSite((movieJSONResults.getJSONObject(i).get("site").toString() == null) ? "N/A" : movieJSONResults.getJSONObject(i).get("site").toString());
                        results[i].setSize((movieJSONResults.getJSONObject(i).get("size").toString() == null) ? "N/A" : movieJSONResults.getJSONObject(i).get("size").toString());
                        results[i].setType((movieJSONResults.getJSONObject(i).get("type").toString() == null) ? "N/A" : movieJSONResults.getJSONObject(i).get("type").toString());
                    }
                }
                response.setResults(results);
                response.setId(movieJson.get("id").toString());

                return response;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(MoviesVideosList moviesData) {
            if (moviesData != null) {
                movieVideosAdapter.setMoviesData(moviesData);
            } else {
                MoviesVideosList mDefault = new MoviesVideosList();
                MovieVideos[] mVDefault = new MovieVideos[1];
                mVDefault[0] = new MovieVideos();
                mVDefault[0].setName("There is no trailers!");
                mDefault.setResults(mVDefault);
                movieVideosAdapter.setMoviesData(mDefault);
            }
        }
    }

    public class FetchMovieReviewsTask extends AsyncTask<Void, Void, MovieReviewsList> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected MovieReviewsList doInBackground(Void... params) {
            try {
                URL moviesRequestUrl = NetworkUtils.buildUrl(3, 0, Long.parseLong(movieID));
                String jsonMoviesResponse = NetworkUtils
                        .getResponseFromHttpUrl(moviesRequestUrl);

                JSONObject movieJson = new JSONObject(jsonMoviesResponse);

                MovieReviewsList response = new MovieReviewsList();
                JSONArray movieJSONResults = movieJson.getJSONArray("results");
                MovieReviews[] results = new MovieReviews[movieJSONResults.length()];
                for(int i = 0; i < movieJSONResults.length(); i++ ){
                    results[i] = new MovieReviews();
                    if (movieJSONResults.getJSONObject(i) != null) {
                        results[i].setId((movieJSONResults.getJSONObject(i).getString("id").toString() == null) ? "N/A" : movieJSONResults.getJSONObject(i).getString("id").toString());
                        results[i].setAuthor((movieJSONResults.getJSONObject(i).get("author").toString() == null) ? "N/A" : movieJSONResults.getJSONObject(i).get("author").toString());
                        results[i].setContent((movieJSONResults.getJSONObject(i).get("content").toString() == null) ? "N/A" : movieJSONResults.getJSONObject(i).get("content").toString());
                        results[i].setUrl((movieJSONResults.getJSONObject(i).get("url").toString() == null) ? "N/A" : movieJSONResults.getJSONObject(i).get("url").toString());
                    }
                }
                response.setResults(results);
                response.setId(movieJson.get("id").toString());
                response.setPage(movieJson.get("page").toString());
                response.setTotal_pages(movieJson.get("total_pages").toString());
                response.setTotal_results(movieJson.get("total_results").toString());

                return response;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(MovieReviewsList moviesData) {
            if (moviesData != null) {
                movieReviewsAdapter.setMoviesData(moviesData);
            } else {
                MovieReviewsList mDefault = new MovieReviewsList();
                MovieReviews[] mVDefault = new MovieReviews[1];
                mVDefault[0] = new MovieReviews();
                mVDefault[0].setContent("There is no trailers!");
                mVDefault[0].setAuthor("");
                mDefault.setResults(mVDefault);
                movieReviewsAdapter.setMoviesData(mDefault);
            }
        }
    }

    public class FetchImage extends AsyncTask<String,Void,Bitmap>{

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                URL url = new URL("http://image.tmdb.org/t/p/w185" + params[0]);
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

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            movieImage.setImageBitmap(bitmap);
            super.onPostExecute(bitmap);
        }
    }
}
