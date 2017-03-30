package org.binaryeye.popularmovies;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import org.binaryeye.popularmovies.Models.MovieVideos;
import org.binaryeye.popularmovies.Models.MoviesVideosList;
import org.binaryeye.popularmovies.Models.Result;
import org.binaryeye.popularmovies.Models.TMDBJsonResponse;
import org.binaryeye.popularmovies.Utilities.NetworkUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class MovieActivity extends AppCompatActivity {

    String currentMovie;
    TextView movieTitle;
    ImageView movieImage;
    TextView movieOverview;
    RatingBar movieRating;
    TextView movieReleaseDate;
    ImageButton favImageBtn;
    String movieID;
    MovieVideosAdapter movieVideosAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
        Intent intent = getIntent();
        if(intent != null){
            currentMovie = intent.getStringExtra("Details");
        }
        movieTitle = (TextView) findViewById(R.id.movie_title);
        movieImage = (ImageView) findViewById(R.id.movie_image);
        movieOverview = (TextView) findViewById(R.id.movie_overview);
        movieRating = (RatingBar) findViewById(R.id.movie_rating);
        movieReleaseDate = (TextView) findViewById(R.id.movie_release_date);
        favImageBtn = (ImageButton) findViewById(R.id.fav_btn);

        favImageBtn.setTag(0);

        try {
            movieTitle.setText(currentMovie.split(":")[3]);
            movieOverview.setText(currentMovie.split(":")[1]);
            movieRating.setEnabled(false);
            movieRating.setRating((Float.parseFloat(currentMovie.split(":")[4]) * 5.0f) / 10.0f);
            movieReleaseDate.setText(currentMovie.split(":")[2]);
            new FetchImage().execute(currentMovie.split(":")[0]);
            movieID = currentMovie.split(":")[5];
        }catch (Exception e){}

        loadMovieVideos();
    }

    private void loadMovieVideos(){
        new FetchMovieVideosTask().execute();
    }

    public class FetchMovieVideosTask extends AsyncTask<Void, Void, MoviesVideosList>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected MoviesVideosList doInBackground(Void... params) {
            URL moviesRequestUrl = NetworkUtils.buildUrl(2, 0, Long.parseLong(movieID));

            try {
                String jsonMoviesResponse = NetworkUtils
                        .getResponseFromHttpUrl(moviesRequestUrl);

                JSONObject movieJson = new JSONObject(jsonMoviesResponse);

                MoviesVideosList response = new MoviesVideosList();
                JSONArray movieJSONResults = movieJson.getJSONArray("results");
                MovieVideos[] results = new MovieVideos[movieJSONResults.length()];
                for(int i = 0; i < movieJSONResults.length(); i++ ){
                    results[i] = new MovieVideos();
                    results[i].setId(movieJSONResults.getJSONObject(i).getString("id"));
                    results[i].setIso_639_1(movieJSONResults.getJSONObject(i).get("iso_639_1").toString());
                    results[i].setIso_3166_1(movieJSONResults.getJSONObject(i).get("iso_3166_1").toString());
                    results[i].setKey(movieJSONResults.getJSONObject(i).get("key").toString());
                    results[i].setName(movieJSONResults.getJSONObject(i).get("name").toString());
                    results[i].setSite(movieJSONResults.getJSONObject(i).get("site").toString());
                    results[i].setSize(movieJSONResults.getJSONObject(i).get("size").toString());
                    results[i].setType(movieJSONResults.getJSONObject(i).get("type").toString());
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
                movieVideosAdapter.setMoviesData(null);
            }
        }
    }

    public void favBtnOnClick(View view){
        if(Integer.parseInt(favImageBtn.getTag().toString())==0) {
            favImageBtn.setImageResource(R.drawable.ic_favorite_48px);
            favImageBtn.setTag(1);
        } else {
            favImageBtn.setImageResource(R.drawable.ic_nfavorite_border_48px);
            favImageBtn.setTag(0);
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
