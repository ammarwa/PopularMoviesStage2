package org.binaryeye.popularmovies;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.binaryeye.popularmovies.Models.Result;
import org.binaryeye.popularmovies.Models.TMDBJsonResponse;
import org.binaryeye.popularmovies.Utilities.NetworkUtils;
import org.binaryeye.popularmovies.data.MoviesContract;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.MoviesAdapterOnClickHandler {

    int popularOrRated = 0;
    int pageNumber = 1;

    Cursor mData;

    public static ArrayList<Result> favMovies = new ArrayList<Result>();

    ProgressBar mLoadingIndicator;
    TextView pageNumberTextView;
    ImageButton nextPage;
    ImageButton previousPage;
    int totalNumberOfPages;
    boolean inFav = false;
    private RecyclerView mRecyclerView;
    private MoviesAdapter moviesAdapter;
    private TextView mErrorMessageDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movies);
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        GridLayoutManager layoutManager = new GridLayoutManager(this,5);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.hasFixedSize();
        moviesAdapter = new MoviesAdapter(this);
        mRecyclerView.setAdapter(moviesAdapter);
        pageNumberTextView = (TextView) findViewById(R.id.page_number);
        pageNumberTextView.setText("1");
        nextPage = (ImageButton) findViewById(R.id.next_btn);
        previousPage = (ImageButton) findViewById(R.id.previous_btn);
        previousPage.setEnabled(false);
        loadMoviesData();
        new FetchFavMoviesTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.sort_pop) {
            popularOrRated = 0;
            pageNumber = 1;
            previousPage.setEnabled(false);
            pageNumberTextView.setText("1");
            nextPage.setEnabled(true);
            loadMoviesData();
            inFav = false;
            return true;
        }
        if (id == R.id.sort_rated) {
            popularOrRated = 1;
            pageNumber = 1;
            previousPage.setEnabled(false);
            pageNumberTextView.setText("1");
            nextPage.setEnabled(true);
            loadMoviesData();
            inFav = false;
            return true;
        }
        if (id == R.id.sort_fav) {
            if (favMovies.size() > 0) {
                TMDBJsonResponse tmdbJsonResponse = new TMDBJsonResponse();
                Result[] results = new Result[favMovies.size()];
                for(int i = 0; i < favMovies.size(); i++){
                    results[i] = favMovies.get(i);
                }
                tmdbJsonResponse.setResults(results);
                showMoviesDataView();
                moviesAdapter.setMoviesData(tmdbJsonResponse);
                inFav = true;
            } else {
                Toast.makeText(this, "No Movies in your favourites!", Toast.LENGTH_LONG);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadMoviesData() {
        showMoviesDataView();
        new FetchMoviesTask().execute();
    }

    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    private void showMoviesDataView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the weather data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(Result currentMovie) {
        Context context = this;
        Class destinationClass = MovieActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra("Details",  currentMovie.toString());
        intentToStartDetailActivity.putExtra("fav", inFav + "");
        intentToStartDetailActivity.putExtra("movie_id", currentMovie.getId());
        startActivity(intentToStartDetailActivity);
    }

    public void previous_page_onClick(View v) {
        pageNumber--;
        if(pageNumber==1){
            previousPage.setEnabled(false);
        }
        nextPage.setEnabled(true);
        pageNumberTextView.setText(Integer.toString(pageNumber));
        loadMoviesData();
    }

    public void next_page_onClick(View v) {
        pageNumber++;
        if(pageNumber == totalNumberOfPages){
            nextPage.setEnabled(false);
        }
        previousPage.setEnabled(true);
        pageNumberTextView.setText(Integer.toString(pageNumber));
        loadMoviesData();
    }

    public void loadFavMovies() {
        if (mData != null) {
            favMovies = new ArrayList<Result>();
            int mtitle = mData.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_TITLE);
            int mPoster = mData.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_POSTER_PATH);
            int mOverview = mData.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_OVERVIEW);
            int mRDate = mData.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE);
            int mMID = mData.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_MOVIE_ID);
            int mVAvg = mData.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE);
            try {
                while (mData.moveToNext()) {
                    Result result = new Result();

                    result.setTitle(mData.getString(mtitle));
                    result.setPoster_path(mData.getString(mPoster));
                    result.setOverview(mData.getString(mOverview));
                    result.setRelease_date(mData.getString(mRDate));
                    result.setId(mData.getString(mMID));
                    result.setVote_average(mData.getString(mVAvg));


                    favMovies.add(result);
                }
            } finally {
                mData.close();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        new FetchFavMoviesTask().execute();
    }

    public class FetchFavMoviesTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... params) {
            ContentResolver resolver = getContentResolver();
            Cursor cursor = resolver.query(MoviesContract.MoviesEntry.CONTENT_URI,
                    null, null, null, null);
            return cursor;
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            super.onPostExecute(cursor);
            mData = cursor;
            loadFavMovies();
        }
    }

    public class FetchMoviesTask extends AsyncTask<Void, Void, TMDBJsonResponse>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);
        }

        @Override
        protected TMDBJsonResponse doInBackground(Void... params) {
            URL moviesRequestUrl = NetworkUtils.buildUrl(popularOrRated, pageNumber, 0L);

            try {
                String jsonMoviesResponse = NetworkUtils
                        .getResponseFromHttpUrl(moviesRequestUrl);

                JSONObject movieJson = new JSONObject(jsonMoviesResponse);

                TMDBJsonResponse response = new TMDBJsonResponse();

                response.setPage(Integer.parseInt(movieJson.get("page").toString()));
                Result[] results = new Result[20];
                JSONArray movieJSONResults = movieJson.getJSONArray("results");
                for(int i = 0; i < movieJSONResults.length(); i++ ){
                    results[i] = new Result();
                    results[i].setPoster_path(movieJSONResults.getJSONObject(i).getString("poster_path"));
                    results[i].setAdult(movieJSONResults.getJSONObject(i).get("adult").toString());
                    results[i].setOverview(movieJSONResults.getJSONObject(i).get("overview").toString());
                    results[i].setRelease_date(movieJSONResults.getJSONObject(i).get("release_date").toString());
                    results[i].setGenre_ids(movieJSONResults.getJSONObject(i).get("genre_ids").toString());
                    results[i].setId(movieJSONResults.getJSONObject(i).get("id").toString());
                    results[i].setOriginal_title(movieJSONResults.getJSONObject(i).get("original_title").toString());
                    results[i].setOriginal_language(movieJSONResults.getJSONObject(i).get("original_language").toString());
                    results[i].setTitle(movieJSONResults.getJSONObject(i).get("title").toString());
                    results[i].setBackdrop_path(movieJSONResults.getJSONObject(i).get("backdrop_path").toString());
                    results[i].setPopularity(movieJSONResults.getJSONObject(i).get("popularity").toString());
                    results[i].setVote_count(movieJSONResults.getJSONObject(i).get("vote_count").toString());
                    results[i].setVideo(movieJSONResults.getJSONObject(i).get("video").toString());
                    results[i].setVote_average(movieJSONResults.getJSONObject(i).get("vote_average").toString());
                }
                response.setResults(results);
                response.setTotal_results(movieJson.get("total_results").toString());
                response.setTotal_pages(movieJson.get("total_pages").toString());
                totalNumberOfPages = Integer.parseInt(response.getTotal_pages());

                return response;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(TMDBJsonResponse moviesData) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
            if (moviesData != null) {
                showMoviesDataView();
                moviesAdapter.setMoviesData(moviesData);
            } else {
                showErrorMessage();
            }
        }
    }
}
