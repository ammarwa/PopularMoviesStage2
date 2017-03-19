package org.binaryeye.popularmoviesstage1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import org.binaryeye.popularmoviesstage1.Models.Result;

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

        try {
            movieTitle.setText(currentMovie.split(":")[3]);
            movieOverview.setText(currentMovie.split(":")[1]);
            movieRating.setEnabled(false);
            movieRating.setRating((Float.parseFloat(currentMovie.split(":")[4]) * 5.0f) / 10.0f);
            movieReleaseDate.setText(currentMovie.split(":")[2]);
            new FetchImage().execute(currentMovie.split(":")[0]);
        }catch (Exception e){}

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
